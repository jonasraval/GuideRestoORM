package ch.hearc.ig.guideresto.persistence.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.function.Consumer;
import java.util.function.Function;

public class JpaUtils {

    private static EntityManagerFactory emf;
    private static EntityManager em;


    public static EntityManager getEntityManager() {
        if (em == null || !em.isOpen()) {
            if (emf == null) {
                emf = Persistence.createEntityManagerFactory("guideRestoJPA");
            }
            em = emf.createEntityManager();
        }
        return em;
    }

    public static void inTransaction(Consumer<EntityManager> consumer) {
        EntityManager em = JpaUtils.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            consumer.accept(em);
            em.flush();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Erreur lors de la transaction : "+ ex.getMessage());
            throw new RuntimeException("Erreur de persistance : ", ex);
        }
    }

    public static <T> T inTransactionWithResult(Function<EntityManager, T> function) {
        EntityManager em = JpaUtils.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            T result = function.apply(em);
            em.flush();
            transaction.commit();
            return result;
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Erreur lors de la transaction : "+ ex.getMessage());
            throw new RuntimeException("Erreur de persistance : ", ex);
        }
    }

    public static void closeEntityManager() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

}
