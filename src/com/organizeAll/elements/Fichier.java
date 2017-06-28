package com.organizeAll.elements;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Nico on 27/06/2017.
 */
public class Fichier extends Element {
    private String extension;

    Fichier(File file, String base) {
        super(file, base);

        String[] fileAndExt = getNom().split(Pattern.quote("."));

        this.type = Type.FICHIER;
        this.extension = fileAndExt[fileAndExt.length - 1];
        this.nom = String.join(".", Arrays.copyOfRange(fileAndExt, 0, fileAndExt.length - 1));
    }

    @Override public String getChemin() {
        return super.getChemin() + "." + getExtension();
    }
    public String getExtension() {
        return extension;
    }
    public String getNomEtExtension() {
        return getNom() + "." + getExtension();
    }

    @Override
    public String toString() {
        return "Fichier{" +
                "extension='" + extension + '\'' +
                ", nom='" + nom + '\'' +
                ", base='" + base + '\'' +
                ", dossiers=" + Arrays.toString(dossiers) +
                ", derniereModification='" + derniereModification + '\'' +
                ", taille=" + taille +
                '}';
    }
}
