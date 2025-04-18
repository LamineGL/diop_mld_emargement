package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Cours;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Emargement;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils.JPAUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class PresenceDAO {
    private final EntityManager entityManager = JPAUtil.getEntityManager();

    public List<Cours> getCoursDuProfesseur(int professeurId) {
        return entityManager.createQuery("SELECT c FROM Cours c WHERE c.professeur.id = :profId", Cours.class)
                .setParameter("profId", professeurId)
                .getResultList();
    }

    public Emargement getEmargement(int professeurId, Long coursId) {
        try {
            return entityManager.createQuery("SELECT e FROM Emargement e WHERE e.professeur.id = :profId AND e.cours.id = :coursId", Emargement.class)
                    .setParameter("profId", professeurId)
                    .setParameter("coursId", coursId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void enregistrerPresence(Emargement emargement) {
        entityManager.getTransaction().begin();
        entityManager.persist(emargement);
        entityManager.getTransaction().commit();
    }

    public void updatePresence(Emargement emargement) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(emargement);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public List<Emargement> getAllPresences() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM Emargement e", Emargement.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public List<Emargement> getPresencesByYearAndSemester(String anneeScolaire, String semestre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Emargement e WHERE e.anneeScolaire = :anneeScolaire AND e.semestre = :semestre",
                            Emargement.class
                    )
                    .setParameter("anneeScolaire", anneeScolaire)
                    .setParameter("semestre", semestre)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    public List<Emargement> getPresencesByYearSemesterAndMonth(String anneeScolaire, String semestre, int mois) {
        EntityManager entityManager = JPAUtil.getEntityManager();
        String jpql = "SELECT e FROM Emargement e WHERE e.date BETWEEN :startDate AND :endDate";

        // Déterminer la période selon le semestre
        int anneeDebut = Integer.parseInt(anneeScolaire.split("/")[0]);
        int anneeFin = Integer.parseInt(anneeScolaire.split("/")[1]);

        LocalDate startDate;
        LocalDate endDate;

        if ("Semestre 1".equals(semestre)) {
            startDate = LocalDate.of(anneeDebut, 9, 1); // Septembre
            endDate = LocalDate.of(anneeFin, 1, 31); // Janvier
        } else {
            startDate = LocalDate.of(anneeFin, 2, 1); // Février
            endDate = LocalDate.of(anneeFin, 6, 30); // Juin
        }

        // Filtrer sur le mois sélectionné
        startDate = startDate.withMonth(mois);
        endDate = endDate.withMonth(mois).withDayOfMonth(endDate.lengthOfMonth());

        TypedQuery<Emargement> query = entityManager.createQuery(jpql, Emargement.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        return query.getResultList();
    }


    public List<Object[]> getStatsPresencesParProfesseur() {
        return entityManager.createQuery(
                        "SELECT p.nom, COUNT(e.id) FROM Emargement e JOIN e.professeur p GROUP BY p.id, p.nom", Object[].class)
                .getResultList();
    }


    public List<Object[]> getStatsPresencesParDate() {
        return entityManager.createQuery(
                        "SELECT e.date, COUNT(e.id) FROM Emargement e GROUP BY e.date ORDER BY e.date", Object[].class)
                .getResultList();
    }


    public List<Object[]> getStatsTauxPresenceParCours() {
        return entityManager.createQuery(
                        "SELECT c.nom, " +
                                "CASE WHEN COUNT(e.id) = 0 THEN 0.0 ELSE (CAST(COUNT(e.id) AS double) * 100.0 / " +
                                "(SELECT CAST(COUNT(e2.id) AS double) FROM Emargement e2 WHERE e2.cours.id = c.id)) END " +
                                "FROM Cours c LEFT JOIN c.emargements e " +
                                "GROUP BY c.id, c.nom", Object[].class)
                .getResultList();
    }



    // --- Méthodes de statistiques à ajouter ---

    /**
     * Retourne le nombre d'émargements enregistrés pour la date du jour.
     */
    public long getNbEmargementsDuJour() {
        LocalDate currentDate = LocalDate.now();
        Long count = (Long) entityManager.createQuery("SELECT COUNT(e) FROM Emargement e WHERE e.date = :currentDate")
                .setParameter("currentDate", currentDate)
                .getSingleResult();
        return count != null ? count : 0L;
    }

    /**
     * Retourne le nombre de cours "actifs" (ceux dont la date est aujourd'hui ou future).
     */
    public long getNbCoursActifs() {
        LocalDate currentDate = LocalDate.now();
        Long count = (Long) entityManager.createQuery("SELECT COUNT(c) FROM Cours c WHERE c.dateCours >= :currentDate")
                .setParameter("currentDate", currentDate)
                .getSingleResult();
        return count != null ? count : 0L;
    }

    /**
     * Calcule le taux de présence pour la date du jour.
     * Exemple : (nb d'émargements du jour / nb de cours programmés aujourd'hui) * 100.
     */
    public double getTauxPresenceAujourdHui() {
        LocalDate currentDate = LocalDate.now();
        Long nbEmargements = (Long) entityManager.createQuery("SELECT COUNT(e) FROM Emargement e WHERE e.date = :currentDate")
                .setParameter("currentDate", currentDate)
                .getSingleResult();
        Long nbCours = (Long) entityManager.createQuery("SELECT COUNT(c) FROM Cours c WHERE c.dateCours = :currentDate")
                .setParameter("currentDate", currentDate)
                .getSingleResult();
        if (nbCours == 0) return 0.0;
        return (nbEmargements / (double) nbCours) * 100.0;
    }

    /**
     * Retourne une liste d'objets contenant le nom du professeur et le nombre d'émargements.
     */
    public List<Object[]> getStatPresencesParProfesseur() {
        return entityManager.createQuery(
                        "SELECT p.nom, COUNT(e.id) FROM Emargement e JOIN e.professeur p GROUP BY p.id, p.nom", Object[].class)
                .getResultList();
    }

    /**
     * Retourne une liste d'objets contenant la date et le nombre d'émargements pour cette date.
     */
    public List<Object[]> getStatPresencesParDate() {
        return entityManager.createQuery(
                        "SELECT e.date, COUNT(e.id) FROM Emargement e GROUP BY e.date ORDER BY e.date", Object[].class)
                .getResultList();
    }

    /**
     * Retourne une liste d'objets contenant le nom du cours et le taux de présence.
     */
    public List<Object[]> getStatTauxPresenceParCours() {
        return entityManager.createQuery(
                        "SELECT c.nom, " +
                                "CASE WHEN COUNT(e.id) = 0 THEN 0.0 ELSE (CAST(COUNT(e.id) AS double) * 100.0 / " +
                                "(SELECT CAST(COUNT(e2.id) AS double) FROM Emargement e2 WHERE e2.cours.id = c.id)) END " +
                                "FROM Cours c LEFT JOIN c.emargements e " +
                                "GROUP BY c.id, c.nom", Object[].class)
                .getResultList();
    }







}
