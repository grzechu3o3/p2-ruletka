package org.giereczka;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class RuletkaServer {
    static int port = 666;
    static List<Player> players = Collections.synchronizedList(new ArrayList<>());
    private boolean gameStarted = false;
    private static final int ROUND_TIME = 30 * 1000;
    private Timer gameTime;
    private Random random = new Random();
    private long lastGame = 0;

    public void register(Player player) {
        players.add(player);
    }


    public void start() throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("[INFO] Server listening on port " + port);

        startGame();

        while(true) {
            Socket client = server.accept();
            System.out.println("[INFO] New client connected from: " + client.getInetAddress());
            ClientHandling handler = new ClientHandling(client, this);
            new Thread(handler).start();
        }

    }

    private void startGame() {
        if(gameTime != null) gameTime.cancel();

        gameTime = new Timer();
        gameTime.scheduleAtFixedRate(new TimerTask() { // run every ROUND_TIME for ROUND_TIME
            @Override
            public void run() {
                Game();
            }
        }, ROUND_TIME, ROUND_TIME);

        lastGame = System.currentTimeMillis();
        System.out.println("[INFO] Gra rozpoczęta, czas między rundami " + ROUND_TIME / 1000 + "s");
    }

    private void Game() {
        gameStarted = true;
        System.out.println("[INFO] Runda rozpoczęta!");

        sendMsg("[INFO] Kręcę... zakłady zamknięte");

        int winning_number = 7;
        String result = String.valueOf(winning_number);
        System.out.println("[INFO] Wynik: " + result);

        sendMsg("[RESULT] "+result);

        for(Player p : players) {
            if(p.currentBet != null && p.currentBet.equalsIgnoreCase(result)) {
                p.totalWon++;
                p.out.println("[WIN] Wygrałeś!");
            } else if(p.currentBet != null) {
                p.out.println("[LOSE] Niestety przegrałeś!");
            }
            p.currentBet = null;
            if(p.protocol!=null) p.protocol.resetGame();
        }
        sendMsg("[NEW_ROUND] "+(ROUND_TIME/1000) + " Nowa runda, masz"+(ROUND_TIME/1000) + "s na obstawienie");
        gameStarted = false;
    }

    private void sendMsg(String msg) {
        synchronized (players) {
            for(Player p : players) {
                p.out.println(msg);
            }
        }
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public String getTime() {
        if(lastGame == 0) return String.valueOf(ROUND_TIME/1000);
        long timeElapsed = System.currentTimeMillis() - lastGame;
        long timeRemaining = (ROUND_TIME - timeElapsed) / 1000;
        return String.valueOf(Math.max(0, timeRemaining));
    }

    public static void main(String[] args) throws IOException {
        new RuletkaServer().start();
    }
}
