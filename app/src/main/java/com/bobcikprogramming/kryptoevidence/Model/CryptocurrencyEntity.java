package com.bobcikprogramming.kryptoevidence.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

}
