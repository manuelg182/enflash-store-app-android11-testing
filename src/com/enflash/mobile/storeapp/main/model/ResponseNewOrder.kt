package com.enflash.mobile.storeapp.main.model

import androidx.annotation.Keep
import com.enflash.mobile.storeapp.ordenes.data.OrderItems
import com.enflash.mobile.storeapp.ordenes.data.OrderTarget
import com.enflash.mobile.storeapp.ordenes.enums.OrderStatus
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Keep
class ResponseNewOrder {

    @SerializedName("orderId")
    @Expose
    var orderId: String? = null

    @SerializedName("version")
    @Expose
    var version: String? = null

    @SerializedName("thumbnail")
    @Expose
    var thumbnail = ""

    @SerializedName("createdAt")
    @Expose
    var createdAt: Long? = null

    @SerializedName("status")
    @Expose
    var status: OrderStatus? = null

    @SerializedName("store")
    @Expose
    var store: OrderTarget? = null

    @SerializedName("customer")
    @Expose
    var customer: OrderTarget? = null

    @SerializedName("items")
    @Expose
    var items: List<OrderItems>? = null

    @SerializedName("notes")
    @Expose
    var notes = ""

    @SerializedName("tip")
    @Expose
    var tip: Double? = null

    @SerializedName("total")
    @Expose
    var total: Double? = null

    @SerializedName("discount")
    @Expose
    var discount: Double? = null

    @SerializedName("totalPaid")
    @Expose
    var totalPaid: Double? = null

    @SerializedName("totalToPay")
    @Expose
    var totalToPay: Double? = null

    @SerializedName("folio")
    @Expose
    var folio = ""

    @SerializedName("paymentMethod")
    @Expose
    var paymentMethod = ""

    @SerializedName("averageTime")
    @Expose
    var averageTime: Long? = null

    @SerializedName("extraData")
    @Expose
    var extraData: Any? = null
}