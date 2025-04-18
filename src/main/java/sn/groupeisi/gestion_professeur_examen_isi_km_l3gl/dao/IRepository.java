package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao;

import javafx.collections.ObservableList;

public interface IRepository<T> {
    int add(T t); // Ajouter un élément
    int update(T t); // Modifier un élément
    int delete(int id); // Supprimer un élément
    ObservableList<T> getAll(); // Récupérer tous les éléments
    T get(int id); // Récupérer un élément par son ID
}
