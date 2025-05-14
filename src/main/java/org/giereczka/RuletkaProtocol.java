package org.giereczka;

public class RuletkaProtocol {
    private static final int HELLO = 0;
    private static final int WAITING = 1;
    private static final int BET = 2;
    private static final int ROLL = 3;
    private static final int WINNER_ANNOUNCEMENT = 4;
    private static final int ROUND_END = 5;

    private int state = HELLO;
    String nick = null;
    int playerBet = -1;

    public String processInput(String theInput) {
        String theOutput = "";
        if(theInput== null && state == HELLO) {
            theOutput = "Witaj w ruletce! Podaj nazwę gracza: ";
            state = WAITING;
        } else if(state == WAITING) {
            theOutput = "Witaj !"+nick +"!";
            state = BET;
        } else if(state == BET) {
            theOutput = "Podaj zakład: liczba od 0 do 36: ";
            if(playerBet < 0 || playerBet > 36) {
                theOutput = "Zakład przyjęty!";
                state = ROLL;
            } else {
                theOutput = "Zakład musi być liczbą od 0 do 36!";
            }
            state = ROLL;
        } else if(state == ROLL) {
            theOutput = "Kręcę...";
            state = WINNER_ANNOUNCEMENT;
        } else if(state == WINNER_ANNOUNCEMENT) {
            theOutput = "Wygrałeś!";
            state = ROUND_END;
        } else if(state == ROUND_END) {
            theOutput = "Koniec rundy";
            state = BET;
        }

        return theOutput;
    }
}
