package com.enflash.mobile.storeapp.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class OrdersDataActivity : RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "flash"

        @Volatile
        private var INSTANCE: OrdersDB? = null

        fun getInstance(context: Context): OrdersDB? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, OrdersDB::class.java, DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build()
                return INSTANCE!!
            }
            return INSTANCE!!
        }
    }
}