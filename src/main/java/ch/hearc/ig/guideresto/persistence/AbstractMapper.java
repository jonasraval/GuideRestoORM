package ch.hearc.ig.guideresto.persistence;

import ch.hearc.ig.guideresto.business.IBusinessObject;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    } //possible de faire avec un em.find(type, id) mais il nous est demandé d'utiliser JPQL

    public List<T> findAll() {
        return em.createNamedQuery(type.getSimpleName()+".findAll",type).getResultList();
    }

    public T save(T entity) {
        if (entity.getId() == null) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }
    }

    public void delete(T entity) {
        em.remove(em.contains(entity)
                ? entity //on remove si elle est déjà gérée
                : em.merge(entity)); //sinon on la merge avanT de la remove
    }
}
