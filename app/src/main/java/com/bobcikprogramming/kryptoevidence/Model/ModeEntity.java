package com.bobcikprogramming.kryptoevidence.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ModeEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mode_id")
    public long uidMode;

    @ColumnInfo(name = "mode_type", defaultValue = "system")
    public String modeType;
}
