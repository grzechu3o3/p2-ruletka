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
    private final RuletkaServer server;
    private Player player;
    private RuletkaProtocol protocol = new RuletkaProtocol();

    public ClientHandling(Socket client, RuletkaServer server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            out.println("Witaj w ruletce! Podaj nick: (NICK|twojnick");
            out.println("[TIME]"+server.getTime() + "s do następnej rundy");

            String clientIn;

            while((clientIn = in.readLine()) != null) {
                if(clientIn.equalsIgnoreCase("exit")) {
                    out.println("Rozłączam!");
                    break;
                }

                if(clientIn.startsWith("NICK") && player == null) {
                    String nickname = clientIn.split("\\|")[1];
                    System.out.println("[DEBUG] New player: " + nickname);

                    player = new Player(nickname, out, protocol);
                    server.register(player);

                    out.println("[INFO] Witaj " + nickname+ "! Masz" + server.getTime() + "s na obstawienie. Użyj BET|liczba|kwota");
                } else if(clientIn.startsWith("BET") && player != null) {
                    if(server.isGameStarted()) {
                        out.println("[ERROR] Gra w toku! Poczekaj na nową rundę!");
                    } else {
                        try {
                            String[] parts = clientIn.split("\\|");
                            int number = Integer.parseInt(parts[1]);
                            int amount = Integer.parseInt(parts[2]);

                            if(number < 0 || number > 36) {
                                out.println("[ERROR] Liczba musi być z zakresu 0-36");
                            } else if (amount<=0) {out.println("[ERROR] Kwota zakładu musi być dodatnia!");}
                            else {
                                player.currentBet = String.valueOf(number);
                                out.println("[INFO] Postawiono " + amount + " na " + number + ". Czekaj na wynik");
                            }
                        } catch (Exception e) {
                            out.println("[ERROR] Nieprawidłowy zakład! Format: BET|liczba|kwota");
                        }
                    }
                } else if (clientIn.equals("TIME")) {
                    out.println("[TIME] " + server.getTime());
                } else {
                    String res = protocol.processInput(clientIn);
                    out.println(res);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("[INFO] Client disconnected from "+client.getInetAddress());
                if(player != null) synchronized (server.players) {server.players.remove(player);}
                if(client != null && !client.isClosed()) client.close();
                if(in != null)  in.close();
                if(out != null) out.close();
            } catch (IOException e) {e.printStackTrace();}
        }
    }
}
