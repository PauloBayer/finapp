package com.example.finapp.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromType(type: TransactionType) = type.name

    @TypeConverter
    fun toType(name: String) = enumValueOf<TransactionType>(name)

}