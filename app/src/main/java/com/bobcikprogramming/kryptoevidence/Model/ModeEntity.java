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
public class ModeEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mode_id")
    public long uidMode;

    @ColumnInfo(name = "mode_type", defaultValue = "system")
    public String modeType;
}
