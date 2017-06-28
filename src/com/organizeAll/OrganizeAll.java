package com.organizeAll;

import com.organizeAll.elements.Dossier;
import com.organizeAll.elements.Fichier;
import com.organizeAll.fxml.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.organizeAll.elements.Element;
import javafx.util.Pair;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Nico on 27/06/2017.
 */
public class OrganizeAll {
    private final static String PATTERN = "(\\*+)";
    private final static String PATTERN_DIGIT = "(\\d+)";
    private static int nbReplace;

    private File base;
    private Dossier dossierBase;

    private int nombreElements;
    private int nombreDossiers;
    private int extensionsDifferentes;

    private ArrayList<Fichier> lt;
    private ArrayList<Pair<Fichier, File>> lf;
    private ArrayList<Fichier> lc;
    private ArrayList<String> lp;

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
        lp = findPrefixes(dossierBase.getFichiers());
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

    private String formatterAvecPattern(String prefixe, int num) {
        String iFormatted = "";
        for (int i = nbReplace - 1; i > 0; i--)
            if (num < Math.pow(10, i))
                iFormatted += "0";
            else
                break;

        return prefixe.replaceAll(PATTERN, iFormatted + String.valueOf(num));
    }

    /**
     * Associe un nouveau nom à chaque fichier fourni grâce au prefixe
     * @param lf la liste de fichier à renommer
     * @param prefixe le prefixe sur lequel se baser pour le renommage
     * @return la liste des fichiers fournis, avec leur nouveau java.io.File renommé
     */
    private ArrayList<Pair<Fichier, File>> nommage(ArrayList<Fichier> lf, String prefixe) {
        int i = 1;
        ArrayList<Pair<Fichier, File>> lp = new ArrayList<>();

        File nf;
        for (Fichier f : lf) {
            nf = new File(f.getBase() + formatterAvecPattern(prefixe, i++) + "." + f.getExtension());
            lp.add(new Pair<>(f, nf));
        }

        return lp;
    }

    /**
     * Cherche les conflits de renommage
     * @param lp la liste des fichiers, avec leur java.io.File renommé
     * @return une liste des fichiers qui vont poser problème pour le renommage
     */
    private ArrayList<Fichier> conflits(ArrayList<Pair<Fichier, File>> lp) {
        ArrayList<Fichier> lc = new ArrayList<>();

        Pair<Fichier, File> lpe;
        for (Iterator<Pair<Fichier, File>> it = lp.iterator(); it.hasNext();) {
            lpe = it.next();
            if (lpe.getKey().getCheminAbsolu().equals(lpe.getValue().getAbsolutePath())) {
                it.remove();
            } else if (lpe.getValue().exists()) {
                lc.add(getFichier(lpe.getValue().getAbsolutePath()));
            }
        }

        return lc;
    }

    private void renommageConflits(ArrayList<Fichier> lc) {
        File ctmp = null;
        int i = 0;
        for (Fichier f : lc) {
            while (ctmp == null || ctmp.exists()) ctmp = new File(base, "conflitResolver" + (i++) + ".conflit");
            f.rename(ctmp, true);
        }
    }
    private void renommageFinal(ArrayList<Pair<Fichier, File>> lf) {
        for (Pair<Fichier, File> e : lf)
            e.getKey().rename(e.getValue());
    }

    private ArrayList<Fichier> tri(ArrayList<Fichier> l, Controller.Tri type, String prefixe) {
        ArrayList<Fichier> lt = new ArrayList<>(l);
        switch (type) {
            case DEFAUT:
                // Fichiers "matchs" = fichiers qui ont déjà le bon format
                HashMap<Integer, Fichier> matchs = new HashMap<>();

                // Patterne compilé
                String capturePattern = prefixe.replaceAll(PATTERN, "(.*)");
                Pattern p = Pattern.compile(capturePattern);

                // Temporaire
                Matcher m;
                Fichier ita;
                for (Iterator<Fichier> it = lt.iterator(); it.hasNext();) { // Pour chaque fichier
                    ita = it.next(); // Get le next dans l'itérateur
                    m = p.matcher(ita.getNom()); // Match le pattern sur le nom du fichier actuel
                    if (m.find()) { // si le matcher a trouvé
                        it.remove(); // on le traite autre part (dans la HashMap)

                        Integer index = Integer.parseInt(m.group(1));
                        matchs.put(index, ita);
                    }
                }

                // On traite d'abord les fichiers déjà au bon format
                TreeMap<Integer, Fichier> matchsSorted = new TreeMap<>(matchs);

                int i = 0;
                for (Fichier f : matchsSorted.values())
                    lt.add(i++, f);

                break;
            case NOM:
                lt.sort(Comparator.comparing(Fichier::getNom));
                break;
            case TAILLE:
                lt.sort(Comparator.comparing(Fichier::getTaille));
                break;
            case DOSSIER:
                //lt.sort(Comparator.comparing(Fichier::getChemin));
                break;
            case EXTENSION:
                lt.sort(Comparator.comparing(Fichier::getExtension));
                break;
            case DERNIERE_MODIFICATION:
                lt.sort(Comparator.comparing(Fichier::getDerniereModification));
                break;
            default:
                System.err.println("Impossible de trier: tri inconnu");
                break;
        }

        return lt;
    }

