package com.enflash.mobile.storeapp.main.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
class ResponseStoreStatusChange {

    @SerializedName("storeId")
    @Expose
    var storeId: Long? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null

}