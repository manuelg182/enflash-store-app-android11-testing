package com.enflash.mobile.storeapp.database.tableproductos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = OrderItem.TABLE_NAME)
data class OrderItem(@PrimaryKey
                     @ColumnInfo(name = "uuid") @NotNull var uuid: String,
                     @ColumnInfo(name = "orderUuid") @NotNull var orderUuid: String,
                     @ColumnInfo(name = "productId") var productId: String,
                     @ColumnInfo(name = "productPrice") var productPrice: Double,
                     @ColumnInfo(name = "quantity") var quantity: Int,
                     @ColumnInfo(name = "description") var description: String,
                     @ColumnInfo(name = "price") var price: Double,
                     @ColumnInfo(name = "discount") var discount: Double,
                     @ColumnInfo(name = "total") var total: Double

) : Serializable {
    companion object {
        const val TABLE_NAME = "productos"
    }
}