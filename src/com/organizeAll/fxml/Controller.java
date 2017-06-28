package com.organizeAll.fxml;

import com.organizeAll.ListViewCell;
import com.organizeAll.elements.Element;
import com.organizeAll.elements.Fichier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import com.organizeAll.OrganizeAll;
import javafx.util.Pair;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {
    enum Tri {
        DEFAUT,
        TAILLE,
        EXTENSION,
        DERNIERE_MODIFICATION,
        DOSSIER
    }

    // Constant:
    private final static String USER_HOME = System.getProperty("user.home");

    @FXML private Button bDossier;
    @FXML private Button bPrevisualiser;
    @FXML private Label lNombreElements;
    @FXML private Label lNombreDossiers;
    @FXML private Label lExtensionsDifferentes;
    @FXML private CheckBox cbDeepSearch;
    @FXML private ComboBox<Tri> cbTri;
    @FXML private TextField tfPrefixe;
    @FXML private ListView<Element> lvItems;

    private OrganizeAll organizeAll = new OrganizeAll();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvItems.setCellFactory(item -> new ListViewCell());
        cbTri.setItems(FXCollections.observableArrayList(Tri.values()));

        setDossier(new File(USER_HOME + "/Pictures/Wallhaven"));
    }

    @FXML
    private void previsualiser() {

    }

    /**
     * Choix du dossier au clic sur le bouton "Dossier: "
     */
    @FXML
    private void choisirDossier() {
        DirectoryChooser directoryChooser = new DirectoryChooser(); // init
        directoryChooser.setTitle("Choisir un dossier"); // titre
        directoryChooser.setInitialDirectory(new File(USER_HOME)); // dossier initial

        File choix = directoryChooser.showDialog(null); // on montre la fenêtre
        if (choix != null) { // l'utilisateur a choisi un dossier
            setDossier(choix);
        }
    }

    private void initDossier(File dossier) {
        ObservableList<Element> items = organizeAll.initDossier(dossier);

        if (items != null)
            lvItems.setItems(items);
        else
            System.err.println("Erreur: impossible de charger le dossier " + dossier.getAbsolutePath());
    }

    private void setDossier(File dossier) {
        bDossier.setText(dossier.getAbsolutePath());
        initDossier(dossier);

        lNombreElements.setText(String.valueOf(organizeAll.getNombreElements()));
        lNombreDossiers.setText(String.valueOf(organizeAll.getNombreDossiers()));
        lExtensionsDifferentes.setText(String.valueOf(organizeAll.getExtensionsDifferentes()));
    }

    public static void erreur(String message) {

    }
}
