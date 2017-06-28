package com.organizeAll.elements;

import java.io.File;
import java.util.ArrayList;
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
    public ArrayList<Dossier> getSousDossiers() {
        ArrayList<Dossier> ret = new ArrayList<>();
        for (Element e : elements.stream().filter(e -> e.getType() == Type.DOSSIER).collect(Collectors.toList()))
            ret.add((Dossier) e);
        return ret;
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
}
