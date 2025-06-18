package com.example.finapp.data

import android.content.Context
import android.util.Log
import androidx.room.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.Executors

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dao(): TransactionDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: build(context).also { INSTANCE = it }
            }

        private fun build(ctx: Context) =
            Room.databaseBuilder(ctx, AppDatabase::class.java, "finapp.db")
                .fallbackToDestructiveMigration()
                .setQueryCallback({ sql, args ->
                    Log.d("FinApp-SQL", "SQL: $sql  ARGS: $args")
                }, Executors.newSingleThreadExecutor())
                .build()
    }

}