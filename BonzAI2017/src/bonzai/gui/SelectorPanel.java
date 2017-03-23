package bonzai.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.metal.MetalComboBoxUI;

import bonzai.GameWrapper;
import bonzai.Jar;
import bonzai.Scenario;
import bonzai.util.FileEvent;
import bonzai.util.FileListener;
import bonzai.util.FileMonitor;

@SuppressWarnings("serial")
public class SelectorPanel extends JPanel implements ActionListener, FileListener {
	private final BonzAIFrame bonzai;
	private final GameWrapper game;

	private DefaultComboBoxModel<Scenario> scenarios;
	private List<DefaultComboBoxModel<Jar>> jars;

	private FileMonitor scenarioWatcher;
	private FileMonitor jarWatcher;

	private JComboBox<Scenario> scenarioSelector;
	private List<JComboBox<Jar>> jarSelectors;

	//private JButton replay;
	private JButton resume;
	private JButton play;

	private JLabel footer;

	public SelectorPanel(BonzAIFrame bonzai, GameWrapper game) {
		super(new GridBagLayout());
		this.bonzai = bonzai;
		this.game = game;

		this.scenarios = new DefaultComboBoxModel<>();
		this.scenarioWatcher = new FileMonitor("scenarios/");
		this.scenarioWatcher.fileListeners().add(this);

		this.scenarios.addElement(null);
		for (File file : this.scenarioWatcher.files()) {
			try {
				Scenario scenario = game.scenario(file);
				int x = 1; 
				for(; x < this.scenarios.getSize() && this.scenarios.getElementAt(x).compareTo(scenario) < 0; x++);
				this.scenarios.insertElementAt(scenario, x);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.printf("Scenario file %s failed to load%n", file);
			}
		}

		jars = new LinkedList<DefaultComboBoxModel<Jar>>();
		for(int x = 0; x < game.teams(); x += 1) {
			this.jars.add(new DefaultComboBoxModel<Jar>());
		}

		this.jarWatcher = new FileMonitor("ais/");
		this.jarWatcher.fileListeners().add(this);

		for(MutableComboBoxModel<Jar> model : this.jars) {
			model.addElement(null);
		}
		
		File f = new File("CompetitorAI....");
		
		try {
			Jar jar = game.jar(f);
			for(MutableComboBoxModel<Jar> model : this.jars) {
				int x = 1; 
				for(; x < model.getSize() && model.getElementAt(x).compareTo(jar) < 0; x++);
				model.insertElementAt(jar, x);
			}
		
		} catch (Exception Ex) {
			Ex.printStackTrace();
		}

		for (File file : this.jarWatcher.files()) {
			try {
				Jar jar = game.jar(file);
				for(MutableComboBoxModel<Jar> model : this.jars) {
					int x = 1; 
					for(; x < model.getSize() && model.getElementAt(x).compareTo(jar) < 0; x++);
					model.insertElementAt(jar, x);
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
				System.out.printf("AI file %s failed to load%n", file);
			}
		}

		this.scenarioSelector = createScenarioSelector(scenarios);
		this.scenarioSelector.addActionListener(this);

		this.jarSelectors = new ArrayList<JComboBox<Jar>>();
		for (int x = 0; x < game.teams(); x += 1) {
			this.jarSelectors.add(createJarSelector(jars.get(x), game.color(x)));
		}

		//this.replay = new JButton("<html><font size=-1>Load Replay...</font></html>");
		this.resume = new JButton("<html><font size=-1>Resume</font></html>");
		this.resume.setEnabled(false);
		this.play = new JButton("<html><font size=+1>Battle Get!</font></html>");
		this.footer = new JLabel("<html><font size=-1>Version " + game.version() + "</font></html>");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//if (replay.equals(e.getSource())) {}
		if (resume.equals(e.getSource())) {
			bonzai.flip();
		}
		if (play.equals(e.getSource())) {
			Scenario scenario;
			{
				Object selected =  scenarioSelector.getSelectedItem();
				scenario = selected.equals(scenarioSelector.getItemAt(0)) ? null : (Scenario)selected;
			}

			List<Jar> jars = new ArrayList<Jar>();
			for(JComboBox<Jar> selector : jarSelectors) {
				jars.add((Jar)(selector.getSelectedItem()));
			}

			game.run(scenario, jars);
			bonzai.flip();

			resume.setEnabled(true);
		}
		if (scenarioSelector.equals(e.getSource())) {
			Scenario selected = scenarioSelector.getItemAt(scenarioSelector.getSelectedIndex());
			final int TEAMS = selected != null ? selected.getNumTeams() : 0;

			int x = 0;
			for(; x < TEAMS; x += 1)               { jarSelectors.get(x).setEnabled(true ); }
			for(; x < jarSelectors.size(); x += 1) { jarSelectors.get(x).setEnabled(false); }
		}
	}

	@Override
	public void fileCreated(FileEvent e) {
		if (scenarioWatcher.equals(e.getSource())) {
			System.out.printf("Found new scenario file %s. Loading...%n", e.getFile());
			try {
				Scenario scenario = game.scenario(e.getFile());

				int x = 1; 
				for(; x < this.scenarios.getSize() && this.scenarios.getElementAt(x).compareTo(scenario) < 0; x++);
				this.scenarios.insertElementAt(scenario, x);
			} 
			catch(Exception exception) {
				System.out.println("Failed to load new scenario file. Perhaps its malformed?");
			}
		}
		if (jarWatcher.equals(e.getSource())) {
			System.out.printf("Found new ai file %s. Loading...%n", e.getFile());
			try {
				Jar jar = game.jar(e.getFile());
				for(MutableComboBoxModel<Jar> model : this.jars) {
					int x = 1; 
					for(; x < model.getSize() && model.getElementAt(x).compareTo(jar) < 0; x++);
					model.insertElementAt(jar, x);
				}
			} catch(Exception exception){
				System.out.println("Failed to load new ai file. Perhaps its malformed?");
			}
		}
	}

	@Override
	public void fileDeleted(FileEvent e) {
		if (scenarioWatcher.equals(e.getSource())) {
			System.out.printf("The scenario file %s was deleted. Updating records...%n", e.getFile());

			File file = e.getFile();
			for(int x = 1; x < scenarios.getSize(); x += 1) {
				if(scenarios.getElementAt(x).getFile().equals(file)) { 
					if(scenarios.getIndexOf(scenarios.getSelectedItem()) == x) {
						scenarioSelector.setSelectedIndex(0);
					}
					scenarios.removeElementAt(x); 
				}
			}
		}

		if (jarWatcher.equals(e.getSource())) {
			System.out.printf("The ai file %s was deleted. Updating records...%n", e.getFile());

			File file = e.getFile();
			for(JComboBox<Jar> combo : this.jarSelectors) {
				DefaultComboBoxModel<Jar> model = (DefaultComboBoxModel<Jar>)combo.getModel();
				for(int x = 1; x < model.getSize(); x += 1) {
					if(model.getElementAt(x).file().equals(file)) { 
						if(model.getIndexOf(combo.getSelectedItem()) == x) {
							combo.setSelectedIndex(0);
						}
						model.removeElementAt(x); 
					}
				}
			}
		}
	}

	@Override
	public void fileModified(FileEvent e) {
		if (scenarioWatcher.equals(e.getSource())) {
			System.out.printf("The scenario file %s was modified. Reloading...%n", e.getFile());

			try {
				Scenario scenario = game.scenario(e.getFile());

				File file = e.getFile();
				for(int x = 1; x < scenarios.getSize(); x += 1) {
					if(scenarios.getElementAt(x).getFile().equals(file)) { 
						scenarios.insertElementAt(scenario, x);
						scenarios.removeElementAt(x + 1); 
					}
				}
			}
			catch(Exception exception) {
				System.out.println("Failed to load modified scenario file. Perhaps its malformed?");
				fileDeleted(e);
			}
		}

		if (jarWatcher.equals(e.getSource())) {
			System.out.printf("The jar file %s was modified. Reloading...%n", e.getFile());

			try {
				Jar jar = game.jar(e.getFile());

				File file = e.getFile();
				for(JComboBox<Jar> combo : this.jarSelectors) {
					DefaultComboBoxModel<Jar> model = (DefaultComboBoxModel<Jar>)combo.getModel();
					for(int x = 1; x < model.getSize(); x += 1) {
						if(model.getElementAt(x).file().equals(file)) { 
							model.insertElementAt(jar, x);
							model.removeElementAt(x + 1); 
						}
					}
				}
			}
			catch(Exception exception) {
				System.out.println("Failed to load modified jar file. Perhaps its malformed?");
				fileDeleted(e);
			}
		}
	}

	public static SelectorPanel create(BonzAIFrame bonzai, GameWrapper game) {
		final SelectorPanel panel = new SelectorPanel(bonzai, game);

		//panel.replay.addActionListener(panel);
		panel.resume.addActionListener(panel);
		panel.play.addActionListener(panel);

		panel.footer.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.footer.setVerticalAlignment(SwingConstants.CENTER);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 4;
		c.gridheight = game.teams();
		c.weightx = 1;
		panel.add(panel.scenarioSelector, c);

		c.gridx = 4;
		c.gridwidth = 4;
		c.gridheight = 1;
		for (int y = 0; y < game.teams(); y += 1) {
			c.gridy = y;
			panel.add(panel.jarSelectors.get(y), c);
		}

		/*
		c.gridx = 0;
		c.gridy = game.teams();
		c.gridwidth = 4;
		c.gridheight = 1;
		c.weightx = 0;
		panel.add(panel.replay, c);
		 */

		c.gridx = 4;
		c.gridy = game.teams();
		c.gridwidth = 1;
		c.gridheight = 1;
		panel.add(panel.resume, c);

		c.gridx = 5;
		c.gridy = game.teams();
		c.gridwidth = 3;
		c.gridheight = 1;
		panel.add(panel.play, c);

		c.gridx = 0;
		c.gridy = game.teams() + 1;
		c.gridwidth = 8;
		c.gridheight = 1;
		c.weighty = 1;
		c.weightx = 1;
		panel.add(new JPanel(), c);

		c.gridx = 0;
		c.gridy = game.teams() + 2;
		c.gridwidth = 8;
		c.gridheight = 1;
		c.weighty = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.EAST;
		panel.add(panel.footer, c);

		panel.scenarioWatcher.start();
		panel.jarWatcher.start();

		return panel;
	}

	private static JComboBox<Scenario> createScenarioSelector(MutableComboBoxModel<Scenario> scenarios) {
		JComboBox<Scenario> combo = new JComboBox<Scenario>(scenarios);
		combo.setRenderer(new ScenarioFileSelectRenderer());
		combo.setBackground(Color.LIGHT_GRAY);
		combo.setBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.GRAY));
		combo.setFocusable(false);
		combo.setPreferredSize(new Dimension(300, 300));
		combo.setMaximumRowCount(4);

