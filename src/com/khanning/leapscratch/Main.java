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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.leapmotion.leap.Controller;

public class Main {
	
	private static final String VERSION = "1.0.0";
	
	private static LeapListener mLeapListener;
	private static Controller mLeapController;
	private static ScratchSocket mScratchSocket;
	
	private static JLabel leapLabel;
	private static JLabel scratchLabel;
	
	private static final ImageIcon APP_ICON = new ImageIcon(Main.class.getResource("/res/icon.png"));
	private static final ImageIcon RED_LED= new ImageIcon(Main.class.getResource("/res/led_red.png"));
	private static final ImageIcon GREED_LED = new ImageIcon(Main.class.getResource("/res/led_green.png"));
	
	private static final Font TITLE_FONT = new Font("Verdana", Font.BOLD, 20);
	private static final Font LABEL_FONT = new Font("Verdana", Font.PLAIN, 16);
	private static final Font SUB_FONT = new Font("Verdana", Font.BOLD, 14);
	
	private static final Color BACKGROUND_COLOR = new Color(70,70,70);
	private static final Color FOREGROUND_COLOR = new Color(240,240,240);
	private static final Color SUB_FOREGROUND_COLOR = new Color(175,175,175);
	
	public static void main(String[] args) {
		
		drawScreen();
		
		setupLibs();
		
		// Start Scratch socket
		mScratchSocket = new ScratchSocket(50007);
		Thread scratchThread = new Thread(mScratchSocket);
		scratchThread.start();
		
		//Start Leap Motion Listener
		mLeapListener = new LeapListener();
		mLeapListener.setSocket(mScratchSocket);
		mLeapController = new Controller();
		mLeapController.addListener(mLeapListener);
		mLeapController.setPolicyFlags(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
		
	}
	
	private static void loadLib(String name, String os, String arch) {
		// Method to load libraries from resources into tmp space 
		
		// Directory structure for native library resources
		String resource = "/lib/" + os + "/" + arch + "/" + name;
		
		InputStream lib = Main.class.getResourceAsStream(resource);
		byte[] buff = new byte[1024];
		int read = -1;
		try {
			File file = new File(new File(System.getProperty("java.io.tmpdir")), name);
			FileOutputStream fos = new FileOutputStream(file);
			
			while ((read = lib.read(buff)) != -1) {
				fos.write(buff, 0, read);
			}
			fos.close();
			lib.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private static void setupLibs() {
		// Method to load the appropriate native libraries
		
		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
				
		if (os.contains("Linux")) {
			loadLib("libLeap.so", "Linux", arch);
			loadLib("libLeapJava.so", "Linux", arch);
		} else if (os.contains("Windows")) {
			loadLib("Leap.dll", "Windows", arch);
			loadLib("LeapJava.dll", "Windows", arch);
		} else if (os.contains("Mac")) {
			loadLib("libLeap.dylib", "Mac", "x86_64");
			loadLib("libLeapJava.dylib", "Mac", "x86_64");
		}
		
		System.setProperty("java.library.path", System.getProperty("java.io.tmpdir"));
		
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
		} catch (NoSuchFieldException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private static void drawScreen() {
		
		final JFrame frame = new JFrame();
		frame.setTitle("LeapScratch");
		frame.setIconImage(APP_ICON.getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		
		JPanel masterPanel = new JPanel();
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));
		frame.add(masterPanel);
		
		JPanel borderPanel = new JPanel();
		borderPanel.setBorder(BorderFactory.createEmptyBorder(20,40,0,40));
		borderPanel.setBackground(BACKGROUND_COLOR);
		borderPanel.setLayout(new BorderLayout());
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(BACKGROUND_COLOR);
		borderPanel.add(contentPanel, BorderLayout.CENTER);
		masterPanel.add(borderPanel, BorderLayout.CENTER);
		
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		titlePanel.setBackground(BACKGROUND_COLOR);
		titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JLabel titleLabel = new JLabel("Leap Motion to Scratch 2.0");
		titleLabel.setForeground(FOREGROUND_COLOR);
		titleLabel.setFont(TITLE_FONT);
		titlePanel.add(titleLabel);
		contentPanel.add(titlePanel);
		
		contentPanel.add(Box.createRigidArea(new Dimension(0,20)));
		
		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridBagLayout());
		gridPanel.setBackground(BACKGROUND_COLOR);
		GridBagConstraints gc = new GridBagConstraints();
		gridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPanel.add(gridPanel);
		
		gc.fill = GridBagConstraints.HORIZONTAL;
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(1,0,0,10);
		leapLabel = new JLabel(RED_LED);
		gridPanel.add(leapLabel, gc);
		
		gc.gridx = 1;
		gc.insets = new Insets(0,0,0,0);
		JLabel leapLabel = new JLabel("Leap Motion");
		leapLabel.setForeground(FOREGROUND_COLOR);
		leapLabel.setFont(LABEL_FONT);
		gridPanel.add(leapLabel, gc);
		
		gc.gridx = 0;
		gc.gridy = 1;
		gc.insets = new Insets(6,0,0,10);
		scratchLabel = new JLabel(RED_LED);
		gridPanel.add(scratchLabel, gc);
		
		gc.gridx = 1;
		gc.insets = new Insets(5,0,0,0);
		JLabel scratchLabel = new JLabel("Scratch 2.0");
		scratchLabel.setForeground(FOREGROUND_COLOR);
		scratchLabel.setFont(LABEL_FONT);
		gridPanel.add(scratchLabel, gc);
		
		contentPanel.add(Box.createRigidArea(new Dimension(0,20)));
		
		JButton quitButton = new JButton();
		quitButton.setText("Quit");
		quitButton.setFocusable(false);
		quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		contentPanel.add(quitButton);
		
		contentPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		JPanel footerPanel = new JPanel();
		footerPanel.setLayout(new GridBagLayout());
		footerPanel.setBackground(BACKGROUND_COLOR);
		gc = new GridBagConstraints();
		footerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(10,10,10,0);
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.anchor = GridBagConstraints.LINE_START;
		JLabel createdBy = new JLabel("Created by Kreg Hanning");
		createdBy.setFont(SUB_FONT);
		createdBy.setForeground(SUB_FOREGROUND_COLOR);
		footerPanel.add(createdBy, gc);
		
		gc.gridx = 1;
		gc.insets = new Insets(10,0,10,10);
		gc.anchor = GridBagConstraints.LINE_END;
		JLabel versionLabel = new JLabel("v" + VERSION);
		versionLabel.setFont(SUB_FONT);
		versionLabel.setForeground(SUB_FOREGROUND_COLOR);
		footerPanel.add(versionLabel, gc);
		
		masterPanel.add(footerPanel);
		
		frame.pack();
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				mScratchSocket.close();
				mLeapController.removeListener(mLeapListener);
				System.exit(0);
			}
		});
	}
	
	public static void refresh() {
		if (mLeapListener.isConnected)
			leapLabel.setIcon(GREED_LED);
		else
			leapLabel.setIcon(RED_LED);
		
		if (mScratchSocket.isConnected)
			scratchLabel.setIcon(GREED_LED);
		else
			scratchLabel.setIcon(RED_LED);
	}
		
}
