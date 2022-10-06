package com.enflash.mobile.storeapp.main.model

import androidx.annotation.Keep
import com.enflash.mobile.storeapp.ordenes.enums.OrderStatus
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
class ResponseOrderStatus {

    @SerializedName("companyId")
    @Expose
    var companyId: Long? = null

    @SerializedName("orderId")
    @Expose
    var orderId: String? = null

    @SerializedName("status")
    @Expose
    var status: OrderStatus? = null
}