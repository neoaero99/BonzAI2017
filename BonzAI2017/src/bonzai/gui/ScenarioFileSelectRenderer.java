package bonzai.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.WeakHashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import bonzai.Scenario;

public class ScenarioFileSelectRenderer implements ListCellRenderer<Scenario> {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private static final String MESSAGE = "<html><center><font size=+1><b>%s</b></font><p><font size=-1>%s   %s</font></center></html>";
	
	private final WeakHashMap<Scenario, JLabel> labels = new WeakHashMap<Scenario, JLabel>();
	
	
	/**
	 *
	 **/
	public Component getListCellRendererComponent(JList<? extends Scenario> list, Scenario value, int index, boolean isSelected, boolean cellHasFocus) {
		if(!labels.containsKey(value)) {
			JLabel label = new JLabel();
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setVerticalTextPosition(SwingConstants.TOP);
			label.setHorizontalTextPosition(SwingConstants.CENTER);
			label.setOpaque(true);
			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			String    name = value == null ? "" : value.getName();
			String    team = value == null ? "" : String.format("%d Players", value.getNumTeams());	
			String    date = value == null ? "" : DATE_FORMAT.format(value.getFile().lastModified());
			
			ImageIcon icon = null;
			if(value != null) {
				Image image = value.getImage();
			
				double factor = 0.4;
				BufferedImage scale = new BufferedImage(
					(int)(image.getWidth(null)  * factor), 
					(int)(image.getHeight(null) * factor), 
					BufferedImage.TYPE_INT_ARGB
				);
			
				scale.getGraphics().drawImage(
					value.getImage(),
					0, 0, scale.getWidth(null), scale.getHeight(null),
					0, 0, image.getWidth(null), image.getHeight(null),
					null
				);
			
				icon = new ImageIcon(scale);
			}

			label.setText(String.format(MESSAGE, name, team, date));
			label.setIcon(icon);
			labels.put(value, label);
		}

		JLabel label = labels.get(value);
		label.setBackground(isSelected ? new Color(175, 175, 175) : Color.LIGHT_GRAY);
		return label;
	}
}