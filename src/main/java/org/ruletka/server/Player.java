package org.ruletka.server;

import java.io.PrintWriter;

public class Player {
    public final String nick;
    public Bet currentBet = null;
    public int totalWon = 0;
    public int totalLost = 0;
    public final PrintWriter out;
    public RuletkaProtocol protocol;

    public Player(String nick, PrintWriter out, RuletkaProtocol protocol) {
        this.nick = nick;
        this.out = out;
        this.protocol = protocol;
    }

    public void placeBet(Bet bet) {
        currentBet = bet;
    }
    public void clearBet() {
        this.currentBet = null;
    }
}
