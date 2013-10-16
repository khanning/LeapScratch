/*****************************************************************************
 *   This file is part of LeapScratch.                                       *
 *                                                                           *
 *   LeapScratch is free software: you can redistribute it and/or modify     *
 *   it under the terms of the GNU General Public License as published by    *
 *   the Free Software Foundation, either version 3 of the License, or       *
 *   (at your option) any later version.                                     *
 *                                                                           *
 *   LeapScratch is distributed in the hope that it will be useful,          *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 *   GNU General Public License for more details.                            *
 *                                                                           *
 *   You should have received a copy of the GNU General Public License       *
 *   along with LeapScratch.  If not, see <http://www.gnu.org/licenses/>.    *
 *                                                                           *
 *****************************************************************************/

package com.khanning.leapscratch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class BubbleTip {
	
	private static final String OS = System.getProperty("os.name");
	
	private static LinkedList<String> bubbleList = new LinkedList<String>();
	
	private static final ScheduledExecutorService worker = 
			  Executors.newSingleThreadScheduledExecutor();
		
	private static final Font LABEL_FONT = new Font("Verdana", Font.BOLD, 14);

	private static final Color BACKGROUND_COLOR = new Color(70,70,70);
	private static final Color FOREGROUND_COLOR = new Color(240,240,240);
	private static final ImageIcon IMG_ICON_SMALL = new ImageIcon(
			Main.class.getResource("/res/icon_xsmall.png"));
	
	private static boolean isOpen;
	
	public static void create(String message) {
		bubbleList.add(message);
		if (!isOpen)
			show();
	}
	
	public static void show() {
				
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
						
		final JFrame frame = new JFrame();
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setFocusable(false);
		
		final JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5,20,5,20));
		panel.setBackground(BACKGROUND_COLOR);
		
		panel.add(new JLabel(IMG_ICON_SMALL));
				
		JLabel label = new JLabel(bubbleList.pop());
		label.setBorder(new EmptyBorder(0,10,0,0));
		label.setForeground(FOREGROUND_COLOR);
		label.setFont(LABEL_FONT);
		panel.add(label);
		
		frame.add(panel);
		frame.pack();
				
		int xLoc = (int) screenSize.getWidth() - frame.getSize().width - 20;
		int yLoc = 40;
		if (OS.contains("Windows"))
			yLoc -= 20;
		
		frame.setLocation(xLoc, yLoc);
		
		isOpen = true;
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);

		worker.schedule(new Runnable() {
			public void run() {
				frame.dispose();
				if (bubbleList.isEmpty())
					isOpen = false;
				else
					show();
			}
		}, 4, TimeUnit.SECONDS);
		
	}
}