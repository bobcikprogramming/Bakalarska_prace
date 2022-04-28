package com.bobcikprogramming.kryptoevidence.Controller;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

public class SellTransactionPDFList {
    String date, quantity, name, profit, fee, total;

    public SellTransactionPDFList(String date, String quantity, String name, String profit, String fee, String total) {
        this.date = date;
        this.quantity = quantity;
        this.name = name;
        this.profit = profit;
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

    public String getProfit() {
        return profit;
    }

    public String getFee() {
        return fee;
    }

    public String getTotal() {
        return total;
    }
}
