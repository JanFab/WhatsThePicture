package sk.tuke.gamestudio.service;

import org.junit.Test;
import sk.tuke.gamestudio.entity.Comment;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CommentServiceTest {
    private CommentService createService() {
        //return new CommentServiceFile();
        return new CommentServiceJDBC();
    }

    @Test
    public void testAddComment() {
        CommentService service = createService();
        service.reset();
        Date date = new Date();
        service.addComment(new Comment("GuessThePicture", "Jaro", "Bardzo dobra hra!", date));

        List<Comment> comments = service.getComments("GuessThePicture");

        assertEquals(1, comments.size());
        assertEquals("GuessThePicture", comments.get(0).getGame());
        assertEquals("Jaro", comments.get(0).getPlayer());
        assertEquals("Bardzo dobra hra!", comments.get(0).getComment());
        assertEquals(date, comments.get(0).getCommentedOn());
    }

    @Test
    public void testGetComments() {
        CommentService service = createService();
        service.reset();
        service.addComment(new Comment("GuessThePicture", "Jaro", "Bardzo dobra hra!", new Date()));
        service.addComment(new Comment("GuessThePicture", "Jaro", "Bardzo dobra hra!", new Date()));
        service.addComment(new Comment("GuessThePicture", "Jaro", "Bardzo dobra hra!", new Date()));
        service.addComment(new Comment("GuessThePicture", "Jaro", "Bardzo dobra hra!", new Date()));

        List<Comment> comments = service.getComments("GuessThePicture");
        assertEquals(4, comments.size());
    }


}
