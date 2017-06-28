package com.organizeAll;

import com.organizeAll.elements.Element;
import com.organizeAll.fxml.Controller;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import com.organizeAll.elements.Fichier;

/**
 * Nico on 27/06/2017.
 */
public class ListViewCell extends ListCell<Element> {
    private final Controller controller;

    private ImageView imageView = new ImageView();
    private ContextMenu contextMenu = new ContextMenu();
    private MenuItem menuItemRenommer = new MenuItem("Renommer");
    private MenuItem menuItemSupprimer = new MenuItem("Supprimer");
    private MenuItem menuItemDebug = new MenuItem("Debug");

    public ListViewCell(Controller c) {
        controller = c;

        imageView.setFitHeight(16);
        imageView.setFitWidth(16);

        contextMenu.getItems().addAll(menuItemRenommer, menuItemSupprimer, menuItemDebug);
    }

    private void clearContenu() {
        setGraphic(null);
        setText(null);
        setOnMouseClicked(null);
        setTooltip(null);
        setContextMenu(null);
    }
    private void setContenu(Element item) {
        imageView.setImage(item.getIcone());
        setGraphic(imageView);
        setOnMouseClicked(e -> {
            if (e.getClickCount() == 2)
                item.open();
        });
        setTooltip(new Tooltip("Taille : " + item.getTailleHumain(true)));
        setContextMenu(contextMenu);

        menuItemRenommer.setOnAction(e -> controller.renommer(item));
        menuItemSupprimer.setOnAction(e -> controller.supprimer(item));
        menuItemDebug.setOnAction(e -> controller.debug(item));

        if (item.getType() == Element.Type.FICHIER)
            setText(((Fichier) item).getNomEtExtension());
        else
            setText(item.getNom());
    }

    @Override
    protected void updateItem(Element item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            clearContenu();
        } else {
            setContenu(item);
        }
    }
}
