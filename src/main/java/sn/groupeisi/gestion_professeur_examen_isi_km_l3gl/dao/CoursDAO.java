package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Cours;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils.JPAUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CoursDAO {

    private EntityManager entityManager = JPAUtil.getEntityManager();

    public void ajouterCours(Cours cours) {
        entityManager.getTransaction().begin();
        entityManager.persist(cours);
        entityManager.getTransaction().commit();
    }

    public void modifierCours(Cours cours) {
        entityManager.getTransaction().begin();
        entityManager.merge(cours);
        entityManager.getTransaction().commit();
    }

    public void supprimerCours(Long id) {
        entityManager.getTransaction().begin();
        Cours cours = entityManager.find(Cours.class, id);
        if (cours != null) {
            entityManager.remove(cours);
        }
        entityManager.getTransaction().commit();
    }

    public boolean existeConflitHoraire(Long salleId, LocalDate dateCours, LocalTime heureDebut, LocalTime heureFin, Long coursIdExclu) {
        String jpql = "SELECT COUNT(c) FROM Cours c WHERE c.salle.id = :salleId " +
                "AND c.dateCours = :dateCours " +
                "AND (c.heureDebut < :heureFin AND c.heureFin > :heureDebut)";

        if (coursIdExclu != null) {
            jpql += " AND c.id != :coursIdExclu"; // Exclure le cours en modification
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("salleId", salleId);
        query.setParameter("dateCours", dateCours);
        query.setParameter("heureDebut", heureDebut);
        query.setParameter("heureFin", heureFin);

        if (coursIdExclu != null) {
            query.setParameter("coursIdExclu", coursIdExclu);
        }

        return query.getSingleResult() > 0;
    }


    public boolean estAttribueAProfesseur(Long coursId) {
        TypedQuery<Long> query = entityManager.createQuery(
                        "SELECT COUNT(c) FROM Cours c WHERE c.id = :coursId AND c.professeur IS NOT NULL", Long.class)
                .setParameter("coursId", coursId);

        return query.getSingleResult() > 0;
    }

    public List<Cours> getAllCours() {
        return entityManager.createQuery("SELECT c FROM Cours c", Cours.class).getResultList();
    }


    public  long getNbCoursActifs() {
        LocalDate currentDate = LocalDate.now();
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Cours c WHERE c.dateCours >= :currentDate", Long.class);
        query.setParameter("currentDate", currentDate);
        return query.getSingleResult();
    }

}
