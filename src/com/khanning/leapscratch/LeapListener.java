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

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Tool;

public class LeapListener extends Listener {
		
	private static final String OS = System.getProperty("os.name");
		
	public StringBuilder data = new StringBuilder();
	
	private static Frame currentFrame;
	
	private Hand currentHand;
	private Hand[] hands = new Hand[2];
	private boolean[] handOpen = new boolean[2];
	private static int[] handIds = new int[2];
	private final String[] HAND_NAMES = {
		"hand-one",
		"hand-two"
	};
	
	public Tool[] tools = new Tool[2];
	public final String[] TOOL_NAMES = {
		"tool-one",
		"tool-two"
	};
	
	public Finger[] fingers = new Finger[10];		
	public final String[] FINGER_NAMES = {
		"finger-one",
		"finger-two",
		"finger-three",
		"finger-four",
		"finger-five",
		"finger-six",
		"finger-seven",
		"finger-eight",
		"finger-nine",
		"finger-ten"
	};
	
	public boolean isConnected;
	
	public void onInit(Controller controller) {
		// Initialize arrays
		for (int i = 0; i < hands.length; i++)
			hands[i] = new Hand();
		for (int i = 0; i < tools.length; i++)
			tools[i] = new Tool();
		for (int i = 0; i < fingers.length; i++)
			fingers[i] = new Finger();
	}
	
	public void onConnect(Controller controller) {
		isConnected = true;
		BubbleTip.create("Leap Motion Controller connected");
		Main.refresh();
		
	}
	
	public void onDisconnect(Controller controller) {
		isConnected = false;
		BubbleTip.create("Leap Motion Controller disconnected");
		Main.refresh();
	}
	
	public void onExit(Controller controller) {
		
	}
	
	public void onFrame(Controller controller) {
		
		currentFrame = controller.frame();
		
		boolean handsFlipped = false;
		
		for (int i = 0; i < 2; i++) {
			currentHand = currentFrame.hands().get(i);
			if (currentHand.isValid()) {
				
				for (int n = 0; n < 2; n++) {
					if (currentHand.id() == handIds[n] && n != i)
						handsFlipped = true;
				}
				
				int num = i;
				if (handsFlipped)
					num = ((num == 0) ? 1 : 0);
				
				hands[num] = currentHand;
				handIds[num] = currentHand.id();
				
				if (!currentHand.tools().empty()) {
					tools[num] = currentHand.tools().get(0);
				}
												
				FingerList fings = currentHand.fingers();
				
				if (fings.count() >= 3)
					handOpen[num] = true;
				else
					handOpen[num] = false;
				
				for (int f = 0; f < 5; f++) {
					if (f < fings.count()) {
						Finger finger = fings.get(f);
						fingers[f + (5 * num)] = finger;
					} else
						fingers[f + (5 * num)] = new Finger();
				}
				
			} else {
				int num = i;
				if (handsFlipped)
					num = ((num == 0) ? 1 : 0);
				hands[num] = new Hand();
				handOpen[num] = false;
			}
		}
		
		if (isConnected) {
			
			data = new StringBuilder();
						
			for (int i = 0; i < hands.length; i++) {
				if (OS.contains("Linux")) {
					data.append(HAND_NAMES[i] + "-x " + Math.round(hands[i].palmPosition().getX()) + "\n");
					data.append(HAND_NAMES[i] + "-y " + Math.round((hands[i].palmPosition().getY() - 220) * 1.6) + "\n");
					data.append(HAND_NAMES[i] + "-z " + Math.round(hands[i].palmPosition().getZ()) + "\n");
				} else {
					data.append(HAND_NAMES[i] + "-x " + Math.round(hands[i].stabilizedPalmPosition().getX()) + "\n");
					data.append(HAND_NAMES[i] + "-y " + Math.round((hands[i].stabilizedPalmPosition().getY() - 220) * 1.6) + "\n");
					data.append(HAND_NAMES[i] + "-z " + Math.round(hands[i].stabilizedPalmPosition().getZ()) + "\n");
				}
				data.append(HAND_NAMES[i] + "-visible " + hands[i].isValid() + "\n");
				data.append(HAND_NAMES[i] + "-open " + handOpen[i] + "\n");
			}
					
			for (int i = 0; i < fingers.length; i++) {
				data.append(FINGER_NAMES[i] + "-x " + Math.round(fingers[i].stabilizedTipPosition().getX()) + "\n");
				data.append(FINGER_NAMES[i] + "-y " + Math.round((fingers[i].stabilizedTipPosition().getY() - 220) * 1.6) + "\n");
				data.append(FINGER_NAMES[i] + "-z " + Math.round(fingers[i].stabilizedTipPosition().getZ()) + "\n");
				data.append(FINGER_NAMES[i] + "-visible " + fingers[i].isValid() + "\n");
			}
			
			for (int i = 0; i < tools.length; i++) {
				data.append(TOOL_NAMES[i] + "-x " + Math.round(tools[i].stabilizedTipPosition().getX()) + "\n");
				data.append(TOOL_NAMES[i] + "-y " + Math.round((tools[i].stabilizedTipPosition().getY() - 220) * 1.6) + "\n");
				data.append(TOOL_NAMES[i] + "-z " + Math.round(tools[i].stabilizedTipPosition().getZ()) + "\n");
				data.append(TOOL_NAMES[i] + "-visible " + tools[i].isValid() + "\n");
			}
						
		}
		
	}
	
}
