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

    public EvaluationService() {
        EntityManager em = JpaUtils.getEntityManager();
        this.basicEvaluationMapper = new BasicEvaluationMapper(BasicEvaluation.class, em);
        this.completeEvaluationMapper = new CompleteEvaluationMapper(CompleteEvaluation.class, em);
        this.evaluationCriteriaMapper = new EvaluationCriteriaMapper(EvaluationCriteria.class, em);
    }


    // ------------------- READ (pas de transaction) -------------------
    @Override
    public Set<EvaluationCriteria> getAllCriteria() {
        return evaluationCriteriaMapper.findAll();
    }

    @Override
    public Long countLikesForRestaurantId(Integer id, boolean like) {
        return basicEvaluationMapper.countLikesForRestaurant(id);
    }

    // ------------------- WRITE (Transaction) -------------------
    @Override
    public BasicEvaluation addBasicEvaluation(Restaurant restaurant, Boolean like, String ipAddress) throws Exception {
        return JpaUtils.inTransactionWithResult(em -> {
            Restaurant managedRestaurant = em.contains(restaurant) ? restaurant : em.merge(restaurant);

            BasicEvaluation newBasicEvaluation = new BasicEvaluation(
                    null,
                    new Date(),
                    managedRestaurant,
                    like,
                    ipAddress
            );
            em.persist(newBasicEvaluation);
            //newBasicEvaluation = basicEvaluationMapper.save(newBasicEvaluation);
            managedRestaurant.getEvaluations().add(newBasicEvaluation);
            return newBasicEvaluation;
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

            em.persist(newCompleteEvaluation);

            //newCompleteEvaluation = completeEvaluationMapper.save(newCompleteEvaluation); //persister avant de cascader les notes
            // Attribution d'une note à chaque critère
            // Avec var le compilateur arrive à déduire automatiquement grâce à l'entrySet le type (inférence)
            for (var entry : gradesMap.entrySet()) {
                Grade grade = new Grade(
                        entry.getValue(),
                        newCompleteEvaluation,
                        entry.getKey()
                );
                em.persist(grade);
                newCompleteEvaluation.getGrades().add(grade);
            }
            managedRestaurant.addEvaluation(newCompleteEvaluation);
            return newCompleteEvaluation;
        });
    }
}
