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

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.ServerSocket;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.leapmotion.leap.Controller;

public class Main {
		
	private static final ImageIcon IMG_HELP = new ImageIcon(Main.class.getResource("/res/help.png"));
	private static final ImageIcon IMG_HELP_DOWN = new ImageIcon(Main.class.getResource("/res/help_down.png"));
	private static final ImageIcon IMG_HELP_HOVER = new ImageIcon(Main.class.getResource("/res/help_hover.png"));
	private static final ImageIcon IMG_ICON = new ImageIcon(Main.class.getResource("/res/icon.png"));
	private static final ImageIcon IMG_CHECK = new ImageIcon(Main.class.getResource("/res/check.png"));
	private static final ImageIcon IMG_CROSS = new ImageIcon(Main.class.getResource("/res/cross.png"));
	
	private static final Font LABEL_FONT = new Font("Verdana", Font.PLAIN, 18);
	private static final Font STATUS_FONT = new Font("Verdana", Font.BOLD, 18);
	
	private static final Color BACKGROUND_COLOR = new Color(70,70,70);
	private static final Color FOREGROUND_COLOR = new Color(240,240,240);
	private static final Color CONNECTED_COLOR = new Color(15,159,0);
	private static final Color DISCONNECTED_COLOR = new Color(250,50,50);
	private static final Color DIVIDER_COLOR = new Color(50,50,50);
	
	private static MenuItem leapMenuItem;
	private static MenuItem scratchMenuItem;
	
	private static Controller mLeapController;
	private static HelpWindow mHelpWindow;
	private static Image trayImageConnected;
	private static Image trayImageDisconnected;
	private static JFrame masterFrame;
	private static JLabel leapStatus;
	private static JLabel scratchStatus;
	private static JPanel leapLabel;
	private static JPanel scratchLabel;
	private static LeapListener mLeapListener;
	private static ScratchSocket mScratchSocket;
	private static TrayIcon trayIcon;
	
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
			
			System.out.println("Loaded Native Lib: " + file.getAbsolutePath());
			
			while ((read = lib.read(buff)) != -1) {
				fos.write(buff, 0, read);
			}
			fos.close();
			lib.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private static void setupNativeLibs() {
		// Method to load the appropriate native libraries
		
		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
		
		System.out.println("OS: " + os + "\nARCH: " + arch);
				
		if (os.contains("Linux")) {
			if (arch.contains("64"))
				arch = "x64";
			else
				arch = "x86";
			loadLib("libLeap.so", "Linux", arch);
			loadLib("libLeapJava.so", "Linux", arch);
		} else if (os.contains("Windows")) {
			if (arch.contains("64"))
				arch = "x64";
			else
				arch = "x86";
			loadLib("Leap.dll", "Windows", arch);
			loadLib("LeapJava.dll", "Windows", arch);
		} else if (os.contains("Mac") || os.contains("Darwin")) {
			loadLib("libLeap.dylib", "Mac", "x64");
			loadLib("libLeapJava.dylib", "Mac", "x64");
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
		
		masterFrame = new JFrame();
		masterFrame.setTitle("Scratch 2.0 plug-in for Leap Motion");
		masterFrame.setIconImage(IMG_ICON.getImage());
		masterFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		masterFrame.setLayout(new BorderLayout());
		masterFrame.setResizable(false);
		
		JPanel masterPanel = new JPanel();
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));
		masterFrame.add(masterPanel);
		
		JPanel borderPanel = new JPanel();
		borderPanel.setBorder(BorderFactory.createEmptyBorder(25,50,0,50));
		borderPanel.setLayout(new BorderLayout());
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		borderPanel.add(contentPanel, BorderLayout.CENTER);
		masterPanel.add(borderPanel, BorderLayout.CENTER);
		
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JLabel titleIcon = new JLabel(IMG_ICON);
		titlePanel.add(titleIcon);
		contentPanel.add(titlePanel);
		
		contentPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new GridBagLayout());
		statusPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		int rowCount = 0;
		
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.gridx = 0;
		gc.gridy = rowCount++;
		gc.gridwidth = 2;
		gc.ipady = 1;
		gc.insets = new Insets(10,0,10,0);
		gc.fill = GridBagConstraints.HORIZONTAL;
		JLabel divider = new JLabel("");
		divider.setBackground(DIVIDER_COLOR);
		divider.setOpaque(true);
		statusPanel.add(divider, gc);
		
		gc.insets = new Insets(0,40,0,0);
		gc.anchor = GridBagConstraints.EAST;
		gc.gridx = 0;
		gc.gridy = rowCount++;
		gc.gridheight = 2;
		gc.gridwidth = 1;
		leapStatus = new JLabel(IMG_CROSS);
		statusPanel.add(leapStatus, gc);
		
		gc.gridx = 1;
		gc.gridheight = 1;
		gc.insets = new Insets(0,30,0,40);
		gc.anchor = GridBagConstraints.WEST;
		ShadowLabel label = new ShadowLabel(
				"Leap Motion Controller", 1, 1, DIVIDER_COLOR);
		label.setFont(STATUS_FONT);
		statusPanel.add(label, gc);
		
		gc.gridy = rowCount++;
		leapLabel = new JPanel();
		leapLabel.setLayout(new BorderLayout());
		leapLabel.setBorder(new EmptyBorder(0,0,0,0));
		label = new ShadowLabel(
				"Not connected", 1, 1, DIVIDER_COLOR);
		label.setFont(STATUS_FONT);
		label.setForeground(DISCONNECTED_COLOR);
		leapLabel.add(label);
		statusPanel.add(leapLabel, gc);
		
		gc.gridx = 0;
		gc.gridy = rowCount++;
		gc.gridwidth = 2;
		gc.ipady = 1;
		gc.insets = new Insets(15,0,10,0);
		gc.fill = GridBagConstraints.HORIZONTAL;
		divider = new JLabel("");
		divider.setBackground(DIVIDER_COLOR);
		divider.setOpaque(true);
		statusPanel.add(divider, gc);
		
		gc.gridy = rowCount++;
		gc.ipady = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.gridwidth = 1;
		gc.insets = new Insets(0,0,0,0);
		gc.anchor = GridBagConstraints.EAST;
		gc.gridheight = 2;
		scratchStatus = new JLabel(IMG_CROSS);
		statusPanel.add(scratchStatus, gc);
		
		gc.gridx = 1;
		gc.gridheight = 1;
		gc.insets = new Insets(0,30,0,0);
		gc.anchor = GridBagConstraints.WEST;
		label = new ShadowLabel(
				"Scratch 2.0", 1, 1, DIVIDER_COLOR);
		label.setFont(STATUS_FONT);
		statusPanel.add(label, gc);
		
		gc.gridy = rowCount++;
		scratchLabel = new JPanel();
		scratchLabel.setLayout(new BorderLayout());
		scratchLabel.setBorder(new EmptyBorder(0,0,0,0));
		label = new ShadowLabel(
				"Not connected", 1, 1, DIVIDER_COLOR);
		label.setFont(STATUS_FONT);
		label.setForeground(DISCONNECTED_COLOR);
		scratchLabel.add(label);
		statusPanel.add(scratchLabel, gc);
		
		gc.gridx = 0;
		gc.gridy = rowCount++;
		gc.gridwidth = 2;
		gc.ipady = 1;
		gc.insets = new Insets(15,0,0,0);
		gc.fill = GridBagConstraints.HORIZONTAL;
		divider = new JLabel("");
		divider.setBackground(DIVIDER_COLOR);
		divider.setOpaque(true);
		statusPanel.add(divider, gc);
		
		contentPanel.add(statusPanel);
		
		contentPanel.add(Box.createRigidArea(new Dimension(0,20)));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		gc = new GridBagConstraints();
		
		gc.gridx = 0;
		gc.gridy = 0;
		JButton quitButton = new JButton();
		quitButton.setText("Quit");
		quitButton.setFocusable(false);
		quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				masterFrame.dispose();
			}
		});
		buttonPanel.add(quitButton, gc);
		
		quitButton.setFocusable(false);
		quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				masterFrame.dispose();
			}
		});
		
		gc.gridx = 1;
		gc.ipadx = 0;
		gc.insets = new Insets(0,15,0,0);
		final JLabel helpButton = new JLabel(IMG_HELP);
		helpButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				mHelpWindow.open();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				helpButton.setIcon(IMG_HELP_HOVER);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				helpButton.setIcon(IMG_HELP);
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				helpButton.setIcon(IMG_HELP_DOWN);
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				helpButton.setIcon(IMG_HELP);
			}	
		});
		buttonPanel.add(helpButton, gc);
		
		contentPanel.add(buttonPanel);
				
		contentPanel.add(Box.createRigidArea(new Dimension(0,20)));
		
		mHelpWindow = new HelpWindow();
		
		masterFrame.pack();
		masterFrame.setLocationRelativeTo(null);
		masterFrame.setVisible(true);
		
		masterFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (mScratchSocket != null)
					mScratchSocket.close();
				if (mLeapController != null)
					mLeapController.removeListener(mLeapListener);
				System.exit(0);
			}
		});
	}
	
	private static void systemTray() {
		if (!System.getProperty("os.name").contains("Linux") && SystemTray.isSupported()) {
			
			final SystemTray tray = SystemTray.getSystemTray();
			
			try {
				
				BufferedImage trayIconImage = ImageIO.read(Main.class.getResource("/res/tray_icon_connected.png"));
				int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
				trayImageConnected = trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH);
				
				trayIconImage = ImageIO.read(Main.class.getResource("/res/tray_icon.png"));
				trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
				trayImageDisconnected = trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH);
				
				trayIcon = new TrayIcon(trayImageDisconnected);
								
				PopupMenu popup = new PopupMenu();
								
				MenuItem leapMenuLabel = new MenuItem ("Leap Motion Controller");
				leapMenuLabel.setEnabled(false);
				popup.add(leapMenuLabel);
				
				leapMenuItem = new MenuItem("\u2717  Not connected");
				leapMenuItem.setEnabled(false);
				popup.add(leapMenuItem);
				
				popup.addSeparator();
				
				MenuItem scratchMenuLabel = new MenuItem ("Scratch 2.0");
				scratchMenuLabel.setEnabled(false);
				popup.add(scratchMenuLabel);
							
				scratchMenuItem = new MenuItem("\u2717  Not connected");
				scratchMenuItem.setEnabled(false);
				popup.add(scratchMenuItem);
								
				popup.addSeparator();

				MenuItem helpMenuItem = new MenuItem("Help");
				helpMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						masterFrame.setAlwaysOnTop(true);
						masterFrame.setAlwaysOnTop(false);
						mHelpWindow.open();	
					}
				});
				popup.add(helpMenuItem);
				
				popup.addSeparator();
				
				MenuItem exitMenuItem = new MenuItem("Exit");
				exitMenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						masterFrame.dispose();
					}
				});
				popup.add(exitMenuItem);
				
				//Workaround for left button mouse clicking on Windows
				if (System.getProperty("os.name").contains("Win")) {
					trayIcon.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							if (e.getButton() == MouseEvent.BUTTON1) {
								try {
									Robot robot = new Robot();
									robot.mouseMove(e.getXOnScreen(), e.getYOnScreen());
									robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
									robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
								} catch (AWTException e1) {
									e1.printStackTrace();
								}
							}
						}
					});
				}
				
				trayIcon.setPopupMenu(popup);
				
				tray.add(trayIcon);
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
			
		}
	}
	
	public static void refresh() {
		SwingUtilities.invokeLater(new Runnable() {
            public void run () {
            	if (mLeapListener.isConnected) {
        			if (mScratchSocket.isConnected) {
        				setLabelConnected(leapLabel);
        				leapStatus.setIcon(IMG_CHECK);
        				setLabelConnected(scratchLabel);
        				scratchStatus.setIcon(IMG_CHECK);
        				if (!System.getProperty("os.name").contains("Linux")) {
        					leapMenuItem.setLabel("\u2713  Connected");
        					scratchMenuItem.setLabel("\u2713  Connected");
        					trayIcon.setImage(trayImageConnected);
        				}
        			} else {
        				setLabelConnected(leapLabel);
        				leapStatus.setIcon(IMG_CHECK);
        				setLabelDisconnected(scratchLabel);
        				scratchStatus.setIcon(IMG_CROSS);
        				if (!System.getProperty("os.name").contains("Linux")) {
        					leapMenuItem.setLabel("\u2713  Connected");
        					scratchMenuItem.setLabel("\u2717  Not connected");
        					trayIcon.setImage(trayImageDisconnected);
        				}
        			}
        		} else {
        			if (mScratchSocket.isConnected) {
        				setLabelDisconnected(leapLabel);
        				leapStatus.setIcon(IMG_CROSS);
        				setLabelConnected(scratchLabel);
        				scratchStatus.setIcon(IMG_CHECK);
        				
        				if (!System.getProperty("os.name").contains("Linux")) {
        					leapMenuItem.setLabel("\u2717  Not connected");
        					scratchMenuItem.setLabel("\u2713  Connected");
        					trayIcon.setImage(trayImageDisconnected);
        				}
        			} else {
        				setLabelDisconnected(leapLabel);
        				leapStatus.setIcon(IMG_CROSS);
        				setLabelDisconnected(scratchLabel);
        				scratchStatus.setIcon(IMG_CROSS);
        				if (!System.getProperty("os.name").contains("Linux")) {
        					leapMenuItem.setLabel("\u2717  Not connected");
        					scratchMenuItem.setLabel("\u2717  Not connected");
        					trayIcon.setImage(trayImageDisconnected);
        				}
        			}
        		}
            }
        });
	}
	
	private static void setLabelConnected(JPanel panel) {
		ShadowLabel label = new ShadowLabel(
				"Connected", 1, 1, DIVIDER_COLOR);
		label.setForeground(CONNECTED_COLOR);
		label.setFont(STATUS_FONT);
		panel.removeAll();
		panel.add(label);
		panel.revalidate();
	}
	
	private static void setLabelDisconnected(JPanel panel) {
		ShadowLabel label = new ShadowLabel(
				"Not connected", 1, 1, DIVIDER_COLOR);
		label.setForeground(DISCONNECTED_COLOR);
		label.setFont(STATUS_FONT);
		panel.removeAll();
		panel.add(label);
		panel.revalidate();
	}
	
	public static void socketError() {
		UIManager.put("Panel.background", UIManager.getLookAndFeelDefaults().get("Panel.background"));
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		
		JOptionPane.showMessageDialog(masterFrame,
				"Port 50007 is in use",
				"Socket Error",
				JOptionPane.ERROR_MESSAGE);
		masterFrame.dispose();
	}
	
	public static void main(String[] args) {

		UIManager.put("Panel.background", BACKGROUND_COLOR);
		UIManager.put("Label.font", LABEL_FONT);
		UIManager.put("Label.foreground", FOREGROUND_COLOR);
		UIManager.put("Label.background", BACKGROUND_COLOR);
		
		systemTray();
		drawScreen();
		
		// Check to make sure the port isn't already being used
		try {
			ServerSocket tester = new ServerSocket(50007);
			tester.close();
		} catch (IOException e) {
			socketError();
			return;
		}

		setupNativeLibs();
		
		//Start Leap Motion Listener
		mLeapListener = new LeapListener();
		mLeapController = new Controller();
		mLeapController.addListener(mLeapListener);
		mLeapController.setPolicyFlags(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
		
		// Start Scratch socket
		mScratchSocket = new ScratchSocket(50007);
		mScratchSocket.setLeap(mLeapListener);

	}
		
}
