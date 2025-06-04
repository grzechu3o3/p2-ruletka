package org.ruletka.server;

public class RuletkaProtocol {

    // stany
    private static final int WAITING_FOR_NAME = 0;
    private static final int WAITING_FOR_BET = 1;
    private static final int GAME_IN_PROGRESS = 2;

    private int gameState = WAITING_FOR_NAME;
    private String nickname;

    public String processInput(String theInput) {
        if (theInput == null && nickname == null) {
            return "[INFO] Podaj nick";
        }

        String[] parts = theInput.split("\\|");
        String command = parts[0];

        switch (gameState) {
            case WAITING_FOR_NAME:
                if (command.equalsIgnoreCase("N")) {
                    nickname = parts[1].trim();
                    gameState = WAITING_FOR_BET;
                    return "[INFO] Witaj " + nickname + " | Podaj zakład w formie B|liczba/kolor|kwota";
                }

            case WAITING_FOR_BET:
                if (command.equalsIgnoreCase("B")) {
                    if (parts.length < 3) {
                        return "[BŁĄD] Nieprawidłowa forma zakładu, Format: B|liczba|kwota";
                    }
                    String target = parts[1].toLowerCase();
                    int amount;
                    try {
                        amount = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        return "[BŁĄD] Nieprawidłowy format danych, muszą być liczbami całkowitymi!";
                    }
                    if(target.equals("red") || target.equals("black")) {
                        gameState = GAME_IN_PROGRESS;
                        return "[BET_SUCCESS]";
                    } else {
                        try {
                            int number = Integer.parseInt(target);
                            if (number < 0 || number > 36) {
                                return "[BŁĄD] Zakład musi być od 0 do 36!";
                            }
                            if (amount <= 0) {
                                return "[BŁĄD] Kwota zakładu musi być większa od 0";
                            }

                            gameState = GAME_IN_PROGRESS;
                            return "[BET_SUCCESS]";
                        } catch (NumberFormatException e) {
                            return "[BŁĄD] Nieprawidłowy zakład! Użyj liczby od 0 do 36 lub 'red'/'black'";
                        }
                    }
                } else if (command.equalsIgnoreCase("EXIT")) {
                    return "[INFO] Dziękuje za grę";
                }

            case GAME_IN_PROGRESS:
                if (command != null && !command.toLowerCase().startsWith("c")) {
                    return "[INFO] Gra w toku, czekaj na wynik.";
                }

            default:
                if(command != null) {
                    return command;
                }
                return "[BŁĄD] Nieznany stan gry!";
        }
    }

    public void resetGame() {
        gameState = WAITING_FOR_BET;
    }
}
