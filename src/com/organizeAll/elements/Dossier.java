package com.organizeAll.elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Nico on 27/06/2017.
 */
public class Dossier extends Element {
    // Constant: Level max for deepSearch
    private final static int MAX_LEVEL = 5;
    
    private ArrayList<Element> elements; // liste de sous éléments

    /**
     * Créer un dossier à partir d'une base déjà construite.
     * @param file le fichier correspondant
     * @param base la base déjà construite
     */
    public Dossier(File file, String base) {
        super(file, base);

        this.type = Type.DOSSIER;
        this.elements = new ArrayList<>();
    }

    private boolean addElement(Element e) {
        return elements.add(e);
    }

    public ArrayList<Element> getElements() {
        return elements;
    }
    public ArrayList<Fichier> getFichiers() {
        ArrayList<Fichier> ret = new ArrayList<>();
        for (Element e : elements.stream().filter(e -> e.getType() == Type.FICHIER).collect(Collectors.toList()))
            ret.add((Fichier) e);
        return ret;
    }
    public ArrayList<Fichier> getFichiers(boolean deepSearch) {
        ArrayList<Fichier> ret = new ArrayList<>();

        // si deep search
        if (deepSearch) {
            for (Dossier d : getSousDossiers(true))
                ret.addAll(d.getFichiers());
        }

        ret.addAll(getFichiers());

        return ret;
    }
    public ArrayList<Dossier> getSousDossiers() {
        ArrayList<Dossier> ret = new ArrayList<>();
        for (Element e : elements.stream().filter(e -> e.getType() == Type.DOSSIER).collect(Collectors.toList()))
            ret.add((Dossier) e);
        return ret;
    }
    private ArrayList<Dossier> getSousDossiers(boolean deepSearch) {
        if (!deepSearch) return getSousDossiers();
        return getSousDossier(0, MAX_LEVEL);
    }
    private ArrayList<Dossier> getSousDossier(int level, int maxLevel) {
        ArrayList<Dossier> a = new ArrayList<>();

        for (Dossier d : getSousDossiers()) {
            a.add(d);
            if (level < maxLevel) {
                a.addAll(d.getSousDossier(level + 1, maxLevel));
            }
        }

        return a;
    }

    public void deepSearch() {
        deepSearch(MAX_LEVEL);
    }
    private void deepSearch(int maxLevel) {
        deepSearch(0, maxLevel);
    }
    private void deepSearch(int level, int maxLevel) {
        if (level == maxLevel) return;

        Dossier t;
        File[] dossiers = file.listFiles(File::isDirectory);
        if (dossiers != null) {
            for (File d : dossiers) {
                t = new Dossier(d, base);
                if (addElement(t)) {
                    t.deepSearch(level + 1, maxLevel);
                }
            }
        }

        File[] fichiers = file.listFiles(File::isFile);
        if (fichiers != null)
            for (File f : fichiers)
                addElement(new Fichier(f, base));
    }

    @Override
    public String toString() {
        return "Dossier{" +
                "elements=" + elements +
                ", nom='" + nom + '\'' +
                ", base='" + base + '\'' +
                ", dossiers=" + Arrays.toString(dossiers) +
                ", derniereModification='" + derniereModification + '\'' +
                ", taille=" + taille +
                '}';
    }
}
