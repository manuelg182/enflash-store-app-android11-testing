package com.enflash.mobile.storeapp.database.tablemodifiers

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ModifierItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(modifier: ModifierItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg modifier: ModifierItem)

    @Delete
    fun delete(vararg modifier: ModifierItem)

    @Query("SELECT * FROM " + ModifierItem.TABLE_NAME + "  WHERE modifierUuid = :id")
    fun findModifierById(id: String): List<ModifierItem>

    @Query("SELECT * FROM " + ModifierItem.TABLE_NAME + "  WHERE modifierUuid = :modifierUuid")
    fun getModifiers(modifierUuid: String): List<ModifierItem>

    @Query("SELECT * FROM " + ModifierItem.TABLE_NAME)
    fun getModifier(): LiveData<List<ModifierItem>>

    @Query("DELETE FROM " + ModifierItem.TABLE_NAME)
    fun deleteAll()

}