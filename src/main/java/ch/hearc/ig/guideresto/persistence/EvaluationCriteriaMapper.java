package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.EvaluationCriteria;
import jakarta.persistence.EntityManager;

public class EvaluationCriteriaMapper extends AbstractMapper<EvaluationCriteria> {
    protected EvaluationCriteriaMapper(Class<EvaluationCriteria> type, EntityManager em) {
        super(type, em);
    }
}
