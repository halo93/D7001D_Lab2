package se.ltu.netprog.javaprog.sockets;

import java.net.*;
import java.io.*;
import java.util.*;

public class UDPClient {
    static final int BUFF_SIZE = 1024;

    static public void main(String args[]) throws SocketException {

        if (args.length != 1){
            throw new IllegalArgumentException("Must specify a port!");
        }
        Scanner scan = new Scanner(System.in);
        int port = Integer.parseInt(args[0]);
        byte[] buff = new byte[BUFF_SIZE];
        DatagramSocket s = new DatagramSocket();

        try {
            InetAddress ip = InetAddress.getLocalHost();

            while (!s.isClosed()) {
                System.out.print("Enter a text: ");
                String request = scan.nextLine();
                DatagramPacket dp = new DatagramPacket(request.getBytes(), request.getBytes().length, ip, port);
                s.send(dp);
                if (request.equals("bye") || request.equals("shutdown")) {
                    s.close();
                    break;
                }
                dp = new DatagramPacket(buff, BUFF_SIZE);
                s.receive(dp);
                System.out.println("Response from Server: " + new String(dp.getData(), dp.getOffset(), dp.getLength()));
                buff = new byte[BUFF_SIZE];
            }
        } catch (IOException e) {
            System.out.println("Fatal I/O Error !");
            System.exit(0);
        }
    }
}
