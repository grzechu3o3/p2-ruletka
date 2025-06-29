package org.ruletka;

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

            Thread serverHandling = new Thread(() -> {
                try {
                    String serverMsg;
                    while((serverMsg = in.readLine()) != null) {
                        System.out.println("[CLIENT]: " + serverMsg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            serverHandling.start();

            String userInput;
            while((userInput = userIn.readLine()) != null) {
                out.println(userInput);
                if(userInput.equalsIgnoreCase("exit")) break;
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } finally {
            try {
                Thread.sleep(500);
                if (s!=null) s.close();
                if(in!=null) in.close();
                if(out!=null) out.close();
                if(userIn!=null) userIn.close();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new RuletkaClient("127.0.0.1", 666);
    }
}

