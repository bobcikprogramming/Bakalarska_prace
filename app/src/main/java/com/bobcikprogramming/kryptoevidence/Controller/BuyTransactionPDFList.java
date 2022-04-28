package com.bobcikprogramming.kryptoevidence.Controller;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

public class BuyTransactionPDFList {
    private String date, time, quantity, name, price, fee, total;

    public BuyTransactionPDFList(String date, String time, String quantity, String name, String price, String fee, String total) {
        this.date = date;
        this.time = time;
        this.quantity = quantity;
        this.name = name;
        this.price = price;
        this.fee = fee;
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
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
