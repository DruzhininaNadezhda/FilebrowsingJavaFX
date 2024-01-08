package com.example.druzhinia_120_4;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Menu {
    TextArea text = new TextArea();
    private Map<TreeItem<String>, File> maps;
    private Map<TreeItem<String>, File> fileForRead;
    File systemFil = new File("").getAbsoluteFile();
    static TreeView<String> menu;
    AnchorPane textOfFiles;
    AnchorPane systemFileMenu;
    Stage stage;
    Scene scene;
    SplitPane root;

    public TreeView init(File systemFile) {
        maps = new HashMap<>();
        fileForRead = new HashMap<>();
        TreeItem<String> rootItem = new TreeItem<>(systemFile.getName());
        maps.put(rootItem, systemFile);
        rootItem.getChildren().add(new TreeItem<>(""));
        sortedAll(rootItem);
        menu = new TreeView<>(rootItem);
        menu.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                oneParentClickOnArrow();
                oneMenuClickOnArrow();
                textMenuClickOnArrow();
            }
        });
        AnchorPane.setTopAnchor(menu, 0.0);
        AnchorPane.setBottomAnchor(menu, 0.0);
        AnchorPane.setLeftAnchor(menu, 0.0);
        AnchorPane.setRightAnchor(menu, 0.0);
        return menu;
    }
    public Stage forStart () {
        textOfFiles = new AnchorPane();
        systemFileMenu= new AnchorPane();
        systemFileMenu.getChildren().add(init(systemFil));
        root = new SplitPane(systemFileMenu, textOfFiles);
        scene = new Scene(root, 600, 400);
        text.setEditable(false);
        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(systemFil.toString());
        return stage;
    }
    private void openAll(TreeItem<String> item, File file) {
        item.getChildren().clear();
        item.getChildren().add(new TreeItem<>("..."));
        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                TreeItem<String> itemm = new TreeItem<>(f.getName());
                item.getChildren().add(itemm);
                if (f.isDirectory()) {
                    itemm.getChildren().add(new TreeItem<>(""));
                } else if (f.getName().endsWith(".txt") || f.getName().endsWith(".doc") || f.getName().endsWith(".docx")) {
                    fileForRead.put(itemm, f);
                    System.out.println(fileForRead);
                }
                maps.put(itemm, f);

            }
        } else {
            item.getChildren().clear();
        }

        sortedAll(item);
    }
    private void sortedAll(TreeItem<String> item) {
        ObservableList<TreeItem<String>> list = item.getChildren();
        if (!list.isEmpty()) {
            list.sort((item1, item2) -> {
                if (!item1.getChildren().isEmpty() && item2.getChildren().isEmpty()) {
                    return -1;
                } else if (item1.getChildren().isEmpty() && !item2.getChildren().isEmpty()) {
                    return 1;
                } else if (Objects.equals(item1.getValue(), "...") || Objects.equals(item2.getValue(), "...")) {
                    return 1;
                } else {
                    return item1.getValue().compareTo(item2.getValue());
                }
            });
            list.forEach(this::sortedAll);
        }
    }
    public void oneMenuClickOnArrow() {
        MultipleSelectionModel<TreeItem<String>> selectionModel = menu.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        if (selectionModel.getSelectedItem() != null && !fileForRead.containsKey(selectionModel.getSelectedItem())) {
            openAll(selectionModel.getSelectedItem(), maps.get(selectionModel.getSelectedItem()));
            text.clear();
        }
    }
    public void textMenuClickOnArrow() {
        MultipleSelectionModel<TreeItem<String>> selectionModel = menu.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        StringBuilder sb;
        if (selectionModel.getSelectedItem() != null && fileForRead.containsKey(selectionModel.getSelectedItem())) {
            text = new TextArea();
            sb = new StringBuilder();
            if (fileForRead.get(selectionModel.getSelectedItem()) != null && fileForRead.get(selectionModel.getSelectedItem()).canRead()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(fileForRead.get(selectionModel.getSelectedItem())))) {
                    String temp;
                    while ((temp = reader.readLine()) != null) {
                        sb.append(temp).append("\n");
                    }
                    fileForRead.get(selectionModel.getSelectedItem()).setWritable(false);
                } catch (IOException ioe) {
                }
                text.setText(sb.toString());
                AnchorPane.setTopAnchor(text, 0.0);
                AnchorPane.setBottomAnchor(text, 0.0);
                AnchorPane.setLeftAnchor(text, 0.0);
                AnchorPane.setRightAnchor(text, 0.0);
                text.setEditable(false);
                 textOfFiles.getChildren().add(text);
            }
        }
    }
    public void oneParentClickOnArrow() {
        MultipleSelectionModel<TreeItem<String>> selectionModel = menu.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        if (Objects.equals(selectionModel.getSelectedItem().getValue(), "...")) {
            String d = systemFil.toString();
            d = d.substring(0, d.lastIndexOf("\\"));
            systemFil = new File(d);
            systemFileMenu.getChildren().add(init(systemFil));
            textOfFiles.getChildren().clear();
            stage.setTitle(systemFil.toString());
        }
    }
}