package ch.hearc.ig.guideresto.presentation;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.jpa.JpaUtils;
import ch.hearc.ig.guideresto.service.EvaluationService;
import ch.hearc.ig.guideresto.service.RestaurantService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;

/**
 * @author cedric.baudet
 * @author alain.matile
 */
public class Application {

    private static Scanner scanner;
    private static final Logger logger = LogManager.getLogger(Application.class);

    private static RestaurantService restaurantService;
    private static EvaluationService evaluationService;


    public Application(RestaurantService restaurantService, EvaluationService evaluationService) {
        this.restaurantService = restaurantService;
        this.evaluationService = evaluationService;
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        try {

            restaurantService = new RestaurantService();
            evaluationService = new EvaluationService();

            System.out.println("Bienvenue dans GuideResto ! Que souhaitez-vous faire ?");
            int choice;
            do {
                printMainMenu();
                choice = readInt();
                proceedMainMenu(choice);
            } while (choice != 0);
        } catch (Exception e) {
            System.err.println("Erreur au démarrage de l'application : " + e.getMessage());
            logger.error("Erreur critique dans l'application", e);
        } finally {
            // Fermeture à la FIN de l'application
            if (scanner != null) {
                scanner.close();
            }
            JpaUtils.closeEntityManager();
            JpaUtils.closeEntityManagerFactory();
            System.out.println("Application terminée proprement.");
        }
    }

    /**
     * Affichage du menu principal de l'application
     */
    private static void printMainMenu() {
        System.out.println("======================================================");
        System.out.println("Que voulez-vous faire ?");
        System.out.println("1. Afficher la liste de tous les restaurants");
        System.out.println("2. Rechercher un restaurant par son nom");
        System.out.println("3. Rechercher un restaurant par ville");
        System.out.println("4. Rechercher un restaurant par son type de cuisine");
        System.out.println("5. Saisir un nouveau restaurant");
        System.out.println("0. Quitter l'application");
    }

    /**
     * On gère le choix saisi par l'utilisateur
     *
     * @param choice Un nombre entre 0 et 5.
     */
    private static void proceedMainMenu(int choice) throws SQLException {
        switch (choice) {
            case 1:
                showRestaurantsList();
                break;
            case 2:
                searchRestaurantsByName();
                break;
            case 3:
                searchRestaurantsByCity();
                break;
            case 4:
                searchRestaurantsByType();
                break;
            case 5:
                addNewRestaurant();
                break;
            case 0:
                System.out.println("Au revoir !");
                break;
            default:
                System.out.println("Erreur : saisie incorrecte. Veuillez réessayer");
                break;
        }
    }

    /**
     * On affiche à l'utilisateur une liste de restaurants numérotés, et il doit en sélectionner un !
     *
     * @param restaurants Liste à afficher
     * @return L'instance du restaurant choisi par l'utilisateur
     */
    private static Restaurant pickRestaurant(Set<Restaurant> restaurants) {
        if (restaurants.isEmpty()) {
            System.out.println("Aucun restaurant n'a été trouvé !");
            return null;
        }

        String result;
        for (Restaurant currentRest : restaurants) {
            result = "";
            result = "\"" + result + currentRest.getName() + "\" - " + currentRest.getAddress().getStreet() + " - ";
            result = result + currentRest.getAddress().getCity().getZipCode() + " " + currentRest.getAddress().getCity().getCityName();
            System.out.println(result);
        }

        System.out.println("Veuillez saisir le nom exact du restaurant dont vous voulez voir le détail, ou appuyez sur Enter pour revenir en arrière");
        String choice = readString();

        if (choice == null || choice.trim().isEmpty()) {
            return null;  // Retour en arrière
        }

        Restaurant restaurant = restaurantService.getRestaurantByExactName(choice);

        if (restaurant == null) {
            System.out.println("Le restaurant : "+ choice +" introuvable");
        }
        return restaurant;
    }

