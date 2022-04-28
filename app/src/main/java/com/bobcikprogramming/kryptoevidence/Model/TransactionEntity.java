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
public class TransactionEntity {

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

    @ColumnInfo(name = "amount_left")
    public String amountLeft;

    @ColumnInfo(name = "amount_left_change_sell")
    public String amountLeftChangeSell;

    @ColumnInfo(name = "used_from_first")
    public String usedFromFirst;

    @ColumnInfo(name = "used_from_last")
    public String usedFromLast;

    @ColumnInfo(name = "first_taken_from")
    public long firstTakenFrom;

    @ColumnInfo(name = "last_taken_from")
    public long lastTakenFrom;
}
