package com.enflash.mobile.storeapp.main.model

import androidx.annotation.Keep
import com.enflash.mobile.storeapp.main.enums.StoreStatus
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
class StoreStatusChange {
    @SerializedName("storeId")
    @Expose
    var storeId: Long? = null

    @SerializedName("status")
    @Expose
    var status: StoreStatus? = null
}