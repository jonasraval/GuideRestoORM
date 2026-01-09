package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class BasicEvaluationMapper extends AbstractMapper<BasicEvaluation>{

    public BasicEvaluationMapper(Class<BasicEvaluation> type, EntityManager em) {
        super(type, em);
    }


    public Long countLikesForRestaurant(Integer restaurantId){
        try {
            return em.createNamedQuery("BasicEvaluation.countLikesForRestaurant",  Long.class)
                    .setParameter("restaurantId", restaurantId)
                    .setParameter("like", true)
                    .getSingleResult();
        } catch (NoResultException ne) {
            return null;
        }
    }
}
