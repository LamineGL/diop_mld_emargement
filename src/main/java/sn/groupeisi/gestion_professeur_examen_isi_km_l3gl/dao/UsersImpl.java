package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Users;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils.JPAUtil;

import java.util.List;

public class UsersImpl implements IRepository<Users> {

    private EntityManager entityManager = JPAUtil.getEntityManager();

    @Override
    public int add(Users user) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(user);
            transaction.commit();
            return 1;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int update(Users user) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(user);
            transaction.commit();
            return 1;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int delete(int id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Users user = entityManager.find(Users.class, id);
            if (user != null) {
                entityManager.remove(user);
                transaction.commit();
                return 1;
            }
            return 0;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public ObservableList<Users> getAll() {
        TypedQuery<Users> query = entityManager.createQuery("SELECT u FROM Users u", Users.class);
        List<Users> userList = query.getResultList();
        return FXCollections.observableArrayList(userList);
    }

    @Override
    public Users get(int id) {
        return entityManager.find(Users.class, id);
    }

    public Users findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM Users u WHERE u.email = :email", Users.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }




}