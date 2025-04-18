package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@Entity
@Table(name = "emargements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"professeur_id", "cours_id"}))
public class Emargement {

    // Getters et Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime heure;

    @Column(nullable = false)
    private String statut; // "Présent" ou "Absent"

    @ManyToOne
    @JoinColumn(name = "professeur_id", nullable = false)
    private Users professeur;

    @ManyToOne
    @JoinColumn(name = "cours_id", nullable = false)
    private Cours cours;


    public Emargement() {
        this.statut = "Absent";
    }

    public Emargement(LocalDate date, LocalTime heure, String statut, Users professeur, Cours cours) {
        this.date = date;
        this.heure = heure;
        this.statut = statut;
        this.professeur = professeur;
        this.cours = cours;
    }



}
