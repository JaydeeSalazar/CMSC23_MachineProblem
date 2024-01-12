package com.example.cmsc23_mp4;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class HelloController {

    private static String imagePath;
    private static final String Volumes = "CondimentBeverage";

    @FXML
    private TextField itemName;

    @FXML
    private ChoiceBox<String> category;

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

    @FXML
    private Button importButton;

    @FXML
    private ImageView imageView;

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

                if (selectedItem.getImportedImagePath() != null && !selectedItem.getImportedImagePath().isEmpty()) {
                    File imageFile = new File(selectedItem.getImportedImagePath());
                    final InputStream targetStream; // Creating the InputStream
                    try
                    {
                        targetStream = new DataInputStream(new FileInputStream(imageFile));
                        Image image = new Image(targetStream);
                        imageView.setImage(image);
                    } catch (FileNotFoundException fileNotFoundException)
                    {
                        fileNotFoundException.printStackTrace();
                    }
                }
            }
        });
        // Set initial properties, dont do it initially sa scenebuilder or mahirap i-edit
        enableTextFields(false);
        confirmButton.setVisible(false);
        cancelButton.setVisible(false);
        importButton.setVisible(false);

        // Populate choices in the category ChoiceBox
        category.setItems(FXCollections.observableArrayList("Beverage", "Condiment", "Dry Good", "Fresh Ingredient", "Rice and Noodles"));

        category.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                String chosen = category.getItems().get((Integer) number2);
                if (Volumes.contains(chosen)){
                    weight.setDisable(true);
                    volume.setDisable(false);
                }
                else{
                    weight.setDisable(false);
                    volume.setDisable(true);
                }
            }
        });
    }

    @FXML
    protected void addItem() {
        selectedItem = null;  // Clear selected item
        resetAction();
        enableTextFields(true);
        confirmButton.setVisible(true);
        cancelButton.setVisible(true);

        // Set the visibility of importButton
        importButton.setVisible(true);
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
        importButton.setVisible(false);
    }

    @FXML
    protected void confirmAction() {
        if (selectedItem == null) {
            // Add a new item
            Item newItem = new Item("SKU", itemName.getText(), category.getValue().toString(),
                    brand.getText(), weight.getText(), volume.getText(), quantity.getText(),
                    color.getText(), type.getText(), description.getText(),
                    imageView.getImage().getUrl());
            System.out.println(imageView.getImage().getUrl());

            // Set the image using InputStream
            //newItem.setInputStream(getImageInputStream());

            itemList.add(newItem);
        } else {
            // Update SKU if the category or itemName has changed
            selectedItem.updateSKU(category.getValue(), itemName.getText());

            // Edit the selected item
            selectedItem.setAllFields(itemName.getText(), category.getValue().toString(),
                    brand.getText(), weight.getText(), volume.getText(), quantity.getText(),
                    color.getText(), type.getText(), description.getText(), imageView.getImage().getUrl());

            // Set the image using InputStream
            //selectedItem.setInputStream(getImageInputStream());

            // Update the TableView with the edited item
            int index = itemList.indexOf(selectedItem);
            table.getItems().set(index, selectedItem);
        }

        table.setItems(itemList);
        resetAction();
        cancelAction();  // Ensure to clear the TextFields and hide buttons

        // Handle the visibility of importButton
        importButton.setVisible(false);
        imageView.setImage(null);
    }

    @FXML
    protected void resetAction() {
        itemName.clear();
        category.setValue(null);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void importImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            final InputStream targetStream; // Creating the InputStream
            try
            {
                targetStream = new DataInputStream(new FileInputStream(file));
                Image image = new Image(targetStream);
                imageView.setImage(image);
                imagePath = file.getAbsolutePath();

            } catch (FileNotFoundException fileNotFoundException)
            {
                fileNotFoundException.printStackTrace();
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
                    if (values.length == 11) {
                        Item newItem = new Item(values[0], values[1], values[2], values[3],
                                values[4], values[5], values[6], values[7], values[8], values[9], values[10]);
                        itemList.add(newItem);
                    }
                }
                table.setItems(itemList);
            } catch (Exception e) {
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

            // Set the visibility of importButton
            importButton.setVisible(true);
        }
        else{
            if (table.getItems().size() == 1){
                selectedItem = table.getItems().getFirst();
                setFieldsFromSelectedItem();
                enableTextFields(true);
                confirmButton.setVisible(true);
                cancelButton.setVisible(true);

                // Set the visibility of importButton
                importButton.setVisible(true);
            }
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
        else{
            if (table.getItems().size() == 1){
                itemList.removeFirst();
            }
        }
    }

    @FXML
    protected void logUsage() {
        System.out.println("Placeholder");
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
        category.setValue(selectedItem.getCategory());
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
        private String importedImagePath;

        @FXML
        private ImageView imageView;

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

        public String getImportedImagePath() {
            return importedImagePath;
        }

        public String generateSKU(String category, String itemName) {
            // get first three consonants from category
            String categoryPrefix = getConsonantsPrefix(category, 3);
            // get first three consonants from item name
            String itemPrefix = getConsonantsPrefix(itemName, 3);

            // random number from 0000 to 9999
            String randomNumber = String.format("%04d", new Random().nextInt(10000));

            return categoryPrefix + "/" + itemPrefix + "-" + randomNumber;
        }

        public void updateSKU(String newCategory, String newItemName) {
            if (newCategory != null && newItemName != null) {
                // Get the first three consonants of the new category
                String newCategoryPrefix = getConsonantsPrefix(newCategory, 3);

                // Get the first three characters of the new itemName
                String newItemNamePrefix = getConsonantsPrefix(newItemName, 3);

                // Update SKU with the new category and itemName
                this.sku = newCategoryPrefix + "/" + newItemNamePrefix + this.sku.substring(7);
            }
        }

        private String getConsonantsPrefix(String input, int length) {
            StringBuilder prefix = new StringBuilder();
            int count = 0;
            int consonantCount = 0;
            Character firstVowel = null;

            for (char ch : input.toUpperCase().toCharArray()) {
                if (Character.isLetter(ch) && !isVowel(ch)) {
                    consonantCount++;
                } else if (Character.isLetter(ch) && isVowel(ch) && firstVowel != null) {
                    firstVowel = ch;
                }
            }
            if (consonantCount > 2) {
                for (char ch : input.toUpperCase().toCharArray()) {
                    if (Character.isLetter(ch) && !isVowel(ch)) {
                        prefix.append(ch);
                        count++;
                    }

                    if (count == length) {
                        break;
                    }
                }
            } else {
                boolean insertedVowelAlready = false;
                for (char ch : input.toUpperCase().toCharArray()) {
                    if (Character.isLetter(ch) && !isVowel(ch)) {
                        prefix.append(ch);
                        count++;
                    }
                    if (Character.isLetter(ch) && isVowel(ch) && !insertedVowelAlready) {
                        insertedVowelAlready = true;
                        prefix.append(ch);
                        count++;
                    }

                    if (count == length) {
                        break;
                    }
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

        public ImageView getImageView() {
            return imageView;
        }

        public void setInputStream(InputStream inputStream) {
            if (inputStream != null) {
                this.imageView = new ImageView(new Image(inputStream));
            }
        }

        private void updateImageView() {
            if (importedImagePath != null && !importedImagePath.isEmpty()) {
                File imageFile = new File(importedImagePath);
                final InputStream targetStream; // Creating the InputStream
                try
                {
                    targetStream = new DataInputStream(new FileInputStream(imageFile));
                    Image image = new Image(targetStream);
                    imageView.setImage(image);
                } catch (FileNotFoundException fileNotFoundException)
                {
                    fileNotFoundException.printStackTrace();
                }
            }
        }


        public Item(String sku, String itemName, String category, String brand, String weight,
                    String volume, String quantity, String color, String type, String description, String importedImagePath) {
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
            this.importedImagePath = importedImagePath;
            updateImageView();
        }

        public String toCSV() {
            return String.join(",", sku, itemName, category, brand, weight, volume, quantity,
                    color, type, description, importedImagePath);
        }

        public void setAllFields(String itemName, String category, String brand, String weight,
                                 String volume, String quantity, String color, String type, String description, String importedImagePath) {
            this.itemName = itemName;
            this.category = category;
            this.brand = brand;
            this.weight = weight;
            this.volume = volume;
            this.quantity = quantity;
            this.color = color;
            this.type = type;
            this.description = description;
            this.importedImagePath = importedImagePath;
        }
    }
}
