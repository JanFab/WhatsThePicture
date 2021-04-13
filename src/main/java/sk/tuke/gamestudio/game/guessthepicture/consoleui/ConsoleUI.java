package sk.tuke.gamestudio.game.guessthepicture.consoleui;

import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.guessthepicture.core.FieldState;
import sk.tuke.gamestudio.game.guessthepicture.core.Tile;
import sk.tuke.gamestudio.game.guessthepicture.core.TileField;
import sk.tuke.gamestudio.service.*;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsoleUI {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";

    private TileField tileField;
    private String asciiString;
    private int colWidth;
    private final Scanner scanner = new Scanner(System.in);
    public static final String GAME_NAME = "GuessThePicture";
    private final Pattern COMMAND = Pattern.compile("([a-z])([1-9])");
    private String playerName;
    private String playerComment;
    private int playerRating;

//    private ScoreService scoreService = new ScoreServiceJDBC();
//    private CommentService commentService = new CommentServiceJDBC();
//    private RatingService ratingService = new RatingServiceJDBC();

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;



    public void play() {
        intro();

        generatePicture();
        /*Game loop*/
        do {
            renderField();
            handleInput();
        } while (tileField.getState() == FieldState.PLAYING);

        this.scoreService.addScore(new Score(GAME_NAME, playerName, tileField.getScore(), new Date()));
        outro();
    }

    private void intro() {
        System.out.println(ANSI_GREEN_BACKGROUND + "\n" + ANSI_RESET + ANSI_BLACK_BACKGROUND + ANSI_RED + "\n" +
                "   ___                  _____ _          ___ _    _                \n" +
                "  / __|_  _ ___ ______ |_   _| |_  ___  | _ (_)__| |_ _  _ _ _ ___ \n" +
                " | (_ | || / -_|_-<_-<   | | | ' \\/ -_) |  _/ / _|  _| || | '_/ -_)\n" +
                "  \\___|\\_,_\\___/__/__/   |_| |_||_\\___| |_| |_\\__|\\__|\\_,_|_| \\___|\n");
        System.out.println(ANSI_YELLOW + " Welcome to the game! Guess whats in the picture");
        System.out.println(" Type \"h\" for help and more info about controls.\n");

        System.out.print(ANSI_WHITE + "Enter player name: ");
        this.playerName = scanner.nextLine();
    }

    private void renderField() {
        System.out.println();
        System.out.println(ANSI_BLUE + "Player: " + ANSI_WHITE + playerName + ANSI_BLUE + " Score: " + ANSI_WHITE + tileField.getScore());
        System.out.print(ANSI_WHITE + "Guess the word: " + ANSI_RED);

        for (int i = 0; i < tileField.getPictureName().length(); i++) {
            System.out.print("_ ");
        }
        System.out.println();

        System.out.print("  ");
        for (int column = 0; column < this.tileField.getColCount(); column++) {
            for (int i = 0; i < this.colWidth / 2; i++) {
                System.out.print(" ");
            }
            System.out.print(ANSI_GREEN + (column + 1));
            for (int i = 0; i < this.colWidth / 2; i++) {
                System.out.print(" ");
            }
        }
        System.out.println();
        for (int row = 0; row < this.tileField.getRowCount(); row++) {
            System.out.print(ANSI_GREEN + (char) (row + 'A') + " " + ANSI_WHITE);
            for (int column = 0; column < this.tileField.getColCount(); column++) {
                Tile tile = this.tileField.getTile(row, column);
                if (tile.isOpen()) {
                    for (int i = 0; i < colWidth; i++) {
                        System.out.print(ANSI_BLUE + asciiString.charAt(column * colWidth + i + row * this.tileField.getColCount() * colWidth) + ANSI_WHITE);
                    }
                } else {
                    for (int i = 0; i < colWidth; i++) {
                        System.out.print('▓');
                    }
                }
            }
            System.out.println();
        }
    }

    private void outro() {
        gameResult();
        printOutroHelp();
        boolean quit = false;
        while (!quit) {
            System.out.print(ANSI_WHITE + "Enter command: ");
            String input = scanner.nextLine().toLowerCase();
            if ("h".equals(input)) {
                printOutroHelp();
            }
            if ("c".equals(input)) {
                addComment();
            }
            if ("sc".equals(input)) {
                printComments();
            }
            if ("r".equals(input)) {
                addRating();
            }
            if ("sr".equals(input)) {
                showRating();
            }
            if ("s".equals(input)) {
                printTopScores();
            }
            if ("q".equals(input)) {
                quit = true;
            }
            if ("p".equals(input)) {
                this.play();
                quit = true;
            }
        }
    }

    private void handleInput() {
        boolean tileOpened = false;
        while (!tileOpened) {
            System.out.print(ANSI_WHITE + "Enter command: ");
            String line = scanner.nextLine().toLowerCase();

            if ("x".equals(line)) {
                System.exit(0);
            }
            if ("i".equals(line)) {
                System.out.println(ANSI_BLUE + ">>> Jan Fabian 2021 --- jan.fabian@student.tuke.sk <<<");
                continue;
            }
            if ("h".equals(line)) {
                printInputHelp();
                continue;
            }
            if ("r".equals(line)) {
                printTopScores();
                continue;
            }
            if (tileField.guessPictureName(line)) {
                break;
            }

            Matcher matcher = COMMAND.matcher(line);
            if (matcher.matches()) {
                int row = matcher.group(1).charAt(0) - 'a';
                int col = Integer.parseInt(matcher.group(2)) - 1;

                if (row >= tileField.getRowCount() || col >= tileField.getColCount()) {
                    System.out.println(ANSI_RED + "Provided coordinates are out of bound " + line);
                    continue;
                }

                if (!tileField.getTile(row, col).isOpen()) {
                    tileField.openTile(row, col);
                    tileOpened = true;
                } else {
                    System.out.println(ANSI_RED + "Tile \"" + line + "\" is already open");
                }
            } else {
                System.out.println(ANSI_RED + "Wrong guess or coordinates: \"" + line + "\"");
            }
        }
    }

    private void printTopScores() {
        List<Score> scores = scoreService.getTopScores(GAME_NAME);
        int i = 1;
        for (Score score : scores) {
            System.out.print(ANSI_YELLOW + i + ".");
            System.out.print(ANSI_BLUE + "Player: ");
            System.out.print(ANSI_WHITE + score.getPlayer());
            System.out.print(ANSI_BLUE + "  Score: ");
            System.out.println(ANSI_WHITE + score.getPoints() + "pt");
            i++;
        }
    }

    private void printComments() {
        List<Comment> comments = commentService.getComments(GAME_NAME);
        for (Comment comment : comments) {
            System.out.print(ANSI_YELLOW + comment.getCommentedOn() + ": ");
            System.out.print(ANSI_BLUE + "Player: ");
            System.out.print(ANSI_WHITE + comment.getPlayer());
            System.out.print(ANSI_BLUE + "  Comment: ");
            System.out.println(ANSI_WHITE + comment.getComment());
        }
    }

    private void addComment() {
        System.out.print(ANSI_PURPLE + "Write comment: ");
        playerComment = scanner.nextLine();
        commentService.addComment(new Comment(GAME_NAME, playerName, playerComment, new Date()));
    }

    private void addRating() {
        do {
            System.out.print(ANSI_PURPLE + "Give rating (1 = the worst, 5 = the best): ");
            while (!scanner.hasNextInt()) {
                System.out.print(ANSI_RED + "Only numbers are accepted! Enter again: ");
                scanner.next();
            }
            playerRating = scanner.nextInt();
        } while (playerRating > 5 || playerRating < 0);
        ratingService.setRating(new Rating(GAME_NAME, playerName, playerRating, new Date()));
    }

    private void showRating() {
        System.out.println(ANSI_WHITE + "Game rating: " + ANSI_RED + ratingService.getAverageRating(GAME_NAME));
    }

    private void printOutroHelp() {
        System.out.println(ANSI_YELLOW + "Specify your next step:");
        System.out.println("\t-> 'h'  for help");
        System.out.println("\t-> 'c'  to leave comment");
        System.out.println("\t-> 'sc' to show comments");
        System.out.println("\t-> 'r'  to give rating");
        System.out.println("\t-> 'sr' to show game rating");
        System.out.println("\t-> 's'  to show score");
        System.out.println("\t-> 'p'  to play again");
        System.out.println("\t-> 'q'  to quit the game");
    }

    private void printInputHelp() {
        System.out.println(ANSI_YELLOW + "Type:");
        System.out.println("\t-> \"A1\", 'b2' - to uncover tile");
        System.out.println("\t-> 'i' for owner info");
        System.out.println("\t-> 'x' for exit the game");
        System.out.println("\t-> 'h' for help");
        System.out.println("\t-> \"guess\" for guessing, whats on the picture");
    }

    private void gameResult() {
        if (tileField.getState() == FieldState.SOLVED) {
            for (int row = 0; row < tileField.getRowCount(); row++) {
                System.out.print("  ");
                for (int col = 0; col < tileField.getColCount(); col++) {
                    for (int i = 0; i < colWidth; i++) {
                        System.out.print(ANSI_BLUE + asciiString.charAt(col * colWidth + i + row * this.tileField.getColCount() * colWidth) + ANSI_WHITE);
                    }
                }
                System.out.println();
            }
            System.out.println(ANSI_GREEN + "Congratulations, game solved!");
        } else {
            System.out.println(ANSI_RED + "Sorry, you failed! The word was: " + this.tileField.getPictureName());
        }
    }

    private void generatePicture() {
        Random random = new Random();
        switch (random.nextInt(2)) {
            case 0 -> {
                this.tileField = new TileField(6, 7, "car");
                this.asciiString =
                        "       _______       " +
                                "      //  ||\\ \\      " +
                                "_____//___||_\\ \\___  " +
                                ")  _          _    \\ " +
                                "|_/ \\________/ \\___| " +
                                "  \\_/        \\_/     ";
                this.colWidth = 3;
            }
            case 1 -> {
                this.tileField = new TileField(6, 3, "house");
                this.asciiString =
                        "   ____||____  " +
                                "  ///////////\\ " +
                                " ///////////  \\" +
                                " |    _    |  |" +
                                " |[] | | []|[]|" +
                                " |   | |   |  |";
                this.colWidth = 5;
            }
            default -> throw new IllegalArgumentException("Wrong product of Random");
        }
    }
}
