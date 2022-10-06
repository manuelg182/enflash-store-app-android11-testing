package com.enflash.mobile.storeapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.enflash.mobile.storeapp.database.tablemodifiers.ModifierItem
import com.enflash.mobile.storeapp.database.tablemodifiers.ModifierItemDao
import com.enflash.mobile.storeapp.database.tableorders.Order
import com.enflash.mobile.storeapp.database.tableorders.OrderDao
import com.enflash.mobile.storeapp.database.tableproductos.OrderItem
import com.enflash.mobile.storeapp.database.tableproductos.OrderItemDao

@Database(entities = [Order::class, OrderItem::class, ModifierItem::class], version = 1)
abstract class OrdersDB : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun modifierItemDao(): ModifierItemDao
}