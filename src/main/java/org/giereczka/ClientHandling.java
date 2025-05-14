package org.giereczka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandling implements Runnable {
    private Socket client = null;
    private BufferedReader in;
    private PrintWriter out;
    private RuletkaProtocol protocol;

    public ClientHandling(Socket client) {
        this.client = client;
        this.protocol = new RuletkaProtocol();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            String clientIn, serverMsg;

            serverMsg = protocol.processInput(null);
            out.println(serverMsg);

            while((clientIn = in.readLine()) != null) {
                serverMsg = protocol.processInput(clientIn);
                out.println(serverMsg);

                if(clientIn.equalsIgnoreCase("exit")) {
                    out.println("Rozłączam!");
                    break;
                }

                if (serverMsg.equals("Koniec rundy")) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("[INFO] Client disconnected from "+client.getInetAddress());
                if(client != null && !client.isClosed()) client.close();
                if(in != null)  in.close();
                if(out != null) out.close();
            } catch (IOException e) {e.printStackTrace();}
        }
    }
}
