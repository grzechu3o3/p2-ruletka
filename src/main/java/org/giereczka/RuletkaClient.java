package org.giereczka;

import java.io.*;
import java.net.Socket;

public class RuletkaClient {
    private Socket s = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private BufferedReader userIn = null;


    public RuletkaClient(String addr, int port) {
        try {
            s = new Socket(addr, port);
            System.out.println("Connected!");

            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream(), true);
            userIn = new BufferedReader(new InputStreamReader(System.in));

            String serverMsg, userInput;

            while((serverMsg = in.readLine()) != null) {
                System.out.println("[SERVER]: " + serverMsg);
                userInput = userIn.readLine();
                out.println(userInput);

                if(userInput.equalsIgnoreCase("exit")) {
                    System.exit(0);
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new RuletkaClient("127.0.0.1", 666);
    }
}

