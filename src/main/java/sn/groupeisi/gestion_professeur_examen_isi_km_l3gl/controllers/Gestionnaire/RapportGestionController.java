package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Gestionnaire;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao.PresenceDAO;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Emargement;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils.ExportService;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

public class RapportGestionController {

    @FXML
    private BorderPane exportPane;

    @FXML
    private Label lbTitre, lblMessage;

    @FXML
    private DatePicker dateAnnesScolaire;

    @FXML
    private ComboBox<String> cmbsemestre;

    @FXML
    private ComboBox<String> cmbAnneeScolaire;


    @FXML
    private Button btnExportExcel, btnExportPDF;

    @FXML private ComboBox<String> cmbMois;



    private final PresenceDAO presenceDAO = new PresenceDAO();

    @FXML
    public void initialize() {
        // Ajouter les années scolaires
        ObservableList<String> anneesScolaires = FXCollections.observableArrayList();
        int anneeActuelle = LocalDate.now().getYear();
        for (int i = anneeActuelle; i >= anneeActuelle - 10; i--) {
            anneesScolaires.add(i + "/" + (i + 1));
        }
        cmbAnneeScolaire.setItems(anneesScolaires);

        // Ajouter les semestres
        ObservableList<String> semestres = FXCollections.observableArrayList("Semestre 1", "Semestre 2");
        cmbsemestre.setItems(semestres);

        // Ajouter les mois
        ObservableList<String> mois = FXCollections.observableArrayList(
                "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
                "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        );
        cmbMois.setItems(mois);

        // Masquer l'export au début
        exportPane.setVisible(false);

        // Vérifier la sélection lorsque l'utilisateur change de valeur
        cmbAnneeScolaire.setOnAction(event -> verifierSelection());
        cmbsemestre.setOnAction(event -> verifierSelection());
        cmbMois.setOnAction(event -> verifierSelection());
    }





    /**
     * Vérifie si l'année scolaire et le semestre sont valides
     * et affiche le BorderPane si la sélection est correcte.
     */
    private void verifierSelection() {
        lblMessage.setText("");

        if (cmbAnneeScolaire.getValue() == null || cmbsemestre.getValue() == null || cmbMois.getValue() == null) {
            exportPane.setVisible(false);
            return;
        }

        // Vérifier si l'année scolaire est correcte (format YYYY/YYYY)
        String anneeScolaire = cmbAnneeScolaire.getValue();
        if (!Pattern.matches("\\d{4}/\\d{4}", anneeScolaire)) {
            lblMessage.setText("❌ Format d'année invalide. Ex: 2024/2025");
            exportPane.setVisible(false);
            return;
        }

        // Tout est bon, afficher les options d'export
        exportPane.setVisible(true);
    }




    /**
     * Exporter les présences en PDF en fonction de l'année et du semestre.
     */
    @FXML
    void exporterPDF(ActionEvent event) {
        lblMessage.setText(""); // Réinitialiser le message

        String anneeScolaire = cmbAnneeScolaire.getValue();
        String semestre = cmbsemestre.getValue();
        String mois = cmbMois.getValue();

        if (anneeScolaire == null || semestre == null || mois == null) {
            lblMessage.setText("❌ Veuillez sélectionner toutes les options.");
            return;
        }

        int moisIndex = cmbMois.getSelectionModel().getSelectedIndex() + 1;

        List<Emargement> presences = presenceDAO.getPresencesByYearSemesterAndMonth(anneeScolaire, semestre, moisIndex);

        if (presences.isEmpty()) {
            lblMessage.setText("❌ Aucun rapport trouvé pour cette période.");
            return;
        }

        try {
            boolean success = ExportService.exportToPDF(presences, anneeScolaire, semestre);
            lblMessage.setText(success ? "✅ Rapport PDF généré !" : "❌ Erreur lors de l'export PDF.");
        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText("❌ Une erreur est survenue : " + e.getMessage());
        }
    }


    @FXML
    void exporterExcel(ActionEvent event) {
        String anneeScolaire = cmbAnneeScolaire.getValue();
        String semestre = cmbsemestre.getValue();
        String mois = cmbMois.getValue();

        int moisIndex = cmbMois.getSelectionModel().getSelectedIndex() + 1;

        List<Emargement> presences = presenceDAO.getPresencesByYearSemesterAndMonth(anneeScolaire, semestre, moisIndex);
        if (presences.isEmpty()) {
            lblMessage.setText(" Aucun rapport trouvé pour cette période.");
            return;
        }

        boolean success = ExportService.exportToExcel(presences, anneeScolaire, semestre);
        lblMessage.setText(success ? " Rapport Excel généré !" : " Erreur lors de l'export Excel.");
    }


}
