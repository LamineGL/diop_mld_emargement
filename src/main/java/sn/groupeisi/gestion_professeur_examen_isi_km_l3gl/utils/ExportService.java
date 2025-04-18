package sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.utils;

import javafx.scene.control.Alert;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import sn.groupeisi.gestion_professeur_examen_isi_km_l3gl.models.Emargement;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportService {

    public static boolean exportToPDF(List<Emargement> presences, String anneeScolaire, String semestre) {
        Document document = new Document();
        try {
            String folderPath = "Rapport_Presences_" + anneeScolaire;
            File folder = new File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            String filePath = folderPath + File.separator + semestre + ".pdf";
            File file = new File(filePath);

            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            document.add(new Paragraph("Rapport de Présences"));
            document.add(new Paragraph("Année Scolaire : " + anneeScolaire));
            document.add(new Paragraph("Semestre : " + semestre));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.addCell("Professeur");
            table.addCell("Date");
            table.addCell("Cours");
            table.addCell("Statut");

            for (Emargement em : presences) {
                table.addCell(em.getProfesseur().getNom());
                table.addCell(em.getDate().toString());
                table.addCell(em.getCours().getNom());
                table.addCell(em.getStatut());
            }

            document.add(table);

            // 📂 Ouvrir le fichier PDF après sa création
            openFile(filePath);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            document.close();
        }
    }


    public static boolean exportToExcel(List<Emargement> presences, String anneeScolaire, String semestre) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Présences");

        // 🔵 Création de l'en-tête du fichier Excel
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Professeur");
        headerRow.createCell(1).setCellValue("Date");
        headerRow.createCell(2).setCellValue("Cours");
        headerRow.createCell(3).setCellValue("Statut");

        // 🟢 Remplissage des données
        int rowNum = 1;
        for (Emargement em : presences) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(em.getProfesseur().getNom());
            row.createCell(1).setCellValue(em.getDate().toString());
            row.createCell(2).setCellValue(em.getCours().getNom());
            row.createCell(3).setCellValue(em.getStatut());
        }

        try {
            // 📂 Création du dossier si nécessaire
            String folderPath = "Rapport_Presences_" + anneeScolaire;
            File folder = new File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            // 📁 Définition du fichier Excel à générer
            String filePath = folderPath + File.separator + semestre + ".xlsx";
            File file = new File(filePath);

            // ✍ Écriture des données dans le fichier
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            // ✅ Affichage d'une alerte de succès
            showAlert("Succès", "Le rapport Excel a été généré et ouvert !");

            // 🔥 Ouvrir automatiquement le fichier Excel
            openFile(filePath);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void openFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                System.out.println("Le fichier n'existe pas !");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
