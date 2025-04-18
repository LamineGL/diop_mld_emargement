package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao;

import jakarta.persistence.EntityManager;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Notification;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils.JPAUtil;

public class NotificationDAO {

    private final EntityManager entityManager = JPAUtil.getEntityManager();

    public void ajouterNotification(Notification notification) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(notification);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
