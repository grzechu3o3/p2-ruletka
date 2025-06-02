package org.giereczka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler {
    private final Socket s;
    private final PrintWriter out;
    private final BufferedReader in;

    public ConnectionHandler(String ip, int port) throws IOException {
        s = new Socket(ip, port);
        out = new PrintWriter(s.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public void send(String msg) {
        out.println(msg);
    }

    public String read() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        s.close();
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }
}
