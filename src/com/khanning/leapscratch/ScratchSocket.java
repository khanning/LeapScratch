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
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScratchSocket {

	private LeapListener mLeapListener;
	private BufferedReader input;	
	private ExecutorService pool;
	private String line;

	private static OutputStream output;
	private static ServerSocket socket;
	private static Socket client;
	
	public boolean isConnected = false;
	
	private final String HTTP_HEADER = "HTTP/1.1 200 OK\r\n"
			+ "Content-Type: text/html; charset=ISO-8859-1\r\n"
			+ "Access-Control-Allow-Origin: *\r\n\r\n";
	
	private final String HTTP_POLICY = "<cross-domain-policy>\n"
			+ "  <allow-access-from domain=\"*\" to-ports=\"*\"/>\n"
			+ "</cross-domain-policy>\n\0\r\n";
	
	public ScratchSocket(int port) {		
		pool = Executors.newCachedThreadPool();
		
		try {
			socket = new ServerSocket(port);
			System.out.println("Starting Scratch socket");
		} catch (IOException e) {
			Main.socketError();
			System.out.println("Error starting Socket");
			return;
		}
		
		pool.submit(new HttpListener());
	}
	
	class HttpListener implements Runnable {
		
		@Override
		public void run() {
							
			try {

				client = socket.accept();
				output = client.getOutputStream();
				input = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				if ((line = input.readLine()) != null) {

					if (line.contains("crossdomain.xml")) {
						
						send(HTTP_POLICY);
												
					} else if (line.contains("poll")) {
						
						if(!isConnected) {
							isConnected = true;
							BubbleTip.create("Scratch 2.0 connected");
							socket.setSoTimeout(1000);
							Main.refresh();
						}
						
						send(mLeapListener.data.toString());
							
					}
				} else {
					send("null\n");
				}
				
				output.flush();
				output.close();
				client.close();
				
				pool.submit(new HttpListener());
					
			} catch (SocketTimeoutException e) {
				isConnected = false;
				BubbleTip.create("Scratch 2.0 disconnected");
				Main.refresh();
				try {
					socket.setSoTimeout(0);
				} catch (SocketException e1) {
					e1.printStackTrace();
				}
				pool.submit(new HttpListener());
			} catch (IOException e1) {}
		}
	}
	
	public void send(String data) {
							
		try { output.write((HTTP_HEADER + data).getBytes()); } 
		catch (IOException e) {}
	}
	
	public void setLeap(LeapListener leap) {
		mLeapListener = leap;
	}
	
	public void close() {
		try {
			if (output != null) {
				output.flush();
				output.close();
			}
			socket.close();
		} catch (IOException e) {}
	}
	
}
