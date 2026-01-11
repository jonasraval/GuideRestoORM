package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.IBusinessObject;
import jakarta.persistence.EntityManager;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractMapper<T extends IBusinessObject> {
    protected EntityManager em;
    private final Class<T> type;

    protected AbstractMapper(Class<T> type, EntityManager em) {
        this.type = type;
        this.em = em;
    }

    public T findById(Integer id) {
        return em.createNamedQuery(type.getSimpleName()+".findById",type)
                .setParameter("id", id)
                .getSingleResult();
    }

    public Set<T> findAll() {
        return em.createNamedQuery(type.getSimpleName()+".findAll",type).getResultStream().collect(Collectors.toUnmodifiableSet());
    }

    public void delete(T entity) {
        T managed = em.contains(entity) ? entity : em.merge(entity);
        em.remove(managed);
    }


}
