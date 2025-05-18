package org.giereczka;

public class RuletkaProtocol {

    // stany
    private static final int WAITING_FOR_NAME = 0;
    private static final int WAITING_FOR_BET = 1;
    private static final int GAME_IN_PROGRESS = 2;

    private int gameState = WAITING_FOR_NAME;
    private String nickname;

    public String processInput(String theInput) {
        if(theInput == null && nickname == null) {
            return "[INFO] Podaj nick";
        }

        String[] parts = theInput.split("\\|");
        String command = parts[0];

        switch(gameState) {
            case WAITING_FOR_NAME:
                if(command.equals("NICK")) {
                    nickname = parts[1].trim();
                    gameState = WAITING_FOR_BET;
                    return "[INFO] Witaj " + nickname + " | Podaj zakład w formie BET|liczba|kwota";
                } else {
                    return "[BŁĄD] Nie podano nicku!";
                }
            case WAITING_FOR_BET:
                if(command.equals("BET")) {
                    if(parts.length < 3) {return "[BŁĄD] Nieprawidłowa forma zakładu, Format: BET|liczba|kwota";}
                    try {
                        int number = Integer.parseInt(parts[1]);
                        int amount = Integer.parseInt(parts[2]);

                        if(number < 0 || number > 36) {
                            return "BŁĄD|Zakład musi być od 0 do 36!";
                        }
                        if(amount <=0) {
                            return "BŁĄD|Kwota zakładu musi być większa od 0";
                        }

                        gameState = GAME_IN_PROGRESS;
                        return "INFO|Postawiono " + amount + " na " + number;
                    } catch (NumberFormatException e) {
                        return "ERROR|Nieprawidłowy format danych, muszą być liczbami całkowitymi!";
                    }
                } else if(command.equalsIgnoreCase("EXIT")) {
                    return "[INFO] Dziękuje za grę";
                }
            case GAME_IN_PROGRESS:
                if(command.equals("CHAT")) {
                    return "CHAT|" + nickname + "|" + parts[1];
                } else {
                    return "INFO|Gra w toku, czekaj na wynik";
                }
            default:
                return "ERROR|Nieznany stan gry! Spróbuj połączyć się ponownie.";
        }
    }

    public void resetGame() {
        gameState = WAITING_FOR_BET;
    }
}
