package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Date;

@Transactional
public class RatingServiceJPA implements RatingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
//        entityManager.persist(rating);
        entityManager.createNativeQuery("INSERT INTO rating(player,game, rated_on, rating) VALUES(?1,?2,?3, ?4) " +
                "ON CONFLICT (player,game) DO UPDATE set rating = ?4, rated_on = ?3").setParameter(1, rating.getPlayer())
                .setParameter(2, rating.getGame()).setParameter(3, rating.getRatedOn())
                .setParameter(4, rating.getRating()).executeUpdate();
    }

    @Override
    public double getAverageRating(String game) throws RatingException {
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@" + entityManager.createNamedQuery("Rating.getAverageRating").setParameter("game", game).getSingleResult());

        try {
            return (double) entityManager.createNamedQuery("Rating.getAverageRating").setParameter("game", game).getSingleResult();
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        return -1;
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try {
            return (int) entityManager.createNamedQuery("Rating.getRating").setParameter("game", game).setParameter("player", player).getSingleResult();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return -1;
    }

    @Override
    public void reset() throws RatingException {

    }
}
