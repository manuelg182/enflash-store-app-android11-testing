package com.enflash.mobile.storeapp.events

import com.enflash.mobile.storeapp.main.model.ResponseNewOrder

data class NewOrderEvent(var order: ResponseNewOrder)