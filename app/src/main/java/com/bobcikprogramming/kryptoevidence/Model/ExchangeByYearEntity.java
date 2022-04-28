package com.bobcikprogramming.kryptoevidence.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

@Entity
public class ExchangeByYearEntity {

    @PrimaryKey
    @ColumnInfo(name = "year")
    public int year;

    @ColumnInfo(name = "eur")
    public double eur;

    @ColumnInfo(name = "usd")
    public double usd;

}
