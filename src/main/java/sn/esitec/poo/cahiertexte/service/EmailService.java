package sn.esitec.poo.cahiertexte.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.InputStream;
import java.util.Properties;

/**
 * Service utilitaire d'envoi d'emails via SMTP (Jakarta Mail).
 * <p>
 * La configuration SMTP est chargée depuis {@code application.properties}
 * (clés {@code mail.smtp.host}, {@code mail.smtp.port}, {@code mail.from},
 * {@code mail.password}) ou depuis les variables d'environnement équivalentes.
 * Si les identifiants ne sont pas renseignés, les envois sont silencieusement
 * ignorés pour éviter tout crash.
 * </p>
 */
public class EmailService {

    private static final Properties CONFIG = loadConfig();

    private static final String SMTP_HOST = getConfig("mail.smtp.host", "smtp.gmail.com");
    private static final String SMTP_PORT = getConfig("mail.smtp.port", "587");
    private static final String EMAIL_FROM = getConfig("mail.from", "");
    private static final String EMAIL_PASSWORD = getConfig("mail.password", "");
    private static final String REDIRECT_ALL_TO = getConfig("mail.redirect.all.to", "");
    private static final String REDIRECT_ENSEIGNANT_TO = getConfig("mail.redirect.enseignant.to", "");
    private static final String REDIRECT_RESPONSABLE_TO = getConfig("mail.redirect.responsable.to", "");

    /**
     * Envoie un email via SMTP avec authentification STARTTLS.
     * <p>
     * Si {@code MAIL_FROM} ou {@code MAIL_PASSWORD} sont vides, l'envoi est
     * annulé avec un avertissement sur {@code System.err}.
     * </p>
     *
     * @param destinataire Adresse email du destinataire
     * @param sujet        Sujet de l'email
     * @param message      Corps du message (texte brut)
     */
    public static void envoyerNotification(String destinataire, String sujet, String message) {
        if (EMAIL_FROM.isBlank() || EMAIL_PASSWORD.isBlank()) {
            System.err.println("⚠️ Email non envoyé: configurez MAIL_FROM et MAIL_PASSWORD (ou mail.from/mail.password).");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            Message mail = new MimeMessage(session);
            mail.setFrom(new InternetAddress(EMAIL_FROM));
            mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            mail.setSubject(sujet);
            mail.setText(message);
            Transport.send(mail);
            System.out.println("✅ Email envoyé à : " + destinataire);
        } catch (MessagingException e) {
            System.err.println("❌ Erreur envoi email : " + e.getMessage());
        }
    }

