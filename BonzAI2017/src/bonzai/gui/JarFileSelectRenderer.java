package bonzai.gui;

import java.awt.Color;
import java.awt.Component;

import java.text.SimpleDateFormat;
import java.util.WeakHashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import bonzai.Jar;

public class JarFileSelectRenderer implements ListCellRenderer<Jar> {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private static final String MESSAGE = "<html><font size=+1><b>%s</b></font><br><font size=-1>%s   %s</font></html>";

	WeakHashMap<Jar, JLabel> labels = new WeakHashMap<Jar, JLabel>();
	private final Color color;

	/**
	 *
	 **/
	public JarFileSelectRenderer(Color color) {
		this.color = color;
	}

	/**
	 *
	 **/
	public Component getListCellRendererComponent(JList<? extends Jar> list, Jar value, int index, boolean isSelected, boolean cellHasFocus) {
		if(!labels.containsKey(value)) {
			JLabel label = new JLabel();	
			label.setHorizontalAlignment(SwingConstants.LEFT);
			label.setVerticalAlignment(SwingConstants.CENTER);
			label.setBackground(color);
			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			String name = value == null ? "Click to Choose!" : value.name();
			String date = value == null ? "" : DATE_FORMAT.format(value.file().lastModified());
			String file = value == null ? "" : value.file().getName();
			
			label.setText(String.format(MESSAGE, name, date, file));
			labels.put(value, label);
		}
		
		JLabel label = labels.get(value);
		label.setOpaque(isSelected);
		label.setBackground(isSelected ? color : color);
		return label;
	}
}