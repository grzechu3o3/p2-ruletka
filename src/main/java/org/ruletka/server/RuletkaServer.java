package org.ruletka.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class RuletkaServer {
    static int port = 666;
    static List<Player> players = Collections.synchronizedList(new ArrayList<>());
    private boolean gameStarted = false;
    private static final int ROUND_TIME = 30 * 1000; // czas w sekundach
    private Timer gameTime;
    private Random random = new Random();
    private long lastGame = 0;
    private Timer roundTimer;

    public void register(Player player) {
        synchronized (players) {
            players.add(player);
            if(players.size() == 1 && !gameStarted) {
                System.out.println("[INFO] Dołączył pierwszy gracz - rozpoczynam grę");
                lastGame = System.currentTimeMillis();
            }
        }
    }

    public void start() throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("[INFO] Server listening on port " + port);

        timerBroadcast();
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

        gameStarted = false;
        gameTime.scheduleAtFixedRate(new TimerTask() { // run every ROUND_TIME for ROUND_TIME
            @Override
            public void run() {
                Game();
            }
        }, 20*1000, ROUND_TIME);

        lastGame = System.currentTimeMillis();
        System.out.println("[INFO] Probuje rozpoczac gre, czas między rundami " + ROUND_TIME / 1000 + "s");
    }

    private void Game() {
        synchronized(players) {
            if(players.isEmpty()) {
                System.out.println("[INFO] Brak graczy, gra nie zostanie rozpoczęta");
                gameStarted = false;
                sendMsg("[INFO] Brak graczy - wstrzymano rundę");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Game();
                    }
                }, 5000);
                return;
            }
        }

        gameStarted = true;
        System.out.println("[INFO] Runda rozpoczęta!");

        sendMsg("[INFO] Kręcę...");

        int winning_number = random.nextInt(37);
        String result = String.valueOf(winning_number);
        System.out.println("[INFO] Wynik: " + result);

        sendMsg("[RESULT] "+result);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for(Player p : players) {
                    if(p.currentBet != null && p.currentBet.getType()==Bet.Type.NUM && p .currentBet.getNum().equals(winning_number)) {
                        p.totalWon+=p.currentBet.getAmount();
                        p.out.println("[WIN] Wygrałeś: " + p.currentBet.getAmount());
                    } else if(p.currentBet != null && p.currentBet.getType()==Bet.Type.COLOR) {
                        Set<Integer> reds = Set.of(
                                1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36
                        );
                        boolean isRed = reds.contains(winning_number);
                        if(isRed && p.currentBet.getColor().equalsIgnoreCase("red")
                        || !isRed && p.currentBet.getColor().equalsIgnoreCase("black")) {
                            p.totalWon+=p.currentBet.getAmount();
                            p.out.println("[WIN] Wygrałeś: " + p.currentBet.getAmount());
                        }
                    } else if(p.currentBet != null) {
                        p.out.println("[LOSE] Niestety przegrałeś!");
                        p.totalLost+=p.currentBet.getAmount();
                    }
                    p.currentBet = null;
                    if(p.protocol!=null) p.protocol.resetGame();
                }
                sendMsg("[NEW_ROUND] Nowa runda, masz "+(ROUND_TIME/1000) + "s na obstawienie");
                lastGame = System.currentTimeMillis();
                gameStarted = false;
            }
        }, 9000);

    }

    public void sendMsg(String msg) {
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

    private void timerBroadcast() {
        if (roundTimer != null) return;

        roundTimer = new Timer();
        roundTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    String time = "[TIMER] " + getTime();
                    sendMsg(time);
            }
        }, 0, 1000);
    }

    
    public static void main(String[] args) throws IOException {
        new RuletkaServer().start();
    }
}
