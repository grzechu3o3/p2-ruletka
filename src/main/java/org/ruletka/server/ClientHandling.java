package org.ruletka.server;

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

            out.println("Witaj w ruletce! Podaj nick: (N|twojnick)");
            out.println("[TIME] "+server.getTime() + "s do następnej rundy");

            String clientIn;

            while((clientIn = in.readLine()) != null) {
                if(clientIn.equalsIgnoreCase("exit")) {
                    out.println("Rozłączam!");
                    break;
                }
                if(clientIn.toLowerCase().startsWith("c|")) {
                    int index = clientIn.toLowerCase().indexOf("c|");
                    if(index != -1 && index+1 < clientIn.length()) {
                        String msg = clientIn.substring(index+2).trim();
                        if(!msg.isEmpty()) {
                            server.sendMsg("[CHAT] " + player.nick + ":" + msg);
                        } else continue;
                    } else continue;
                }
                if(clientIn.toUpperCase().startsWith("N") && player == null) {
                    String nickname = clientIn.split("\\|")[1];
                    System.out.println("[DEBUG] New player: " + nickname);

                    player = new Player(nickname, out, protocol);
                    server.register(player);

                    out.println("[INFO] Witaj " + nickname+ "! Masz " + server.getTime() + "s na obstawienie. Użyj B|liczba|kwota");
                } else if(clientIn.toLowerCase().startsWith("b") && player != null) {
                    if(server.isGameStarted()) {
                        out.println("[ERROR] Gra w toku! Poczekaj na nową rundę!");
                        continue;
                    } else {
                        try {
                            String[] parts = clientIn.split("\\|");

                            String typeOrNum = parts[1].toLowerCase();
                            int amount = Integer.parseInt(parts[2]);

                            if(typeOrNum.equals("red") || typeOrNum.equals("black")) {
                                player.placeBet(new Bet(typeOrNum, amount));
                                out.println("[BET] Postawiono " + amount + " na " + typeOrNum.toUpperCase() + ". Czekaj na wynik");
                                continue;
                            }

                            int number = Integer.parseInt(typeOrNum);
                            if(number < 0 || number > 36) {
                                out.println("[ERROR] Liczba musi być z zakresu 0-36");
                            } else if (amount<=0) {out.println("[ERROR] Kwota zakładu musi być dodatnia!");}
                            else {
                                player.placeBet(new Bet(number, amount));
                                out.println("[BET] Postawiono " + amount + " na " + number + ". Czekaj na wynik");
                            }
                        } catch (NumberFormatException e) {
                            out.println("[ERROR] Nieprawidłowy zakład! Format: B|liczba|kwota");
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
