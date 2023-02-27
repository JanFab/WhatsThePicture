package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.guessthepicture.core.FieldState;
import sk.tuke.gamestudio.game.guessthepicture.core.Tile;
import sk.tuke.gamestudio.game.guessthepicture.core.TileField;
import sk.tuke.gamestudio.service.CommentService;
import sk.tuke.gamestudio.service.RatingService;
import sk.tuke.gamestudio.service.ScoreService;

import java.awt.*;
import java.util.Date;
import java.util.Random;

@Controller
@RequestMapping("/GuessThePicture")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class GuessThePictureController {

    @Autowired
    private UserController userController;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RatingService ratingService;


    public static final String GAME_NAME = "GuessThePicture";
    private String imageName = "name";
    private Integer diff = 1;


    private Integer rowCountTmp = 5;
    private Integer colCountTmp = 5;

    private Integer rowCount = rowCountTmp * diff;
    private Integer colCount = colCountTmp * diff;


    private TileField field;

    @RequestMapping
    public String GuessThePicture(@RequestParam(required = false) String row, @RequestParam(required = false) String column, Model model) {
        if (field == null) {
            newGame();
        }
        if (row != null && column != null) {
            processCommand(row, column);
        }
        fillModelGame(model);
        return "GuessThePicture";
    }

    @RequestMapping("/statistics")
    public String statistics(Model model) {
        fillModelStatistics(model);
        return "statistics";
    }

    @RequestMapping("/new")
    public String newGame(Model model) {
        newGame();
        fillModelGame(model);
        return "redirect:/GuessThePicture";
    }

    @RequestMapping("/guess")
    public String guess(@RequestParam(required = false) String guess, Model model) {
        field.guessPictureName(guess);
        if (field.getState() == FieldState.SOLVED && userController.isLogged())
            scoreService.addScore(new Score(GAME_NAME, userController.getLoggedUser().getLogin(), field.getScore(), new Date()));
        fillModelGame(model);
        return "redirect:/GuessThePicture";
    }

    @RequestMapping("/comment")
    public String handleComment(@RequestParam String comment) {
        Comment c = new Comment(GAME_NAME, userController.getLoggedUser().getLogin(), comment, new Date());
        commentService.addComment(c);
        return "redirect:/GuessThePicture/statistics";
    }

    @RequestMapping("/rate")
    public String handleRating(@RequestParam String rating) {
        Rating r = new Rating(GAME_NAME, userController.getLoggedUser().getLogin(), Integer.parseInt(rating), new Date());
        ratingService.setRating(r);
        return "redirect:/GuessThePicture/statistics";
    }

    @RequestMapping("/difficulty")
    public String setDifficulty(@RequestParam String difficulty, Model model){
        this.diff = Integer.parseInt(difficulty);
        rowCount = rowCountTmp * diff;
        colCount = colCountTmp * diff;
        newGame();
        fillModelGame(model);
        return "redirect:/GuessThePicture";
    }

    private void fillModelGame(Model model) {
        model.addAttribute("gamestate", getGamestateString());
        model.addAttribute("currentScore", field.getScore());
        model.addAttribute("field", getHtmlField());
    }

    private void fillModelStatistics(Model model) {
        model.addAttribute("scores", scoreService.getTopScores("GuessThePicture"));
        model.addAttribute("comments", commentService.getComments("GuessThePicture"));
        model.addAttribute("rating", ratingService.getAverageRating("GuessThePicture"));
        if (userController.isLogged()) {
            model.addAttribute("personalRating", ratingService.getRating(GAME_NAME, userController.getLoggedUser().getLogin()));
        }
    }

    private void processCommand(String row, String column) {
        try {
            if (field.getState() == FieldState.PLAYING) {
                field.openTile(Integer.parseInt(row), Integer.parseInt(column));
            }

        } catch (Exception e) {
            //Jano: Tato vynimka znamena, ze parametre neprisli
            e.printStackTrace();
        }
    }


    public String getHtmlField() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<table class=\"tableField\" style=\"background-image: url('images/%s.png')\">\n", imageName));

        for (int row = 0; row < field.getRowCount(); row++) {
            sb.append("<tr class=\"tableRow\">\n");
            for (int column = 0; column < field.getColCount(); column++) {
                Tile tile = field.getTile(row, column);
                sb.append("<td class=\"tableData\">");
                sb.append(String.format("<a class='tableData-link' href='/GuessThePicture?row=%d&column=%d'>", row, column));
                if (!tile.isOpen()) {
                    sb.append("<img class='tableData-image' src='images/qMark.png'/>");
                }
                sb.append("</a>\n");
                sb.append("</td>\n");
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n");
        return sb.toString();
    }

    public void newGame() {
        Random random = new Random();
        switch (random.nextInt(9)) {
            case 0:
                imageName = "sun";
                break;
            case 1:
                imageName = "mercury";
                break;
            case 2:
                imageName = "venus";
                break;
            case 3:
                imageName = "earth";
                break;
            case 4:
                imageName = "mars";
                break;
            case 5:
                imageName = "jupiter";
                break;
            case 6:
                imageName = "saturn";
                break;
            case 7:
                imageName = "uranus";
                break;
            case 8:
                imageName = "neptune";
                break;
            default:
                imageName = "fire";
                break;
        }
        field = new TileField(rowCount, colCount, imageName);
    }

    public String getGamestateString() {
        switch (field.getState()) {
            case PLAYING:
                return "Game is rolling on!!";
            case SOLVED:
                for (int i = 0; i < rowCount; i++) {
                    for (int j = 0; j < colCount; j++) {
                        field.openTile(i, j);
                    }
                }
                return "Congratulation, you won";
            case FAILED:
                return "Sorry, you lost";
            default:
                return "Unknown state";
        }
    }
}
