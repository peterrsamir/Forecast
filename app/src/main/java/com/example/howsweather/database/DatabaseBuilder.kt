package com.example.howsweather.database

import android.content.Context
import androidx.room.*
import androidx.room.Database
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.howsweather.model.CustomAlert
import com.example.howsweather.model.Forecast

@Database(entities = [Forecast::class, CustomAlert::class], version = 2)
@TypeConverters(Converter::class)
abstract class DatabaseBuilder : RoomDatabase() {

    companion object {
        @Volatile
        private var databaseBuilder: DatabaseBuilder? = null
        @Synchronized
        public fun getInstance(context: Context): DatabaseBuilder {
            if (databaseBuilder == null) {
                databaseBuilder =
                    Room.databaseBuilder(context, DatabaseBuilder::class.java, "Forecast").fallbackToDestructiveMigration().build()
            }
            return databaseBuilder!!
        }
    }

    override fun createOpenHelper(config: DatabaseConfiguration?): SupportSQLiteOpenHelper {
        TODO("Not yet implemented")
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        TODO("Not yet implemented")
    }

    override fun clearAllTables() {
        TODO("Not yet implemented")
    }

    abstract fun getDao(): Dao?
}