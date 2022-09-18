package se.ltu.netprog.javaprog.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {
    static final int BUFSIZE = 1024;

    public static void main(String[] args) throws IOException {

        if (args.length != 1){
            throw new IllegalArgumentException("Must specify a port!");
        }

        int port = Integer.parseInt(args[0]);
        Scanner scan = new Scanner(System.in);

        Socket client_socket = new Socket("localhost", port);

        InputStreamReader isReader = new InputStreamReader(client_socket.getInputStream());
        BufferedReader bf = new BufferedReader(isReader);
        PrintWriter out = new PrintWriter(client_socket.getOutputStream(), true);

        String input = null;
        String response;

        while (input == null || !input.equals("bye")) {
            System.out.print("Enter a text: ");
            input = scan.nextLine();
            out.print(input);
            out.flush();
            response = bf.readLine();
            System.out.println("Response from Sever: " + response);
        }
        client_socket.close();
    }
}
