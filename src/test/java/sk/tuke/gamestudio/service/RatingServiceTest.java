package sk.tuke.gamestudio.service;

import org.junit.Test;
import sk.tuke.gamestudio.entity.Rating;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class RatingServiceTest {
    private RatingService createService() {
        //return new CommentServiceFile();
        return new RatingServiceJDBC();
    }

    @Test
    public void testSetRating() {

        RatingService service = createService();
        service.reset();
        Date date = new Date();
        service.setRating(new Rating("guess", "Jaro", 2, date));

        int rating = service.getRating("guess", "Jaro");

        assertEquals(2, rating);
    }

    @Test
    public void testGetAverageRating() {
        RatingService service = createService();
        service.reset();

        Date date = new Date();
        service.setRating(new Rating("guess", "Jaro", 2, date));
        service.setRating(new Rating("guess", "Fero", 3, date));
        service.setRating(new Rating("guess", "Miro", 4, date));

        int averageRating = service.getAverageRating("guess");

        assertEquals(3, averageRating);
    }

    @Test
    public void testGetRating() {
        RatingService service = createService();
        service.reset();

        Date date = new Date();
        service.setRating(new Rating("guess", "Jaro", 2, date));

        assertEquals(2, service.getRating("guess", "Jaro"));
    }

}