package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.business.RestaurantType;
import jakarta.persistence.EntityManager;

import java.util.List;

public class RestaurantMapper extends AbstractMapper<Restaurant> {
    public RestaurantMapper(Class<Restaurant> type, EntityManager em) {
        super(type, em);
    }

    public List<Restaurant> findByCity(City city) {
        return em.createNamedQuery("Restaurant.findByCity", Restaurant.class)
                .setParameter("city", city)
                .getResultList();
    }

    public List<Restaurant> findByType(RestaurantType type) {
        return em.createNamedQuery("Restaurant.findByType", Restaurant.class)
                .setParameter("type", type)
                .getResultList();
    }

    public List<Restaurant> findByName(String name) {
        return em.createNamedQuery("Restaurant.findByName", Restaurant.class)
                .setParameter("name", name)
                .getResultList();
    }


}
