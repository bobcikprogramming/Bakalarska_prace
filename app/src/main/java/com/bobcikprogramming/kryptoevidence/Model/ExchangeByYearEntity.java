package com.bobcikprogramming.kryptoevidence.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
