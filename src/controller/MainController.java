package controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;
import view.MainView;
import view.ProductDialog;

import java.io.*;
import java.util.Locale;
import java.util.Map;

public class MainController {

    private MainView mainView;
    private InventoryModel inventoryModel;
    private SortingModule sortingModule;
    private FilterModule filterModule;
    private Stage primaryStage;

    public MainController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeModel();
        initializeView();
        setupEventHandlers();
    }

    private void initializeModel() {
        inventoryModel = new InventoryModel();
        sortingModule = new SortingModule(inventoryModel.getSortedProducts());
        filterModule = new FilterModule(inventoryModel);
    }

    private void initializeView() {
        mainView = new MainView(this, inventoryModel);

        mainView.getProductsTable().setItems(inventoryModel.getSortedProducts());

        updateHistory();
        updateStatistics();
    }

    private void setupEventHandlers() {
    }

    public void handleAddProduct() {
        ProductDialog dialog = new ProductDialog(this, ProductDialog.Mode.ADD);
        dialog.show();

        updateStatistics();
        updateHistory();
    }

    public void handleEditProduct() {
        Product selectedProduct = mainView.getProductsTable().getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            showEditConfirmation(selectedProduct);
        } else {
            showSearchAndEditDialog();
        }
    }

    public void handleDeleteProduct() {
        Product selectedProduct = mainView.getProductsTable().getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            showDeleteConfirmation(selectedProduct);
        } else {
            showSearchAndDeleteDialog();
        }
    }

    public void handleFileOperation(String operation) {
        if ("Сохранить".equals(operation)) {
            saveToFile();
        } else if ("Загрузить".equals(operation)) {
            loadFromFile();
            updateStatistics();
            updateHistory();
        }
        mainView.resetFileComboBox();
    }

    public void handleSearchChange(String newValue) {
        filterModule.setSearchText(newValue);
    }

    public void handleSortChange(String newValue) {
        sortingModule.setSorting(newValue);
    }

    public void handleMinQuantityChange(Integer newValue) {
        filterModule.setMinQuantity(newValue);
    }

    public void handleShowZeroQuantityChange(Boolean newValue) {
        filterModule.setShowZeroQuantity(newValue);
    }

    public void showEditConfirmation(Product product) {
        javafx.scene.control.Alert confirmation = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение");
        confirmation.setHeaderText("Редактирование товара");
        confirmation.setContentText("Вы хотите изменить товар: " + product.getName() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                openEditDialog(product);
            }
        });
    }

    private void openEditDialog(Product product) {
        ProductDialog dialog = new ProductDialog(this, ProductDialog.Mode.EDIT, product);
        dialog.show();

        updateStatistics();
        updateHistory();
    }

    public void showDeleteConfirmation(Product product) {
        javafx.scene.control.Alert confirmation = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Подтверждение удаления");
        confirmation.setHeaderText("Удаление товара");
        confirmation.setContentText("Вы уверены, что хотите удалить товар: " + product.getName() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                inventoryModel.removeProduct(product);
                updateStatistics();
                updateHistory();
            }
        });
    }

    private void showSearchAndEditDialog() {
        String productName = showSearchDialog("Редактирование товара", "Введите имя товара для редактирования:");
        if (productName != null) {
            Product product = inventoryModel.findProductByName(productName);
            if (product != null) {
                openEditDialog(product);
            } else {
                showAlert("Товар не найден", "Товар с именем '" + productName + "' не найден", javafx.scene.control.Alert.AlertType.WARNING);
            }
        }
    }

    private void showSearchAndDeleteDialog() {
        String productName = showSearchDialog("Удаление товара", "Введите имя товара для удаления:");
        if (productName != null) {
            Product product = inventoryModel.findProductByName(productName);
            if (product != null) {
                showDeleteConfirmation(product);
            } else {
                showAlert("Товар не найден", "Товар с именем '" + productName + "' не найден", javafx.scene.control.Alert.AlertType.WARNING);
            }
        }
    }

    private String showSearchDialog(String title, String content) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);

        return dialog.showAndWait().orElse(null);
    }

    public boolean addProduct(String name, String brand, double price, int quantity, int averageQuantity) {
        return inventoryModel.addProduct(name, brand, price, quantity, averageQuantity);
    }

    public boolean updateProduct(Product oldProduct, String newName, String newBrand,
                                 double newPrice, int newQuantity, int newAverageQuantity) {
        return inventoryModel.updateProduct(oldProduct, newName, newBrand, newPrice, newQuantity, newAverageQuantity);
    }

    private void saveToFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить данные");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files", "*.csv"),
                new FileChooser.ExtensionFilter("Text files", "*.txt"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("ID,Name,Brand,Price,Quantity,AverageQuantity");

                for (Product product : inventoryModel.getProductList()) {
                    String priceFormatted = String.format(Locale.US, "%.2f", product.getPrice());
                    if (priceFormatted.endsWith(".00")) {
                        priceFormatted = priceFormatted.substring(0, priceFormatted.length() - 3);
                    }

                    writer.printf("%d,%s,%s,%s,%d,%d%n",
                            product.getId(),
                            product.getName(),
                            product.getBrand(),
                            priceFormatted, // Используем отформатированную цену
                            product.getQuantity(),
                            product.getAverageQuantity());
                }

                showAlert("Успех", "Данные успешно сохранены в файл: " + file.getName(), javafx.scene.control.Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                showAlert("Ошибка", "Не удалось сохранить файл: " + e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
            }
        }
    }

    private void loadFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить данные");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files", "*.csv"),
                new FileChooser.ExtensionFilter("Text files", "*.txt"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine(); // Пропускаем заголовок

                inventoryModel.getProductList().clear();

                int loadedCount = 0;
                int errorCount = 0;

                while ((line = reader.readLine()) != null) {
                    try {
                        String[] parts = line.split(",");
                        if (parts.length >= 6) {
                            int id = Integer.parseInt(parts[0].trim());
                            String name = parts[1].trim();
                            String brand = parts[2].trim();

                            String priceStr = parts[3].trim();
                            double price;
                            if (priceStr.contains(",")) {
                                priceStr = priceStr.replace(",", ".");
                            }
                            price = Double.parseDouble(priceStr);

                            int quantity = Integer.parseInt(parts[4].trim());
                            int avgQuantity = Integer.parseInt(parts[5].trim());

                            boolean success = inventoryModel.loadProductFromFile(id, name, brand, price, quantity, avgQuantity);
                            if (success) {
                                loadedCount++;
                            } else {
                                errorCount++;
                                System.err.println("Не удалось загрузить товар: " + name + " (" + brand + ")");
                            }
                        } else {
                            errorCount++;
                            System.err.println("Неверный формат строки: " + line);
                        }
                    } catch (NumberFormatException e) {
                        errorCount++;
                        System.err.println("Ошибка преобразования числа в строке: " + line);
                    } catch (Exception e) {
                        errorCount++;
                        System.err.println("Ошибка при загрузке строки: " + line + " - " + e.getMessage());
                    }
                }

                filterModule.setMinQuantity(0);
                filterModule.setShowZeroQuantity(true);
                filterModule.setSearchText("");

                mainView.getMinQuantitySpinner().getValueFactory().setValue(0);
                mainView.getShowZeroQuantityCheckbox().setSelected(true);
                mainView.getSearchField().setText("");

                updateStatistics();
                updateHistory();

                String message = "Данные успешно загружены из файла: " + file.getName() +
                        "\nЗагружено товаров: " + loadedCount;
                if (errorCount > 0) {
                    message += "\nНе загружено товаров из-за ошибок: " + errorCount;
                }

                showAlert("Успех", message, javafx.scene.control.Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                showAlert("Ошибка", "Не удалось загрузить файл: " + e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
            }
        }
    }

    public void updateStatistics() {
        Map<String, Object> stats = inventoryModel.getStatistics();
        mainView.updateStatistics(stats);
    }

    private void updateHistory() {
        mainView.updateHistoryString(inventoryModel.getChangeHistory());
    }

    private void showAlert(String title, String content, javafx.scene.control.Alert.AlertType type) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public MainView getMainView() {
        return mainView;
    }
}