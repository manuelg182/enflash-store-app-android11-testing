package com.enflash.mobile.storeapp.mqtt;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.regions.Region;

import java.security.KeyStore;


public class MqttManager extends AWSIotMqttManager {

    public static AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus status;


    public MqttManager(String mqttClientId, String endpoint) {
        super(mqttClientId, endpoint);
        status = AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost;
        this.setOfflinePublishQueueEnabled(true);
    }

    public MqttManager(String mqttClientId, Region region, String accountEndpointPrefix) {
        super(mqttClientId, region, accountEndpointPrefix);
        status = AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost;
    }

    public AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus getStatus() {
        return status;
    }

    @Override
    public void connect(AWSCredentialsProvider credentialsProvider, AWSIotMqttClientStatusCallback statusCallback) {
        super.connect(credentialsProvider, (awsIotMqttClientStatus, throwable) -> {
            status = awsIotMqttClientStatus;
            if (statusCallback != null) {
                statusCallback.onStatusChanged(status, throwable);
            }
        });
    }

    @Override
    public void connect(KeyStore keyStore, AWSIotMqttClientStatusCallback statusCallback) {
        super.connect(keyStore, (awsIotMqttClientStatus, throwable) -> {
            status = awsIotMqttClientStatus;
            if (statusCallback != null) {
                statusCallback.onStatusChanged(status, throwable);
            }
        });
    }

}
