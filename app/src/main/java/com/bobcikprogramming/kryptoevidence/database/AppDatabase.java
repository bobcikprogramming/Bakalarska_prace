package com.bobcikprogramming.kryptoevidence.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// zdroj https://www.youtube.com/watch?v=ONb_MuPBBlg&list=PL7ir2AVHqRHj8-Flj9fD9O4Gei-x9EUzG&index=5
@Database(entities = {TransactionEntity.class, PhotoEntity.class, ModeEntity.class}, version = 1)
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
