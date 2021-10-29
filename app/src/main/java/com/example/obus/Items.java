package com.example.obus;

public class Items {
    private String itemUri;
    private String itemName;

    public Items(String itemUri, String itemName) {
        this.itemUri = itemUri;
        this.itemName = itemName;
    }

    public String getItemUri() {
        return itemUri;
    }

    public void setItemUri(String itemUri) {
        this.itemUri = itemUri;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
