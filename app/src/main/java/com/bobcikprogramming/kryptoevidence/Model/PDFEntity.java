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
public class PDFEntity {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "file_name")
    public String fileName;

    @ColumnInfo(name = "year")
    public String year;

    @ColumnInfo(name = "date")
    public long date;

    @ColumnInfo(name = "total")
    public String total;

    @ColumnInfo(name = "created")
    public long created;
}
