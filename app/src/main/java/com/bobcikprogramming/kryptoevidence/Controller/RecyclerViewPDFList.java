package com.bobcikprogramming.kryptoevidence.Controller;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

public class RecyclerViewPDFList {
    private String year, date;

    public RecyclerViewPDFList(String year, String date) {
        this.year = year;
        this.date = date;
    }

    public String getYear() {
        return year;
    }

    public String getDate() {
        return date;
    }
}
