package bonzai.util;

import java.io.File;

public class FileEvent {
	private final Object source;
	private final File file;

	public FileEvent(Object source, File file) {
		this.source = source;
		this.file = file;
	}

	public Object getSource() {
		return source;
	}

	public File getFile() {
		return file;
	}
}