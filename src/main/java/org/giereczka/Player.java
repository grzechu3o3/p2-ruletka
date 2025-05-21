package org.giereczka;

import java.io.PrintWriter;

public class Player {
    public final String nick;
    public String currentBet = null;
    public int totalWon = 0;
    public final PrintWriter out;
    public RuletkaProtocol protocol;

    public Player(String nick, PrintWriter out, RuletkaProtocol protocol) {
        this.nick = nick;
        this.out = out;
        this.protocol = protocol;
    }
}
