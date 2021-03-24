package sk.tuke.gamestudio;

import sk.tuke.gamestudio.game.guessthepicture.consoleui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("Game started");
        ConsoleUI consoleUI = new ConsoleUI();
        do {
            consoleUI.play();
        } while (consoleUI.isPlayAgain());
    }
}

