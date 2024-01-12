package com.example.cmsc23_mp4;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
    private static int arithmetic = 0;
    private static final String Volumes = "CondimentBeverage";

    @FXML
    private TextField search;

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
        itemSearch();
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Set the items into the table
        //table.setItems(itemList);

        // Add listener to handle row selection for editing/deleting
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedItem = newSelection;
                if (!importButton.isVisible()) {
                    if (selectedItem.getImportedImagePath() != null && !selectedItem.getImportedImagePath().isEmpty()) {
                        updateImageView();
                    } else {
                        imageView.setImage(null);
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

    private boolean repeatedSKU(String SKUnumber) {

        for(Item i : itemList) {
            String itemSKUNumber = i.getSku().substring(8);
            if(itemSKUNumber.equalsIgnoreCase(SKUnumber)) {
                return true;
            }
        }

        return false;
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
        if (selectedItem != null) {
            arithmetic = 1;
            arithmeticMethod();
        }
        else{
            selectedItem = table.getItems().getFirst();
            if (selectedItem.getImportedImagePath() != null && !selectedItem.getImportedImagePath().isEmpty()) {
                updateImageView();
            }
        }
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
            Item newItem;
            if (itemName.getText().isEmpty() || category.getValue().isEmpty() || brand.getText().isEmpty() || (weight.getText().isEmpty() && volume.getText().isEmpty())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Required fields are empty");
                alert.setContentText("The following must be filled out:\nItem Name\nCategory\nBrand\nWeight or Volume");

                alert.showAndWait();
                return;
            }
            if (Volumes.contains(category.getValue())) {
                newItem = new Item("SKU", itemName.getText(), category.getValue(),
                        brand.getText(), "-", volume.getText(),
                        color.getText(), type.getText(), description.getText(),
                        imagePath);
            }
            else{
                newItem = new Item("SKU", itemName.getText(), category.getValue(),
                        brand.getText(), weight.getText(), "-",
                        color.getText(), type.getText(), description.getText(),
                        imagePath);
            }

            itemList.add(newItem);
        } else {
            // Edit the selected item

            switch (arithmetic) {
                case 0:
                    // Update SKU if the category or itemName has changed
                    selectedItem.updateSKU(category.getValue(), itemName.getText());
                    if (Volumes.contains(category.getValue())) {
                        selectedItem.setAllFields(itemName.getText(), category.getValue(),
                                brand.getText(), "-", volume.getText(),
                                color.getText(), type.getText(), description.getText(), imagePath);
                    }
                    else{
                        selectedItem.setAllFields(itemName.getText(), category.getValue(),
                                brand.getText(), weight.getText(), "-",
                                color.getText(), type.getText(), description.getText(), imagePath);
                    }
                    break;
                case 1: // add existing
                    if (Volumes.contains(category.getValue())){
                        selectedItem.setField(true, volume.getText(), 1);
                    }
                    else{
                        selectedItem.setField(false, weight.getText(), 1);
                    }
                    break;
                case 2: // log usage
                    if (Volumes.contains(category.getValue())){
                        selectedItem.setField(true, volume.getText(), -1);
                    }
                    else{
                        selectedItem.setField(false, weight.getText(), -1);
                    }
                    break;
            }

            // Set the image using InputStream
            //selectedItem.setInputStream(getImageInputStream());

            // Update the TableView with the edited item
            int index = itemList.indexOf(selectedItem);
            table.getItems().set(index, selectedItem);
        }

        //table.setItems(itemList);
        resetAction();
        cancelAction();  // Ensure to clear the TextFields and hide buttons

        // Handle the visibility of importButton
        importButton.setVisible(false);
        imageView.setImage(null);

        if (arithmetic>0){
            arithmetic = 0;
        }
        itemSearch();
    }

    @FXML
    protected void resetAction() {
        itemName.clear();
        category.setValue(null);
        brand.clear();
        weight.clear();
        volume.clear();
        color.clear();
        type.clear();
        description.clear();
        imageView.setImage(null);
        imagePath = "";
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
                    if (values.length == 10) {
                        Item newItem = new Item(values[0], values[1], values[2], values[3],
                                values[4], values[5], values[6], values[7], values[8], values[9]);
                        itemList.add(newItem);
                    }
                }
                //table.setItems(itemList);
                itemSearch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void editExisting() {
        if (selectedItem != null) {
            if (selectedItem.getImportedImagePath() != null && !selectedItem.getImportedImagePath().isEmpty()) {
                updateImageView();
            }
            setFieldsFromSelectedItem();
            enableTextFields(true);
            confirmButton.setVisible(true);
            cancelButton.setVisible(true);

            // Set the visibility of importButton
            importButton.setVisible(true);
        }
        else{
            selectedItem = table.getItems().getFirst();
            editExisting();
            if (selectedItem.getImportedImagePath() != null && !selectedItem.getImportedImagePath().isEmpty()) {
                updateImageView();
            }
        }
        itemSearch();
    }

    private void updateImageView() {
        File imageFile = new File(selectedItem.getImportedImagePath());
        final InputStream targetStream; // Creating the InputStream
        try {
            targetStream = new DataInputStream(new FileInputStream(imageFile));
            Image image = new Image(targetStream);
            imageView.setImage(image);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }

    public void itemSearch() {
        System.out.println("1");
        FilteredList<Item> filter =  new FilteredList<> (itemList, e -> true);
        search.textProperty().addListener((observable, oldValue, newValue) -> {filter.setPredicate(Item -> {

            if(newValue == null || newValue.isEmpty()) {
                return true;
            }

            String searchKey = newValue.toLowerCase();
            String parsedSku = (Item.getSku().replace("/","")).replace("-","");
            if(parsedSku.toLowerCase().contains(searchKey)) {
                return true;
            }
            else if(Item.getItemName().toLowerCase().contains(searchKey)) {
                return true;
            }
            else if(Item.getCategory().toLowerCase().contains(searchKey)) {
                return true;
            }
            else if(Item.getBrand().toLowerCase().contains(searchKey)) {
                return true;
            }
            else if(Item.getColor().toLowerCase().contains(searchKey)) {
                return true;
            }
            else if(Item.getType().toLowerCase().contains(searchKey)) {
                return true;
            }
            else if(Item.getDescription().toLowerCase().contains(searchKey)) {
                return true;
            }
            else {
                return false;
            }
        });

        });

        SortedList<Item> sortedData = new SortedList<>(filter);
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        table.setItems(sortedData);
        System.out.println("2");

    }

    @FXML
    protected void deleteExisting() {
        if (selectedItem != null) {
            itemList.remove(selectedItem);
            selectedItem = null;
            //table.setItems(itemList);
            resetAction();
        }
        else{
            itemList.removeFirst();
        }
        itemSearch();
    }

    @FXML
    protected void logUsage() {
        if (selectedItem != null) {
            arithmetic = 2;
            arithmeticMethod();
        }
        else{
            selectedItem = table.getItems().getFirst();
            if (selectedItem.getImportedImagePath() != null && !selectedItem.getImportedImagePath().isEmpty()) {
                updateImageView();
            }
            logUsage();
        }
    }

    private void arithmeticMethod() {
        resetAction();
        enableTextFields(false);
        category.setValue(selectedItem.getCategory());
        if (Volumes.contains(selectedItem.getCategory())){
            weight.setDisable(true);
            volume.setDisable(false);
        }
        else{
            weight.setDisable(false);
            volume.setDisable(true);
        }
        confirmButton.setVisible(true);
        cancelButton.setVisible(true);
    }

    private void enableTextFields(boolean enable) {
        itemName.setDisable(!enable);
        category.setDisable(!enable);
        brand.setDisable(!enable);
        weight.setDisable(true);
        volume.setDisable(true);
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
        color.setText(selectedItem.getColor());
        type.setText(selectedItem.getType());
        description.setText(selectedItem.getDescription());
        if (selectedItem.getImportedImagePath() != null && !selectedItem.getImportedImagePath().isEmpty()) {
            imagePath = selectedItem.getImportedImagePath();
        }
        else{
            imagePath = "";
        }
    }


    public class Item {
        private String sku;
        private String itemName;
        private String category;
        private String brand;
        private String weight;
        private String volume;
        private String color;
        private String type;
        private String description;
        private String importedImagePath;


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
            String randomNumber = String.format("%04d", new Random().nextInt(10000));;
            while (repeatedSKU(randomNumber)){
            randomNumber = String.format("%04d", new Random().nextInt(10000));
            }


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


        public Item(String sku, String itemName, String category, String brand, String weight,
                    String volume, String color, String type, String description, String importedImagePath) {
            this.sku = generateSKU(category, itemName);
            this.itemName = itemName;
            this.category = category;
            this.brand = brand;
            this.weight = weight;
            this.volume = volume;
            this.color = color;
            this.type = type;
            this.description = description;
            this.importedImagePath = importedImagePath;
        }

        public String toCSV() {
            return String.join(",", sku, itemName, category, brand, weight, volume,
                    color, type, description, importedImagePath);
        }

        public void setAllFields(String itemName, String category, String brand, String weight,
                                 String volume, String color, String type, String description, String importedImagePath) {
            this.itemName = itemName;
            this.category = category;
            this.brand = brand;
            this.weight = weight;
            this.volume = volume;
            this.color = color;
            this.type = type;
            this.description = description;
            this.importedImagePath = importedImagePath;
        }
        public void setField(boolean VolumeUnits, String initialAmount, int sign) {
            double amount = Double.parseDouble(initialAmount);
            if (VolumeUnits){
                String value = this.volume;
                if (value == null || value.isEmpty()){
                    value = "0.0";
                }
                Double finalValue = Double.parseDouble(value) + (amount*sign);
                if (finalValue<0){
                    finalValue = 0.0;
                }
                this.volume = String.valueOf(finalValue);
            }
            else{
                String value = this.weight;
                if (value == null || value.isEmpty()){
                    value = "0.0";
                }
                Double finalValue = Double.parseDouble(value) + (amount*sign);
                if (finalValue<0){
                    finalValue = 0.0;
                }
                this.weight = String.valueOf(finalValue);
            }
        }
    }
}
