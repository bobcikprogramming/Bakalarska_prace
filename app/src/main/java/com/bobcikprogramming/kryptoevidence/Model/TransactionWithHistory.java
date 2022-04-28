package com.bobcikprogramming.kryptoevidence.Model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/**
 * Projekt: Krypto Evidence
 * Autor: Pavel Bobčík
 * Institut: VUT Brno - Fakulta informačních technologií
 * Rok vytvoření: 2021
 *
 * Bakalářská práce (2022): Správa transakcí s kryptoměnami
 */

public class TransactionWithHistory {

    @Embedded
    public TransactionEntity transaction;
    @Relation(
            parentColumn = "transaction_id",
            entityColumn = "parent_id"
    )

    public List<TransactionHistoryEntity> history;

}
