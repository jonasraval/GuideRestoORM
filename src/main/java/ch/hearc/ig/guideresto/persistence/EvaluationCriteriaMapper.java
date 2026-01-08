package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import jakarta.persistence.EntityManager;

public class EvaluationCriteriaMapper extends AbstractMapper<EvaluationCriteria> {

    public EvaluationCriteriaMapper(Class<EvaluationCriteria> type, EntityManager em) {
        super(type, em);
    }


}
