package org.giereczka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RuletkaServer {
    static int port = 666;
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(port);
            Socket client = server.accept();
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
