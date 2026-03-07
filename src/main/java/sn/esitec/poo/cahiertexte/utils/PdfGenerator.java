package sn.esitec.poo.cahiertexte.utils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import sn.esitec.poo.cahiertexte.model.Seance;
import sn.esitec.poo.cahiertexte.model.StatutSeance;
import sn.esitec.poo.cahiertexte.model.Utilisateur;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utilitaire de génération de fiches de suivi pédagogique au format PDF.
 * <p>
 * Utilise la bibliothèque <em>iText 7</em> pour produire un document PDF
 * structuré comprenant l'en-tête de l'établissement, les informations de
 * l'enseignant, un récapitulatif statistique (total séances, heures
 * effectuées, taux de validation) et le tableau détaillé de toutes les
 * séances.
 * </p>
 * <p>
 * Le fichier généré est enregistré dans le répertoire courant de
 * l'application sous le nom :
 * {@code fiche_suivi_<prenom>_<nom>.pdf}
 * </p>
 */
public class PdfGenerator {

    /**
     * Génère la fiche de suivi pédagogique d'un enseignant au format PDF.
     * <p>
     * Le document inclut :
     * <ul>
     *   <li>L'en-tête de l'établissement (ESITEC)</li>
     *   <li>Les informations personnelles de l'enseignant</li>
     *   <li>Un tableau de statistiques (total, validées, heures, taux)</li>
     *   <li>Le détail tabulaire de chaque séance (date, heure, durée, contenu, statut)</li>
     * </ul>
     * </p>
     *
     * @param user    Utilisateur (enseignant) dont on génère la fiche
     * @param seances Liste des séances à inclure dans le document
     */
    public static void generateFicheSuivi(Utilisateur user, List<Seance> seances) {
        String fileName = System.getProperty("user.dir") + "\\fiche_suivi_"
        + user.getPrenom().toLowerCase() + "_"
        + user.getNom().toLowerCase() + ".pdf";

        try {
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            DeviceRgb bleuEsitec = new DeviceRgb(30, 58, 138);
            DeviceRgb grisClair  = new DeviceRgb(241, 245, 249);

            // ── En-tête ──────────────────────────────────────────────
            Paragraph titre = new Paragraph("ESITEC — Cahier de Texte Numérique")
                    .setFontSize(18).setBold()
                    .setFontColor(bleuEsitec)
                    .setTextAlignment(TextAlignment.CENTER);
            doc.add(titre);

            doc.add(new Paragraph("Fiche de Suivi Pédagogique")
                    .setFontSize(13)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY));

            doc.add(new Paragraph(" "));

            // ── Infos enseignant ──────────────────────────────────────
            doc.add(new Paragraph("Enseignant : " + user.getPrenom() + " " + user.getNom())
                    .setFontSize(11).setBold());
            doc.add(new Paragraph("Email : " + user.getEmail())
                    .setFontSize(10));
            doc.add(new Paragraph("Généré le : " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")))
                    .setFontSize(10).setFontColor(ColorConstants.GRAY));

            doc.add(new Paragraph(" "));

            // ── Statistiques ──────────────────────────────────────────
            long total    = seances.size();
            long validees = seances.stream().filter(s -> s.getStatut() == StatutSeance.VALIDEE).count();
            long attente  = seances.stream().filter(s -> s.getStatut() == StatutSeance.EN_ATTENTE).count();
            long rejetees = seances.stream().filter(s -> s.getStatut() == StatutSeance.REJETEE).count();
            int  heures   = seances.stream()
                    .filter(s -> s.getStatut() == StatutSeance.VALIDEE)
                    .mapToInt(Seance::getDuree).sum() / 60;
            double taux   = total > 0 ? (validees * 100.0 / total) : 0.0;

            doc.add(new Paragraph("Récapitulatif")
                    .setFontSize(12).setBold().setFontColor(bleuEsitec));

            Table stats = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .setWidth(UnitValue.createPercentValue(100));

            stats.addCell(statCell("Total séances", String.valueOf(total), grisClair));
            stats.addCell(statCell("Séances validées", String.valueOf(validees), grisClair));
            stats.addCell(statCell("Heures effectuées", heures + "h", grisClair));
            stats.addCell(statCell("Taux de validation", String.format("%.1f%%", taux), grisClair));
            stats.addCell(statCell("En attente", String.valueOf(attente), grisClair));
            stats.addCell(statCell("Rejetées", String.valueOf(rejetees), grisClair));

            doc.add(stats);
            doc.add(new Paragraph(" "));

            // ── Tableau des séances ───────────────────────────────────
            doc.add(new Paragraph("Détail des séances")
                    .setFontSize(12).setBold().setFontColor(bleuEsitec));

            if (seances.isEmpty()) {
                doc.add(new Paragraph("Aucune séance enregistrée.")
                        .setFontSize(10).setFontColor(ColorConstants.GRAY));
            } else {
                Table table = new Table(UnitValue.createPercentArray(new float[]{15, 10, 10, 45, 20}))
                        .setWidth(UnitValue.createPercentValue(100));

                // En-têtes
                for (String h : new String[]{"Date", "Heure", "Durée", "Contenu", "Statut"}) {
                    table.addHeaderCell(new Cell()
                            .add(new Paragraph(h).setBold().setFontColor(ColorConstants.WHITE))
                            .setBackgroundColor(bleuEsitec));
                }

                // Lignes
                for (Seance s : seances) {
                    DeviceRgb rowColor = switch (s.getStatut()) {
                        case VALIDEE    -> new DeviceRgb(220, 252, 231);
                        case EN_ATTENTE -> new DeviceRgb(254, 249, 195);
                        case REJETEE    -> new DeviceRgb(254, 226, 226);
                    };

                    table.addCell(cellule(s.getDateSeance().toString(), rowColor));
                    table.addCell(cellule(s.getHeureDebut().toString(), rowColor));
                    table.addCell(cellule(s.getDuree() + " min", rowColor));
                    table.addCell(cellule(s.getContenu(), rowColor));
                    table.addCell(cellule(s.getStatut().toString(), rowColor));
                }
                doc.add(table);
            }

            // ── Pied de page ──────────────────────────────────────────
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("ESITEC — Système de Gestion Pédagogique | 2026")
                    .setFontSize(8)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.close();
            System.out.println("PDF généré : " + fileName);

        } catch (Exception e) {
            System.err.println("Erreur génération PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Cell statCell(String label, String valeur, DeviceRgb bg) {
        return new Cell()
                .add(new Paragraph(label + " : " + valeur).setFontSize(10))
                .setBackgroundColor(bg)
                .setPadding(6);
    }

    private static Cell cellule(String texte, DeviceRgb bg) {
        return new Cell()
                .add(new Paragraph(texte != null ? texte : "").setFontSize(9))
                .setBackgroundColor(bg)
                .setPadding(4);
    }
}