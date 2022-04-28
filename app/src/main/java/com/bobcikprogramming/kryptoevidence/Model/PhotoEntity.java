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
public class PhotoEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "photo_id")
    public long uidPhoto;

    @ColumnInfo(name = "parent_id")
    public long transactionId;

    @ColumnInfo(name = "file_destination")
    public String dest;
}
