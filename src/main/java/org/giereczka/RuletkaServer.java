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
        try(ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            while(true) {
                Socket client = server.accept();
                System.out.println("[INFO] New client connected from: " + client.getInetAddress());
                new Thread(new ClientHandling(client)).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
