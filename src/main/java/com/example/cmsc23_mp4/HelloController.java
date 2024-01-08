package com.example.cmsc23_mp4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.util.Random;

import java.io.*;
import java.util.Scanner;

public class HelloController {

    @FXML
    private TextField itemName;

    @FXML
    private TextField category;

    @FXML
    private TextField brand;

    @FXML
    private TextField weight;

    @FXML
    private TextField volume;

    @FXML
    private TextField quantity;

    @FXML
    private TextField color;

    @FXML
    private TextField type;

    @FXML
    private TextArea description;

    @FXML
    private TableView<Item> table;

    @FXML
    private TableColumn<Item, String> skuColumn;

    @FXML
    private TableColumn<Item, String> itemNameColumn;

    @FXML
    private TableColumn<Item, String> categoryColumn;

    @FXML
    private TableColumn<Item, String> brandColumn;

    @FXML
    private TableColumn<Item, String> weightColumn;

    @FXML
    private TableColumn<Item, String> volumeColumn;

    @FXML
    private TableColumn<Item, String> quantityColumn;

    @FXML
    private TableColumn<Item, String> colorColumn;

    @FXML
    private TableColumn<Item, String> typeColumn;

    @FXML
    private TableColumn<Item, String> descriptionColumn;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    private ObservableList<Item> itemList = FXCollections.observableArrayList();

    private Item selectedItem; // tracks the selected item for editing/deleting

