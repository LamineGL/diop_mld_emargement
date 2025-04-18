package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "salles")
@Getter
@Setter
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String libelle;

    public Salle() {}

    public Salle(String libelle) {
        this.libelle = libelle;
    }

    //public Long getId() { return id; }
   // public void setId(Long id) { this.id = id; }

   // public String getLibelle() { return libelle; }
   // public void setLibelle(String libelle) { this.libelle = libelle; }


    @Override
    public String toString() {
        return "Salle{" +
                "id=" + id +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}