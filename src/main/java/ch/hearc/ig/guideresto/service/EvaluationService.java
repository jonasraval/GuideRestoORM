package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.BasicEvaluationMapper;
import ch.hearc.ig.guideresto.persistence.EvaluationCriteriaMapper;
import ch.hearc.ig.guideresto.persistence.jpa.JpaUtils;
import jakarta.persistence.EntityManager;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class EvaluationService implements IEvaluationService {
    private final BasicEvaluationMapper basicEvaluationMapper;
    private final EvaluationCriteriaMapper evaluationCriteriaMapper;

    public EvaluationService() {
        EntityManager em = JpaUtils.getEntityManager();
        this.basicEvaluationMapper = new BasicEvaluationMapper(BasicEvaluation.class, em);
        this.evaluationCriteriaMapper = new EvaluationCriteriaMapper(EvaluationCriteria.class, em);
    }


    // ------------------- READ (pas de transaction) -------------------
    /**
     * Récupère l'ensemble des critères d'évaluation disponibles
     *
     * @return Un ensemble de tous les critères d'évaluation (Service, Cuisine, Cadre etc..)
     */
    @Override
    public Set<EvaluationCriteria> getAllCriteria() {
        return evaluationCriteriaMapper.findAll();
    }

    /**
     * Compte le nombre de likes ou dislikes pour un restaurant donné
     *
     * @param id L'identifiant du restaurant
     * @param like True pour compter les likes, false pour compter les dislikes
     * @return Le nombre de likes ou dislikes
     */
    @Override
    public Long countLikesForRestaurantId(Integer id, boolean like) {
        return basicEvaluationMapper.countLikesForRestaurant(id, like);
    }

    // ------------------- WRITE (Transaction) -------------------
    /**
     * Ajoute une évaluation basique (like/dislike) à un restaurant
     *
     * @param restaurant Le restaurant à évaluer
     * @param like True pour un like, false pour un dislike
     * @param ipAddress L'adresse IP de l'utilisateur
     * @return L'évaluation basique créée
     * @throws Exception Si une erreur survient lors de la transaction
     * @throws IllegalArgumentException Si un des paramètres est null
     */
    @Override
    public BasicEvaluation addBasicEvaluation(Restaurant restaurant, Boolean like, String ipAddress) throws Exception {
        if (restaurant == null) {
            throw new IllegalArgumentException("Le restaurant ne peut pas être null");
        }
        if (like == null) {
            throw new IllegalArgumentException("L'appreciation ne peut pas être null");
        }
        if (ipAddress == null) {
            throw new IllegalArgumentException("L'adresse IP ne peut pas être null");
        }
        return JpaUtils.inTransactionWithResult(em -> {
            // s'assurer que le restaurant est géré par l'EntityManager actuel
            //Restaurant managedRestaurant = em.contains(restaurant) ? restaurant : em.merge(restaurant);
            Restaurant managedRestaurant = em.getReference(Restaurant.class, restaurant.getId());
            if (managedRestaurant == null) {
                throw new IllegalArgumentException("Restaurant introuvable en base !");
            }

            BasicEvaluation newBasicEvaluation = new BasicEvaluation(
                    null,
                    new Date(),
                    managedRestaurant,
                    like,
                    ipAddress
            );
            em.persist(newBasicEvaluation);

            // maintient relation bidirectionnelle
            managedRestaurant.getEvaluations().add(newBasicEvaluation);
            return newBasicEvaluation;
        });
    }

    /**
     * Crée une évaluation complète pour un restaurant avec commentaire et notes par critère
     *
     * @param restaurant Le restaurant à évaluer
     * @param username Le nom de l'utilisateur qui évalue
     * @param comment Le commentaire de l'évaluation
     * @param gradesMap Une map associant chaque critère d'évaluation à une note (1-5)
     * @return L'évaluation complète créée
     * @throws Exception Si une erreur survient lors de la transaction
     * @throws IllegalArgumentException Si un paramètre est null, vide ou si une note n'est pas entre 1 et 5
     */
    @Override
    public CompleteEvaluation evaluateRestaurant(Restaurant restaurant, String username, String comment, Map<EvaluationCriteria, Integer> gradesMap) throws Exception {
        if (restaurant == null) {
            throw new IllegalArgumentException("Le restaurant ne peut pas être null");
        }
        if (username == null || username.trim().isEmpty()){
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut pas être null");
        }
        if (comment == null || comment.trim().isEmpty()){
            throw new IllegalArgumentException("Le commentaire ne peut pas être null");
        }
        if (gradesMap == null || gradesMap.isEmpty()){
            throw new IllegalArgumentException("Il faut au moins une note");
        }
        return JpaUtils.inTransactionWithResult(em -> {
            // s'assurer que le restaurant est géré par l'EntityManager actuel
            //Restaurant managedRestaurant = em.contains(restaurant) ? restaurant : em.merge(restaurant);
            Restaurant managedRestaurant = em.getReference(Restaurant.class, restaurant.getId());
            if (managedRestaurant == null) {
                throw new IllegalArgumentException("Restaurant introuvable en base !");
            }

            CompleteEvaluation newCompleteEvaluation = new CompleteEvaluation(
                    null,
                    new Date(),
                    managedRestaurant,
                    comment,
                    username
            );

            em.persist(newCompleteEvaluation);

            // Attribution d'une note à chaque critère
            // Avec var le compilateur arrive à déduire automatiquement grâce à l'entrySet le type (inférence)
            for (var entry : gradesMap.entrySet()) {
                Integer gradeValue = entry.getValue();
                // validation de la note
                if (gradeValue == null || gradeValue < 1 || gradeValue > 5) {
                    throw new IllegalArgumentException("La note doit être entre 1 et 5");
                }

                // s'assurer que le critère est géré par l'EntityManager actuel
                EvaluationCriteria managedCriteria = em.merge(entry.getKey());

                Grade grade = new Grade(
                        entry.getValue(),
                        newCompleteEvaluation,
                        managedCriteria
                );
                em.persist(grade);
                // maintenir relation bidirectionnelle
                newCompleteEvaluation.getGrades().add(grade);
            }
            // maintenir relation bidirectionnelle
            managedRestaurant.addEvaluation(newCompleteEvaluation);
            return newCompleteEvaluation;
        });
    }
}
