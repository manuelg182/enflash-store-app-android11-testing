package com.enflash.mobile.storeapp.events

import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback

data class StatusEvent(var status: AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus)