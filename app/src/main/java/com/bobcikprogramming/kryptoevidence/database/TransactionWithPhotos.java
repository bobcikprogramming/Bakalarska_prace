package com.bobcikprogramming.kryptoevidence.database;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TransactionWithPhotos {

    @Embedded
    public TransactionEntity transaction;
    @Relation(
            parentColumn = "transaction_id",
            entityColumn = "parent_id"
    )
    public List<PhotoEntity> photos;

}
