package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.business.RestaurantType;
import jakarta.persistence.EntityManager;

import java.util.Set;
import java.util.stream.Collectors;

public class RestaurantMapper extends AbstractMapper<Restaurant> {
    protected RestaurantMapper(Class<Restaurant> type, EntityManager em) {
        super(type, em);
    }

    public Set<Restaurant> findByCity(City city) {
        return em.createNamedQuery("Restaurant.findByCity", Restaurant.class)
                .setParameter("city", city)
                .getResultStream()
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Restaurant> findByType(RestaurantType type) {
        return em.createNamedQuery("Restaurant.findByType", Restaurant.class)
                .setParameter("type", type)
                .getResultStream()
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Restaurant> findByName(String name) {
        return em.createNamedQuery("Restaurant.findByName", Restaurant.class)
                .setParameter("name", name)
                .getResultStream()
                .collect(Collectors.toUnmodifiableSet());
    }


}
