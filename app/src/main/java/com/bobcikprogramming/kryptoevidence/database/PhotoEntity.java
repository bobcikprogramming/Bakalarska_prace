package com.bobcikprogramming.kryptoevidence.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PhotoEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "photo_id")
    public long uidPhoto;

    @ColumnInfo(name = "parent_id")
    public long transactionId;

    @ColumnInfo(name = "file_destination")
    public String dest;
}
