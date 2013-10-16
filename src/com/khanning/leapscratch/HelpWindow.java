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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class HelpWindow {
	
	private static final String VERSION = "1.0.0";

	private static JFrame frame;
	
	private static final ImageIcon IMG_FILE_MENU = new ImageIcon(
			Main.class.getResource("/res/file_menu.png"));
	private static final ImageIcon IMG_ICON = new ImageIcon(
			Main.class.getResource("/res/icon.png"));
	private static final ImageIcon IMG_ICON_SMALL = new ImageIcon(
			Main.class.getResource("/res/icon_small.png"));
	private static final ImageIcon IMG_IMPORT = new ImageIcon(
			Main.class.getResource("/res/import.png"));
	private static final ImageIcon IMG_HELP_LARGE = new ImageIcon(
			Main.class.getResource("/res/help_large.png"));
	private static final ImageIcon IMG_LEAP_CONNECTED = new ImageIcon(
			Main.class.getResource("/res/leap_connected.png"));
	private static final ImageIcon IMG_MORE_BLOCKS = new ImageIcon(
			Main.class.getResource("/res/more_blocks.png"));

	private static final Font BODY_LINK_FONT = new Font("Verdana", Font.BOLD, 16);
	private static final Font FOOTER_FONT = new Font("Verdana", Font.BOLD, 14);
	private static final Font TITLE_FONT = new Font("Verdana", Font.PLAIN, 22);

	private static final Color BACKGROUND_COLOR = new Color(70,70,70);
	private static final Color LINK_COLOR = new Color(128,191,255);
	private static final Color LINK_HOVER_COLOR = new Color(173,214,255);
	private static final Color SUB_FOREGROUND_COLOR = new Color(175,175,175);
	private static final Color TITLE_COLOR = new Color(255,140,5);
	
	public boolean isOpen;
	
	public void open() {
		
		if (isOpen) {
			frame.setAlwaysOnTop(true);
			frame.setAlwaysOnTop(false);
			return;
		}
				
		frame = new JFrame();
		isOpen = false;
		
		frame.setTitle("Help");
		frame.setIconImage(IMG_ICON.getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setBounds(0, 0, 450, 500);
		frame.setResizable(false);
		
		JPanel boxPanel = new JPanel();
		boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		contentPanel.setBorder(new EmptyBorder(20,20,20,20));
		GridBagConstraints gc = new GridBagConstraints();
		contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		int row = 0;
		
		gc.gridx = 0;
		gc.gridy = row++;
		gc.gridwidth = 2;
		gc.insets = new Insets(5,0,5,0);
		contentPanel.add(new JLabel(IMG_HELP_LARGE),gc);
		
		gc.anchor = GridBagConstraints.NORTHWEST;
		
		JLabel titleLabel = new JLabel("Getting Started");
		titleLabel.setFont(TITLE_FONT);
		titleLabel.setForeground(TITLE_COLOR);
		gc.gridy = row++;
		gc.insets = new Insets(0,0,5,0);
		contentPanel.add(titleLabel, gc);
		
		JLabel scratchOffline = new JLabel(
				"<html><div width=450><i>Currently Scratch 2.0 "
				+ "Experimental Extensions are only supported with the "
				+ "offline version of Scratch 2.0.</i></div></html>");
		gc.gridy = row++;
		gc.insets = new Insets(10,0,0,0);
		contentPanel.add(scratchOffline, gc);
		
		gc.gridy = row++;
		gc.insets = new Insets(0,0,10,0);
		String sratch20Download = "http://scratch.mit.edu/scratch2download/";
		JButton scratchButton = createLink("Download the Scratch 2.0 Offline Editor", sratch20Download);
		contentPanel.add(scratchButton, gc);
		
		gc.gridy = row++;
		gc.gridwidth = 1;
		gc.ipadx = 5;
		gc.ipady = 0;
		gc.insets = new Insets(15,0,5,0);
		contentPanel.add(new JLabel("1. "), gc);
		
		gc.gridx = 1;
		contentPanel.add(new JLabel(
				"<html><div width=400>Make sure the Leap Motion software is running "
				+ "and the Leap Motion Controller is connected to the computers USB port</div>"
				+ "</html>"), gc);
		
		gc.gridy = row++;
		gc.insets = new Insets(0,0,15,0);
		contentPanel.add(new JLabel(IMG_LEAP_CONNECTED), gc);
		
		gc.gridx = 0;
		gc.gridy = row++;
		gc.insets = new Insets(15,0,5,0);
		contentPanel.add(new JLabel("2. "), gc);
		
		gc.gridx = 1;
		contentPanel.add(new JLabel(
				"<html><div width=400>Launch \"Scratch 2.0 plug-in for Leap Motion\""
				+ "</div></html>"), gc);
		
		gc.gridy = row++;
		gc.insets = new Insets(0,0,15,0);
		contentPanel.add(new JLabel(IMG_ICON_SMALL), gc);
		
		gc.gridx = 0;
		gc.gridy = row++;
		gc.insets = new Insets(15,0,0,0);
		contentPanel.add(new JLabel("3. "), gc);
		
		gc.gridx = 1;
		contentPanel.add(new JLabel("<html><div width=400>Open the Scratch 2.0 "
				+ "offline Editor and try out some of my sample projects</div></html>"), gc);
				
		gc.gridy = row++;
		gc.insets = new Insets(0,0,0,0);
		String sample = "http://khanning.com/leapscratch/Scratch20PlugInForLeapMotion_SampleProjects.zip";
		JButton sampleButton = createLink("Download the Sample Projects", sample);
		contentPanel.add(sampleButton, gc);
						
		gc.gridx = 0;
		gc.gridy = row++;
		gc.gridwidth = 2;
		gc.insets = new Insets(20,0,5,0);
		titleLabel = new JLabel("Using with your projects");
		titleLabel.setFont(TITLE_FONT);
		titleLabel.setForeground(TITLE_COLOR);
		contentPanel.add(titleLabel, gc);
		
		gc.gridx = 0;
		gc.gridy = row++;
		gc.gridwidth = 1;
		gc.ipadx = 0;
		gc.insets = new Insets(15,0,0,0);
		contentPanel.add(new JLabel("1. "), gc);
		
		gc.gridx = 1;
		contentPanel.add(new JLabel("Save this file to your computer:"), gc);
		
		JButton json = jsonLink("LeapMotion.json");
		gc.gridy = row++;
		gc.insets = new Insets(0,0,15,0);
		contentPanel.add(json, gc);
		
		gc.gridx = 0;
		gc.gridy = row++;
		gc.ipadx = 5;
		gc.insets = new Insets(15,0,15,0);
		contentPanel.add(new JLabel("2. "), gc);
		
		gc.gridx = 1;
		contentPanel.add(new JLabel(
				"<html><div width=400>Open your project in the Scratch "
				+ "2.0 Offline Editor and switch to the editor mode"
				+ "</div></html>"), gc);
		
		gc.gridx = 0;
		gc.gridy = row++;
		gc.ipady = 0;
		gc.insets = new Insets(15,0,5,0);
		contentPanel.add(new JLabel("3. "), gc);
		
		gc.gridx = 1;
		contentPanel.add(new JLabel(
				"<html><div width=400>Hold the shift key and click on the file menu"
				+ "</div></html>"), gc);
		
		gc.gridy = row++;
		gc.insets = new Insets(0,0,15,0);
		contentPanel.add(new JLabel(IMG_FILE_MENU), gc);
		
		gc.gridx = 0;
		gc.gridy = row++;
		gc.insets = new Insets(15,0,5,0);
		contentPanel.add(new JLabel("4. "), gc);
		
		gc.gridx = 1;
		contentPanel.add(new JLabel(
				"<html><div width=400>Click \"Import Experimental Extension\"</div>"
				+ "</html>"), gc);
		
		gc.gridy = row++;
		gc.insets = new Insets(5,5,15,0);
		contentPanel.add(new JLabel(IMG_IMPORT), gc);
		
		gc.gridx = 0;
		gc.gridy = row++;
		gc.insets = new Insets(15,0,15,0);
		contentPanel.add(new JLabel("5. "), gc);
		
		gc.gridx = 1;
		contentPanel.add(new JLabel(
				"<html><div width=400>Select the LeapMotion.json file you downloaded "
				+ "in step 1</div></html>"), gc);
		
		gc.gridx = 0;
		gc.gridy = row++;
		gc.insets = new Insets(15,0,5,0);
		contentPanel.add(new JLabel("6. "), gc);
		
		gc.gridx = 1;
		contentPanel.add(new JLabel(
				"<html><div width=400>Now you can use the Leap Motion blocks under "
				+ "the \"More Blocks\" category"), gc);
		
		gc.gridy = row++;
		gc.insets = new Insets(5,5,15,0);
		contentPanel.add(new JLabel(IMG_MORE_BLOCKS), gc);
		
		gc.gridx = 0;
		gc.gridy = row++;
		gc.insets = new Insets(15,0,5,0);
		contentPanel.add(new JLabel("<html><b>Tip:</b></html>"), gc);
		
		gc.gridx = 1;
		contentPanel.add(new JLabel(
				"<html><div width=400>When you save your project the .json file is "
				+ "saved with it, so you only have to import it once.</div>"
				+ "</html>"), gc);
		
		gc.gridx = 0;
		gc.gridy = row++;
		gc.insets = new Insets(15,0,15,0);
		contentPanel.add(new JLabel("<html><b>Tip:</b></html>"), gc);
		
		gc.gridx = 1;
		contentPanel.add(new JLabel(
				"<html><div width=400>Once you save your project you can "
				+ "upload the .sb2 file to your Scratch 2.0 account and "
				+ "use it online in Firefox, Chrome, or Safari!</div>"
				+ "</html>"), gc);
		
		gc.gridx = 0;
		gc.gridy = row++;
		gc.gridwidth = 2;
		gc.insets = new Insets(20,0,5,0);
		titleLabel = new JLabel("Resources");
		titleLabel.setFont(TITLE_FONT);
		titleLabel.setForeground(TITLE_COLOR);
		contentPanel.add(titleLabel, gc);
				
		JButton scratchLink = createLink(
				"\u2022   Scratch 2.0 Offline Editor", "http://scratch.mit.edu/scratch2download/");
		gc.gridy = row++;
		gc.insets = new Insets(15,0,0,0);
		contentPanel.add(scratchLink, gc);
		
		JButton sampleLink = createLink(
				"\u2022   Sample Projects", "http://khanning.com/leapscratch/Scratch20PlugInForLeapMotion_SampleProjects.zip");
		gc.gridy = row++;
		gc.insets = new Insets(0,0,0,0);
		contentPanel.add(sampleLink, gc);
		
		JButton jsonLink = jsonLink("\u2022   LeapMotion.json");
		gc.gridy = row++;
		gc.insets = new Insets(0,0,0,0);
		contentPanel.add(jsonLink, gc);
		
		JButton leapLink = createLink(
				"\u2022   Leap Motion", "http://leapmotion.com");
		gc.gridy = row++;
		gc.insets = new Insets(0,0,0,0);
		contentPanel.add(leapLink, gc);
		
		JButton sourceLink = createLink(
				"\u2022   Source Code", "http://github.com/khanning");
		gc.gridy = row++;
		gc.insets = new Insets(0,0,25,0);
		contentPanel.add(sourceLink, gc);
		
		JScrollPane jsp = new JScrollPane(contentPanel);
		jsp.setPreferredSize(new Dimension(500, 450));
		jsp.getVerticalScrollBar().setUnitIncrement(16);
		jsp.setHorizontalScrollBar(null);
		jsp.setBorder(new EmptyBorder(0,0,0,0));
		boxPanel.add(jsp);
				
		boxPanel.add(Box.createRigidArea(new Dimension(0,10)));
		
		JButton closeButton = new JButton("Close");
		closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		boxPanel.add(closeButton);
		
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();				
			}
			
		});
				
		JPanel footerPanel = new JPanel();
		footerPanel.setLayout(new GridBagLayout());
		gc = new GridBagConstraints();
		footerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(10,10,10,0);
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.anchor = GridBagConstraints.LINE_START;
		JLabel createdBy = new JLabel("Created by Kreg Hanning");
		createdBy.setFont(FOOTER_FONT);
		createdBy.setForeground(SUB_FOREGROUND_COLOR);
		footerPanel.add(createdBy, gc);
		
		gc.gridx = 1;
		gc.insets = new Insets(10,0,10,10);
		gc.anchor = GridBagConstraints.LINE_END;
		JLabel versionLabel = new JLabel("v" + VERSION);
		versionLabel.setFont(FOOTER_FONT);
		versionLabel.setForeground(SUB_FOREGROUND_COLOR);
		footerPanel.add(versionLabel, gc);
		
		boxPanel.add(footerPanel);
		
		frame.add(boxPanel);
		
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowClosed(WindowEvent e) {
				isOpen = false;
			}

			@Override
			public void windowClosing(WindowEvent e) {}

			@Override
			public void windowDeactivated(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowOpened(WindowEvent e) {
				isOpen = true;
			}
		});
						
		frame.pack();
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		frame.setAlwaysOnTop(true);
		frame.setAlwaysOnTop(false);
	}
	
	public boolean isMinimized() {
		return frame.getState() == JFrame.ICONIFIED;
	}
	
	public void minimize() {
		frame.setState(JFrame.ICONIFIED);
	}
	
	public void restore() {
		frame.toFront();
		frame.setState(JFrame.NORMAL);
	}
	
	public void close() {
		frame.dispose();
	}
	
	public int getState() {
		return frame.getState();
	}
	
	public void setState(int state) {
		frame.setState(state);
	}
	
	public void toFront() {
		frame.toFront();
	}
	
	private void writeFile(URL in, File out) {
		
		try {
			
			
			InputStream input = in.openStream();
			OutputStream output = new FileOutputStream(out);
			
			byte[] buff = new byte[1024];
			int len;
			while ((len = input.read(buff)) > 0) {
				output.write(buff, 0, len);
			}
			
			input.close();
			output.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Unable to location LeapMotion.json file");
		} catch (IOException e) {
			System.out.println("Error copying file");
		}
	}
	 
	private JButton createLink(String label, String u) {
		
		final JButton button = new JButton(label);
		final String url = u;
		
		button.setFont(BODY_LINK_FONT);
		button.setFocusPainted(false);
		button.setMargin(new Insets(0,0,0,0));
		button.setContentAreaFilled(false);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setBorderPainted(false);
		button.setBorder(new EmptyBorder(0,0,0,0));
		button.setOpaque(false);
		button.setBackground(BACKGROUND_COLOR);
		button.setForeground(LINK_COLOR);
		
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setForeground(LINK_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				button.setForeground(LINK_COLOR);
			}
		});
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URI(url));
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				} else {
					StringSelection textUrl = new StringSelection(url);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(textUrl, null);
					
					UIManager.put("Panel.background", UIManager.getLookAndFeelDefaults().get("Panel.background"));
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					} catch (Exception e) {}
					JOptionPane.showMessageDialog(
							frame, "URL copied to clipboard", "Link Copied", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			
		});
		
		return button;
		
	}
	
	private JButton jsonLink(String label) {
		
		final JButton button = new JButton(label);
		
		button.setFont(BODY_LINK_FONT);
		button.setFocusPainted(false);
		button.setMargin(new Insets(0,0,0,0));
		button.setContentAreaFilled(false);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setBorderPainted(false);
		button.setBorder(new EmptyBorder(0,0,0,0));
		button.setOpaque(false);
		button.setBackground(BACKGROUND_COLOR);
		button.setForeground(LINK_COLOR);
		
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setForeground(LINK_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				button.setForeground(LINK_COLOR);
			}
		});
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				UIManager.put("Panel.background", UIManager.getLookAndFeelDefaults().get("Panel.background"));
				
				String desktopPath = System.getProperty("user.home") + "/Desktop";						
				File jsonFile = new File("src/res/LeapMotion.json");
				URL jsonURL = HelpWindow.class.getResource("/res/LeapMotion.json");
				
				JFileChooser fileChooser = new JFileChooser(desktopPath);
				fileChooser.setSelectedFile(jsonFile);
				
				int selection = fileChooser.showSaveDialog(frame);
								
				if (selection == JFileChooser.APPROVE_OPTION) {
					File saveTo = fileChooser.getSelectedFile();
					
					if (saveTo.exists()) {
						
						int n = JOptionPane.showConfirmDialog(
						frame,
						"File already exists\nOverwrite?",
						"File Exists",
						JOptionPane.YES_NO_OPTION);
						
						if (n == JOptionPane.YES_OPTION) {
							writeFile(jsonURL, saveTo);
						}
						
					} else {
						writeFile(jsonURL, saveTo);
					}
				}
				
				UIManager.put("Panel.background", BACKGROUND_COLOR);
				
			}
		});
		
		return button;
		
	}

}
