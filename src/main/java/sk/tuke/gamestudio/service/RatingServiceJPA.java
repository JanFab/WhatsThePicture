package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class RatingServiceJPA implements RatingService{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        entityManager.persist(rating);
    }

    @Override
    public double getAverageRating(String game) throws RatingException {
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@" + entityManager.createNamedQuery("Rating.getAverageRating").setParameter("game", game).getSingleResult());
        return (double) entityManager.createNamedQuery("Rating.getAverageRating").setParameter("game", game).getSingleResult();
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        return (int) entityManager.createNamedQuery("Rating.getRating").setParameter("game", game).setParameter("player", player).getSingleResult();
    }

    @Override
    public void reset() throws RatingException {

    }
}
