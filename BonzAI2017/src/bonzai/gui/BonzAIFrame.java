package bonzai.gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.UIManager;

import bonzai.GameWrapper;

@SuppressWarnings("serial")
public class BonzAIFrame extends JFrame {
	private final CardLayout switcher;
	
	@SuppressWarnings("unused")
	private static SelectorPanel selectorPanel;
	
	private BonzAIFrame(String title) {
		super(title);
		switcher = new CardLayout();
		setLayout(switcher);
	}

	public void flip() {
		switcher.next(this.getContentPane());
	}

	public boolean isFullscreen() {
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		Window current = device.getFullScreenWindow();
		return current != null;
	}

	public void toggleFullscreen() {
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		Window current = device.getFullScreenWindow();

		if (current == null) {
			setVisible(false);
			dispose();
			setUndecorated(true);

			device.setFullScreenWindow(this);

			Dimension size = new Dimension(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
			setMinimumSize(size);
			setPreferredSize(size);

			pack();
			setVisible(true);
			return;
		}

		if (current.equals(this)) {
			setVisible(false);
			dispose();
			setUndecorated(false);

			device.setFullScreenWindow(null);

			setMinimumSize(new Dimension(700, 500));
			setPreferredSize(new Dimension(700, 500));

			pack();
			setVisible(true);
			return;
		}

		// silently fail otherwise...
	}
	
	public static BonzAIFrame create(String title, GameWrapper game) throws Exception {
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

		BonzAIFrame frame = new BonzAIFrame(title);
		
		frame.add(selectorPanel = SelectorPanel.create(frame, game), "Selector");
		frame.add(GameplayPanel.create(frame, game), "Gameplay");

		frame.setMinimumSize(new Dimension(700, 500));
		frame.setPreferredSize(new Dimension(700, 500));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

		return frame;
	}
	
	public static BonzAIFrame createGameOnly(String title, GameWrapper game) throws Exception {
		UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		//System.out.println("not hilarious print statement");
		BonzAIFrame frame = new BonzAIFrame(title);
		
		frame.add(GameplayPanel.create(frame, game), "Gameplay");

		frame.setMinimumSize(new Dimension(700, 500));
		frame.setPreferredSize(new Dimension(700, 500));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

		return frame;
	}
}