    @FXML
    public void initialize() {
        // Initialize table columns
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Set the items into the table
        table.setItems(itemList);

        // Add listener to handle row selection for editing/deleting
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedItem = newSelection;
            }
        });
        // Set initial properties, dont do it initially sa scenebuilder or mahirap i-edit
        enableTextFields(false);
        confirmButton.setVisible(false);
        cancelButton.setVisible(false);
    }

    @FXML
    protected void addItem() {
        selectedItem = null;  // Clear selected item
        resetAction();
        enableTextFields(true);
        confirmButton.setVisible(true);
        cancelButton.setVisible(true);
    }

    @FXML
    protected void addExisting() {
        System.out.println("Mahiwagang Good Luck");
    }

    @FXML
    protected void cancelAction() {
        resetAction();
        enableTextFields(false);
        confirmButton.setVisible(false);
        cancelButton.setVisible(false);
    }

    @FXML
    protected void confirmAction() {
        if (selectedItem == null) {
            // Add a new item
            Item newItem = new Item("SKU", itemName.getText(), category.getText(), brand.getText(),
                    weight.getText(), volume.getText(), quantity.getText(), color.getText(),
                    type.getText(), description.getText());

            itemList.add(newItem);
        } else {
            // Edit the selected item
            selectedItem.setAllFields(itemName.getText(), category.getText(), brand.getText(),
                    weight.getText(), volume.getText(), quantity.getText(), color.getText(),
                    type.getText(), description.getText());

            // Update the TableView with the edited item
            int index = itemList.indexOf(selectedItem);
            table.getItems().set(index, selectedItem);

            // Reset the selected item after editing
            //selectedItem = null;
        }

        table.setItems(itemList);
        resetAction();
        cancelAction();  // Ensure to clear the TextFields and hide buttons

        // Clear the selected item after editing
        //selectedItem = null;
    }


    @FXML
    protected void resetAction() {
        itemName.clear();
        category.clear();
        brand.clear();
        weight.clear();
        volume.clear();
        quantity.clear();
        color.clear();
        type.clear();
        description.clear();
    }

    @FXML
    protected void exportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                for (Item item : itemList) {
                    writer.println(item.toCSV());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void importCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] values = line.split(",");
                    if (values.length == 10) {
                        Item newItem = new Item(values[0], values[1], values[2], values[3],
                                values[4], values[5], values[6], values[7], values[8], values[9]);
                        itemList.add(newItem);
                    }
                }
                table.setItems(itemList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void editExisting() {
        if (selectedItem != null) {
            setFieldsFromSelectedItem();
            enableTextFields(true);
            confirmButton.setVisible(true);
            cancelButton.setVisible(true);
        }
    }


    @FXML
    protected void deleteExisting() {
        if (selectedItem != null) {
            itemList.remove(selectedItem);
            selectedItem = null;
            table.setItems(itemList);
            resetAction();
        }
    }

    private void enableTextFields(boolean enable) {
        itemName.setDisable(!enable);
        category.setDisable(!enable);
        brand.setDisable(!enable);
        weight.setDisable(!enable);
        volume.setDisable(!enable);
        quantity.setDisable(!enable);
        color.setDisable(!enable);
        type.setDisable(!enable);
        description.setDisable(!enable);
    }

    private void setFieldsFromSelectedItem() {
        itemName.setText(selectedItem.getItemName());
        category.setText(selectedItem.getCategory());
        brand.setText(selectedItem.getBrand());
        weight.setText(selectedItem.getWeight());
        volume.setText(selectedItem.getVolume());
        quantity.setText(selectedItem.getQuantity());
        color.setText(selectedItem.getColor());
        type.setText(selectedItem.getType());
        description.setText(selectedItem.getDescription());
    }

    public static class Item {
        private String sku;
        private String itemName;
        private String category;
        private String brand;
        private String weight;
        private String volume;
        private String quantity;
        private String color;
        private String type;
        private String description;

        public String getSku() {
            return sku;
        }

        public String getItemName() {
            return itemName;
        }

        public String getCategory() {
            return category;
        }

        public String getBrand() {
            return brand;
        }

        public String getWeight() {
            return weight;
        }

        public String getVolume() {
            return volume;
        }

        public String getQuantity() {
            return quantity;
        }

        public String getColor() {
            return color;
        }

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }


        public String generateSKU(String category, String itemName) {
            // get first three consonants from category
            String categoryPrefix = getConsonantsPrefix(category, 3);
            // get first three consonants from item name
            String itemPrefix = getConsonantsPrefix(itemName, 3);

            // random number [from ]0000 to 9999
            String randomNumber = String.format("%04d", new Random().nextInt(10000));

            return categoryPrefix + "/" + itemPrefix + "-" + randomNumber;
        }

        private String getConsonantsPrefix(String input, int length) {
            StringBuilder prefix = new StringBuilder();
            int count = 0;

            for (char ch : input.toUpperCase().toCharArray()) {
                if (Character.isLetter(ch) && !isVowel(ch)) {
                    prefix.append(ch);
                    count++;
                }

                if (count == length) {
                    break;
                }
            }

            // If the word contains fewer consonants than needed, use vowels for the remaining
            while (count < length) {
                prefix.append(getNextVowel());
                count++;
            }

            return prefix.toString();
        }

        private boolean isVowel(char ch) {
            return ch == 'A' || ch == 'E' || ch == 'I' || ch == 'O' || ch == 'U';
        }

        private char getNextVowel() {
            return "AEIOU".charAt(new Random().nextInt(5));
        }


        public Item(String sku, String itemName, String category, String brand, String weight,
                    String volume, String quantity, String color, String type, String description) {
            this.sku = generateSKU(category, itemName);
            this.itemName = itemName;
            this.category = category;
            this.brand = brand;
            this.weight = weight;
            this.volume = volume;
            this.quantity = quantity;
            this.color = color;
            this.type = type;
            this.description = description;

        }


        public String toCSV() {
            return String.join(",", sku, itemName, category, brand, weight, volume, quantity, color, type, description);
        }

        public void setAllFields(String itemName, String category, String brand, String weight,
                                 String volume, String quantity, String color, String type, String description) {
            this.itemName = itemName;
            this.category = category;
            this.brand = brand;
            this.weight = weight;
            this.volume = volume;
            this.quantity = quantity;
            this.color = color;
            this.type = type;
            this.description = description;
        }
    }
}
