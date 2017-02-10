package bonzai;

import java.awt.Color;
import java.io.File;
import java.util.List;

import javax.swing.JPanel;

public interface GameWrapper {
	public String version();

	public int teams();

	public Color color(int team);

	public Jar jar(File file) throws Exception;

	public Scenario scenario(File file) throws Exception;

	public void run(Scenario scenario, List<Jar> jars);

	public void setCurrentFrame(int frame);

	public int getCurrentFrame();

	public int availableFrames();

	public int totalFrames();

	public int getFPS();

	public void setFPS(int fps);

	public void togglePlay();

	public JPanel view();

	public void resetView();
}