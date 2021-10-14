package com.bobcikprogramming.kryptoevidence.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

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

    @Transaction
    @Query("SELECT * FROM TransactionEntity WHERE transaction_id = :transactionID")
    TransactionWithPhotos getByTransactionID(String transactionID);

    @Query("SELECT * FROM PhotoEntity")
    List<PhotoEntity> getPhoto();

    @Insert
    long insertTransaction(TransactionEntity transaction);

    @Insert
    void insertPhoto(PhotoEntity photo);

    @Delete
    void deleteTransaction(TransactionEntity transaction);

    @Update
    void updateTransaction(TransactionEntity transaction);
}