    /**
     * Affiche la liste de tous les restaurants, sans filtre
     */
    private static void showRestaurantsList() {
        System.out.println("Liste des restaurants : ");
        Set<Restaurant> restaurants = restaurantService.getAllRestaurants();
        Restaurant restaurant = pickRestaurant(restaurants);

        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }


    /**
     * Affiche une liste de restaurants dont le nom contient une chaîne de caractères saisie par l'utilisateur
     */
    private static void searchRestaurantsByName() {
        System.out.println("Veuillez entrer une partie du nom recherché : ");
        String research = readString();

        while (research == null || research.trim().isEmpty()) {
            System.out.println("Veuillez saisir au moins un caractère");
            research = readString();
        }

        Set<Restaurant> filteredList = restaurantService.getRestaurantsByName(research);

        Restaurant restaurant = pickRestaurant(filteredList);

        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }

    /**
     * Affiche une liste de restaurants dont le nom de la ville contient une chaîne de caractères saisie par l'utilisateur
     */
    private static void searchRestaurantsByCity() {
        System.out.println("Veuillez entrer une partie du nom de la ville désirée : ");
        String research = readString();
        while (research == null || research.trim().isEmpty()){
            System.out.println("Veuillez saisir au moins un caractère");
            research = readString();
        }

        Set<Restaurant> filteredList = restaurantService.getRestaurantsByCity(research);
        Restaurant restaurant = pickRestaurant(filteredList);

        if (restaurant != null) {
            showRestaurant(restaurant);
        }
    }

    /**
     * L'utilisateur choisit une ville parmi celles présentes dans le système.
     *
     * @param cities La liste des villes à présnter à l'utilisateur
     * @return La ville sélectionnée, ou null si aucune ville n'a été choisie.
     */
    private static City pickCity(Set<City> cities) {
        if (cities == null || cities.isEmpty()){
            System.out.println("Aucune ville disponible");
            return null;
        }

        System.out.println("Voici la liste des villes possibles, veuillez entrer le NPA de la ville désirée : ");

        for (City currentCity : cities) {
            System.out.println(currentCity.getZipCode() + " " + currentCity.getCityName());
        }

        System.out.println("Entrez \"NEW\" pour créer une nouvelle ville");

        String choice = readString();

        if (choice == null || choice.trim().isEmpty()) {
            System.out.println("Aucune ville sélectionné");
            return null;
        }

        if (choice.equals("NEW")) {
            System.out.println("Veuillez entrer le NPA de la nouvelle ville : ");
            String zipCode = readString();
            while (zipCode == null || zipCode.trim().isEmpty()) {
                System.out.println("Le NPA ne peut pas être vide");
                zipCode = readString();
            }

            System.out.println("Veuillez entrer le nom de la nouvelle ville : ");
            String cityName = readString();
            while (cityName == null || cityName.trim().isEmpty()){
                System.out.println("Le nom de la ville ne peut pas être vide");
                cityName = readString();
            }

            try {
                City city = restaurantService.createCity(zipCode, cityName);
                System.out.println("Nouvelle ville ajoutée avec succès !");
                return city;
            } catch (Exception e) {
                System.out.println(" Erreur : "+e.getMessage());
                return null;
            }
        }

        City city = searchCityByZipCode(cities, choice);
        if (city == null) {
            System.out.println("Ville avec le NPA : "+ choice+" introuvable");
        }
        return city;
    }

    /**
     * L'utilisateur choisit un type de restaurant parmis ceux présents dans le système.
     *
     * @param types La liste des types de restaurant à présnter à l'utilisateur
     * @return Le type sélectionné, ou null si aucun type n'a été choisi.
     */
    private static RestaurantType pickRestaurantType(Set<RestaurantType> types) {
        System.out.println("Voici la liste des types possibles, veuillez entrer le libellé exact du type désiré : ");
        for (RestaurantType currentType : types) {
            System.out.println("\"" + currentType.getLabel() + "\" : " + currentType.getDescription());
        }
        String choice = readString();
        if (choice == null || choice.trim().isEmpty()) {
            return null;
        }

        RestaurantType type = restaurantService.getRestaurantTypeByLabel(choice);

        if (type == null) {
            System.out.println("Type "+choice+" introuvable");
        }

        return type;
    }

