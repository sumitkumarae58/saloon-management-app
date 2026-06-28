package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.BookingEntity
import com.example.data.model.SalonEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.StaffEntity

@Database(
    entities = [
        SalonEntity::class,
        ServiceEntity::class,
        StaffEntity::class,
        BookingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SalonDatabase : RoomDatabase() {

    abstract fun salonDao(): SalonDao

    companion object {
        @Volatile
        private var INSTANCE: SalonDatabase? = null

        fun getDatabase(context: Context): SalonDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SalonDatabase::class.java,
                    "salon_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
