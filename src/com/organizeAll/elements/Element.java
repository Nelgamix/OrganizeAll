package com.organizeAll.elements;

import com.organizeAll.Icons;
import javafx.scene.image.Image;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

    String nom;
    String base;

    protected String[] dossiers;
    Type type;
    protected String derniereModification;
    protected long taille;

    Element(File file, String base) {
        this.file = file;

        setBase(base);
        actualiser();
    }

    protected void actualiser() {
        String path = formatPath(file.getAbsolutePath());

        // liste de dossier. <!> le dernier index est le nom du fichier + extension
        String[] dossiers = path.replace(base, "").split(Pattern.quote(File.separator));

        this.dossiers = Arrays.copyOfRange(dossiers, 0, dossiers.length - 1);
        this.nom = dossiers[dossiers.length - 1];
        this.taille = this.file.length();
        this.derniereModification = new SimpleDateFormat("HH/MM/dd hh:mm").format(this.file.lastModified());
    }

    protected void setBase(String base) {
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
    public Image getIcone() {
        return Icons.getIcone(this);
    }

    protected String formatPath(String path) {
        return (path.endsWith(File.separator) ? path : (path += File.separator));
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