    private ArrayList<String> findPrefixes(ArrayList<Fichier> lf) {
        //System.out.println("Find prefixes ---");

        ArrayList<String> liste = new ArrayList<>();

        Pattern p = Pattern.compile(PATTERN_DIGIT);
        Matcher m;
        String nom;
        for (Fichier f : lf) {
            //System.out.print("Fichier " + f.getNom() + ": ");
            m = p.matcher(f.getNom());
            while (m.find()) {
                nom = f.getNom().substring(0, m.start());
                for (int i = m.start(); i < m.end(); i++)
                    nom += "*";
                nom += f.getNom().substring(m.end(), f.getNom().length());

                if (!liste.contains(nom)) liste.add(nom);

                //System.out.print(" '" + nom + "' ");
            }
            //System.out.println();
        }

        //System.out.println("Fin prefixes end ---");

        return liste;
    }

    public ArrayList<Pair<Fichier, File>> analyse(Controller.Tri tri, String prefixe, boolean deepSearch) {
        if (!verifierPrefixe(prefixe)) return null;

        lt = tri(dossierBase.getFichiers(deepSearch), tri, prefixe);

        lf = nommage(lt, prefixe);
        lc = conflits(lf);

        return lf;
    }
    public boolean renommage() {
        renommageConflits(lc);
        renommageFinal(lf);

        return true;
    }

    private Fichier getFichier(String chemin) {
        for (Pair<Fichier, File> e : lf)
            if (e.getKey().getCheminAbsolu().equals(chemin))
                return e.getKey();

        return null;
    }

    public String removeBaseFromString(String s) {
        return s.replace(dossierBase.getBase(), "");
    }

    private boolean verifierPrefixe(String prefixe) {
        // nombre de * dans le préfixe
        nbReplace = prefixe.length() - prefixe.replace("*", "").length();

        // on cherche les groupes de * : les * doivent être collées (sinon le renommage ne peut pas être fait)
        int precond = 0;

        Pattern p = Pattern.compile(PATTERN);
        Matcher m = p.matcher(prefixe);
        while (m.find())
            precond++;

        return nbReplace > 0 && precond == 1;
    }

    public File getBase() {
        return base;
    }
    public ArrayList<String> getLp() {
        return lp;
    }
    //    public ArrayList<Pair<Fichier, File>> analyseBase(String prefix) {
//        ArrayList<Pair<Fichier, File>> toRename = new ArrayList<>();
//
//        Pattern p1 = Pattern.compile(PATTERN);
//
//        //int numberCount = StringUtils.countMatches(prefix, "*");
//        int precond = 0;
//
//        Matcher m1 = p1.matcher(prefix);
//        while (m1.find())
//            precond++;
//
//        if (prefix.length() == 0 || precond != 1) {
//            Controller.erreur("Pattern non valide");
//            return null;
//        }
//
//        // Fichiers dans la gridView
//        ArrayList<Fichier> fichiers = new ArrayList<>(items);
//
//        // Fichiers "matchs" = fichiers qui ont déjà le bon format
//        HashMap<Integer, Fichier> matchs = new HashMap<>();
//
//        // Patterne compilé
//        String capturePattern = prefix.replaceAll(PATTERN, "(.*)");
//        Pattern p = Pattern.compile(capturePattern);
//
//        // Temporaire
//        Matcher m;
//        Fichier ita;
//        for (Iterator<Fichier> it = fichiers.iterator(); it.hasNext();) { // Pour chaque fichier
//            ita = it.next(); // Get le next dans l'itérateur
//            m = p.matcher(ita.getNom()); // Match le pattern sur le nom du fichier actuel
//            if (m.find()) { // si le matcher a trouvé
//                it.remove(); // on le traite autre part (dans la HashMap)
//
//                Integer index = Integer.parseInt(m.group(1));
//                matchs.put(index, ita);
//            }
//        }
//
//        // On traite d'abord les fichiers déjà au bon format
//        int i = 1;
//        String newName;
//        File newFile;
//        TreeMap<Integer, Fichier> matchsSorted = new TreeMap<>(matchs);
//        for (Fichier matched : matchsSorted.values()) {
//            newName = format(prefix, i++, numberCount);
//            newFile = new File(matched.getDossier() + newName + "." + matched.getExtension());
//            if (!newFile.exists()) {
//                toRename.add(new Pair<>(matched, newFile));
//            }
//        }
//
//        // Puis le reste
//        for (Fichier nonMatched : fichiers) {
//            newName = format(prefix, i++, numberCount);
//            newFile = new File(nonMatched.getDossier() + newName + "." + nonMatched.getExtension());
//            if (!newFile.exists()) {
//                toRename.add(new Pair<>(nonMatched, newFile));
//            }
//        }
//
//        return toRename;
//    }
}
