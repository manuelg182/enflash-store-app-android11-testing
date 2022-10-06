package com.enflash.mobile.storeapp.database.tablemodifiers

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = ModifierItem.TABLE_NAME)
data class ModifierItem(@PrimaryKey
                        @ColumnInfo(name = "uuid") @NotNull var uuid: String,
                        @ColumnInfo(name = "id") @NotNull var id: String,
                        @ColumnInfo(name = "modifierUuid") @NotNull var modifierUuid: String,
                        @ColumnInfo(name = "name") @NotNull var name: String,
                        @ColumnInfo(name = "type") @NotNull var type: String,
                        @ColumnInfo(name = "quantity") var quantity: Int,
                        @ColumnInfo(name = "price") var price: Double

) : Serializable {
    companion object {
        const val TABLE_NAME = "modifiers"
    }
}