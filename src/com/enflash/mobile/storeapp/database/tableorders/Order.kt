package com.enflash.mobile.storeapp.database.tableorders

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = Order.TABLE_NAME)
data class Order(@PrimaryKey
                 @ColumnInfo(name = "orderId") @NotNull var orderId: String,
                 @ColumnInfo(name = "notes") var notes: String,
                 @ColumnInfo(name = "folio") var folio: String,
                 @ColumnInfo(name = "image") var image: String,
                 @ColumnInfo(name = "tip") var tip: Double,
                 @ColumnInfo(name = "total") var total: Double,
                 @ColumnInfo(name = "discount") var discount: Double,
                 @ColumnInfo(name = "totalPaid") var totalPaid: Double,
                 @ColumnInfo(name = "totalToPay") var totalToPay: Double,
                 @ColumnInfo(name = "paymentMethod") var paymentMethod: String,
                 @ColumnInfo(name = "createdAt") var createdAt: Long,
                 @ColumnInfo(name = "averageTime") var averageTime: Long,
                 @ColumnInfo(name = "status") var status: String

) : Serializable {
    companion object {
        const val TABLE_NAME = "orders"
    }
}