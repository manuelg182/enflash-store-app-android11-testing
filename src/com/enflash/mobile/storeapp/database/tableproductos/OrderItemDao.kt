package com.enflash.mobile.storeapp.database.tableproductos

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface OrderItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(order: OrderItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg order: OrderItem)

    @Delete
    fun delete(vararg order: OrderItem)

    @Query("SELECT * FROM " + OrderItem.TABLE_NAME + "  WHERE uuid = :id LIMIT 1")
    fun findProductoById(id: String): OrderItem

    @Query("SELECT * FROM " + OrderItem.TABLE_NAME + " WHERE orderUuid = :orderUuid")
    fun getItemsByOrderId(orderUuid: String): List<OrderItem>

    @Query("SELECT sum(quantity) FROM " + OrderItem.TABLE_NAME + " WHERE orderUuid = :orderUuid")
    fun countProducts(orderUuid: String): Int

    @Query("SELECT * FROM " + OrderItem.TABLE_NAME)
    fun getItems(): LiveData<List<OrderItem>>

    @Query("DELETE FROM " + OrderItem.TABLE_NAME)
    fun deleteAll()

}