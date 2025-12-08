package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.CompleteEvaluation;
import jakarta.persistence.EntityManager;

public class CompleteEvaluationMapper extends AbstractMapper<CompleteEvaluation>{

    //mis en public pour tests
    public CompleteEvaluationMapper(Class<CompleteEvaluation> type, EntityManager em) {
        super(type, em);
    }
}
