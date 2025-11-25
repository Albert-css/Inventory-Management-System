package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.*;
import java.util.function.Predicate;

public class InventoryModel {
    private final ObservableList<Product> productList;
    private final FilteredList<Product> filteredProducts;
    private final SortedList<Product> sortedProducts;
    private int nextId = 1;

    private final StringBuilder changeHistory;

    private int addOperations = 0;
    private int updateOperations = 0;
    private int deleteOperations = 0;

    public InventoryModel() {
        this.productList = FXCollections.observableArrayList();
        this.filteredProducts = new FilteredList<>(productList);
        this.sortedProducts = new SortedList<>(filteredProducts);
        this.changeHistory = new StringBuilder();
    }

    public boolean addProduct(String name, String brand, double price, int quantity, int averageQuantity) {
        if (!isNameBrandUnique(name, brand)) {
            return false;
        }

        if (price < 0 || quantity < 0 || averageQuantity < 0) {
            return false;
        }

        Product product = new Product(nextId++, name, brand, price, quantity, averageQuantity);
        productList.add(product);

        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        changeHistory.append(timestamp).append(" создан товар: ").append(name).append("\n");

        addOperations++;
        return true;
    }

    public boolean updateProduct(Product oldProduct, String newName, String newBrand,
                                 double newPrice, int newQuantity, int newAverageQuantity) {
        if (!isNameBrandUnique(newName, newBrand, oldProduct)) {
            return false;
        }

        if (newPrice < 0 || newQuantity < 0 || newAverageQuantity < 0) {
            return false;
        }

        StringBuilder changes = new StringBuilder();

        if (!oldProduct.getName().equals(newName)) {
            changes.append("имя: ").append(oldProduct.getName()).append(" -> ").append(newName).append(", ");
        }
        if (!oldProduct.getBrand().equals(newBrand)) {
            changes.append("бренд: ").append(oldProduct.getBrand()).append(" -> ").append(newBrand).append(", ");
        }
        if (oldProduct.getPrice() != newPrice) {
            changes.append("цена: ").append(oldProduct.getPrice()).append(" -> ").append(newPrice).append(", ");
        }
        if (oldProduct.getQuantity() != newQuantity) {
            changes.append("количество: ").append(oldProduct.getQuantity()).append(" -> ").append(newQuantity).append(", ");
        }
        if (oldProduct.getAverageQuantity() != newAverageQuantity) {
            changes.append("СрКол: ").append(oldProduct.getAverageQuantity()).append(" -> ").append(newAverageQuantity).append(", ");
        }

        String changesStr = changes.toString();
        if (changesStr.endsWith(", ")) {
            changesStr = changesStr.substring(0, changesStr.length() - 2);
        }

        oldProduct.setName(newName);
        oldProduct.setBrand(newBrand);
        oldProduct.setPrice(newPrice);
        oldProduct.setQuantity(newQuantity);
        oldProduct.setAverageQuantity(newAverageQuantity);

        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        changeHistory.append(timestamp).append(" изменен товар ").append(oldProduct.getName())
                .append(": ").append(changesStr).append("\n");

        updateOperations++;
        return true;
    }


    public boolean removeProduct(Product product) {
        boolean removed = productList.remove(product);
        if (removed) {
            String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            changeHistory.append(timestamp).append(" удален товар: ").append(product.getName()).append("\n");

            deleteOperations++;
        }
        return removed;
    }


    public Product findProductByName(String name) {
        return productList.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }


    private boolean isNameBrandUnique(String name, String brand) {
        return productList.stream()
                .noneMatch(p -> p.getName().equalsIgnoreCase(name) && p.getBrand().equalsIgnoreCase(brand));
    }

    private boolean isNameBrandUnique(String name, String brand, Product excludeProduct) {
        return productList.stream()
                .filter(p -> p != excludeProduct)
                .noneMatch(p -> p.getName().equalsIgnoreCase(name) && p.getBrand().equalsIgnoreCase(brand));
    }

    public ObservableList<Product> getProductList() { return productList; }
    public SortedList<Product> getSortedProducts() { return sortedProducts; }
    public FilteredList<Product> getFilteredProducts() { return filteredProducts; }
    public String getChangeHistory() { return changeHistory.toString(); }


    public void setQuantityFilter(int minQuantity, boolean showZeroQuantity) {
        Predicate<Product> filter = product -> {
            boolean quantityCondition = product.getQuantity() >= minQuantity;
            boolean zeroCondition = showZeroQuantity || product.getQuantity() > 0;
            return quantityCondition && zeroCondition;
        };
        filteredProducts.setPredicate(filter);
    }

    public void setNameFilter(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredProducts.setPredicate(null);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            filteredProducts.setPredicate(product ->
                    product.getName().toLowerCase().contains(lowerCaseFilter));
        }
    }


    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", productList.size());

        long uniqueNames = productList.stream()
                .map(Product::getName)
                .distinct()
                .count();
        stats.put("uniqueNames", uniqueNames);

        double avgProductsPerName = productList.size() / (double) Math.max(uniqueNames, 1);
        stats.put("avgProductsPerName", String.format("%.2f", avgProductsPerName));

        stats.put("addOperations", addOperations);
        stats.put("updateOperations", updateOperations);
        stats.put("deleteOperations", deleteOperations);

        return stats;
    }


    public boolean loadProductFromFile(int id, String name, String brand, double price, int quantity, int averageQuantity) {
        if (!isNameBrandUnique(name, brand)) {
            return false;
        }

        if (price < 0 || quantity < 0 || averageQuantity < 0) {
            return false;
        }

        if (id >= nextId) {
            nextId = id + 1;
        }

        Product product = new Product(id, name, brand, price, quantity, averageQuantity);
        productList.add(product);

        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        changeHistory.append(timestamp).append(" создан товар: ").append(name).append("\n");

        addOperations++;
        return true;
    }
}