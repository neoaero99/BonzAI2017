package bonzai.util;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class FileMonitor extends Thread {
	private Set<FileListener> fileListeners;
	private Path path;

	public FileMonitor(String directory) {
		this.fileListeners = new HashSet<>();
		this.path = new File(directory).toPath();
	}

	public void run() {
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			//WatchKey master = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

			while (true) {
				try {
					WatchKey key = watcher.take();

					for (WatchEvent<?> event : key.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();
						if (kind == OVERFLOW) {
							continue;
						}

						@SuppressWarnings("unchecked")
						WatchEvent<Path> ev = (WatchEvent<Path>) event;
						Path filename = path.resolve(ev.context());

						FileEvent e = new FileEvent(this, filename.toFile());
						for (FileListener listener : fileListeners) {
							if (ev.kind().equals(ENTRY_CREATE)) {
								listener.fileCreated(e);
							}
							if (ev.kind().equals(ENTRY_DELETE)) {
								listener.fileDeleted(e);
							}
							if (ev.kind().equals(ENTRY_MODIFY)) {
								listener.fileModified(e);
							}
						}
					}

					boolean valid = key.reset();
					if (!valid) {
						break;
					}
				} catch (InterruptedException exception) {
				}
			}
		} catch (IOException exception) {
		}
	}

	public Set<File> files() {
		Set<File> files = new HashSet<>();

		Queue<File> frontier = new ArrayDeque<>();
		frontier.add(path.toFile());
		while (!frontier.isEmpty()) {
			File current = frontier.remove();

			if (current.isDirectory()) {
				for (File child : current.listFiles()) {
					frontier.add(child);
				}
			} else {
				files.add(current);
			}
		}

		return files;
	}

	public Set<FileListener> fileListeners() {
		return fileListeners;
	}
}
