package org.squidy.nodes.optitrack.multicursor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.squidy.designer.util.ImageUtils;


public class TransparentCursor extends JComponent{
	

	public TransparentCursor(Dimension dimension, BufferedImage image)
	{
		setPreferredSize(dimension);
		setSize(dimension);
		setOpaque(false);
		BufferedImage imgScaled;
		JLabel label = null;
		try {
			imgScaled = ImageUtils.scale(image, (int)(image.getWidth() *0.9), (int)(image.getHeight() * 0.9));
			label = new JLabel(new ImageIcon(imgScaled));			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		setLayout(new BorderLayout());
		add(label, BorderLayout.CENTER);    		

	}
}

