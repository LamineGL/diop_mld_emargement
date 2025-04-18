module sn.groupeisi.gestion_professeur_examen_isi_km_l3gl {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires static lombok;
    requires org.hibernate.validator;
    requires jakarta.validation;
    requires itextpdf;
    requires org.apache.poi.ooxml;
    requires java.desktop;
    requires jakarta.mail;

    opens sn.groupeisi.gestion_professeur_examen_isi_km_l3gl to javafx.fxml;
    exports sn.groupeisi.gestion_professeur_examen_isi_km_l3gl;

    exports sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models;
    opens sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models to javafx.fxml, org.hibernate.orm.core;

    exports sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils;
    opens sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils to javafx.fxml;

    opens sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Professeurs to javafx.fxml;
    exports sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Administrateurs;
    opens sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Administrateurs to javafx.fxml;

    exports sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Gestionnaire;
    opens sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Gestionnaire to javafx.fxml;

    exports sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao;
    opens sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao to javafx.fxml;

    opens sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Auth to javafx.fxml;
    exports sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Dashboard;
    opens sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Dashboard to javafx.fxml;




}