    /**
     * L'utilisateur commence par sélectionner un type de restaurant, puis sélectionne un des restaurants proposés s'il y en a.
     * Si l'utilisateur sélectionne un restaurant, ce dernier lui sera affiché.
     */
    private static void searchRestaurantsByType() {

        while (true) {
            RestaurantType chosenType =
                    pickRestaurantType(restaurantService.getAllRestaurantsTypes());

            if (chosenType == null) {
                System.out.println("Type invalide (libellé inconnu). Réessayez.");
                continue;
            }

            Set<Restaurant> filteredList = restaurantService.getRestaurantsByType(chosenType);

            if (filteredList == null || filteredList.isEmpty()) {
                System.out.println("Aucun restaurant trouvé pour ce type. Réessayez.");
                continue;
            }

            Restaurant restaurant = pickRestaurant(filteredList);
            if (restaurant != null) showRestaurant(restaurant);
            return;
        }
    }


    /**
     * Le programme demande les informations nécessaires à l'utilisateur puis crée un nouveau restaurant dans le système.
     */
    private static void addNewRestaurant() {
        //Affichage
        System.out.println("Vous allez ajouter un nouveau restaurant !");
        System.out.println("Quel est son nom ?");
        String name = readString();
        while (name == null || name.trim().isEmpty()){
            System.out.println("Le nom ne peut pas être vide");
            name = readString();
        }

        System.out.println("Veuillez entrer une courte description : ");
        String description = readString();
        while (description == null || description.trim().isEmpty()){
            System.out.println("La description ne peut pas être vide");
            description = readString();
        }

        System.out.println("Veuillez entrer l'adresse de son site internet : ");
        String website = readString();
        while (website == null || website.trim().isEmpty()){
            System.out.println("Le site web ne peut pas être vide");
            website = readString();
        }

        System.out.println("Rue : ");
        String street = readString();
        while (street == null || street.trim().isEmpty()){
            System.out.println("La rue ne peut pas être vide");
            street = readString();
        }


        City city = null;
        do
        {
            city = pickCity(restaurantService.getAllCities());
            if (city == null) {
                System.out.println("Vous devez sélectionner une ville");
            }
        } while (city == null);
        RestaurantType restaurantType = null;
        do
        {
            restaurantType = pickRestaurantType(restaurantService.getAllRestaurantsTypes());
            if (restaurantType == null) {
                System.out.println("Vous devez sélectionner un type de restaurant");
            }
        } while (restaurantType == null);
        try {
            Restaurant restaurant = restaurantService.createRestaurant(null, name, description, website, street, city, restaurantType);
            System.out.println("Restaurant créé avec succès");
            showRestaurant(restaurant);
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur de validation : " + e.getMessage());
        } catch (RuntimeException e){
            logger.error("Echec création restaurant : "+ e);
        } catch (Exception e) {
            System.out.println("Erreur inattendue : "+ e.getMessage());
        }
    }

    /**
     * Affiche toutes les informations du restaurant passé en paramètre, puis affiche le menu des actions disponibles sur ledit restaurant
     *
     * @param restaurant Le restaurant à afficher
     */
    private static void showRestaurant(Restaurant restaurant) {
        System.out.println("Affichage d'un restaurant : ");
        StringBuilder sb = new StringBuilder();
        sb.append(restaurant.getName()).append("\n");
        sb.append(restaurant.getDescription()).append("\n");
        sb.append(restaurant.getType().getLabel()).append("\n");
        sb.append(restaurant.getWebsite()).append("\n");
        sb.append(restaurant.getAddress().getStreet()).append(", ");
        sb.append(restaurant.getAddress().getCity().getZipCode()).append(" ").append(restaurant.getAddress().getCity().getCityName()).append("\n");

        Integer id = restaurant.getId();
        if (id != null) {
            sb.append("Nombre de likes : ")
                    .append(evaluationService.countLikesForRestaurantId(id, true))
                    .append("\n");
            sb.append("Nombre de dislikes : ")
                    .append(evaluationService.countLikesForRestaurantId(id, false))
                    .append("\n");
        } else {
            sb.append("Nombre de likes : N/A (non encore enregistré)").append("\n");
            sb.append("Nombre de dislikes : N/A").append("\n");
        }
        sb.append("\nEvaluations reçues : ").append("\n");
        String text;
        for (Evaluation currentEval : restaurant.getEvaluations()) {
            text = getCompleteEvaluationDescription(currentEval);
            if (text != null) {
                sb.append(text).append("\n");
            }
        }

        System.out.println(sb);

        int choice;
        do {
            showRestaurantMenu();
            choice = readInt();

            proceedRestaurantMenu(choice, restaurant);
        } while (choice != 0 && choice != 6);
    }

