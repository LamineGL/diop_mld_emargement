package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor

public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private String destinataireEmail;
    private LocalDateTime dateEnvoi;

    public Notification() {}

    public Notification(String message, String destinataireEmail) {
        this.message = message;
        this.destinataireEmail = destinataireEmail;
        this.dateEnvoi = LocalDateTime.now();
    }

    // Getters et Setters
}
