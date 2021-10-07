package com.bobcikprogramming.kryptoevidence.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class TransactionEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transaction_id")
    public long uidTransaction;

    @ColumnInfo(name = "transaction_type")
    public String transactionType;

    @ColumnInfo(name = "name_bought")
    public String nameBought;

    @ColumnInfo(name = "name_sold")
    public String nameSold;

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
