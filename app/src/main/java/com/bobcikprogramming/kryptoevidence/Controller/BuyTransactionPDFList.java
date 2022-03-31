package com.bobcikprogramming.kryptoevidence.Controller;

public class BuyTransactionPDFList {
    String date, quantity, name, price, fee, total;

    public BuyTransactionPDFList(String date, String quantity, String name, String price, String fee, String total) {
        this.date = date;
        this.quantity = quantity;
        this.name = name;
        this.price = price;
        this.fee = fee;
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getFee() {
        return fee;
    }

    public String getTotal() {
        return total;
    }
}
