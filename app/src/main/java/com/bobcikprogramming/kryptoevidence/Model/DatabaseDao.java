package com.bobcikprogramming.kryptoevidence.Model;

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
    TransactionWithPhotos getTransactionByTransactionID(String transactionID);

    @Transaction
    @Query("SELECT * FROM TransactionEntity WHERE transaction_id = :transactionID")
    TransactionWithHistory getTransactionByTransactionHistoryID(String transactionID);

    @Query("SELECT * FROM PhotoEntity")
    List<PhotoEntity> getPhoto();

    @Query("SELECT * FROM PhotoEntity WHERE parent_id = :transactionID")
    List<PhotoEntity> getPhotoByTransactionID(String transactionID);

    @Query("SELECT * FROM TransactionHistoryEntity")
    List<TransactionHistoryEntity> getHistory();

    @Query("SELECT * FROM OwnedCryptoEntity")
    List<OwnedCryptoEntity> getAllOwnedCrypto();

    @Query("SELECT * FROM OwnedCryptoEntity WHERE short_name = :shortName")
    OwnedCryptoEntity getOwnedCryptoByID(String shortName);

    @Insert
    long insertTransaction(TransactionEntity transaction);

    @Insert
    long insertOldTransaction(TransactionHistoryEntity transaction);

    @Insert
    void insertPhoto(PhotoEntity photo);

    @Insert
    void insertOwnedCrypto(OwnedCryptoEntity ownedCrypto);

    @Update
    void updateTransaction(TransactionEntity transaction);

    @Update
    void updateOwnedCrypto(OwnedCryptoEntity ownedCrypto);

    @Query("DELETE FROM TransactionHistoryEntity WHERE parent_id = :transactionID")
    void deleteHistory(String transactionID);

    @Query("DELETE FROM PhotoEntity WHERE parent_id = :transactionID")
    void deletePhotos(String transactionID);

    @Query("DELETE FROM TransactionEntity WHERE transaction_id = :transactionID")
    void deleteTransactionTable(String transactionID);

    @Query("DELETE FROM PhotoEntity WHERE photo_id = :photoID")
    void deletePhotoById(String photoID);
}
