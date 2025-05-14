package org.giereczka;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class RuletkaClient {
    public static void main(String[] args) throws IOException {
        String host = "127.0.0.1";
        int port = 666;

        try {
            Socket client = new Socket(host, port);

            InputStream in = client.getInputStream();

            OutputStream out = client.getOutputStream();


        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);
        }
    }
}