    /**
     * Retourne un String qui contient le détail complet d'une évaluation si elle est de type "CompleteEvaluation". Retourne null s'il s'agit d'une BasicEvaluation
     *
     * @param eval L'évaluation à afficher
     * @return Un String qui contient le détail complet d'une CompleteEvaluation, ou null s'il s'agit d'une BasicEvaluation
     */
    private static String getCompleteEvaluationDescription(Evaluation eval) {
        StringBuilder result = new StringBuilder();

        if (eval instanceof CompleteEvaluation) {
            CompleteEvaluation ce = (CompleteEvaluation) eval;
            result.append("Evaluation de : ").append(ce.getUsername()).append("\n");
            result.append("Commentaire : ").append(ce.getComment()).append("\n");
            for (Grade currentGrade : ce.getGrades()) {
                result.append(currentGrade.getCriteria().getName()).append(" : ").append(currentGrade.getGrade()).append("/5").append("\n");
            }
        }

        return result.toString();
    }

    /**
     * Affiche dans la console un ensemble d'actions réalisables sur le restaurant actuellement sélectionné !
     */
    private static void showRestaurantMenu() {
        System.out.println("======================================================");
        System.out.println("Que souhaitez-vous faire ?");
        System.out.println("1. J'aime ce restaurant !");
        System.out.println("2. Je n'aime pas ce restaurant !");
        System.out.println("3. Faire une évaluation complète de ce restaurant !");
        System.out.println("4. Editer ce restaurant");
        System.out.println("5. Editer l'adresse du restaurant");
        System.out.println("6. Supprimer ce restaurant");
        System.out.println("0. Revenir au menu principal");
    }

    /**
     * Traite le choix saisi par l'utilisateur
     *
     * @param choice     Un numéro d'action, entre 0 et 6. Si le numéro ne se trouve pas dans cette plage, l'application ne fait rien et va réafficher le menu complet.
     * @param restaurant L'instance du restaurant sur lequel l'action doit être réalisée
     */
    private static void proceedRestaurantMenu(int choice, Restaurant restaurant) {
        switch (choice) {
            case 1:
                addBasicEvaluation(restaurant, true);
                break;
            case 2:
                addBasicEvaluation(restaurant, false);
                break;
            case 3:
                evaluateRestaurant(restaurant);
                break;
            case 4:
                editRestaurant(restaurant);
                break;
            case 5:
                editRestaurantAddress(restaurant);
                break;
            case 6:
                deleteRestaurant(restaurant);
                break;
            case 0:
                break;
            default:
                break;
        }
    }

    /**
     * Ajoute au restaurant passé en paramètre un like ou un dislike, en fonction du second paramètre.
     * L'IP locale de l'utilisateur est enregistrée. S'il s'agissait d'une application web, il serait préférable de récupérer l'adresse IP publique de l'utilisateur.
     *
     * @param restaurant Le restaurant qui est évalué
     * @param like       Est-ce un like ou un dislike ?
     */
    private static void addBasicEvaluation(Restaurant restaurant, Boolean like) {
        String ipAddress;
        try {
            ipAddress = Inet4Address.getLocalHost().toString();
        } catch (UnknownHostException ex) {
            logger.error("Error - Couldn't retreive host IP address");
            ipAddress = "Indisponible";
        }
        try {
            evaluationService.addBasicEvaluation(restaurant, like, ipAddress);
            System.out.println("Votre vote a été pris en compte !");
        } catch (Exception ex) {
            System.out.println("Une erreur est survenue lors de l'enregistrement de votre vote. Veuillez réessayer.");
            logger.error("Failed to add basic evaluation for restaurant: " + restaurant.getName(), ex);
        }
    }

