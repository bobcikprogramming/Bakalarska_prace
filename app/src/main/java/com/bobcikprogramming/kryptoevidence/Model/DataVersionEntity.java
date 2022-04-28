package com.bobcikprogramming.kryptoevidence.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.annotations.NotNull;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

@Entity
public class DataVersionEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid_version")
    public int uidVersion;

    @NotNull
    @ColumnInfo(name = "version_rate", defaultValue = "0")
    public int versionRate;

    @NotNull
    @ColumnInfo(name = "version_crypto", defaultValue = "0")
    public int versionCrypto;

}
