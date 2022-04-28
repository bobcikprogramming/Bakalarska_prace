package com.bobcikprogramming.kryptoevidence.Model;

import androidx.annotation.NonNull;
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
public class CryptocurrencyEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "uid")
    public String uid;

    @ColumnInfo(name = "shortName")
    public String shortName;

    @ColumnInfo(name = "longName")
    public String longName;

    @ColumnInfo(name = "rank")
    public int rank;

    @ColumnInfo(name = "favorite", defaultValue = "0")
    public int favorite;

    @ColumnInfo(name = "amount")
    public String amount;

}
