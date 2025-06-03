package org.ruletka.guiClient;

public class Validator {
    public static boolean isValidNick(String nick) {
        return nick != null && !nick.isEmpty() && !nick.contains("|") && !nick.contains(":");
    }
    public static boolean isValidBet(String bet, String num) {
        if(bet.isEmpty() || num.isEmpty()) return false;
        try {
            int numValue = Integer.parseInt(num);
            int betValue = Integer.parseInt(bet);
            return betValue > 0 && numValue >= 0 && numValue <= 36;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
