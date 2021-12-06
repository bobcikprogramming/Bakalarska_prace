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
    @Query("SELECT * FROM ModeEntity")
    List<ModeEntity> getType();

    @Transaction
    @Query("SELECT * FROM TransactionEntity WHERE transaction_id = :transactionID")
    TransactionWithPhotos getByTransactionID(String transactionID);

    @Transaction
    @Query("SELECT * FROM TransactionEntity WHERE transaction_id = :transactionID")
    TransactionWithHistory getByTransactionHistoryID(String transactionID);

    @Query("SELECT * FROM PhotoEntity")
    List<PhotoEntity> getPhoto();

    @Query("SELECT * FROM TransactionHistoryEntity")
    List<TransactionHistoryEntity> getHistory();

    @Insert
    long insertTransaction(TransactionEntity transaction);

    @Insert
    long insertOldTransaction(TransactionHistoryEntity transaction);

    @Insert
    void insertPhoto(PhotoEntity photo);

    @Insert
    long changeMode(ModeEntity mode);

    @Delete
    void deleteTransaction(TransactionEntity transaction);

    @Delete
    void deletePhoto(PhotoEntity photo);

    @Delete
    void deleteHistory(TransactionHistoryEntity transaction);

    @Update
    void updateTransaction(TransactionEntity transaction);

    @Query("DELETE FROM TransactionHistoryEntity WHERE parent_id = :transactionID")
    void deleteHistory(String transactionID);

    @Query("DELETE FROM PhotoEntity WHERE parent_id = :transactionID")
    void deletePhotos(String transactionID);

    @Query("DELETE FROM TransactionEntity WHERE transaction_id = :transactionID")
    void deleteTransactionTable(String transactionID);
}
