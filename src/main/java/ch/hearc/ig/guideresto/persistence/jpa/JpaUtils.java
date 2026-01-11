package ch.hearc.ig.guideresto.persistence.jpa;

import jakarta.persistence.*;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Classe utilitaire pour gérer les opérations JPA et les transactions.
 * Fournit un EntityManager et des méthodes pour exécuter du code dans des transactions.
 */

public class JpaUtils {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    /**
     * Récupère l'EntityManager.
     * Crée l'EntityManagerFactory et l'EntityManager s'ils n'existent pas ou sont fermés.
     *
     * @return L'EntityManager actif
     */
    public static EntityManager getEntityManager() {
        if (em == null || !em.isOpen()) {
            if (emf == null) {
                emf = Persistence.createEntityManagerFactory("guideRestoJPA");
            }
            em = emf.createEntityManager();
        }
        return em;
    }

    /**
     * Exécute une opération dans une transaction sans retourner de résultat.
     * Gère automatiquement le début, le commit et le rollback en cas d'erreur.
     *
     * @param consumer La fonction à exécuter dans la transaction, reçoit l'EntityManager en paramètre
     * @throws OptimisticLockException Si un conflit de verrouillage optimiste survient
     * @throws RuntimeException Si une erreur survient pendant la transaction (rollback automatique)
     */
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
            throw ex;
        }
    }

    /**
     * Exécute une opération dans une transaction et retourne un résultat.
     * Gère automatiquement le début, le commit et le rollback en cas d'erreur.
     *
     * @param <T> Le type du résultat retourné
     * @param function La fonction à exécuter dans la transaction, reçoit l'EntityManager et retourne un résultat
     * @return Le résultat de la fonction exécutée
     * @throws OptimisticLockException Si un conflit de verrouillage optimiste survient
     * @throws RuntimeException Si une erreur survient pendant la transaction (rollback automatique)
     */
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
            throw ex;
        }
    }

    /**
     * Ferme l'EntityManager s'il est ouvert
     */
    public static void closeEntityManager() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    /**
     * Ferme l'EntityManagerFactory s'il est ouvert
     */
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

}
