package com.bobcikprogramming.kryptoevidence.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class OwnedCryptoEntity {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "short_name")
    public String shortName;

    @ColumnInfo(name = "long_name")
    public String longName;

    @ColumnInfo(name = "amount")
    public String amount;
}
