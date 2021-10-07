package com.bobcikprogramming.kryptoevidence.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface DatabaseDao {

    @Query("SELECT * FROM TransactionEntity WHERE transaction_type='Nákup'")
    List<TransactionEntity> getBuy();

    @Query("SELECT * FROM TransactionEntity WHERE transaction_type='Prodej'")
    List<TransactionEntity> getSell();

    @Query("SELECT * FROM TransactionEntity WHERE transaction_type='Směna'")
    List<TransactionEntity> getChange();

    @Transaction
    @Query("SELECT * FROM TransactionEntity")
    List<TransactionWithPhotos> getAll();


    @Insert
    void insertTransaction(TransactionEntity transaction);

    @Delete
    void deleteTransaction(TransactionEntity transaction);
}
