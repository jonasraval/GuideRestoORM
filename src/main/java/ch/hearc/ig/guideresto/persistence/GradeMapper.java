package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.Grade;
import jakarta.persistence.EntityManager;

import java.util.Set;
import java.util.stream.Collectors;

public class GradeMapper extends AbstractMapper<Grade> {
    public GradeMapper(Class<Grade> type, EntityManager em) {
        super(type, em);
    }

    public Set<Grade> findByEvaluationId(Integer evaluationId) {
        return em.createNamedQuery("Grade.findByEvaluationId", Grade.class)
                .setParameter("evaluationId", evaluationId)
                .getResultStream()
                .collect(Collectors.toUnmodifiableSet());
    }

}
