package com.enflash.mobile.storeapp.main.model

import com.enflash.mobile.storeapp.ordenes.enums.OrderStatus
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseStore {
    @SerializedName("companyId")
    @Expose
    var companyId: Long? = null

    @SerializedName("orderId")
    @Expose
    var orderId: String? = null

    @SerializedName("datetime")
    @Expose
    var datetime: Long? = null

    @SerializedName("status")
    @Expose
    var status: OrderStatus? = null

    @SerializedName("source")
    @Expose
    var source: String? = null
}