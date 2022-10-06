package com.enflash.mobile.storeapp.ordenes.data

import com.enflash.mobile.storeapp.ordenes.enums.OrderStatus
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OrderInfoStatus {
    @SerializedName("companyId")
    @Expose
    val companyId: Long? = null

    @SerializedName("orderId")
    @Expose
    val orderId: String? = null

    @SerializedName("status")
    @Expose
    val status: OrderStatus? = null
}