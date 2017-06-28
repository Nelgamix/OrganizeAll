package com.organizeAll.elements;

import com.organizeAll.Icons;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Nico on 27/06/2017.
 */
public abstract class Element {
    public enum Type {
        FICHIER,
        DOSSIER
    }

    File file;
    private File fileTmp;

    String nom;
    String base;

    private String[] dossiers;
    Type type;
    private String derniereModification;
    private long taille;

    Element(File file, String base) {
        this.file = file;

        setBase(base);
        actualiser();
    }

    private void actualiser() {
        String path = formatPath(file.getAbsolutePath());

        // liste de dossier. <!> le dernier index est le nom du fichier + extension
        String[] dossiers = path.replace(base, "").split(Pattern.quote(File.separator));

        this.dossiers = Arrays.copyOfRange(dossiers, 0, dossiers.length - 1);
        this.nom = dossiers[dossiers.length - 1];
        this.taille = this.file.length();
        this.derniereModification = new SimpleDateFormat("HH/MM/dd hh:mm").format(this.file.lastModified());
    }

    private void setBase(String base) {
        this.base = formatPath(base);
    }

    public File getFile() {
        return file;
    }

    public String getBase() {
        return base;
    }
    public String getCheminAbsolu() {
        return base + getChemin();
    }
    public String getChemin() {
        return String.join(File.separator, dossiers) + getNom();
    }
    public String[] getDossiers() {
        return dossiers;
    }

    public Type getType() {
        return type;
    }
    public String getNom() {
        return nom;
    }
    public String getDerniereModification() {
        return derniereModification;
    }
    public long getTaille() {
        return taille;
    }
    public String getTailleHumain() {
        long s = getTaille();
        long k, m, g; // ko, mo, go

        if (s < 1024) return s + " octets";
        if ((k = s / 1024) < 1024 && (s %= 1024) > -1) return k + "." + s + " ko";
        if ((m = k / 1024) < 1024 && (k %= 1024) > -1) return m + "." + k + " mo";
        if ((g = m / 1024) < 1024 && (m %= 1024) > -1) return g + "." + m + " go";
        return "Impossible de connaître la taille";
    }
    public Image getIcone() {
        return Icons.getIcone(this);
    }

    private String formatPath(String path) {
        return (path.endsWith(File.separator) ? path : (path += File.separator));
    }

    public void open() {
        try {
            Desktop.getDesktop().open(getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void rename(File newFile) {
        rename(newFile, false);
    }
    public void rename(File newFile, boolean temporaire) {
        if (newFile.exists()) {
            System.err.println("File already exists! (" + newFile.getAbsolutePath() + ")");
            return;
        }

        try {
            if (fileTmp != null && fileTmp.exists()) {
                Files.move(fileTmp.toPath(), newFile.toPath());
            } else if (file.exists()) {
                Files.move(getFile().toPath(), newFile.toPath());
            } else {
                System.err.println("Erreur: aucun fichier disponible pour rename");
                return;
            }

            if (temporaire) {
                fileTmp = newFile;
            } else {
                fileTmp = null;
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return "Element{" +
                "nom='" + nom + '\'' +
                ", base='" + base + '\'' +
                ", dossiers=" + Arrays.toString(dossiers) +
                ", derniereModification='" + derniereModification + '\'' +
                ", taille=" + taille +
                '}';
    }
}
