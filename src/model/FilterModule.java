package model;

public class FilterModule {
    private final InventoryModel inventoryModel;
    private int minQuantity = 0;
    private boolean showZeroQuantity = true;
    private String searchText = "";

    public FilterModule(InventoryModel inventoryModel) {
        this.inventoryModel = inventoryModel;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
        applyFilters();
    }

    public void setShowZeroQuantity(boolean showZeroQuantity) {
        this.showZeroQuantity = showZeroQuantity;
        applyFilters();
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
        applyFilters();
    }

    private void applyFilters() {
        inventoryModel.setQuantityFilter(minQuantity, showZeroQuantity);
        inventoryModel.setNameFilter(searchText);
    }
}