package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.Grade;
import jakarta.persistence.EntityManager;

import java.util.List;

public class GradeMapper extends AbstractMapper<Grade> {
    public GradeMapper(Class<Grade> type, EntityManager em) {
        super(type, em);
    }

    public List<Grade> findByEvaluationId(int evaluationId) {
        return em.createNamedQuery("Grade.findByEvaluationId", Grade.class)
                .setParameter("evaluationId", evaluationId)
                .getResultList();
    }

}
