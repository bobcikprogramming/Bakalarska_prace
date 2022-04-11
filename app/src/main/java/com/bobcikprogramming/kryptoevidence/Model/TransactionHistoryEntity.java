package com.bobcikprogramming.kryptoevidence.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;

@Entity
public class TransactionHistoryEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "transaction_id")
    public long uidTransaction;

    @ColumnInfo(name = "transaction_type")
    public String transactionType;

    @ColumnInfo(name = "uid_crypto_bought")
    public String uidBought;

    @ColumnInfo(name = "uid_crypto_sold")
    public String uidSold;

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
    public Double fee;

    @ColumnInfo(name = "date")
    public long date;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "note")
    public String note;

    @ColumnInfo(name = "date_of_change")
    public String dateOfChange;

    @ColumnInfo(name = "time_of_change")
    public String timeOfChange;

    @ColumnInfo(name = "parent_id")
    public long parentTransactionId;
}
