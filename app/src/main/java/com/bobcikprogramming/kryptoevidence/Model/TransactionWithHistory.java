package com.bobcikprogramming.kryptoevidence.Model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TransactionWithHistory {

    @Embedded
    public TransactionEntity transaction;
    @Relation(
            parentColumn = "transaction_id",
            entityColumn = "parent_id"
    )

    public List<TransactionHistoryEntity> history;

}
