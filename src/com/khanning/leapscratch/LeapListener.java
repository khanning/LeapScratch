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
	
	private ScratchSocket socket;
	
	private static final String OS = System.getProperty("os.name");
			
	private static final String[] HAND_NAMES = {
		"hand-one",
		"hand-two"
	};
	
	private static int[] handIds = new int[2];
	
	private static final String[] TOOL_NAMES = {
		"tool-one",
		"tool-two"
	};
		
	private static final String[] FINGER_NAMES = {
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
	
	public Hand[] hands = new Hand[2];
	public Tool[] tools = new Tool[2];
	public Finger[] fingers = new Finger[10];
	
	public boolean isConnected;
	
	public void setSocket(ScratchSocket s) {
		socket = s;
	}
	
	public void onInit(Controller controller) {

	}
	
	public void onConnect(Controller controller) {
		
		// Initialize arrays
		for (int i = 0; i < hands.length; i++)
			hands[i] = new Hand();
		for (int i = 0; i < tools.length; i++)
			tools[i] = new Tool();
		for (int i = 0; i < fingers.length; i++)
			fingers[i] = new Finger();
		isConnected = true;
		
		Main.refresh();
		
	}
	
	public void onDisconnect(Controller controller) {

	}
	
	public void onExit(Controller controller) {
		
	}
	
	public void onFrame(Controller controller) {
				
		Frame frame = controller.frame();
		
		boolean[] handOpen = new boolean[2];
		boolean handsFlipped = false;
		
		for (int i = 0; i < 2; i++) {
			Hand hand = frame.hands().get(i);
			if (hand.isValid()) {
				
				for (int n = 0; n < 2; n++) {
					if (hand.id() == handIds[n] && n != i)
						handsFlipped = true;
				}
				
				int num = i;
				if (handsFlipped)
					num = ((num == 0) ? 1 : 0);
				
				hands[num] = hand;
				handIds[num] = hand.id();
				
				if (!hand.tools().empty()) {
					tools[num] = hand.tools().get(0);
				}
												
				FingerList fings = hand.fingers();
				
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
		
		if (isConnected && socket.isConnected) {
						
			String response = "{\"method\":\"update\",\"params\":[";
						
			for (int i = 0; i < hands.length; i++) {
				if (OS.contains("Linux")) {
					response += "[\"" + HAND_NAMES[i] + "-x\",\"" + Math.round(hands[i].palmPosition().getX()) + "\"],";
					response += "[\"" + HAND_NAMES[i] + "-y\",\"" + Math.round((hands[i].palmPosition().getY() - 220) * 1.6) + "\"],";
					response += "[\"" + HAND_NAMES[i] + "-z\",\"" + Math.round(hands[i].palmPosition().getZ()) + "\"],";
				} else {
					response += "[\"" + HAND_NAMES[i] + "-x\",\"" + Math.round(hands[i].stabilizedPalmPosition().getX()) + "\"],";
					response += "[\"" + HAND_NAMES[i] + "-y\",\"" + Math.round((hands[i].stabilizedPalmPosition().getY() - 220) * 1.6) + "\"],";
					response += "[\"" + HAND_NAMES[i] + "-z\",\"" + Math.round(hands[i].stabilizedPalmPosition().getZ()) + "\"],";
				}
				response += "[\"" + HAND_NAMES[i] + "-visible\"," + hands[i].isValid() + "],";
				response += "[\"" + HAND_NAMES[i] + "-open\"," + handOpen[i] + "],";
			}
						
			for (int i = 0; i < fingers.length; i++) {
				response += "[\"" + FINGER_NAMES[i] + "-x\",\"" + Math.round(fingers[i].stabilizedTipPosition().getX()) + "\"],";
				response += "[\"" + FINGER_NAMES[i] + "-y\",\"" + Math.round((fingers[i].stabilizedTipPosition().getY() - 220) * 1.6) + "\"],";
				response += "[\"" + FINGER_NAMES[i] + "-z\",\"" + Math.round(fingers[i].stabilizedTipPosition().getZ()) + "\"],";
				response += "[\"" + FINGER_NAMES[i] + "-visible\"," + fingers[i].isValid() + "],";
			}
			
			for (int i = 0; i < tools.length; i++) {
				response += "[\"" + TOOL_NAMES[i] + "-x\",\"" + Math.round(tools[i].stabilizedTipPosition().getX()) + "\"],";
				response += "[\"" + TOOL_NAMES[i] + "-y\",\"" + Math.round((tools[i].stabilizedTipPosition().getY() - 220) * 1.6) + "\"],";
				response += "[\"" + TOOL_NAMES[i] + "-z\",\"" + Math.round(tools[i].stabilizedTipPosition().getZ()) + "\"],";
				response += "[\"" + TOOL_NAMES[i] + "-visible\"," + tools[i].isValid() + "],";
			}
			
			response += "]}\n";
			
			socket.send(response);
		}
		
	}
	
}
