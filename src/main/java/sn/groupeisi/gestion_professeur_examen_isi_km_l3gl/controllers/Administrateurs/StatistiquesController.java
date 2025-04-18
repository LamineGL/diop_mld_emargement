package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.controllers.Administrateurs;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao.PresenceDAO;
import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.dao.CoursDAO;

// Imports pour l'export PDF (exemple)
import javafx.event.ActionEvent;
// import com.itextpdf...  // Ex: iText ou autre lib PDF

import java.util.List;

/**
 * Contrôleur pour le dashboard de statistiques.
 * Relié au FXML contenant les cartes (Labels), les graphiques et les boutons.
 */
public class StatistiquesController {

    CoursDAO coursDAO = new CoursDAO();
    long nbCours = coursDAO.getNbCoursActifs();


    // --------------------- Labels (Cartes de statistiques) ---------------------
    @FXML
    private Label nbEmerAcruel;             // Label "Émargements du Jour"
    @FXML
    private Label nbCoursActuel;            // Label "Cours Actifs"
    @FXML
    private Label nbTauxPresenceAujourd;    // Label "Taux de Présence"

    // --------------------- Graphiques ---------------------
    @FXML
    private BarChart<String, Number> barChartProfesseurs;  // "Présences par Professeur"
    @FXML
    private LineChart<String, Number> lineChartPresences;  // "Évolution des Présences"
    @FXML
    private PieChart pieChartCours;                        // "Taux de Présence par Cours"

    // --------------------- Boutons & messages ---------------------
    @FXML
    private Button btnRafraichirStats;      // Bouton "Rafraîchir Statistiques"
    @FXML
    private Label lblMessageStats;          // Label pour les messages d'état

    // --------------------- DAO ou services ---------------------
    private final PresenceDAO presenceDAO = new PresenceDAO();
   // private final CoursDAO coursDAO = new CoursDAO();
    // Ajoutez d'autres DAO si nécessaire

    /**
     * Méthode appelée automatiquement après le chargement du FXML.
     * Initialise l'affichage et les données.
     */
    @FXML
    public void initialize() {
        chargerCartesStatistiques();
        chargerBarChartProfesseurs();
        chargerLineChartEvolution();
        chargerPieChartCours();
    }

    // --------------------------------------------------------------------------
    // 1. CHARGER LES CARTES DE STATISTIQUES
    // --------------------------------------------------------------------------
    private void chargerCartesStatistiques() {
        try {
            // Exemple : récupère le nombre d'émargements du jour
            long nbEmargementsDuJour = presenceDAO.getNbEmargementsDuJour();
            nbEmerAcruel.setText(String.valueOf(nbEmargementsDuJour));

            // Exemple : récupère le nombre de cours actifs
            //long nbCours = CoursDAO.getNbCoursActifs();
            nbCoursActuel.setText(String.valueOf(nbCours));

            // Exemple : récupère le taux de présence du jour (en %)
            double tauxPresence = presenceDAO.getTauxPresenceAujourdHui();
            // Arrondir ou formatter en pourcentage
            nbTauxPresenceAujourd.setText(String.format("%.2f%%", tauxPresence));

        } catch (Exception e) {
            e.printStackTrace();
            nbEmerAcruel.setText("N/A");
            nbCoursActuel.setText("N/A");
            nbTauxPresenceAujourd.setText("N/A");
        }
    }


// 2. CHARGER LE BAR CHART (Présences par Professeur)
// --------------------------------------------------------------------------
    private void chargerBarChartProfesseurs() {
        barChartProfesseurs.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Présences par Professeur");

        List<Object[]> stats = presenceDAO.getStatPresencesParProfesseur();
        for (Object[] stat : stats) {
            // S'assure que le premier élément est converti en String
            String nomProf = (stat[0] != null) ? stat[0].toString() : "Inconnu";
            // Vérifie que le deuxième élément est un Number, sinon on met 0
            Number nbPresences = (stat[1] instanceof Number) ? (Number) stat[1] : 0;
            series.getData().add(new XYChart.Data<>(nomProf, nbPresences));
        }
        // Ajoute la série au BarChart
        barChartProfesseurs.getData().add(series);
    }

    // --------------------------------------------------------------------------
// 3. CHARGER LE LINE CHART (Évolution des Présences)
// --------------------------------------------------------------------------
    private void chargerLineChartEvolution() {
        lineChartPresences.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Évolution des Présences");

        List<Object[]> stats = presenceDAO.getStatsPresencesParDate();
        for (Object[] stat : stats) {
            // Conversion en chaîne, au cas où le résultat n'est pas directement un String
            String dateStr = (stat[0] != null) ? stat[0].toString() : "N/A";
            Number nbPresences = (stat[1] instanceof Number) ? (Number) stat[1] : 0;
            series.getData().add(new XYChart.Data<>(dateStr, nbPresences));
        }
        lineChartPresences.getData().add(series);
    }

    // --------------------------------------------------------------------------
// 4. CHARGER LE PIE CHART (Taux de Présence par Cours)
// --------------------------------------------------------------------------
    private void chargerPieChartCours() {
        pieChartCours.getData().clear();

        List<Object[]> stats = presenceDAO.getStatsTauxPresenceParCours();
        for (Object[] stat : stats) {
            String nomCours = (stat[0] != null) ? stat[0].toString() : "N/A";
            Number tauxPresence = (stat[1] instanceof Number) ? (Number) stat[1] : 0;
            // Utilisation de doubleValue() pour s'assurer que la valeur est en double
            PieChart.Data data = new PieChart.Data(nomCours, tauxPresence.doubleValue());
            pieChartCours.getData().add(data);
        }
    }


    // --------------------------------------------------------------------------
    // BOUTON "RAFRAICHIR STATISTIQUES"
    // --------------------------------------------------------------------------
    @FXML
    private void rafraichirStats() {
        try {
            // Recharger les cartes
            chargerCartesStatistiques();

            // Recharger les graphiques
            chargerBarChartProfesseurs();
            chargerLineChartEvolution();
            chargerPieChartCours();

            lblMessageStats.setText("✅ Statistiques mises à jour !");
        } catch (Exception e) {
            lblMessageStats.setText("❌ Erreur lors du rafraîchissement.");
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------------------------
    // BOUTON "EXPORTER PDF" (EXEMPLE DE SQUELETTE)
    // --------------------------------------------------------------------------
    @FXML
    private void onExporterPDF(ActionEvent event) {
        try {
            // 1. Récupérer les données souhaitées pour le PDF (stats, graphiques, etc.)
            // 2. Utiliser une librairie de génération PDF (iText, PDFBox, etc.)
            // 3. Enregistrer le fichier et/ou l’ouvrir

            // Ex. pseudo-code :
            // Document document = new Document();
            // PdfWriter.getInstance(document, new FileOutputStream("Statistiques.pdf"));
            // document.open();
            // document.add(new Paragraph("Rapport de Statistiques ..."));
            // ...
            // document.close();

            lblMessageStats.setText("✅ Rapport PDF exporté avec succès !");
        } catch (Exception e) {
            lblMessageStats.setText("❌ Échec de l'export PDF.");
            e.printStackTrace();
        }
    }


}
