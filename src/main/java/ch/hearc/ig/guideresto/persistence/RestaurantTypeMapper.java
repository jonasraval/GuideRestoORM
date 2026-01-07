package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.RestaurantType;
import jakarta.persistence.EntityManager;


public class RestaurantTypeMapper extends AbstractMapper<RestaurantType> {

    public RestaurantTypeMapper(Class<RestaurantType> type, EntityManager em) {
        super(type, em);
    }

    public RestaurantType findByLabel(String label) {
        return em.createNamedQuery("RestaurantType.findByLabel", RestaurantType.class)
                .setParameter("label", label)
                .getSingleResult();
    }

}
