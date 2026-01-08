package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import jakarta.persistence.EntityManager;

public class BasicEvaluationMapper extends AbstractMapper<BasicEvaluation>{

    public BasicEvaluationMapper(Class<BasicEvaluation> type, EntityManager em) {
        super(type, em);
    }


    public Long countLikesForRestaurant(Integer restaurantId){
        return em.createNamedQuery("BasicEvaluation.countLikesForRestaurant",  Long.class)
                .setParameter("restaurantId", restaurantId)
                .setParameter("like", true)
                .getSingleResult();

    }
}
