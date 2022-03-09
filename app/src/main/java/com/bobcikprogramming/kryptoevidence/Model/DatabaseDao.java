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

    @Query("SELECT * FROM TransactionEntity WHERE transaction_type = 'Prodej' AND short_name_sold = :shortName AND ((date = :date AND time > :time) OR date > :date) AND amount_left > 0.0 ORDER BY date, time")
    List<TransactionWithPhotos> getSellNotEmptyAfterDate(String date, String time, String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE transaction_type = 'Nákup' AND short_name_bought = :shortName AND date >= :date AND amount_left != quantity_bought ORDER BY date, time")
    List<TransactionWithPhotos> getUsedBuyAfterNewBuy(String date, String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE transaction_type = 'Nákup' AND short_name_bought = :shortName AND ((date = :date AND time > :time) OR date > :date) AND amount_left > 0.0 ORDER BY date, time")
    List<TransactionWithPhotos> getBuyNotEmptyAfterNewBuy(String date, String time, String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE transaction_type = 'Prodej' AND ((date = :date AND time > :time) OR date > :date) AND short_name_sold = :shortName AND amount_left != quantity_sold ORDER BY date, time")
    List<TransactionWithPhotos> getUsedSellAfterNewBuy(String date, String time, String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE transaction_type = 'Nákup' AND short_name_bought = :shortName AND transaction_id != :newTransactionID AND date BETWEEN :dateFrom AND :dateTo ORDER BY date, time")
    List<TransactionWithPhotos> getUsedBuyBetween(String newTransactionID, String dateFrom, String dateTo, String shortName);

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

    @Query("UPDATE TransactionEntity SET amount_left = :amountLeft, used_from_first = :usedFromFirst, first_taken_from = :firstTakenFrom WHERE transaction_id = :transactionID")
    void updateFifoCalc(String transactionID, String amountLeft, String usedFromFirst, String firstTakenFrom);

    @Query("UPDATE TransactionEntity SET amount_left = :amountLeft WHERE transaction_id = :transactionID")
    void updateAmoutLeft(String transactionID, String amountLeft);

    @Query("UPDATE TransactionEntity SET amount_left = quantity_bought WHERE ((date = :date AND time > :time) OR date > :date) AND transaction_type = 'Nákup'")
    void resetAmoutLeftUsedBuy(String date, String time);

    @Query("DELETE FROM TransactionHistoryEntity WHERE parent_id = :transactionID")
    void deleteHistory(String transactionID);

    @Query("DELETE FROM PhotoEntity WHERE parent_id = :transactionID")
    void deletePhotos(String transactionID);

    @Query("DELETE FROM TransactionEntity WHERE transaction_id = :transactionID")
    void deleteTransactionTable(String transactionID);

    @Query("DELETE FROM PhotoEntity WHERE photo_id = :photoID")
    void deletePhotoById(String photoID);
}
