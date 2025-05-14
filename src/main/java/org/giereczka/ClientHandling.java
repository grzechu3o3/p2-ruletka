package org.giereczka;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandling implements Runnable {
    Socket client = null;

    public ClientHandling(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        ) {
            RuletkaProtocol protokol = new RuletkaProtocol();
            String input, output;

            output = protokol.processInput(null);
            out.println(output);

            while((input = in.readLine()) != null) {
                output = protokol.processInput(input);
                out.println(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
                System.out.println("[INFO] Client disconnected from "+client.getInetAddress());
            } catch (IOException e) {e.printStackTrace();}
        }
    }
}
