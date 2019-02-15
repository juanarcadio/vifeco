package org.laeq.video.category;

import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;
import org.laeq.VifecoView;
import org.laeq.model.Category;
import org.laeq.video.CategoryController;
import org.laeq.video.CategoryModel;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;

import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import static javafx.scene.layout.AnchorPane.setRightAnchor;

@ArtifactProviderFor(GriffonView.class)
public class CategoryView extends AbstractJavaFXGriffonView {
    @MVCMember @Nonnull private CategoryController controller;
    @MVCMember @Nonnull private CategoryModel model;
    @MVCMember @Nonnull private VifecoView parentView;

    @FXML private Pane categoryPane;
    @FXML private Text totalLabel;

    private Node parent;
    private HashMap<Category, CategoryGroup>  categoryList;

    @Override
    public void initUI() {
        parent = loadFromFXML();
        categoryList = new HashMap<>();

//        model.generateProperties(categoryList.keySet());
//        categoryList.forEach((k, v) -> {
//            v.getTextLabel().textProperty().bind(model.getCategoryProperty(k).asString());
//        });
//        totalLabel.textProperty().bind(model.totalProperty().asString());

        parentView.getMiddlePane().getItems().add(parent);
    }

    public void clearView(){
        categoryList.forEach((k, v) -> {
            v.getTextLabel().textProperty().unbind();
        });

        totalLabel.textProperty().unbind();

        ((AnchorPane)parent).getChildren().removeAll(categoryList.values());
        categoryList.clear();
    }

    private CategoryGroup generateCategoryGroup(Category category, int index) throws FileNotFoundException {
        CategoryGroup group = new CategoryGroup(category.getIcon());
        group.setStyle("-fx-background-color: rgb(250,250,250);");

        group.setMaxWidth(Control.USE_COMPUTED_SIZE);
        group.setMinWidth(Control.USE_COMPUTED_SIZE);
        group.setPrefWidth(Control.USE_COMPUTED_SIZE);
        group.setPrefHeight(60);

        group.setLayoutX(10);
        group.setLayoutY(65 * index + 44);

        setLeftAnchor(group, 1d);
        setRightAnchor(group, 1d);

        return group;
    }

    public void initView() {
        clearView();

        int i = 0;
        for (Category category: model.getCategorySet()) {
            try {
                URL path = getApplication().getResourceHandler().getResourceAsURL(category.getIcon());

                if(path == null){
                    path = getApplication().getResourceHandler().getResourceAsURL("icons/truck-mvt-blk-64.png");
                }

                CategoryGroup group = new CategoryGroup(path.getPath());
                group.setStyle("-fx-background-color: rgb(250,250,250);");
                group.setMaxWidth(Control.USE_COMPUTED_SIZE);
                group.setMinWidth(Control.USE_COMPUTED_SIZE);
                group.setPrefWidth(Control.USE_COMPUTED_SIZE);
                group.setPrefHeight(60);
                group.setLayoutX(10);
                group.setLayoutY(65 * i + 44);
                setLeftAnchor(group, 1d);
                setRightAnchor(group, 1d);
                categoryList.put(category, group);
                ++i;

            } catch (FileNotFoundException e) {
                getLog().error(String.format("CategoryView: cannot load file for %s.", category));
            }
        }

        ((AnchorPane)parent).getChildren().addAll(categoryList.values());

        categoryList.forEach((k, v) -> {
            v.getTextLabel().textProperty().bind(model.getCategoryProperty(k).asString());
        });

        totalLabel.textProperty().bind(model.totalProperty().asString());
    }
}
