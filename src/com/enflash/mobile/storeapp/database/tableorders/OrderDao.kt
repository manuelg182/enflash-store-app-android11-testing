package com.enflash.mobile.storeapp.database.tableorders

import androidx.lifecycle.LiveData
import androidx.room.*
import com.enflash.mobile.storeapp.main.model.DateTimeDto

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(order: Order)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg order: Order)

    @Query("update " + Order.TABLE_NAME + " SET status = :status WHERE orderId = :orderId")
    fun updateOrderStatus(orderId: String, status: String)

    @Query("update " + Order.TABLE_NAME + " SET status = :statusNew WHERE status = :statusOld")
    fun changeOrderStatusByStatus(statusOld: String, statusNew: String)

    @Delete
    fun delete(vararg order: Order)

    @Query("SELECT * FROM " + Order.TABLE_NAME + " WHERE orderId = :orderId LIMIT 1")
    fun findOrderById(orderId: String): Order

    @Query("SELECT * FROM " + Order.TABLE_NAME)
    fun getOrders(): List<Order>

    @Query("SELECT count(*) > 0 FROM " + Order.TABLE_NAME + " WHERE orderId = :orderId")
    fun exist(orderId: String): Boolean

    @Query("SELECT count(*) > 0 FROM " + Order.TABLE_NAME
            + " WHERE orderId = :orderId and status = :status")
    fun existByOrderIdAndStatus(orderId: String, status: String): Boolean

    @Query("SELECT count(*) > 0 FROM " + Order.TABLE_NAME
            + " WHERE status = :status and " +
            "(cast(createdAt / 1000 as INTEGER) + (averageTime * 60)) - cast(strftime('%s','now') as INTEGER) < :seconds")
    fun existOrderByMarkAsReady(status: String, seconds: Long): Boolean

    @Query("SELECT count(*) > 0 FROM " + Order.TABLE_NAME
            + " WHERE status = :status and cast(strftime('%s','now') as INTEGER) - cast(createdAt / 1000 as INTEGER) > :seconds")
    fun existOrderByAccept(status: String, seconds: Long): Boolean

    @Query("SELECT * FROM " + Order.TABLE_NAME
            + " WHERE status = :status and cast(strftime('%s','now') as INTEGER) - cast(createdAt / 1000 as INTEGER) > :seconds")
    fun getOrderByRejectWithoutAccepted(status: String, seconds: Long): List<Order>

    @Query("SELECT * FROM " + Order.TABLE_NAME + " WHERE status = :status")
    fun getOrdersByStatus(status: String): LiveData<List<Order>>

    @Query("DELETE FROM " + Order.TABLE_NAME)
    fun deleteAll()

    @Query("DELETE " +
            "FROM " + Order.TABLE_NAME + " WHERE cast(createdAt / 1000 as INTEGER) > :now")
    fun deleteOrderPrevious(now: Long)

    @Query("SELECT orderId " +
            "FROM " + Order.TABLE_NAME + " WHERE cast(createdAt / 1000 as INTEGER) > :now")
    fun getOrderPrevious(now: Long): List<String>
}
