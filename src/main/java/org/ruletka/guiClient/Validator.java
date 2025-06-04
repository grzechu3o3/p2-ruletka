package org.ruletka.guiClient;

public class Validator {
    public static boolean isValidNick(String nick) {
        return nick != null && !nick.isEmpty() && !nick.contains("|") && !nick.contains(":");
    }
    public static boolean isValidBet(String num) {
        try {
            int numValue = Integer.parseInt(num);
            return numValue >= 0 && numValue <= 36;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isPositive(String num) {
        try {
            return Integer.parseInt(num) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
