package com.bobcikprogramming.kryptoevidence.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.annotations.NotNull;

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
