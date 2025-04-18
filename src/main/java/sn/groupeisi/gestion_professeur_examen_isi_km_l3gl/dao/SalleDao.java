package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Salle;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils.JPAUtil;

import java.util.List;

public class SalleDao {

    private EntityManager entityManager = JPAUtil.getEntityManager();

    public void ajouterSalle(Salle salle) {
        entityManager.getTransaction().begin();
        entityManager.persist(salle);
        entityManager.getTransaction().commit();
    }

    public void modifierSalle(Salle salle) {
        entityManager.getTransaction().begin();
        entityManager.merge(salle);
        entityManager.getTransaction().commit();
    }

    public void supprimerSalle(Long id) {
        entityManager.getTransaction().begin();
        Salle salle = entityManager.find(Salle.class, id);
        if (salle != null) {
            entityManager.remove(salle);
        }
        entityManager.getTransaction().commit();
    }

    public List<Salle> getToutesLesSalles() {
        TypedQuery<Salle> query = entityManager.createQuery("SELECT s FROM Salle s", Salle.class);
        return query.getResultList();
    }
    public boolean existeLibelle(String libelle) {
        String jpql = "SELECT COUNT(s) FROM Salle s WHERE s.libelle = :libelle";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("libelle", libelle)
                .getSingleResult();
        return count > 0; // Retourne true si une salle avec ce libellé existe
    }
}
