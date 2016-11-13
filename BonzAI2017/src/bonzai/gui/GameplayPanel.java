package bonzai.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bonzai.GameWrapper;

@SuppressWarnings("serial")
public class GameplayPanel extends JPanel implements ActionListener, KeyListener {
	private static final SpriteSheet UI = new SpriteSheet("bonzai/gui/ui.png", 16, 16);

	private final BonzAIFrame bonzai;
	private final GameWrapper game;

	private JToggleButton play;
	private JToggleButton center;
	private JToggleButton fullscreen;
	private JToggleButton close;

	private BufferedSlider track;
	private JSlider speed;

	public GameplayPanel(BonzAIFrame bonzai, GameWrapper game) {
		super(new GridBagLayout());

		this.bonzai = bonzai;
		this.game = game;

		this.play       = createToggleButton(UI.image(0, 0), UI.image(0, 1), UI.image(1, 0), UI.image(1, 1));
		this.fullscreen = createToggleButton(UI.image(2, 0), UI.image(2, 1), UI.image(3, 0), UI.image(3, 1));
		this.center     = createToggleButton(UI.image(4, 0), UI.image(4, 1), UI.image(4, 0), UI.image(4, 1));
		this.close      = createToggleButton(UI.image(5, 0), UI.image(5, 1), UI.image(5, 0), UI.image(5, 1));

		this.track = createTrackSlider();
		this.speed = createSpeedSlider();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (play.equals(e.getSource())) {
			game.togglePlay();
		}

		if (fullscreen.equals(e.getSource())) {
			bonzai.toggleFullscreen();
		}

		if (center.equals(e.getSource())) {
			game.resetView();
		}

		if (close.equals(e.getSource())) {
			if (bonzai.isFullscreen()) {
				bonzai.toggleFullscreen();
				fullscreen.setSelected(false);
			}
			bonzai.flip();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			game.togglePlay();
			play.setSelected(!play.isSelected());
		}
		if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
			bonzai.toggleFullscreen();
			fullscreen.setSelected(!fullscreen.isSelected());
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (bonzai.isFullscreen()) {
				bonzai.toggleFullscreen();
				fullscreen.setSelected(false);
			}
			bonzai.flip();
		}
		((KeyListener) game.view()).keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * 
	 * @param bonzai
	 * @param game
	 * @return
	 */
	public static JPanel create(BonzAIFrame bonzai, final GameWrapper game) {
		final GameplayPanel panel = new GameplayPanel(bonzai, game);
		panel.setBackground(Color.BLACK);
		panel.setFocusTraversalKeysEnabled(false);
		panel.setFocusable(true);
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				panel.requestFocusInWindow();
			}
		});

		panel.play.addActionListener(panel);
		panel.fullscreen.addActionListener(panel);
		panel.center.addActionListener(panel);
		panel.close.addActionListener(panel);
		panel.addKeyListener(panel);

		{
			GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 1,
					GridBagConstraints.NORTH, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0);
			JPanel view = game.view();
			panel.add(view, c);
		}

		final JPanel control = new JPanel() {
			public void paint(Graphics g) {
				panel.track.setMaximum(game.totalFrames());
				panel.track.setAvailable(game.availableFrames());
				panel.track.setValue(game.getCurrentFrame());
				panel.speed.setValue(game.getFPS());
				super.paint(g);
			}
		};
		panel.track.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				game.setCurrentFrame(((JSlider) e.getSource()).getValue());
			}
		});
		panel.speed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				game.setFPS(((JSlider) e.getSource()).getValue());
			}
		});
		control.setLayout(new GridBagLayout());
		(new Thread(new Runnable() {
			public void run() {
				while (true) {
					control.repaint();
					try {
						Thread.sleep(1000 / 60);
					} catch (InterruptedException e) {
					}
				}
			}
		})).start();
		control.setBackground(Color.BLACK);
		control.setMinimumSize(new Dimension(700, 25));
		control.setPreferredSize(new Dimension(700, 25));
		{
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.CENTER;

			c.gridx = 0;
			c.weightx = 0;
			c.fill = GridBagConstraints.NONE;
			control.add(panel.play, c);

			c.gridx = 1;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(0, 10, 0, 5);
			control.add(panel.track, c);

			c.gridx = 2;
			c.weightx = 0;
			c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(0, 5, 0, 8);
			control.add(panel.speed, c);

			c.gridx = 3;
			c.weightx = 0;
			c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(0, 0, 0, 0);
			control.add(panel.fullscreen, c);

			c.gridx = 4;
			c.weightx = 0;
			c.fill = GridBagConstraints.NONE;
			c.insets = new Insets(0, 0, 0, 0);
			control.add(panel.center, c);

			c.gridx = 5;
			c.weightx = 0;
			c.insets = new Insets(0, 10, 0, 0);
			c.fill = GridBagConstraints.NONE;
			control.add(panel.close, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints(0, 1, 1, 1, 1, 0,
					GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
					new Insets(0, 50, 0, 50), 0, 0);
			panel.add(control, c);
		}

		return panel;
	}

	private static BufferedSlider createTrackSlider() {
		BufferedSlider slider = new BufferedSlider();
		slider.setUI(new TrackSliderUI(slider));
		slider.setOpaque(false);
		slider.setFocusable(false);
		slider.setPreferredSize(new Dimension(200, 20));

		return slider;
	}

	private static JSlider createSpeedSlider() {
		JSlider slider = new JSlider(-20, 100, 10);
		slider.setUI(new SpeedSliderUI(slider));
		slider.setOpaque(false);
		slider.setFocusable(false);
		slider.setPreferredSize(new Dimension(75, 20));
		return slider;
	}

	private static JToggleButton createToggleButton(Image offImage,
			Image offHover, Image onImage, Image onHover) {
		JToggleButton button = new JToggleButton();

		button.setBackground(Color.BLACK);
		button.setBorderPainted(false);
		button.setFocusable(false);
		button.setPreferredSize(new Dimension(20, 20));
		button.setContentAreaFilled(false);

		button.setIcon(new ImageIcon(offImage));
		button.setRolloverIcon(new ImageIcon(offHover));
		button.setSelectedIcon(new ImageIcon(onImage));
		button.setRolloverSelectedIcon(new ImageIcon(onHover));

		return button;
	}
}