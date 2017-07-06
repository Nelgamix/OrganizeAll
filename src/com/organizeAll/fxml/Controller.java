package com.organizeAll.fxml;

import com.organizeAll.ListViewCell;
import com.organizeAll.elements.Element;
import com.organizeAll.elements.Fichier;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import com.organizeAll.OrganizeAll;
import javafx.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    public enum Tri {
        DEFAUT,
        NOM,
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
    @FXML private TextField tfIndexationDebut;
    @FXML private ListView<Element> lvItems;

    private OrganizeAll organizeAll = new OrganizeAll();

    private void setupTestFolder(String folder) {
        File f = new File(folder);

        ArrayList<File> files = new ArrayList<>(
                Arrays.asList(
                        new File(f, "test.txt"),
                        new File(f, "file_1.png"),
                        new File(f, "aaa.txt"),
                        new File(f, "file_2.mp4"),
                        new File(f, "try.txt"),
                        new File(f, "v1z1.png")
                )
        );

        File dolder = new File(f, "dolder");
        if (!dolder.exists())
            dolder.mkdir();

        ArrayList<File> filesDolder = new ArrayList<>(
                Arrays.asList(
                        new File(dolder, "sisi.jpg"),
                        new File(dolder, "try.mp4"),
                        new File(dolder, "nop.txt")
                )
        );

        try {
            for (File ft : files)
                if (!ft.exists())
                    new FileOutputStream(ft).close();
            for (File ft : filesDolder)
                if (!ft.exists())
                    new FileOutputStream(ft).close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvItems.setCellFactory(item -> new ListViewCell(this));
        cbTri.setItems(FXCollections.observableArrayList(Tri.values()));
        cbTri.getSelectionModel().selectFirst();

        /*tfPrefixe.setText("file_*");

        String testFolder = "C:\\Users\\Nico\\IdeaProjects\\organizeAll\\Test";
        setupTestFolder(testFolder);
        setDossier(new File(testFolder));*/

        setDossier(new File(USER_HOME));
    }

    @FXML
    private void previsualiser() {
        int indexDeb;
        try {
            indexDeb = Integer.parseInt(tfIndexationDebut.getText());
        } catch (NumberFormatException n) {
            erreur("Le numéro d'indexation de début est invalide");
            return;
        }

        ArrayList<Pair<Fichier, File>> l = organizeAll.analyse(cbTri.getSelectionModel().getSelectedItem(), tfPrefixe.getText(), cbDeepSearch.isSelected(), indexDeb);
        if (l == null) {
            erreur("Le préfixe est invalide.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();

        BorderPane b = new BorderPane();

        Label titre = new Label("Prévisualisation");
        titre.setFont(new Font(null, 24));

        TableView<Pair<Fichier, File>> tableView = new TableView<>();
        TableColumn<Pair<Fichier, File>, String> columnFichier = new TableColumn<>("Fichier concerné");
        TableColumn<Pair<Fichier, File>, String> columnFile = new TableColumn<>("... sera renommé en");

        columnFichier.setCellValueFactory(e -> new ReadOnlyStringWrapper(e.getValue().getKey().getNomEtExtension()));
        columnFile.setCellValueFactory(e -> new ReadOnlyStringWrapper(organizeAll.removeBaseFromString(e.getValue().getValue().getAbsolutePath())));

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().addAll(columnFichier, columnFile);
        tableView.setItems(FXCollections.observableArrayList(l));

        b.setTop(titre);
        b.setCenter(tableView);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK)
                if (organizeAll.renommage())
                    reloadDossier();

            return null;
        });
        dialog.getDialogPane().setContent(b);
        dialog.getDialogPane().setPrefSize(400, 400);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setTitle("Prévisualisation");
        dialog.showAndWait();
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

    @FXML
    private void montrerPrefixes() {
        ArrayList<String> lp = organizeAll.getLp();

        Dialog<String> dialog = new Dialog<>();

        BorderPane borderPane = new BorderPane();
        Label titre = new Label("Préfixes détectés parmi les fichiers");
        titre.setFont(new Font(null, 24));

        ListView<String> listView = new ListView<>();
        listView.setItems(FXCollections.observableArrayList(lp));
        listView.setCellFactory(e ->
            new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setOnMouseClicked(null);
                        setText(null);
                    } else {
                        setOnMouseClicked(e -> {
                            if (e.getClickCount() == 2)
                                ((Button) dialog.getDialogPane().lookupButton(ButtonType.OK)).fire();
                        });
                        setText(item);
                    }
                }
        });

        borderPane.setTop(titre);
        borderPane.setCenter(listView);

        dialog.getDialogPane().setContent(borderPane);
        dialog.setResultConverter(b -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (b == ButtonType.OK && selected != null)
                tfPrefixe.setText(selected);

            return null;
        });
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setTitle("Choisir un préfixe");
        dialog.showAndWait();
    }

    public void supprimer(Element e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Supprimer");
        alert.setHeaderText("Suppression de l'élément " + e.getChemin());
        alert.setContentText("Voulez-vous vraiment supprimer cet élément?\nCette opération est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            e.delete();
            reloadDossier();
        }
    }
    public void renommer(Element e) {
        TextInputDialog textInputDialog = new TextInputDialog(e.getNom());
        textInputDialog.setTitle("Renommer");
        textInputDialog.setHeaderText("Renommage de l'élément " + e.getChemin());
        textInputDialog.setContentText("Entrez un nouveau nom");

        Optional<String> result = textInputDialog.showAndWait();
        if (result.isPresent()) {
            String nc = e.getBase() + result.get();
            if (e.getType() == Element.Type.FICHIER)
                nc += "." + ((Fichier) e).getExtension();
            File ft = new File(nc);
            e.rename(ft);
            reloadDossier();
        }
    }
    public void debug(Element e) {
        Dialog<Void> dialog = new Dialog<>();
        BorderPane borderPane = new BorderPane();
        TextArea textArea = new TextArea(e.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        borderPane.setCenter(textArea);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().setContent(borderPane);
        dialog.showAndWait();
    }

    private void initDossier(File dossier) {
        ObservableList<Element> items = organizeAll.initDossier(dossier);

        if (items != null)
            lvItems.setItems(items);
        else
            System.err.println("Erreur: impossible de charger le dossier " + dossier.getAbsolutePath());
    }

    private void reloadDossier() {
        setDossier(organizeAll.getBase());
    }
    private void setDossier(File dossier) {
        bDossier.setText(dossier.getAbsolutePath());
        initDossier(dossier);

        lNombreElements.setText(String.valueOf(organizeAll.getNombreElements()));
        lNombreDossiers.setText(String.valueOf(organizeAll.getNombreDossiers()));
        lExtensionsDifferentes.setText(String.valueOf(organizeAll.getExtensionsDifferentes()));
    }

    private static void erreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Une erreur est survenue");
        alert.setContentText(message);

        alert.showAndWait();
    }
}
