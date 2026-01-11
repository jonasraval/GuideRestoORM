package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.City;
import ch.hearc.ig.guideresto.business.Restaurant;
import ch.hearc.ig.guideresto.business.RestaurantType;
import ch.hearc.ig.guideresto.persistence.CityMapper;
import ch.hearc.ig.guideresto.persistence.RestaurantMapper;
import ch.hearc.ig.guideresto.persistence.RestaurantTypeMapper;
import ch.hearc.ig.guideresto.persistence.jpa.JpaUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;

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

    /**
     * Récupère l'ensemble des restaurants
     *
     * @return Un ensemble de tous les restaurants
     */
    @Override
    public Set<Restaurant> getAllRestaurants() {
        return restaurantMapper.findAll();
    }

    /**
     * Recherche des restaurants par nom (recherche partielle)
     *
     * @param research Le terme de recherche
     * @return Un ensemble de restaurants dont le nom correspond à la recherche
     */
    @Override
    public Set<Restaurant> getRestaurantsByName(String research) {
        return restaurantMapper.findByName(research);
    }

    /**
     * Recherche un restaurant par son nom exact
     *
     * @param name Le nom exact du restaurant
     * @return Le restaurant correspondant ou null si non trouvé
     */
    @Override
    public Restaurant getRestaurantByExactName(String name) {
        return restaurantMapper.findByExactName(name);
    }

    /**
     * Recherche des restaurants par ville
     *
     * @param research Le nom de la ville recherchée
     * @return Un ensemble de restaurants situés dans la ville correspondante
     */
    @Override
    public Set<Restaurant> getRestaurantsByCity(String research) {
        return restaurantMapper.findByCity(research);
    }

    /**
     * Récupère les restaurants d'un type donné
     *
     * @param restaurantType Le type de restaurant
     * @return Un ensemble de restaurants du type spécifié
     */
    @Override
    public Set<Restaurant> getRestaurantsByType(RestaurantType restaurantType) {
        return restaurantMapper.findByType(restaurantType.getLabel());
    }

    /**
     * Récupère l'ensemble des types de restaurants disponibles
     *
     * @return Un ensemble de tous les types de restaurants
     */
    @Override
    public Set<RestaurantType> getAllRestaurantsTypes() {
        return restaurantTypeMapper.findAll();
    }

    /**
     * Recherche un type de restaurant par son libellé
     *
     * @param label Le libellé du type de restaurant
     * @return Le type de restaurant correspondant ou null si non trouvé
     */
    @Override
    public RestaurantType getRestaurantTypeByLabel(String label) {
        return restaurantTypeMapper.findByLabel(label);
    }

    /**
     * Récupère l'ensemble des villes
     *
     * @return Un ensemble de toutes les villes
     */
    @Override
    public Set<City> getAllCities() {
        return cityMapper.findAll();
    }

    // ------------------- WRITE (Transaction) -------------------

    /**
     * Crée un nouveau restaurant dans la base de données
     *
     * @param id L'identifiant du restaurant (peut être null pour génération automatique)
     * @param name Le nom du restaurant
     * @param description La description du restaurant
     * @param website Le site web du restaurant
     * @param street La rue/adresse du restaurant
     * @param city La ville où se situe le restaurant
     * @param restaurantType Le type de restaurant
     * @return Le restaurant créé
     * @throws Exception Si une erreur survient lors de la transaction
     * @throws IllegalArgumentException Si le nom est vide, la ville ou le type est null
     */
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
            City managedCity = em.merge(city);

            RestaurantType managedType = em.merge(restaurantType);

            Restaurant newRestaurant = new Restaurant(null, name, description, website, street, managedCity, managedType);
            em.persist(newRestaurant);
            return newRestaurant;
        });
    }

    /**
     * Crée une nouvelle ville ou retourne la ville existante si elle existe déjà
     *
     * @param zipCode Le code postal de la ville
     * @param cityName Le nom de la ville
     * @return La ville créée ou existante
     * @throws Exception Si une erreur survient lors de la transaction
     * @throws IllegalArgumentException Si le code postal ou le nom est vide
     */
    @Override
    public City createCity(String zipCode, String cityName) throws Exception {

        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Le code postal ne peut pas être vide");
        }
        if (cityName == null || cityName.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la ville ne peut pas être vide");
        }

        // vérifier si une ville avec ce ZIP Code existe déjà - réutiliser
        for (City city : getAllCities()) {
            if (city.getZipCode().equals(zipCode)) {
                return city;
            }
        }

        // ville n'existe pas encore - création
        return JpaUtils.inTransactionWithResult(em -> {
            City newCity = new City(zipCode, cityName);
            em.persist(newCity);

            return newCity;
        });
    }

    /**
     * Met à jour un restaurant existant
     *
     * @param restaurant Le restaurant à mettre à jour (doit avoir un ID)
     * @throws Exception Si une erreur survient lors de la transaction
     * @throws IllegalArgumentException Si le restaurant est null ou n'a pas d'ID
     * @throws OptimisticLockException en cas de problèmes de concurrence
     */
    @Override
    public void updateRestaurant(Restaurant restaurant) throws Exception {
        try {
            if (restaurant == null || restaurant.getId() == null) {
                throw new IllegalArgumentException("Le restaurant doit avoir un ID pour être mis à jour");
            }
            JpaUtils.inTransaction(em -> {
                Restaurant managedRestaurant = em.getReference(restaurant.getClass(), restaurant.getId());
                em.persist(managedRestaurant);
            });
        } catch (OptimisticLockException e) {
            throw new Exception("Le restaurant n'a pas pu être mis à jour car quelqu'un d'autre le modifie.");
        } catch (Exception e) {
            throw new Exception("Le restaurant n'a pas pu être mis à jour.");
        }

    }

    /**
     * Supprime un restaurant de la base de données
     *
     * @param restaurant Le restaurant à supprimer (doit avoir un ID)
     * @throws Exception Si une erreur survient lors de la transaction
     * @throws IllegalArgumentException Si le restaurant est null ou n'a pas d'ID
     * @throws OptimisticLockException en cas de problèmes de concurrence
     */
    @Override
    public void deleteRestaurant(Restaurant restaurant) throws Exception {
        try {
            if (restaurant == null || restaurant.getId() == null) {
                throw new IllegalArgumentException("Le restaurant doit avoir un ID pour être supprimé");
            }

            JpaUtils.inTransaction(em -> {
                Restaurant managedRestaurant =  em.getReference(restaurant.getClass(), restaurant.getId());
                restaurantMapper.delete(managedRestaurant);
            });
        } catch (OptimisticLockException e) {
            throw new Exception("Le restaurant n'a pas pu être mis à jour car quelqu'un d'autre le modifie.");
        }
    }

    /**
     * Modifie l'adresse d'un restaurant en changeant sa ville.
     * Gère les relations bidirectionnelles entre le restaurant et les villes.
     *
     * @param restaurant Le restaurant dont l'adresse doit être modifiée
     * @param newCity La nouvelle ville du restaurant
     * @throws Exception Si une erreur survient lors de la transaction
     * @throws OptimisticLockException en cas de problèmes de concurrence
     */
    @Override
    public void editRestaurantAddress(Restaurant restaurant, City newCity) throws Exception {
        try {
            JpaUtils.inTransaction(em -> {
                Restaurant managedRestaurant = em.getReference(Restaurant.class, restaurant.getId());

                City managedNewCity;
                if (newCity.getId() == null) {
                    em.persist(newCity);
                    managedNewCity = newCity;
                } else {
                    managedNewCity = em.getReference(City.class, newCity.getId());
                    if (managedNewCity == null) {
                        throw new IllegalArgumentException("La ville n'existe pas en base !");
                    }
                }

                // Récupérer l'ancienne ville managée
                City oldCity = managedRestaurant.getAddress().getCity();

                // Mettre à jour les relations bidirectionnelles
                if (oldCity != null) {
                    oldCity.getRestaurants().remove(managedRestaurant);
                }

                managedRestaurant.getAddress().setCity(managedNewCity);
                managedNewCity.getRestaurants().add(managedRestaurant);
            });
        } catch (OptimisticLockException ex) {
            throw new OptimisticLockException("Erreur lors de la modification du restaurant - Un autre utilisateur modifie ce restaurant.");
        } catch (Exception ex) {
            throw new Exception("Erreur lors de la modification du restaurant.");
        }
    }

    /**
     * Modifie le type d'un restaurant
     *
     * @param restaurant Le restaurant dont le type doit être modifié
     * @param newType Le nouveau type de restaurant
     * @throws Exception Si une erreur survient lors de la transaction
     * @throws OptimisticLockException en cas de problèmes de concurrence
     */
    @Override
    public void editRestaurantType(Restaurant restaurant, RestaurantType newType) throws Exception {
        try {
            JpaUtils.inTransaction(em -> {
                Restaurant managedRestaurant = em.getReference(Restaurant.class, restaurant.getId());
                if (managedRestaurant == null) {
                    throw new IllegalArgumentException("Restaurant introuvable en base !");
                }

                RestaurantType managedType;
                if (newType.getId() == null) {
                    em.persist(newType);
                    managedType = newType;
                } else {
                    managedType = em.getReference(RestaurantType.class, newType.getId());
                    if (managedType == null) {
                        throw new IllegalArgumentException("Type de restaurant introuvable en base !");
                    }
                }
                managedRestaurant.setType(managedType);
            });
        } catch (OptimisticLockException ex) {
            throw new Exception("Erreur lors de la modification du restaurant - Un autre utilisateur modifie ce restaurant.");
        } catch (Exception ex) {
            throw new Exception("Erreur lors de la modification du restaurant.");
        }
    }

}