    /**
     * Crée une évaluation complète pour le restaurant. L'utilisateur doit saisir toutes les informations (dont un commentaire et quelques notes)
     *
     * @param restaurant Le restaurant à évaluer
     */
    private static void evaluateRestaurant(Restaurant restaurant) {
        try {
            System.out.println("Merci d'évaluer ce restaurant !");
            System.out.println("Quel est votre nom d'utilisateur ? ");
            String username = readString();
            while (username == null || username.trim().isEmpty()){
                System.out.println("Le nom d'utilisateur ne peut pas être vide. Veuillez recommencer");
                username = readString();
            }

            System.out.println("Quel commentaire aimeriez-vous publier ?");
            String comment = readString();
            while (comment == null || comment.trim().isEmpty()){
                System.out.println("Le commentaire ne peut pas être vide. Veuillez recommencer");
                comment = readString();
            }

            Map<EvaluationCriteria, Integer> gradesMap = new HashMap<>();
            Set<EvaluationCriteria> criteriaSet = evaluationService.getAllCriteria();

            if (criteriaSet.isEmpty()) {
                System.out.println("Aucun critère d'évaluation disponible");
            }

            System.out.println("Veuillez saisir des notes entre 1 et 5 pour les différentes catégories");

            for (EvaluationCriteria criteria : evaluationService.getAllCriteria()) {
                System.out.println(criteria.getName() + " : " + criteria.getDescription());
                Integer note = readInt();

                while (note < 1 || note > 5) {
                    System.out.println("La note pour "+ criteria.getName()+" : "+criteria.getDescription()+" doit être en 1 et 5");
                    note = readInt();
                }

                gradesMap.put(criteria, note);
            }

            evaluationService.evaluateRestaurant(restaurant, username, comment, gradesMap);
            System.out.println("Votre évaluation a bien été enregistrée, merci !");
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : "+ e.getMessage());
        } catch (Exception ex) {
            System.out.println("Une erreur est survenue lors de l'enregistrement de votre évaluation : " + ex.getMessage());
            logger.error("Erreur lors de l'évaluation d'un restaurant ", ex);
        }
    }