		combo.setUI(new MetalComboBoxUI() {
			@Override
			public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
				Paint gradient = new LinearGradientPaint(
						0, 0, 0, bounds.height, 
						new float[] { 0.0f, 0.25f, 0.30f, 0.60f, 1.0f }, 
						new Color[] { Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.LIGHT_GRAY }
						);
				((Graphics2D) g).setPaint(gradient);
				((Graphics2D) g).fillRect(bounds.x, bounds.y, bounds.width + 20, bounds.height);
			}

			@Override
			protected JButton createArrowButton() {
				JButton arrow = super.createArrowButton();
				arrow.setBorderPainted(false);
				arrow.setContentAreaFilled(false);
				arrow.setOpaque(false);
				return arrow;
			}
		});

		return combo;
	}

	private static JComboBox<Jar> createJarSelector(MutableComboBoxModel<Jar> jars, final Color color) {
		JComboBox<Jar> combo = new JComboBox<Jar>(jars);
		combo.setRenderer(new JarFileSelectRenderer(color));
		combo.setBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.GRAY));
		combo.setFocusable(false);
		combo.setPreferredSize(new Dimension(300, 75));
		combo.setMaximumRowCount(10);
		combo.setEnabled(false);

		combo.setUI(new MetalComboBoxUI() {
			@Override
			public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
				Paint gradient = new LinearGradientPaint(0, 0, 0,
						bounds.height, 
						new float[] { 0.0f, 0.25f, 0.30f, 0.60f, 1.0f }, 
						new Color[] { color, color, color, color, color });
				((Graphics2D) g).setPaint(gradient);
				((Graphics2D) g).fillRect(bounds.x, bounds.y,
						bounds.width + 20, bounds.height);
			}

			@Override
			protected JButton createArrowButton() {
				JButton arrow = super.createArrowButton();
				arrow.setBorderPainted(false);
				arrow.setContentAreaFilled(false);
				arrow.setOpaque(false);
				return arrow;
			}
		});

		return combo;
	}
}