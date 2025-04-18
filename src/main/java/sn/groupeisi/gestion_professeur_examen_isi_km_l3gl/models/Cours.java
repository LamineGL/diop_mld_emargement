package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "cours")
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    @ManyToOne
    @JoinColumn(name = "professeur_id", nullable = false)
    private Users professeur;

    @ManyToOne
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    @Column(nullable = false)
    private String statut = "Absent";

    @Column(nullable = false)
    private LocalDate dateCours;

    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Emargement> emargements;


    public Cours() {}

    public Cours(String nom, String description, LocalTime heureDebut, LocalTime heureFin, LocalDate dateCours, Users professeur, Salle salle) {
        this.nom = nom;
        this.description = description;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.dateCours = dateCours;
        this.professeur = professeur;
        this.salle = salle;
    }


}