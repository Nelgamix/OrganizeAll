package com.organizeAll;

import com.organizeAll.elements.Dossier;
import com.organizeAll.elements.Fichier;
import com.organizeAll.fxml.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.organizeAll.elements.Element;
import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Nico on 27/06/2017.
 */
public class OrganizeAll {
    private final static String PATTERN = "(\\*+)";

    private File base;
    private Dossier dossierBase;

    private int nombreElements;
    private int nombreDossiers;
    private int extensionsDifferentes;

    public OrganizeAll() {}

    public ObservableList<Element> initDossier(File dossier) {
        if (!dossier.exists() || !dossier.isDirectory()) return null;

        base = dossier;
        analyseDossier(dossier);

        nombreElements = dossierBase.getFichiers().size();
        nombreDossiers = dossierBase.getSousDossiers().size();
        extensionsDifferentes = 0;
        countExtensions();

        return FXCollections.observableArrayList(dossierBase.getElements());
    }

    private void analyseDossier(File dossier) {
        dossierBase = new Dossier(dossier, dossier.getAbsolutePath());
        dossierBase.deepSearch();
    }

    private void countExtensions() {
        ArrayList<String> ext = new ArrayList<>();
        String ex;
        Fichier f;
        for (Element e : dossierBase.getFichiers()) {
            f = (Fichier) e;

            ex = f.getExtension();
            if (!ext.contains(ex)) {
                extensionsDifferentes++;
                ext.add(ex);
            }
        }
    }

    public int getNombreElements() {
        return nombreElements;
    }
    public int getNombreDossiers() {
        return nombreDossiers;
    }
    public int getExtensionsDifferentes() {
        return extensionsDifferentes;
    }

    public ArrayList<Pair<Fichier, File>> analyseBase(String prefix) {
        ArrayList<Pair<Fichier, File>> toRename = new ArrayList<>();

        Pattern p1 = Pattern.compile(PATTERN);

        //int numberCount = StringUtils.countMatches(prefix, "*");
        int precond = 0;

        Matcher m1 = p1.matcher(prefix);
        while (m1.find())
            precond++;

        if (prefix.length() == 0 || precond != 1) {
            Controller.erreur("Pattern non valide");
            return null;
        }

        // Fichiers dans la gridView
        ArrayList<Fichier> fichiers = new ArrayList<>(items);

        // Fichiers "matchs" = fichiers qui ont déjà le bon format
        HashMap<Integer, Fichier> matchs = new HashMap<>();

        // Patterne compilé
        String capturePattern = prefix.replaceAll(PATTERN, "(.*)");
        Pattern p = Pattern.compile(capturePattern);

        // Temporaire
        Matcher m;
        Fichier ita;
        for (Iterator<Fichier> it = fichiers.iterator(); it.hasNext();) { // Pour chaque fichier
            ita = it.next(); // Get le next dans l'itérateur
            m = p.matcher(ita.getNom()); // Match le pattern sur le nom du fichier actuel
            if (m.find()) { // si le matcher a trouvé
                it.remove(); // on le traite autre part (dans la HashMap)

                Integer index = Integer.parseInt(m.group(1));
                matchs.put(index, ita);
            }
        }

        // On traite d'abord les fichiers déjà au bon format
        int i = 1;
        String newName;
        File newFile;
        TreeMap<Integer, Fichier> matchsSorted = new TreeMap<>(matchs);
        for (Fichier matched : matchsSorted.values()) {
            newName = format(prefix, i++, numberCount);
            newFile = new File(matched.getDossier() + newName + "." + matched.getExtension());
            if (!newFile.exists()) {
                toRename.add(new Pair<>(matched, newFile));
            }
        }

        // Puis le reste
        for (Fichier nonMatched : fichiers) {
            newName = format(prefix, i++, numberCount);
            newFile = new File(nonMatched.getDossier() + newName + "." + nonMatched.getExtension());
            if (!newFile.exists()) {
                toRename.add(new Pair<>(nonMatched, newFile));
            }
        }

        return toRename;
    }
}
