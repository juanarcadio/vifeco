package org.laeq.video.category;

import eu.lestard.advanced_bindings.api.CollectionBindings;
import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;
import org.laeq.VifecoView;
import org.laeq.model.Category;
import org.laeq.model.CategoryService;
import org.laeq.video.CategoryController;
import org.laeq.video.CategoryModel;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

@ArtifactProviderFor(GriffonView.class)
public class CategoryView extends AbstractJavaFXGriffonView {
    @MVCMember @Nonnull private CategoryController controller;
    @MVCMember @Nonnull private CategoryModel model;
    @MVCMember @Nonnull private VifecoView parentView;

    @FXML private Pane categoryPane;
    @FXML private Text totalLabel;

    @Inject
    private CategoryService service;

    private Category[] categories;

    @Override
    public void initUI() {
        this.categories = service.getCategoryList();

        Node node = loadFromFXML();

//        totalLabel.setText(0);


        HashMap<Category, CategoryGroup> categoryList = new HashMap<>();
        try {
            for (int i = 0; i < categories.length; i++) {
                CategoryGroup group = new CategoryGroup(categories[i].getIcon());
                group.setLayoutX(10);
                group.setLayoutY(65 * i);
                categoryList.put(categories[i], group);

            }
        } catch (FileNotFoundException e){
            getLog().error(e.getMessage());
        }


        categoryPane.getChildren().addAll(categoryList.values());
        model.generateProperties(categoryList.keySet());


        categoryList.forEach((k, v) -> {
            v.getLabel().textProperty().bind(model.getCategoryProperty(k).asString());
        });

        totalLabel.setText("12");


        totalLabel.textProperty().bind(model.totalProperty().asString());

        parentView.getMiddlePane().getItems().add(node);
    }


    private Group generateCategory(String filePath) throws FileNotFoundException {
        Group group = new Group();

        String path = getApplication().getResourceHandler().getResourceAsURL(filePath).getPath();

        FileInputStream inputStream = new FileInputStream(path);
        Image image = new Image(inputStream);
        ImageView imageView = new ImageView(image);
        imageView.setX(0);
        imageView.setY(0);
        imageView.setPreserveRatio(true);
        imageView.setScaleX(0.6);
        imageView.setScaleY(0.6);


        Label label = new Label("12 / 123");
        label.setFont(new Font("Arial", 15));
        label.setLayoutX(76);
        label.setLayoutY(22);
        label.setPrefHeight(17);
        label.setPrefWidth(124);

        group.getChildren().addAll(imageView, label);

        return group;
    }
}
