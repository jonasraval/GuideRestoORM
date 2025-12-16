package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.City;
import jakarta.persistence.EntityManager;

public class CityMapper extends AbstractMapper<City>{
    protected CityMapper(Class<City> type, EntityManager em) {
        super(type, em);
    }
}
