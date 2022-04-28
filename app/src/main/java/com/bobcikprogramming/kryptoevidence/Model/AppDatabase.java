package com.bobcikprogramming.kryptoevidence.Model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

@Database(entities = {TransactionEntity.class, TransactionHistoryEntity.class, PhotoEntity.class, ModeEntity.class, PDFEntity.class, ExchangeByYearEntity.class, DataVersionEntity.class, CryptocurrencyEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DatabaseDao databaseDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDbInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "myDatabase")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

}
