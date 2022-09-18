package se.ltu.netprog.mytcpserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MyTCPServer implements Runnable {

    static {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("./src/main/resources/logging.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final static Logger LOGGER = Logger.getLogger(MyTCPServer.class.getName());
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    static final int BUFSIZE = 1024;
    private Socket socket;

    public MyTCPServer(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Must specify a port!");
        }
        if (args.length == 2) {
            LOGGER.setLevel(Level.parse(args[1].toUpperCase()));
        } else {
            LOGGER.setLevel(Level.INFO);
        }

        int port = Integer.parseInt(args[0]);
        try {
            ServerSocket socket = new ServerSocket(port);
            // Record which port the server is listening. This information is important for server testers and maintainers
            LOGGER.info("TCP Server is listening on port " + port);
            while (true) {
                Socket acceptedSocket = socket.accept();
                //Log client's address to know which client is connecting
                LOGGER.info("Connection from " + acceptedSocket.getInetAddress().getHostAddress());
                executorService.submit(new MyTCPServer(acceptedSocket));
            }
        } catch (IOException e) {
            // Log error message and stack trace when there is an exception when connecting between server and client
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        try {
            handleClient(socket);
        } catch (IOException e) {
            // Log error message and stack trace when there is an exception when handling client's request
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    static void handleClient(Socket s) throws IOException
    {
        byte[] buff = new byte[BUFSIZE];
        int bytesread = 0;

        //Set up streams
        InputStream in = s.getInputStream();
        PrintWriter writer = new PrintWriter(s.getOutputStream(), true);

        while ((bytesread = in.read(buff)) != -1) {
            String str = new String(buff, 0, bytesread, StandardCharsets.ISO_8859_1);
            // Logging the command requested by a client recognized by the IP Address
            LOGGER.log(Level.INFO, String.format("Client Command Received: %s [%s]", str, s.getInetAddress()));
            String[] iterationWithCommand = str.split(";;");
            CommandExecutor commandExecutor = new CommandExecutor(iterationWithCommand[1]);
            String rs = commandExecutor.execute();
            if (Objects.nonNull(rs)) {
                StringBuilder aggregatedRs = new StringBuilder();
                for (int i = 1; i <= Integer.parseInt(iterationWithCommand[0]); i++) {
                    aggregatedRs.append(i).append(".").append("\n").append(rs);
                }
                rs = aggregatedRs.toString();
            }
            // Logging the result before sending to client recognized by the IP Address
            LOGGER.log(Level.INFO, String.format("Preparing to response to client: %s [%s]", rs, s.getInetAddress()));
            writer.println(rs);
            writer.flush();
            buff = new byte[BUFSIZE];
        }
        // Logging the information as notification before client disconnects to the socket
        LOGGER.log(Level.INFO, String.format("Client has left [%s]", s.getInetAddress()));
        s.close();
    }
}
