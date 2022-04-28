package com.bobcikprogramming.kryptoevidence.Controller;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

public class ChangeTransactionPDFList {
    String date, quantityBought, nameBought, quantitySold, nameSold, total;

    public ChangeTransactionPDFList(String date, String quantityBought, String nameBought, String quantitySold, String nameSold, String total) {
        this.date = date;
        this.quantityBought = quantityBought;
        this.nameBought = nameBought;
        this.quantitySold = quantitySold;
        this.nameSold = nameSold;
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public String getQuantityBought() {
        return quantityBought;
    }

    public String getNameBought() {
        return nameBought;
    }

    public String getQuantitySold() {
        return quantitySold;
    }

    public String getNameSold() {
        return nameSold;
    }

    public String getTotal() {
        return total;
    }
}