    private static Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream in = EmailService.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Impossible de lire application.properties: " + e.getMessage());
        }
        return props;
    }

    private static String getConfig(String key, String defaultValue) {
        String envKey = key.toUpperCase().replace('.', '_');
        String env = System.getenv(envKey);
        if (env != null && !env.isBlank()) return env;

        String fromFile = CONFIG.getProperty(key);
        if (fromFile != null && !fromFile.isBlank()) return fromFile;

        return defaultValue;
    }

    /**
     * Notifie un enseignant qu'il vient de se voir assigner un nouveau cours
     * par le chef de département.
     *
     * @param emailEnseignant  Adresse email de l'enseignant
     * @param nomEnseignant    Nom de l'enseignant
     * @param prenomEnseignant Prénom de l'enseignant
     * @param matiere          Intitulé de la matière assignée
     */
    public static void notifierAssignationCours(String emailEnseignant, String nomEnseignant,
                                                 String prenomEnseignant, String matiere) {
        String destinataire = resolveDestinataire(emailEnseignant, "ENSEIGNANT");
        String sujet = "Nouveau cours assigne - ESITEC";
        String message = "Bonjour " + prenomEnseignant + " " + nomEnseignant + ",\n\n"
                + "Le Chef de Departement vous a assigne le cours suivant :\n"
                + "- " + matiere + "\n\n"
                + "Connectez-vous au Cahier de Texte Numerique pour commencer "
                + "a saisir vos seances.\n\n"
                + "Cordialement,\n"
                + "Systeme Cahier de Texte - ESITEC 2026";
        envoyerNotification(destinataire, sujet, message);
    }

    /**
     * Notifie l'enseignant que sa séance a été validée par le responsable.
     *
     * @param emailEnseignant Email de l'enseignant destinataire
     * @param nomEnseignant   Nom de l'enseignant (utilisé dans le corps du message)
     * @param contenuSeance   Contenu de la séance validée (rappel dans l'email)
     */
    public static void notifierValidation(String emailEnseignant, String nomEnseignant,
                                          String contenuSeance) {
        String destinataire = resolveDestinataire(emailEnseignant, "ENSEIGNANT");
        String sujet = "✅ Séance validée - Cahier de Texte ESITEC";
        String message = "Bonjour " + nomEnseignant + ",\n\n"
                + "Votre séance a été validée par le responsable de classe.\n\n"
                + "Contenu : " + contenuSeance + "\n\n"
                + "Cordialement,\n"
                + "Système Cahier de Texte — ESITEC 2026";
        envoyerNotification(destinataire, sujet, message);
    }

    /**
     * Notifie l'enseignant que sa séance a été rejetée, avec le motif.
     *
     * @param emailEnseignant Email de l'enseignant destinataire
     * @param nomEnseignant   Nom de l'enseignant
     * @param contenuSeance   Contenu de la séance rejetée (rappel)
     * @param motif           Motif du rejet saisi par le responsable
     */
    public static void notifierRejet(String emailEnseignant, String nomEnseignant,
                                     String contenuSeance, String motif) {
        String destinataire = resolveDestinataire(emailEnseignant, "ENSEIGNANT");
        String sujet = "❌ Séance rejetée - Cahier de Texte ESITEC";
        String message = "Bonjour " + nomEnseignant + ",\n\n"
                + "Votre séance a été rejetée par le responsable de classe.\n\n"
                + "Contenu : " + contenuSeance + "\n"
                + "Motif du rejet : " + motif + "\n\n"
                + "Veuillez corriger et resoumettre.\n\n"
                + "Cordialement,\n"
                + "Système Cahier de Texte — ESITEC 2026";
        envoyerNotification(destinataire, sujet, message);
    }

    /**
     * Notifie le responsable de classe qu'une nouvelle séance attend sa validation.
     *
     * @param emailResponsable  Adresse email du responsable destinataire
     * @param nomResponsable    Nom du responsable
     * @param prenomResponsable Prénom du responsable
     * @param nomEnseignant     Nom de l'enseignant ayant saisi la séance
     * @param prenomEnseignant  Prénom de l'enseignant
     * @param matiere           Matière concernée
     * @param dateSeance        Date de la séance (format lisible)
     * @param contenuSeance     Résumé du contenu de la séance
     */
    public static void notifierDemandeValidationResponsable(
            String emailResponsable,
            String nomResponsable,
            String prenomResponsable,
            String nomEnseignant,
            String prenomEnseignant,
            String matiere,
            String dateSeance,
            String contenuSeance) {
        String destinataire = resolveDestinataire(emailResponsable, "RESPONSABLE");
        String sujet = "Nouvelle seance a valider - Cahier de Texte ESITEC";
        String message = "Bonjour " + prenomResponsable + " " + nomResponsable + ",\n\n"
                + "Une nouvelle seance a ete saisie et attend votre validation.\n\n"
                + "Enseignant : " + prenomEnseignant + " " + nomEnseignant + "\n"
                + "Matiere : " + matiere + "\n"
                + "Date : " + dateSeance + "\n"
                + "Contenu : " + contenuSeance + "\n\n"
                + "Connectez-vous au tableau Responsable pour valider ou rejeter.\n\n"
                + "Cordialement,\n"
                + "Systeme Cahier de Texte - ESITEC 2026";
        envoyerNotification(destinataire, sujet, message);
    }

    /**
     * Permet de rediriger les notifications en mode démo sans modifier
     * les emails des utilisateurs en base (utile pour conserver des logins stables).
     */
    private static String resolveDestinataire(String destinataireOriginal, String typeNotification) {
        String redirection = "";

        if (!REDIRECT_ALL_TO.isBlank()) {
            redirection = REDIRECT_ALL_TO;
        } else if ("ENSEIGNANT".equals(typeNotification) && !REDIRECT_ENSEIGNANT_TO.isBlank()) {
            redirection = REDIRECT_ENSEIGNANT_TO;
        } else if ("RESPONSABLE".equals(typeNotification) && !REDIRECT_RESPONSABLE_TO.isBlank()) {
            redirection = REDIRECT_RESPONSABLE_TO;
        }

        if (!redirection.isBlank()) {
            System.out.println("📬 Redirection email: " + destinataireOriginal + " -> " + redirection);
            return redirection;
        }

        return destinataireOriginal;
    }
}
