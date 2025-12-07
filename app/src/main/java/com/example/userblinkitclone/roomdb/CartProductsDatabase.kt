package com.example.userblinkitclone.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartProductsTable::class], version = 2, exportSchema = false)
abstract class CartProductsDatabase : RoomDatabase() {
    abstract fun cartProductsDao(): CartProductsDao

    companion object{
        @Volatile
        private var INSTANCE : CartProductsDatabase? = null

        fun getDatabaseInstance(context: Context) : CartProductsDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val roomDatabaseInstance = Room.databaseBuilder(context, CartProductsDatabase::class.java, "CartProductsDatabase").fallbackToDestructiveMigration().allowMainThreadQueries().build()
                INSTANCE = roomDatabaseInstance
                return roomDatabaseInstance

            }
        }
    }
}