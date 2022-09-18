package se.ltu.netprog.javaprog.sma;
public class DNSClient {

    public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: DNSClient host port");
		}

        String host = args[0];
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch(Exception e) {
            port = DNSService.DNS_SERVICE_PORT;
        }
        MessageClient conn;
        try {
            conn = new MessageClient(host,port);
        } catch(Exception e) {
            System.err.println(e);
            return;
        }
        Message m = new Message();
        m.setType(DNSService.DNS_SERVICE_MESSAGE);
        m.setParam("hostname",args[2]);
        m = conn.call(m);
        System.out.println("IP: " + m.getParam("IP"));
        m.setType(100);
        m = conn.call(m);
        System.out.println("Good Reply " + m);
        conn.disconnect();
    }
}
