package model;

import javafx.collections.transformation.SortedList;
import java.util.Comparator;

public class SortingModule {
    private final SortedList<Product> sortedProducts;

    public SortingModule(SortedList<Product> sortedProducts) {
        this.sortedProducts = sortedProducts;
    }

    public void setSorting(String sortType) {
        switch (sortType) {
            case "По имени":
                sortedProducts.setComparator(Comparator.comparing(Product::getName));
                break;
            case "По количеству":
                sortedProducts.setComparator(Comparator.comparing(Product::getQuantity).reversed());
                break;
            case "По цене":
                sortedProducts.setComparator(Comparator.comparing(Product::getPrice).reversed());
                break;
            case "По бренду":
                sortedProducts.setComparator(Comparator.comparing(Product::getBrand));
                break;
            case "По ID":
            default:
                sortedProducts.setComparator(Comparator.comparing(Product::getId));
                break;
        }
    }
}