package com.organizeAll;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import com.organizeAll.elements.Element;
import com.organizeAll.elements.Fichier;

import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

/**
 * Nico on 27/06/2017.
 */
public class Icons {
    private static Image iconeDossier = null;
    private static HashMap<String, Image> iconeFichier = new HashMap<>();

    public static Image getIcone(Element element) {
        Image img;

        if (element.getType() == Element.Type.DOSSIER) {
            img = iconeDossier;
            if (img == null) {
                img = iconeDossier = getIcone(element.getFile());
            }
        } else {
            img = iconeFichier.get(((Fichier) element).getExtension());
            if (img == null) {
                img = getIcone(element.getFile());
                iconeFichier.put(((Fichier) element).getExtension(), img);
            }
        }

        return img;
    }

    private static Image getIcone(File f) {
        Image img;

        FileSystemView view = FileSystemView.getFileSystemView();
        javax.swing.Icon icon = view.getSystemIcon(f);
        BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        img = SwingFXUtils.toFXImage(bufferedImage, null);

        return img;
    }
}
