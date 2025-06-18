package com.example.finapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: TransactionType,

    val description: String,

    val value: Double

)