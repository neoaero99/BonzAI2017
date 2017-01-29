package bonzai.util;

public interface FileListener {
	public void fileCreated(FileEvent event);

	public void fileDeleted(FileEvent event);

	public void fileModified(FileEvent event);
}
