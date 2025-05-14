package org.giereczka;

public class RuletkaProtocol {
    private static final int WAITING = 0;
    private static final int CONNECTED = 1;
    private static final int GAMEBEGAN = 2;

    private int state = WAITING;

    public String processInput(String theInput) {
        String theOutput = "";
        if(state == WAITING) {
            theOutput="Server is waiting for players";
            state = CONNECTED;
        } else if(state == CONNECTED) {
            theOutput="Connected to the game server";
        } else if(state == GAMEBEGAN) {
            theOutput="Game began";
        }

        return theOutput;
    }
}
