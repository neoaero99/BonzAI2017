package bonzai;

import java.io.File;

public interface Jar extends Comparable<Jar> {
	/**
	 * Reference to the jar file
	 **/
	public File file();
	
	/**
	 * The jar file's name
	 **/
	public String name();
}
