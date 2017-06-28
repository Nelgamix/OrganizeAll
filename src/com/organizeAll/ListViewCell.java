package com.organizeAll;

import com.organizeAll.elements.Element;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import com.organizeAll.elements.Fichier;

/**
 * Nico on 27/06/2017.
 */
public class ListViewCell extends ListCell<Element> {
    private ImageView imageView = new ImageView();

    public ListViewCell() {
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
    }

    private void clearContenu() {
        setGraphic(null);
        setText(null);
        setOnMouseClicked(null);
    }
    private void setContenu(Element item) {
        imageView.setImage(item.getIcone());
        setGraphic(imageView);
        setOnMouseClicked(e -> {
            if (e.getClickCount() == 2)
                item.open();
        });

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
