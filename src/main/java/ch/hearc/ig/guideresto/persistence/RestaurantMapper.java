package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.Restaurant;
import jakarta.persistence.EntityManager;

import java.util.Set;
import java.util.stream.Collectors;

public class RestaurantMapper extends AbstractMapper<Restaurant> {
    public RestaurantMapper(Class<Restaurant> type, EntityManager em) {
        super(type, em);
    }

    public Set<Restaurant> findByCity(String cityName) {
        return em.createNamedQuery("Restaurant.findByCity", Restaurant.class)
                .setParameter("cityName", cityName)
                .getResultStream()
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Restaurant> findByType(String label) {
        return em.createNamedQuery("Restaurant.findByType", Restaurant.class)
                .setParameter("label", label)
                .getResultStream()
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Restaurant> findByName(String name) {
        return em.createNamedQuery("Restaurant.findByName", Restaurant.class)
                .setParameter("name", name)
                .getResultStream()
                .collect(Collectors.toUnmodifiableSet());
    }

    public Restaurant findByExactName(String name) {
        return em.createNamedQuery("Restaurant.findByName", Restaurant.class)
                .setParameter("name", name)
                .getSingleResult();
    }



}
