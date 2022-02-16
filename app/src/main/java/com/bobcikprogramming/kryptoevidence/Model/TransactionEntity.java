package com.bobcikprogramming.kryptoevidence.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity
public class TransactionEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transaction_id")
    public long uidTransaction;

    @ColumnInfo(name = "transaction_type")
    public String transactionType;

    @ColumnInfo(name = "short_name_bought")
    public String shortNameBought;

    @ColumnInfo(name = "long_name_bought")
    public String longNameBought;

    @ColumnInfo(name = "short_name_sold")
    public String shortNameSold;

    @ColumnInfo(name = "long_name_sold")
    public String longNameSold;

    @ColumnInfo(name = "currency")
    public String currency;

    @ColumnInfo(name = "quantity_bought")
    public String quantityBought;

    @ColumnInfo(name = "quantity_sold")
    public String quantitySold;

    @ColumnInfo(name = "price_bought")
    public String priceBought;

    @ColumnInfo(name = "price_sold")
    public String priceSold;

    @ColumnInfo(name = "fee")
    public String fee;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "time")
    public String time;
}
