package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.*;
import ch.hearc.ig.guideresto.persistence.BasicEvaluationMapper;
import ch.hearc.ig.guideresto.persistence.CompleteEvaluationMapper;
import ch.hearc.ig.guideresto.persistence.EvaluationCriteriaMapper;
import ch.hearc.ig.guideresto.persistence.jpa.JpaUtils;
import jakarta.persistence.EntityManager;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class EvaluationService implements IEvaluationService {
    private final BasicEvaluationMapper basicEvaluationMapper;
    private final CompleteEvaluationMapper completeEvaluationMapper;
    private final EvaluationCriteriaMapper evaluationCriteriaMapper;

    private EvaluationService() {
        EntityManager em = JpaUtils.getEntityManager();
        this.basicEvaluationMapper = new BasicEvaluationMapper(BasicEvaluation.class, em);
        this.completeEvaluationMapper = new CompleteEvaluationMapper(CompleteEvaluation.class, em);
        this.evaluationCriteriaMapper = new EvaluationCriteriaMapper(EvaluationCriteria.class, em);
    }


    // ------------------- READ (pas de transaction) -------------------
    @Override
    public Set<EvaluationCriteria> getAllCriteria() {
        return Set.of();
    }

    @Override
    public int countLikesForRestaurant(int id, boolean like) {
        return 0;
    }

    // ------------------- WRITE (Transaction) -------------------
    @Override
    public BasicEvaluation addBasicEvaluation(Restaurant restaurant, Boolean like, String ipAddress) throws Exception {
        return JpaUtils.inTransactionWithResult(em -> {
            Restaurant managedRestaurant = em.contains(restaurant) ? restaurant : em.merge(restaurant);

            BasicEvaluation newBaiscEvaluation = new BasicEvaluation(
                    null,
                    new Date(),
                    managedRestaurant,
                    like,
                    ipAddress
            );
            managedRestaurant.getEvaluations().add(newBaiscEvaluation);
            return newBaiscEvaluation;
        });
    }

    @Override
    public CompleteEvaluation evaluateRestaurant(Restaurant restaurant, String username, String comment, Map<EvaluationCriteria, Integer> gradesMap) throws Exception {
        return JpaUtils.inTransactionWithResult(em -> {
            Restaurant managedRestaurant = em.contains(restaurant) ? restaurant : em.merge(restaurant);

            CompleteEvaluation newCompleteEvaluation = new CompleteEvaluation(
                    null,
                    new Date(),
                    managedRestaurant,
                    comment,
                    username
            );

            // Attribution d'une note à chaque critère
            // Avec var le compilateur arrive à déduire automatiquement grâce à l'entrySet le type (inférence)
            for (var entry : gradesMap.entrySet()) {
                Grade grade = new Grade(
                        entry.getValue(),
                        newCompleteEvaluation,
                        entry.getKey()
                );
                newCompleteEvaluation.getGrades().add(grade);
            }
            managedRestaurant.getEvaluations().add(newCompleteEvaluation);
            return newCompleteEvaluation;
        });
    }
}
