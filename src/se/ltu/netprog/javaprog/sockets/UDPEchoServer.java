package se.ltu.netprog.javaprog.sockets;


import java.net.*;// need this for InetAddress, Socket, ServerSocket 
import java.io.*;// need this for I/O stuff
import java.nio.charset.StandardCharsets;

public class UDPEchoServer { 
	static final int BUFSIZE=1024;
	
	static public void main(String args[]) throws SocketException 
	{ 
		
		if (args.length != 1) {
			throw new IllegalArgumentException("Must specify a port!"); 
						
		}
		
		int port = Integer.parseInt(args[0]);
		DatagramSocket s = new DatagramSocket(port);
		byte[] buff = new byte[BUFSIZE];
		System.out.println("Server is listening on port " + port);
		try {
			while (true) {
				DatagramPacket dp = new DatagramPacket(buff, BUFSIZE);
				s.receive(dp);
				// print out client's address 
				System.out.println("Message from " + dp.getAddress().getHostAddress());
				String originalData = new String(dp.getData(), StandardCharsets.UTF_8).trim();
				if (originalData.equals("shutdown")){
					s.close();
					System.out.println("Shutting down UDP Server on port " + port);
					return;
				}
				String modifiedData = "Long & Nafisah: " + originalData;
				dp = new DatagramPacket(modifiedData.getBytes(), modifiedData.getBytes().length, dp.getAddress(), dp.getPort());
				// Send it right back 
				s.send(dp); 
				buff = new byte[BUFSIZE];
			} 
		} catch (IOException e) {
			System.out.println("Fatal I/O Error !"); 
			System.exit(0);
			
		} 

	}
}