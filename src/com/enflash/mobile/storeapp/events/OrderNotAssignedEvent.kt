package com.enflash.mobile.storeapp.events

import com.enflash.mobile.storeapp.main.model.ResponseOrderStatus

data class OrderNotAssignedEvent(var orderStatus: ResponseOrderStatus)