package sk.tuke.gamestudio.game.guessthepicture.consoleui;

import sk.tuke.gamestudio.game.guessthepicture.core.FieldState;
import sk.tuke.gamestudio.game.guessthepicture.core.Tile;
import sk.tuke.gamestudio.game.guessthepicture.core.TileField;

import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleUI {
    private TileField tileField;
    private String asciiString;
    private int colWidth;
    private final Scanner scanner = new Scanner(System.in);
    private final Pattern COMMAND = Pattern.compile("([a-z])([1-9])");
    private boolean playAgain;

    public ConsoleUI() {
        this.playAgain = false;
    }

    public void play() {
        System.out.println("______________________________________");
        System.out.println("Welcome to the game!");
        this.tileField = null;
        generatePicture();

        /*Game loop*/
        do {
            render();
            handleInput();
        } while (tileField.getState() == FieldState.PLAYING);

        /*Game result*/
        if (tileField.getState() == FieldState.SOLVED) {
            System.out.println("Game solved!");
        } else {
            System.out.println("Game failed!");
        }

        /*Play Again?*/
        System.out.println("Play again? [type 'y' for YES, other for NO]");
        String input = scanner.nextLine().toLowerCase();
        this.playAgain = "y".equalsIgnoreCase(input);
    }

    private void render() {
        System.out.print("  ");
        for (int column = 0; column < this.tileField.getColCount(); column++) {
            for (int i = 0; i < this.colWidth / 2; i++) {
                System.out.print(" ");
            }
            System.out.print(column + 1);
            for (int i = 0; i < this.colWidth / 2; i++) {
                System.out.print(" ");
            }
        }
        for (int row = 0; row < this.tileField.getRowCount(); row++) {
            System.out.println();
            System.out.print((char) (row + 'A') + " ");
            for (int column = 0; column < this.tileField.getColCount(); column++) {
                Tile tile = this.tileField.getTile(row, column);
                if (tile.isOpen()) {
                    for (int i = 0; i < colWidth; i++) {
                        System.out.print(asciiString.charAt(column * colWidth + i + row * this.tileField.getColCount() * colWidth));
                    }
                } else {
                    for (int i = 0; i < colWidth; i++) {
                        System.out.print('▓');
                    }
                }
            }
        }
        System.out.println();
    }

    private void handleInput() {
        boolean tileOpened = false;
        while (!tileOpened) {
            System.out.print("Enter command (X - exit, A1 - open, word - guess): ");
            String line = scanner.nextLine().toLowerCase();

            if ("X".equals(line)) {
                System.exit(0);
            }

            if (tileField.guessPictureName(line)) {
                for (int row = 0; row < tileField.getRowCount(); row++) {
                    for (int col = 0; col < tileField.getColCount(); col++) {
                        tileField.openTile(row, col);
                    }
                }
                render();
                break;
            }

            Matcher matcher = COMMAND.matcher(line);
            if (matcher.matches()) {
                int row = matcher.group(1).charAt(0) - 'a';
                int col = Integer.parseInt(matcher.group(2)) - 1;

                if(row >= tileField.getRowCount() || col >= tileField.getColCount()){
                    System.out.println("Provided cordinates out of bound " + line);
                    continue;
                }

                if (!tileField.getTile(row, col).isOpen()) {
                    tileField.openTile(row, col);
                    tileOpened = true;
                } else {
                    System.out.println("Tile \"" + line + "\" is already open");
                }
            } else {
                System.out.println("Wrong guess or coordinates " + line);
            }
        }
    }

    private void generatePicture() {
        Random random = new Random();
        switch (random.nextInt(2)){
            case 0:
                this.tileField = new TileField(6, 7, "car");
                this.asciiString =
                        "       _______       " +
                        "      //  ||\\ \\      " +
                        "_____//___||_\\ \\___  " +
                        ")  _          _    \\ " +
                        "|_/ \\________/ \\___| " +
                        "  \\_/        \\_/     ";
                this.colWidth = 3;
                break;
            case 1:
                this.tileField = new TileField(6, 3, "house");
                this.asciiString =
                        "   ____||____  " +
                        "  ///////////\\ " +
                        " ///////////  \\" +
                        " |    _    |  |" +
                        " |[] | | []|[]|" +
                        " |   | |   |  |";
                this.colWidth = 5;
                break;
            default:
                throw new IllegalArgumentException("Wrong product of Random");
        }
    }

    public boolean isPlayAgain() {
        return this.playAgain;
    }
}
