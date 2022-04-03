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

    @Transaction
    @Query("SELECT * FROM TransactionEntity")
    List<TransactionWithPhotos> getAll();

    @Transaction
    @Query("SELECT * FROM ModeEntity")
    List<ModeEntity> getType();

    @Transaction
    @Query("SELECT * FROM PDFEntity")
    List<PDFEntity> getPDF();

    @Transaction
    @Query("SELECT * FROM TransactionEntity WHERE transaction_id = :transactionID")
    TransactionWithPhotos getTransactionByTransactionID(String transactionID);

    @Transaction
    @Query("SELECT * FROM TransactionEntity WHERE transaction_id = :transactionID")
    TransactionWithHistory getTransactionByTransactionHistoryID(String transactionID);

    @Query("SELECT * FROM PhotoEntity WHERE parent_id = :transactionID")
    List<PhotoEntity> getPhotoByTransactionID(String transactionID);

    @Query("SELECT * FROM TransactionHistoryEntity")
    List<TransactionHistoryEntity> getHistory();

    @Query("SELECT * FROM OwnedCryptoEntity")
    List<OwnedCryptoEntity> getAllOwnedCrypto();

    @Query("SELECT * FROM OwnedCryptoEntity WHERE short_name = :shortName")
    OwnedCryptoEntity getOwnedCryptoByID(String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE ((transaction_type = 'Prodej' AND amount_left > 0.0) OR (transaction_type = 'Směna' AND amount_left_change_sell > 0.0)) AND short_name_sold = :shortName AND ((date = :date AND time >= :time) OR date > :date) ORDER BY date, time")
    List<TransactionWithPhotos> getSellChangeNotEmptyFrom(long date, String time, String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE ((transaction_type = 'Prodej' AND amount_left > 0.0) OR (transaction_type = 'Směna' AND amount_left_change_sell > 0.0)) AND short_name_sold = :shortName AND ((date = :date AND time = :time AND transaction_id > :transactionID) OR (date = :date AND time > :time) OR date > :date) ORDER BY date, time")
    List<TransactionWithPhotos> getSellChangeNotEmptyAfterFirst(String transactionID, long date, String time, String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE ((transaction_type = 'Prodej' AND amount_left > 0.0) OR (transaction_type = 'Směna' AND amount_left_change_sell > 0.0)) AND short_name_sold = :shortName AND ((date = :date AND time = :time AND transaction_id >= :transactionID) OR (date = :date AND time > :time) OR date > :date) ORDER BY date, time")
    List<TransactionWithPhotos> getSellChangeNotEmptyFromFirst(String transactionID, long date, String time, String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE ((transaction_type = 'Prodej' AND amount_left > 0.0) OR (transaction_type = 'Směna' AND amount_left_change_sell > 0.0)) AND short_name_sold = :shortName AND ((date = :date AND time >= :time) OR date > :date) ORDER BY date, time")
    List<TransactionWithPhotos> getSellChangeNotEmptyAllFrom(long date, String time, String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE ((transaction_type = 'Prodej' AND amount_left != quantity_sold) OR (transaction_type = 'Směna' AND amount_left_change_sell != quantity_sold)) AND ((date = :date AND time > :time) OR date > :date) AND short_name_sold = :shortName ORDER BY date, time")
    List<TransactionWithPhotos> getUsedSellChangeAfter(long date, String time, String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE transaction_type = 'Prodej' AND (amount_left == 0 OR amount_left == 0.0) AND date >= :dateFrom AND date <= :dateTo ORDER BY date, time")
    List<TransactionWithPhotos> getUsedSellBetween(long dateFrom, long dateTo);

    @Query("SELECT * FROM TransactionEntity WHERE transaction_type = 'Směna' AND amount_left_change_sell != quantity_sold AND date BETWEEN :dateFrom AND :dateTo ORDER BY date, time")
    List<TransactionWithPhotos> getUsedChangeBetween(long dateFrom, long dateTo);

    @Query("SELECT * FROM TransactionEntity WHERE transaction_type = 'Prodej' AND amount_left != quantity_sold AND short_name_sold = :shortName AND date BETWEEN :dateFrom AND :dateTo ORDER BY date, time")
    List<TransactionWithPhotos> getUsedSellBetweenByShortName(String shortName, long dateFrom, long dateTo);

    @Query("SELECT short_name_sold FROM TransactionEntity WHERE transaction_type = 'Prodej' AND amount_left != quantity_sold AND date BETWEEN :dateFrom AND :dateTo GROUP BY short_name_sold ORDER BY date, time")
    List <String> getUsedShortNameSellBetween(long dateFrom, long dateTo);

    @Query("SELECT * FROM TransactionEntity WHERE ((transaction_type = 'Prodej' AND amount_left != quantity_sold) OR (transaction_type = 'Směna' AND amount_left_change_sell != quantity_sold)) AND ((date = :date AND time >= :time) OR date > :date) AND short_name_sold = :shortName ORDER BY date, time")
    List<TransactionWithPhotos> getUsedSellChangeFrom(long date, String time, String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE ((transaction_type = 'Prodej' AND amount_left != quantity_sold) OR (transaction_type = 'Směna' AND amount_left_change_sell != quantity_sold)) AND ((date = :date AND time = :time AND transaction_id >= :transactionID) OR (date = :date AND time > :time) OR date > :date) AND short_name_sold = :shortName ORDER BY date, time")
    List<TransactionWithPhotos> getUsedSellChangeAllFromFirst(String transactionID, long date, String time, String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE (transaction_type = 'Nákup' OR transaction_type = 'Směna') AND amount_left != quantity_bought AND short_name_bought = :shortName AND ((date = :dateFrom AND time = :timeFrom AND transaction_id > :transactionFrom) OR (date = :dateFrom AND time > :timeFrom) OR date > :dateFrom) AND ((date = :dateTo AND time = :timeTo AND transaction_id < :transactionTo) OR (date = :dateTo AND time < :timeTo) OR date < :dateTo) AND date BETWEEN :dateFrom AND :dateTo ORDER BY date, time")
    List<TransactionWithPhotos> getUsedBuyChangeBetweenWithoutFirstAndLast(String transactionFrom, String transactionTo, long dateFrom, String timeFrom, long dateTo, String timeTo, String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE (transaction_type = 'Nákup' OR transaction_type = 'Směna') AND short_name_bought = :shortName AND amount_left != quantity_bought AND ((date = :date AND time = :time AND transaction_id > :transactionID) OR (date = :date AND time > :time) OR date > :date) ORDER BY date, time")
    List<TransactionWithPhotos> getUsedBuyChangeFrom(String transactionID, long date, String time, String shortName);

    @Query("SELECT * FROM TransactionEntity WHERE (transaction_type = 'Nákup' OR transaction_type = 'Směna') AND short_name_bought = :shortName AND amount_left > 0.0 AND ((date = :date AND time <= :time) OR date < :date) ORDER BY date, time")
    List<TransactionWithPhotos> getNotEmptyBuyChangeTo(long date, String time, String shortName);

    @Query("SELECT * FROM (SELECT * FROM TransactionEntity WHERE (transaction_type = 'Nákup' OR transaction_type = 'Směna') AND short_name_bought = :shortName AND amount_left != quantity_bought AND ((date = :date AND time = :time AND transaction_id >= :transactionID) OR (date = :date AND time > :time) OR date > :date) ORDER BY date, time) WHERE transaction_id = :lookingForID")
    TransactionWithPhotos findIfExistBuyWithIdForUsedBuyChangeFromFirst(String transactionID, long date, String time, String shortName, String lookingForID);

    @Query("SELECT * FROM (SELECT * FROM TransactionEntity WHERE (transaction_type = 'Nákup' OR transaction_type = 'Směna') AND short_name_bought = :shortName AND amount_left != quantity_bought AND ((date = :date AND time >= :time) OR date > :date) ORDER BY date, time) WHERE transaction_id = :lookingForID")
    List<TransactionWithPhotos> findIfExistBuyWithIdForUsedBuyChangeAllFrom(long date, String time, String shortName, String lookingForID);

    @Query("SELECT *, MAX(date) FROM PDFEntity GROUP BY year ORDER BY year")
    List<PDFEntity> getLatestAnnualReport();

    @Insert
    long insertTransaction(TransactionEntity transaction);

    @Insert
    long insertOldTransaction(TransactionHistoryEntity transaction);

    @Insert
    void insertPhoto(PhotoEntity photo);

    @Insert
    void insertOwnedCrypto(OwnedCryptoEntity ownedCrypto);

    @Insert
    void insertPDF(PDFEntity pdfEntity);

    @Update
    void updateTransaction(TransactionEntity transaction);

    @Update
    void updateOwnedCrypto(OwnedCryptoEntity ownedCrypto);

    @Query("UPDATE TransactionEntity SET amount_left = :amountLeft, used_from_first = :usedFromFirst, used_from_last = :usedFromLast, first_taken_from = :firstTakenFrom, last_taken_from = :lastTakenFrom WHERE transaction_id = :transactionID")
    void updateFifoCalc(String transactionID, String amountLeft, String usedFromFirst, String usedFromLast, String firstTakenFrom, String lastTakenFrom);

    @Query("UPDATE TransactionEntity SET amount_left_change_sell = :amountLeft, used_from_first = :usedFromFirst, used_from_last = :usedFromLast, first_taken_from = :firstTakenFrom, last_taken_from = :lastTakenFrom WHERE transaction_id = :transactionID")
    void updateFifoCalcChangeSell(String transactionID, String amountLeft, String usedFromFirst, String usedFromLast, String firstTakenFrom, String lastTakenFrom);

    @Query("UPDATE TransactionEntity SET amount_left = :amountLeft WHERE transaction_id = :transactionID")
    void updateAmountLeft(String transactionID, String amountLeft);

    @Query("UPDATE TransactionEntity SET amount_left_change_sell = :amountLeft WHERE transaction_id = :transactionID")
    void updateAmountLeftChange(String transactionID, String amountLeft);

    @Query("UPDATE TransactionEntity SET amount_left = :amountLeft, date = :date, time = :time WHERE transaction_id = :transactionID")
    void updateForEditingTime(String transactionID, String amountLeft, long date, String time);

    @Query("UPDATE TransactionEntity SET amount_left_change_sell = :amountLeft, date = :date, time = :time WHERE transaction_id = :transactionID")
    void updateChangeSellForEditingTime(String transactionID, String amountLeft, long date, String time);

    @Query("UPDATE TransactionEntity SET amount_left = (amount_left + :amountLeft) WHERE transaction_id = :transactionID")
    void updateAmountLeftMathAdd(String transactionID, String amountLeft);

    @Query("UPDATE TransactionEntity SET amount_left = quantity_bought WHERE transaction_id = :transactionID")
    void resetAmountLeftBuyById(String transactionID);

    @Query("UPDATE TransactionEntity SET amount_left = quantity_bought WHERE (transaction_type = 'Nákup' OR transaction_type = 'Směna') AND short_name_bought = :shortName AND ((date = :date AND time = :time AND transaction_id > :transactionID) OR (date = :date AND time > :time) OR date > :date)")
    void resetAmountLeftBuyChangeAfterFirst(String transactionID, long date, String time, String shortName);

    @Query("UPDATE TransactionEntity SET amount_left = quantity_bought WHERE (transaction_type = 'Nákup' OR transaction_type = 'Směna') AND short_name_bought = :shortName AND ((date = :date AND time >= :time) OR date > :date)")
    void resetAmountLeftBuyChangeFrom(long date, String time, String shortName);

    @Query("UPDATE TransactionEntity SET amount_left = quantity_sold, used_from_first = -1.0, used_from_last = -1.0, first_taken_from = -1, last_taken_from = -1 WHERE ((date = :date AND time >= :time) OR date > :date) AND transaction_type = 'Prodej' AND short_name_sold = :shortName")
    void resetAmountLeftUsedSellAllFrom(long date, String time, String shortName);

    @Query("UPDATE TransactionEntity SET amount_left_change_sell = quantity_sold, used_from_first = -1.0, used_from_last = -1.0, first_taken_from = -1, last_taken_from = -1 WHERE ((date = :date AND time >= :time) OR date > :date) AND transaction_type = 'Směna' AND short_name_sold = :shortName")
    void resetAmountLeftUsedChangeAllFrom(long date, String time, String shortName);

    @Query("UPDATE TransactionEntity SET amount_left = quantity_sold, used_from_first = -1.0, used_from_last = -1.0, first_taken_from = -1, last_taken_from = -1 WHERE transaction_type = 'Prodej' AND short_name_sold = :shortName AND ((date = :date AND time = :time AND transaction_id > :transactionID) OR (date = :date AND time > :time) OR date > :date)")
    void resetAmountLeftUsedSellAfterFirst(String transactionID, long date, String time, String shortName);

    @Query("UPDATE TransactionEntity SET amount_left_change_sell = quantity_sold, used_from_first = -1.0, used_from_last = -1.0, first_taken_from = -1, last_taken_from = -1 WHERE transaction_type = 'Směna' AND short_name_sold = :shortName AND ((date = :date AND time = :time AND transaction_id > :transactionID) OR (date = :date AND time > :time) OR date > :date)")
    void resetAmountLeftUsedChangeAfterFirst(String transactionID, long date, String time, String shortName);

    @Query("UPDATE TransactionEntity SET amount_left = 0 WHERE transaction_id = :transactionID")
    void setTransactionToDeleteById(String transactionID);

    @Query("UPDATE TransactionEntity SET amount_left_change_sell = 0 WHERE transaction_id = :transactionID")
    void setTransactionChangeSellToDeleteById(String transactionID);

    @Query("DELETE FROM TransactionHistoryEntity WHERE parent_id = :transactionID")
    void deleteHistory(String transactionID);

    @Query("DELETE FROM PhotoEntity WHERE parent_id = :transactionID")
    void deletePhotos(String transactionID);

    @Query("DELETE FROM TransactionEntity WHERE transaction_id = :transactionID")
    void deleteTransactionTable(String transactionID);

    @Query("DELETE FROM PhotoEntity WHERE photo_id = :photoID")
    void deletePhotoById(String photoID);

    @Query("DELETE FROM PDFEntity WHERE fila_name = :fileName")
    void deletePDFEntity(String fileName);
}
