package model;

import javafx.beans.property.*;


public class Product {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty brand;
    private final DoubleProperty price;
    private final IntegerProperty quantity;
    private final IntegerProperty averageQuantity;

    public Product(int id, String name, String brand, double price, int quantity, int averageQuantity) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.brand = new SimpleStringProperty(brand);
        this.price = new SimpleDoubleProperty(price);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.averageQuantity = new SimpleIntegerProperty(averageQuantity);
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public String getBrand() { return brand.get(); }
    public void setBrand(String brand) { this.brand.set(brand); }
    public StringProperty brandProperty() { return brand; }

    public double getPrice() { return price.get(); }
    public void setPrice(double price) { this.price.set(price); }
    public DoubleProperty priceProperty() { return price; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public IntegerProperty quantityProperty() { return quantity; }

    public int getAverageQuantity() { return averageQuantity.get(); }
    public void setAverageQuantity(int averageQuantity) { this.averageQuantity.set(averageQuantity); }
    public IntegerProperty averageQuantityProperty() { return averageQuantity; }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', brand='%s', price=%.2f, quantity=%d, avgQuantity=%d}",
                id.get(), name.get(), brand.get(), price.get(), quantity.get(), averageQuantity.get());
    }
}