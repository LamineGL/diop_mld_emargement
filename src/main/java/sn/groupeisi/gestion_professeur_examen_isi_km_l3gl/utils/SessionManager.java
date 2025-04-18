package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils;

import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Users;

public class SessionManager {
    private static Users currentUser;

    public static void setCurrentUser(Users user) {
        currentUser = user;
    }

    public static Users getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }

}
