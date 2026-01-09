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

    public RestaurantService() {
        EntityManager em = JpaUtils.getEntityManager();
        this.restaurantMapper = new RestaurantMapper(Restaurant.class, em);
        this.restaurantTypeMapper = new RestaurantTypeMapper(RestaurantType.class, em);
        this.cityMapper = new CityMapper(City.class, em);
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
        return restaurantMapper.findByExactName(name);
    }

    @Override
    public Set<Restaurant> getRestaurantsByCity(String research) {
        return restaurantMapper.findByCity(research);
    }

    @Override
    public Set<Restaurant> getRestaurantsByType(RestaurantType restaurantType) {
        return restaurantMapper.findByType(restaurantType.getLabel());
    }

    @Override
    public Set<RestaurantType> getAllRestaurantsTypes() {
        return restaurantTypeMapper.findAll();
    }

    @Override
    public RestaurantType getRestaurantTypeByLabel(String label) {
        return restaurantTypeMapper.findByLabel(label);
    }

    @Override
    public Set<City> getAllCities() {
        return cityMapper.findAll();
    }

    // ------------------- WRITE (Transaction) -------------------

    @Override
    public Restaurant createRestaurant(Integer id, String name, String description, String website, String street, City city, RestaurantType restaurantType) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du restaurant ne peut pas être vide");
        }
        if (city == null) {
            throw new IllegalArgumentException("La ville ne peut pas être nulle");
        }
        if (restaurantType == null) {
            throw new IllegalArgumentException("Le type de restaurant ne peut pas être null");
        }
        return JpaUtils.inTransactionWithResult(em -> {
                    City managedCity = em.merge(city);  // This will handle both new and existing cities

                    RestaurantType managedType = em.merge(restaurantType);

                    Restaurant newRestaurant = new Restaurant(null, name, description, website,
                            street, managedCity, managedType);
                    em.persist(newRestaurant);
                    return newRestaurant;
                });
    }

    @Override
    public City createCity(String zipCode, String cityName) throws Exception {

        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Le code postal ne peut pas être vide");
        }
        if (cityName == null || cityName.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la ville ne peut pas être vide");
        }

        //voir s il y a pas deja une ville qui existe
        for (City city : getAllCities()) {
            if (city.getZipCode().equals(zipCode)) {
                return city; // reuse existing city
            }
        }

        //ville existe pas encore - on la crée
        return JpaUtils.inTransactionWithResult(em -> {
            City newCity = new City(zipCode, cityName);
            em.persist(newCity);

            return newCity;
        });
    }

    @Override
    public void updateRestaurant(Restaurant restaurant) throws Exception {
        if (restaurant == null || restaurant.getId() == null) {
            throw new IllegalArgumentException("Le restaurant doit avoir un ID pour être mis à jour");
        }
        JpaUtils.inTransaction(em -> {
            restaurantMapper.save(restaurant);

        });
    }

    @Override
    public void deleteRestaurant(Restaurant restaurant) throws Exception {
        if (restaurant == null || restaurant.getId() == null) {
            throw new IllegalArgumentException("Le restaurant doit avoir un ID pour être supprimé");
        }

        JpaUtils.inTransaction(em -> {
            restaurantMapper.delete(restaurant);
        });
    }

    @Override
    public void editRestaurantAddress(Restaurant restaurant, City newCity) throws Exception {
        if (newCity != null && newCity != restaurant.getAddress().getCity()) {
            JpaUtils.inTransaction(em -> {
                // Récupérer les entités managées
                Restaurant managedRestaurant = em.contains(restaurant) ?
                        restaurant : em.merge(restaurant);

                City managedNewCity = newCity.getId() == null ? cityMapper.save(newCity) : em.merge(newCity);

                // Récupérer l'ancienne ville managée
                City oldCity = managedRestaurant.getAddress().getCity();

                // Mettre à jour les relations bidirectionnelles
                if (oldCity != null) {
                    oldCity.getRestaurants().remove(managedRestaurant);
                }

                managedRestaurant.getAddress().setCity(managedNewCity);
                managedNewCity.getRestaurants().add(managedRestaurant);
            });
        }
    }

    @Override
    public void editRestaurantType(Restaurant restaurant, RestaurantType newType) throws Exception {
        JpaUtils.inTransaction(em -> {
            Restaurant managedRestaurant = em.contains(restaurant) ? restaurant : em.merge(restaurant);
            RestaurantType managedType = newType.getId() == null ? restaurantTypeMapper.save(newType) : em.merge(newType);
            managedRestaurant.setType(managedType);
        });
    }

}
