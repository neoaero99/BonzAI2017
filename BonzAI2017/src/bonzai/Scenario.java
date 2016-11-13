package bonzai;

import java.awt.Image;
import java.io.File;

/**
 *
 **/
public interface Scenario extends Comparable<Scenario> {
	public File getFile();

	public String getName();
	
	public int getNumTeams();
	
	public Image getImage();
}
