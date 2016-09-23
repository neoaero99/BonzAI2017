package bonzai.automator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Visualizer extends JPanel {
	ArrayList<ArrayList<bonzai.Jar>> ais;

	public Visualizer(ArrayList<ArrayList<bonzai.Jar>> ais) {
		this.ais = ais;
	}


	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;

		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());

		int width = getHeight()/40;
		int halfWidth = width/2;

		//Start at the bottom, draw up
		for (int i = 0; i <= 3; i++) {
			int y = ((3 - i + 1) * getWidth() / 6);


			int xChange = 1;
			int xStart = 1;

			//I dont have time for math
			if (i == 0) {
				xChange = 27;	//Top row
				xStart = 13;
			} else if (i == 1) {
				xChange = 9;
				xStart = 4;
			} else if (i == 2) {
				xChange = 3;
				xStart = 1;
			} else if (i == 3) {
				xChange = 1;
				xStart = 0;
			} else {
				System.out.println("Uhh, fail");
			}

			g.setColor(Color.yellow);

			for (int j = xStart; j < 27; j+=xChange) {
				int x = ((j+1) * getHeight() / 29);

				//Draw names
				try {
					g.setColor(Color.red);
					//System.out.println("Check if [" + (3-i) + "][" + j + "] = [" + (3-i+1) + "][" + j/3 + "]");
					if (ais.get(3-i).get(j/xChange).file().getPath().equals(ais.get(3-i+1).get(j/(3*xChange)).file().getPath())) {
						g.setColor(Color.green);
					}
				} catch (Exception e) {}


				try {
					String name = ais.get(3-i).get(j).name();
					name = name.substring(0,Math.min(15, name.length()));
					//Pad until it becomes 15
					while (name.length() < 15) { name = " " + name; }
					g.drawString(name, 10, x+width);


					g.drawOval(y, x, getHeight()/40, getHeight()/40);

					//EXTREME HARD CODING FOR THE WIN!

					//Straight up
					if ((i == 3 && (j-1) % 3 == 0)
							||(i == 2 && (j-4) % 9 == 0)
							||(i == 1 && (j-13) % 27 == 0)) {
						int otherY = ((3 - i + 2) * getWidth() / 6);
						g.drawLine(y+width, x+halfWidth, otherY, x+halfWidth);
					}

					//Left child
					if ((i == 3 && (j) % 3 == 0)
							||(i == 2 && (j-1) % 9 == 0)
							||(i == 1 && (j-4) % 27 == 0)) {
						int otherY = ((3 - i + 2) * getWidth() / 6);
						int otherX = ((j+1+xChange) * getHeight() / 29);
						g.drawLine(y+width, x+halfWidth, otherY, otherX+halfWidth);
					}

					//Right child
					if ((i == 3 && (j+1) % 3 == 0)
							||(i == 2 && (j-7) % 9 == 0)
							||(i == 1 && (j-22) % 27 == 0)) {
						int otherY = ((3 - i + 2) * getWidth() / 6);
						int otherX = ((j+1-xChange) * getHeight() / 29);
						g.drawLine(y+width, x+halfWidth, otherY, otherX+halfWidth);
					}


				} catch (Exception e) { }

			}
		}
	}
}
