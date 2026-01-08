package ch.hearc.ig.guideresto.service;

import ch.hearc.ig.guideresto.business.BasicEvaluation;
import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import ch.hearc.ig.guideresto.business.Restaurant;

import java.util.Map;
import java.util.Set;

public interface IEvaluationService {
    BasicEvaluation addBasicEvaluation(Restaurant restaurant, Boolean like, String ipAddress) throws Exception;
    CompleteEvaluation evaluateRestaurant(Restaurant restaurant, String username, String comment, Map<EvaluationCriteria, Integer> gradesMap) throws Exception;
    Set<EvaluationCriteria> getAllCriteria();
    Long countLikesForRestaurantId(Integer id, boolean like);
}
