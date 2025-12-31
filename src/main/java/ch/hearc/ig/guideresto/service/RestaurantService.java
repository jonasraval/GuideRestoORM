package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.RestaurantType;
import ch.hearc.ig.guideresto.persistence.CityMapper;
import ch.hearc.ig.guideresto.persistence.RestaurantMapper;
import ch.hearc.ig.guideresto.persistence.RestaurantTypeMapper;
import ch.hearc.ig.guideresto.persistence.jpa.JpaUtils;
import jakarta.persistence.EntityManager;

import java.util.Set;

public class RestaurantService implements IRestaurantService {

    private final RestaurantMapper restaurantMapper;
    private final RestaurantTypeMapper restaurantTypeMapper;
    private final CityMapper cityMapper;

    public RestaurantService(RestaurantMapper restaurantMapper,
                             RestaurantTypeMapper restaurantTypeMapper,
                             CityMapper cityMapper) {
        this.restaurantMapper = restaurantMapper;
        this.restaurantTypeMapper = restaurantTypeMapper;
        this.cityMapper = cityMapper;
    }


    // ------------------- READ (pas de transaction) -------------------

    @Override
    public Set<Restaurant> getAllRestaurants() {
        return restaurantMapper.findAll();
    }

    @Override
    public Set<Restaurant> getRestaurantsByName(String research) {
        return restaurantMapper.findByName(research);
    }

    @Override
    public Restaurant getRestaurantByExactName(String name) {
        return null;
    }

    @Override
    public Set<Restaurant> getRestaurantsByCity(String research) {
        //A faire
        return Set.of();
    }

    @Override
    public Set<Restaurant> getRestaurantsByType(RestaurantType restaurantType) {
        return restaurantMapper.findByType(restaurantType);
    }

    @Override
    public Set<RestaurantType> getAllRestaurantsTypes() {
        return restaurantTypeMapper.findAll();
    }

    @Override
    public RestaurantType getRestaurantTypeByLabel(String label) {
        //A faire
        return null;
    }

    @Override
    public Set<City> getAllCities() {
        return cityMapper.findAll();
    }

    // ------------------- WRITE (Transaction) -------------------

    @Override
    public Restaurant createRestaurant(Integer id, String name, String description, String website, String street, City city, RestaurantType restaurantType) throws Exception {
        return null;
    }

    @Override
    public void updateRestaurant(Restaurant restaurant) throws Exception {
        JpaUtils.inTransaction(em ->{
            Restaurant managed = managed(em, restaurant, Restaurant.class);
            restaurantMapper.update(managed);
        });

    }

    @Override
    public void deleteRestaurant(Restaurant restaurant) throws Exception {

    }

    @Override
    public void editRestaurantAddress(Restaurant restaurant, City newCity) {

    }

    @Override
    public void editRestaurantType(Restaurant restaurant, RestaurantType newType) {

    }

    @Override
    public City createCity(String ZipCode, String cityName) throws Exception {
        return null;
    }

    private <T> T managed(EntityManager em, T entity, Class<T> clazz) {
        if (entity == null) return null;
        return em.contains(entity) ? entity : em.merge(entity);
    }

}
