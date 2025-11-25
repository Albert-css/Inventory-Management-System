package view;

import controller.MainController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Product;


public class ProductDialog {

    public enum Mode { ADD, EDIT }

    private Stage dialogStage;
    private MainController controller;
    private Product product;
    private Mode mode;

    private TextField nameField;
    private TextField brandField;
    private TextField priceField;
    private TextField quantityField;
    private TextField avgQuantityField;
    private ComboBox<String> priceComboBox;
    private Button okButton;
    private Button cancelButton;

    private static final String DEFAULT_NAME = "ИмяТовара";
    private static final String DEFAULT_BRAND = "БрендТовара";
    private static final double DEFAULT_PRICE = 100.0;
    private static final int DEFAULT_QUANTITY = 0;
    private static final int DEFAULT_AVG_QUANTITY = 10;

    public ProductDialog(MainController controller, Mode mode) {
        this.controller = controller;
        this.mode = mode;
        initializeDialog();
    }

    public ProductDialog(MainController controller, Mode mode, Product product) {
        this(controller, mode);
        this.product = product;
        if (mode == Mode.EDIT && product != null) {
            fillFieldsWithProductData();
        }
    }

    private void initializeDialog() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setResizable(false);

        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        mainContainer.getStyleClass().add("dialog-container");

        GridPane formGrid = createForm();

        HBox buttonPanel = createButtonPanel();

        mainContainer.getChildren().addAll(formGrid, buttonPanel);

        Scene scene = new Scene(mainContainer);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialogStage.setScene(scene);

        if (mode == Mode.ADD) {
            dialogStage.setTitle("Добавить товар");
            okButton.setText("Добавить");
        } else {
            dialogStage.setTitle("Редактировать товар");
            okButton.setText("Сохранить");
        }
    }

    private GridPane createForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.getStyleClass().add("form-grid");

        Label nameLabel = new Label("Имя:");
        nameLabel.getStyleClass().add("form-label");
        nameField = new TextField();
        nameField.setPromptText(DEFAULT_NAME);
        nameField.getStyleClass().add("form-text-field");

        Label brandLabel = new Label("Бренд:");
        brandLabel.getStyleClass().add("form-label");
        brandField = new TextField();
        brandField.setPromptText(DEFAULT_BRAND);
        brandField.getStyleClass().add("form-text-field");

        Label priceLabel = new Label("Цена:");
        priceLabel.getStyleClass().add("form-label");
        priceField = new TextField();
        priceField.setPromptText(String.valueOf(DEFAULT_PRICE));
        priceField.getStyleClass().add("form-text-field");

        priceComboBox = new ComboBox<>();
        priceComboBox.getItems().addAll("10", "100", "1000");
        priceComboBox.setPromptText("Выберите");
        priceComboBox.getStyleClass().add("form-combo-box");
        priceComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                priceField.setText(newValue);
            }
        });

        Label quantityLabel = new Label("Количество:");
        quantityLabel.getStyleClass().add("form-label");
        quantityField = new TextField();
        quantityField.setPromptText(String.valueOf(DEFAULT_QUANTITY));
        quantityField.getStyleClass().add("form-text-field");

        Label avgQuantityLabel = new Label("Среднее количество:");
        avgQuantityLabel.getStyleClass().add("form-label");
        avgQuantityField = new TextField();
        avgQuantityField.setPromptText(String.valueOf(DEFAULT_AVG_QUANTITY));
        avgQuantityField.getStyleClass().add("form-text-field");

        setupNumericValidation(priceField);
        setupNumericValidation(quantityField);
        setupNumericValidation(avgQuantityField);

        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0, 2, 1);

        grid.add(brandLabel, 0, 1);
        grid.add(brandField, 1, 1, 2, 1);

        grid.add(priceLabel, 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(priceComboBox, 2, 2);

        grid.add(quantityLabel, 0, 3);
        grid.add(quantityField, 1, 3, 2, 1);

        grid.add(avgQuantityLabel, 0, 4);
        grid.add(avgQuantityField, 1, 4, 2, 1);

        return grid;
    }

    private HBox createButtonPanel() {
        HBox buttonPanel = new HBox(15);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        buttonPanel.getStyleClass().add("button-panel");

        okButton = new Button();
        okButton.getStyleClass().add("button");
        okButton.getStyleClass().add("ok-button");

        cancelButton = new Button("Отмена");
        cancelButton.getStyleClass().add("button");
        cancelButton.getStyleClass().add("cancel-button");

        okButton.setOnAction(e -> handleOk());
        cancelButton.setOnAction(e -> handleCancel());

        buttonPanel.getChildren().addAll(okButton, cancelButton);
        return buttonPanel;
    }

    private void setupNumericValidation(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                field.setText(newValue.replaceAll("[^\\d.]", ""));
            }
        });
    }

    private void fillFieldsWithProductData() {
        nameField.setText(product.getName());
        brandField.setText(product.getBrand());
        priceField.setText(String.valueOf(product.getPrice()));
        quantityField.setText(String.valueOf(product.getQuantity()));
        avgQuantityField.setText(String.valueOf(product.getAverageQuantity()));
    }

    private void handleOk() {
        try {
            String name = getFieldValue(nameField, DEFAULT_NAME);
            String brand = getFieldValue(brandField, DEFAULT_BRAND);
            double price = Double.parseDouble(getFieldValue(priceField, String.valueOf(DEFAULT_PRICE)));
            int quantity = Integer.parseInt(getFieldValue(quantityField, String.valueOf(DEFAULT_QUANTITY)));
            int avgQuantity = Integer.parseInt(getFieldValue(avgQuantityField, String.valueOf(DEFAULT_AVG_QUANTITY)));

            if (name.trim().isEmpty() || brand.trim().isEmpty()) {
                showAlert("Ошибка", "Имя и бренд не могут быть пустыми", Alert.AlertType.ERROR);
                return;
            }

            if (price < 0 || quantity < 0 || avgQuantity < 0) {
                showAlert("Ошибка", "Цена и количества не могут быть отрицательными", Alert.AlertType.ERROR);
                return;
            }

            boolean success;
            if (mode == Mode.ADD) {
                success = controller.addProduct(name, brand, price, quantity, avgQuantity);
                if (!success) {
                    showAlert("Ошибка", "Товар с таким именем и брендом уже существует", Alert.AlertType.ERROR);
                    return;
                }
            } else {
                success = controller.updateProduct(product, name, brand, price, quantity, avgQuantity);
                if (!success) {
                    showAlert("Ошибка", "Не удалось обновить товар. Проверьте уникальность имени и бренда", Alert.AlertType.ERROR);
                    return;
                }
            }

            dialogStage.close();

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Неверный формат числовых значений", Alert.AlertType.ERROR);
        }
    }

    private void handleCancel() {
        dialogStage.close();
    }

    private String getFieldValue(TextField field, String defaultValue) {
        String value = field.getText().trim();
        return value.isEmpty() ? defaultValue : value;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void show() {
        dialogStage.showAndWait();
    }
}