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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ScratchSocket implements Runnable {

	private ServerSocket socket;
	private Socket client;
	private int port;
	
	private OutputStream output;
	
	public boolean isConnected = false;
	
	public ScratchSocket(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		try {
			socket = new ServerSocket(port);
			connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void connect() {
		
		try {
			client = socket.accept();
						
			output = client.getOutputStream();
			StringBuffer stringBuff = new StringBuffer();
			BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			char[] buffer = new char[1024];
			
			while (!isConnected) {
				int in = input.read(buffer);
				stringBuff.append(buffer, 0, in);
				if (stringBuff.toString().contains("<policy-file-request/>")) {
					stringBuff = new StringBuffer();
					String response = "<?xml version=\"1.0\"?><cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>";
					output.write(response.getBytes());
					output.write(0);
					output.flush();
					connect();
					isConnected = true;
				} else if (stringBuff.toString().contains("{\"method\": \"poll\", \"params\": []}")) {
					isConnected = true;
				}
			}
			
		} catch (IOException e) {
			//e.printStackTrace();
		}
		
		Main.refresh();
	}
	
	public void send(String response) {
		try {
			output.write(response.getBytes());
		} catch (IOException e) {
			isConnected = false;
			Main.refresh();
			connect();
		}
	}
	
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