    /**
     * Force l'utilisateur à saisir à nouveau toutes les informations du restaurant (sauf la clé primaire) pour le mettre à jour.
     * Par soucis de simplicité, l'utilisateur doit tout resaisir.
     *
     * @param restaurant Le restaurant à modifier
     */
    private static void editRestaurant(Restaurant restaurant) {
        System.out.println("Edition d'un restaurant !");

        System.out.println("Nouveau nom : ");
        String newName = readString();
        while (newName == null || newName.trim().isEmpty()) {
            System.out.println("Le nom ne peut pas être vide");
            newName = readString();
        }
        restaurant.setName(newName);

        System.out.println("Nouvelle description : ");
        String newDescription = readString();
        while (newDescription == null || newDescription.trim().isEmpty()) {
            System.out.println("La description ne peut pas être vide");
            newDescription = readString();
        }
        restaurant.setDescription(newDescription);

        System.out.println("Nouveau site web : ");
        String newWebsite = readString();
        while (newWebsite == null || newWebsite.trim().isEmpty()){
            System.out.println("Le lien pour le site web ne peut pas être vide");
            newWebsite = readString();
        }
        restaurant.setWebsite(newWebsite);


        System.out.println("Nouveau type de restaurant : ");
        RestaurantType newType = null;
        while (newType == null){
            newType = pickRestaurantType(restaurantService.getAllRestaurantsTypes());
            if (newType == null) {
                System.out.println("Type invalide. Veuillez choisir un type de la liste");
            }
        }
        try {
            restaurantService.editRestaurantType(restaurant, newType);
            restaurantService.updateRestaurant(restaurant);
            System.out.println("Merci, le restaurant a bien été modifié !");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Permet à l'utilisateur de mettre à jour l'adresse du restaurant.
     * Par soucis de simplicité, l'utilisateur doit tout resaisir.
     *
     * @param restaurant Le restaurant dont l'adresse doit être mise à jour.
     */
    private static void editRestaurantAddress(Restaurant restaurant) {
        System.out.println("Edition de l'adresse d'un restaurant !");

        System.out.println("Nouvelle rue : ");
        String newStreet = readString();
        while (newStreet == null || newStreet.trim().isEmpty()) {
            System.out.println("La rue ne peut pas être vide");
            newStreet = readString();
        }
        restaurant.getAddress().setStreet(newStreet);

        System.out.println("Nouvelle ville : ");
        City newCity = null;
        do {
            newCity = pickCity(restaurantService.getAllCities());
            if (newCity == null) {
                System.out.println("Vous devez sélectionner une ville");
            }
        } while (newCity == null);

        try {
            if (newCity != restaurant.getAddress().getCity()) {
                restaurantService.editRestaurantAddress(restaurant, newCity);
            }
            restaurantService.updateRestaurant(restaurant);
            System.out.println("L'adresse a bien été modifiée ! Merci !");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Après confirmation par l'utilisateur, supprime complètement le restaurant et toutes ses évaluations du référentiel.
     *
     * @param restaurant Le restaurant à supprimer.
     */
    private static void deleteRestaurant(Restaurant restaurant) {
        System.out.println("Etes-vous sûr de vouloir supprimer ce restaurant ? (O/n)");
        try {
            String choice = readString();
            if (choice.equalsIgnoreCase("o")) {
                try {
                    restaurantService.deleteRestaurant(restaurant);
                    System.out.println("Le restaurant a bien été supprimé !");
                } catch (Exception ex) {
                    System.out.println("Erreur lors de la suppression du restaurant. Veuillez réessayer.");
                    logger.error("Impossible de supprimer le restaurant : " + restaurant.getName(), ex);
                }
            } else {
                System.out.println("Suppression annulée.");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    /**
     * Recherche dans le Set la ville comportant le code NPA passé en paramètre.
     * Retourne null si la ville n'est pas trouvée
     *
     * @param cities  Set de villes
     * @param zipCode NPA de la ville à rechercher
     * @return L'instance de la ville ou null si pas trouvé
     */
    private static City searchCityByZipCode(Set<City> cities, String zipCode) {
        for (City current : cities) {
            if (current.getZipCode().equalsIgnoreCase(zipCode)) {
                return current;
            }
        }
        return null;
    }

    /**
     * readInt ne repositionne pas le scanner au début d'une ligne donc il faut le faire manuellement sinon
     * des problèmes apparaissent quand on demande à l'utilisateur de saisir une chaîne de caractères.
     *
     * @return Un nombre entier saisi par l'utilisateur au clavier
     */
    private static int readInt() {
        int i = 0;
        boolean success = false;
        do {
            try {
                i = scanner.nextInt();
                success = true;
            } catch (InputMismatchException e) {
                System.out.println("Erreur ! Veuillez entrer un nombre entier s'il vous plaît !");
            } finally {
                scanner.nextLine();
            }

        } while (!success);

        return i;
    }

    /**
     * Méthode readString pour rester consistant avec readInt !
     *
     * @return Une chaîne de caractères saisie par l'utilisateur au clavier
     */
    private static String readString() {
        return scanner.nextLine();
    }

    /**
     * Recharge un restaurant depuis la base de données avec ses données les plus récentes
     *
     * @param restaurant Le restaurant à recharger
     * @return Le restaurant rechargé, ou null si introuvable
     */
    private static Restaurant reloadRestaurant(Restaurant restaurant) {
        if (restaurant == null) return null;
        return restaurantService.getRestaurantByExactName(restaurant.getName());
    }

}
