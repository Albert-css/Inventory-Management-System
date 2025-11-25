package view;

import controller.MainController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.InventoryModel;
import model.Product;
import java.util.Map;

public class MainView extends TabPane {

    private MainController controller;
    private InventoryModel model;

    private TableView<Product> productsTable;
    private ComboBox<String> fileComboBox;
    private ComboBox<String> sortComboBox;
    private TextField searchField;
    private Spinner<Integer> minQuantitySpinner;
    private CheckBox showZeroQuantityCheckbox;

    private Label totalProductsLabel;
    private Label uniqueNamesLabel;
    private Label avgProductsPerNameLabel;
    private Label addOperationsLabel;
    private Label updateOperationsLabel;
    private Label deleteOperationsLabel;

    private TextArea historyTextArea;

    public Spinner<Integer> getMinQuantitySpinner() { return minQuantitySpinner; }
    public CheckBox getShowZeroQuantityCheckbox() { return showZeroQuantityCheckbox; }
    public TextField getSearchField() { return searchField; }
    public MainView(MainController controller, InventoryModel model) {
        this.controller = controller;
        this.model = model;

        initializeView();
        setupEventHandlers();
    }

    private void initializeView() {
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        Tab mainTab = createMainTab();
        Tab statsTab = createStatisticsTab();
        Tab historyTab = createHistoryTab();

        this.getTabs().addAll(mainTab, statsTab, historyTab);

        this.getStyleClass().add("main-tab-pane");

        this.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null && newTab.getText().equals("История изменений")) {
                updateHistory();
            }
        });
    }

    private Tab createMainTab() {
        Tab mainTab = new Tab("Главная");
        mainTab.setClosable(false);

        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(15));
        mainContainer.getStyleClass().add("main-container");

        HBox topPanel = createTopPanel();

        productsTable = createProductsTable();

        HBox buttonPanel = createButtonPanel();

        mainContainer.getChildren().addAll(topPanel, productsTable, buttonPanel);
        VBox.setVgrow(productsTable, Priority.ALWAYS);

        mainTab.setContent(mainContainer);
        return mainTab;
    }

    private HBox createTopPanel() {
        HBox topPanel = new HBox(15);
        topPanel.setAlignment(Pos.CENTER_LEFT);
        topPanel.getStyleClass().add("top-panel");

        fileComboBox = new ComboBox<>();
        fileComboBox.getItems().addAll("Сохранить", "Загрузить");
        fileComboBox.setValue("Файл");
        fileComboBox.setPrefWidth(120);
        fileComboBox.getStyleClass().add("combo-box");

        sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("По ID", "По имени", "По количеству", "По цене", "По бренду");
        sortComboBox.setValue("По ID");
        sortComboBox.setPrefWidth(120);
        sortComboBox.getStyleClass().add("combo-box");

        Label searchLabel = new Label("Поиск:");
        searchLabel.getStyleClass().add("label");

        searchField = new TextField();
        searchField.setPromptText("Введите название товара...");
        searchField.setPrefWidth(200);
        searchField.getStyleClass().add("text-field");

        Label quantityLabel = new Label("Мин. количество:");
        quantityLabel.getStyleClass().add("label");

        minQuantitySpinner = new Spinner<>(0, 10000, 0);
        minQuantitySpinner.setPrefWidth(80);
        minQuantitySpinner.getStyleClass().add("spinner");

        // CheckBox для показа нулевых количеств
        showZeroQuantityCheckbox = new CheckBox("Показывать нулевые");
        showZeroQuantityCheckbox.setSelected(true);
        showZeroQuantityCheckbox.getStyleClass().add("check-box");

        topPanel.getChildren().addAll(
                fileComboBox, sortComboBox, searchLabel, searchField,
                quantityLabel, minQuantitySpinner, showZeroQuantityCheckbox
        );

        return topPanel;
    }

    private TableView<Product> createProductsTable() {
        TableView<Product> table = new TableView<>();
        table.getStyleClass().add("products-table");

        TableColumn<Product, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        idColumn.setPrefWidth(50);

        TableColumn<Product, String> nameColumn = new TableColumn<>("Имя");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameColumn.setPrefWidth(200);

        TableColumn<Product, String> brandColumn = new TableColumn<>("Бренд");
        brandColumn.setCellValueFactory(cellData -> cellData.getValue().brandProperty());
        brandColumn.setPrefWidth(150);

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Цена");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        priceColumn.setPrefWidth(100);
        priceColumn.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f руб.", price));
                }
            }
        });

        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Количество");
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        quantityColumn.setPrefWidth(100);

        TableColumn<Product, Integer> avgQuantityColumn = new TableColumn<>("Среднее Количество");
        avgQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().averageQuantityProperty().asObject());
        avgQuantityColumn.setPrefWidth(160);

        table.getColumns().addAll(idColumn, nameColumn, brandColumn, priceColumn, quantityColumn, avgQuantityColumn);

        table.setRowFactory(tv -> new TableRow<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setStyle("");
                } else {
                    applyRowColorStyle(product);
                }
            }

            private void applyRowColorStyle(Product product) {
                int quantity = product.getQuantity();
                int avgQuantity = product.getAverageQuantity();

                if (quantity == 0) {
                    setStyle("-fx-background-color: #d3d3d3;");
                } else if (quantity < 0.5 * avgQuantity) {
                    setStyle("-fx-background-color: #ffcccc;");
                } else if (quantity <= 1.5 * avgQuantity) {
                    setStyle("-fx-background-color: #ffffcc;");
                } else {
                    setStyle("-fx-background-color: #ccffcc;");
                }
            }
        });

        return table;
    }

    private HBox createButtonPanel() {
        HBox buttonPanel = new HBox(15);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.getStyleClass().add("button-panel");

        Button addButton = new Button("Добавить товар");
        addButton.getStyleClass().add("button");
        addButton.getStyleClass().add("add-button");

        Button editButton = new Button("Изменить товар");
        editButton.getStyleClass().add("button");
        editButton.getStyleClass().add("edit-button");

        Button deleteButton = new Button("Удалить товар");
        deleteButton.getStyleClass().add("button");
        deleteButton.getStyleClass().add("delete-button");

        buttonPanel.getChildren().addAll(addButton, editButton, deleteButton);

        addButton.setOnAction(e -> controller.handleAddProduct());
        editButton.setOnAction(e -> controller.handleEditProduct());
        deleteButton.setOnAction(e -> controller.handleDeleteProduct());

        return buttonPanel;
    }

    private Tab createStatisticsTab() {
        Tab statsTab = new Tab("Статистика");
        statsTab.setClosable(false);

        VBox statsContainer = new VBox(15);
        statsContainer.setPadding(new Insets(20));
        statsContainer.getStyleClass().add("stats-container");

        Label title = new Label("Статистика склада");
        title.getStyleClass().add("header-label");

        VBox generalStatsPanel = createStatsPanel("Общая статистика:");
        totalProductsLabel = new Label("Всего товаров: 0");
        uniqueNamesLabel = new Label("Уникальных имен: 0");
        avgProductsPerNameLabel = new Label("Среднее количество на имя: 0.0");

        generalStatsPanel.getChildren().addAll(totalProductsLabel, uniqueNamesLabel, avgProductsPerNameLabel);

        VBox operationsStatsPanel = createStatsPanel("Операции:");
        addOperationsLabel = new Label("Добавлений: 0");
        updateOperationsLabel = new Label("Изменений: 0");
        deleteOperationsLabel = new Label("Удалений: 0");

        operationsStatsPanel.getChildren().addAll(addOperationsLabel, updateOperationsLabel, deleteOperationsLabel);


        statsContainer.getChildren().addAll(title, generalStatsPanel, operationsStatsPanel);

        statsTab.setContent(statsContainer);
        return statsTab;
    }

    private VBox createStatsPanel(String title) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.getStyleClass().add("stats-panel");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("bold-label");

        panel.getChildren().add(titleLabel);
        return panel;
    }

    private Tab createHistoryTab() {
        Tab historyTab = new Tab("История изменений");
        historyTab.setClosable(false);

        VBox historyContainer = new VBox(10);
        historyContainer.setPadding(new Insets(15));

        Label title = new Label("История изменений товаров");
        title.getStyleClass().add("header-label");

        historyTextArea = new TextArea();
        historyTextArea.setEditable(false);
        historyTextArea.getStyleClass().add("history-text-area");
        VBox.setVgrow(historyTextArea, Priority.ALWAYS);

        historyContainer.getChildren().addAll(title, historyTextArea);

        historyTab.setContent(historyContainer);
        return historyTab;
    }

    private void setupEventHandlers() {
        productsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !productsTable.getSelectionModel().isEmpty()) {
                Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
                controller.showEditConfirmation(selectedProduct);
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            controller.handleSearchChange(newValue);
        });

        sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            controller.handleSortChange(newValue);
        });

        minQuantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            controller.handleMinQuantityChange(newValue);
        });

        showZeroQuantityCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            controller.handleShowZeroQuantityChange(newValue);
        });

        fileComboBox.setOnAction(e -> controller.handleFileOperation(fileComboBox.getValue()));
    }

    public TableView<Product> getProductsTable() { return productsTable; }
    public TextArea getHistoryTextArea() { return historyTextArea; }

    public void updateStatistics(Map<String, Object> stats) {
        totalProductsLabel.setText("Всего товаров: " + stats.get("totalProducts"));
        uniqueNamesLabel.setText("Уникальных имен: " + stats.get("uniqueNames"));
        avgProductsPerNameLabel.setText("Среднее количество на имя: " + stats.get("avgProductsPerName"));
        addOperationsLabel.setText("Добавлений: " + stats.get("addOperations"));
        updateOperationsLabel.setText("Изменений: " + stats.get("updateOperations"));
        deleteOperationsLabel.setText("Удалений: " + stats.get("deleteOperations"));
    }

    public void updateHistory() {
        historyTextArea.setText(model.getChangeHistory());
    }

    public void updateHistoryString(String history) {
        historyTextArea.setText(history);
    }

    public void resetFileComboBox() {
        fileComboBox.setValue("Файл");
    }
}