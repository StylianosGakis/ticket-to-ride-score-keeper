package se.hkr.tickettoride.mqtt

import android.util.Log
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import se.hkr.tickettoride.util.Constants
import java.util.*

object MqttConnection {
    val TAG: String = "AppDebug"
    lateinit var mqttClient: MqttClient

    fun publish(
        topic: String,
        message: String
    ) {
        try {
            Log.d(TAG, "MqttConnection: publishing: topic: $topic, message: $message")
            val mqttMessage = MqttMessage(message.toByteArray())
            mqttMessage.qos = Constants.QOS
            mqttMessage.isRetained = true
            mqttClient.publish(topic, mqttMessage)
        } catch (e: Exception) {
            Log.e(TAG, "Exception stack trace: ", e)
        }
    }

    fun connect(
        hostUrl: String
    ) {
        try {
            mqttClient = MqttClient(
                hostUrl,
                UUID.randomUUID().toString(),
                MemoryPersistence()
            )
            val connectionOptions = MqttConnectOptions()
            connectionOptions.isCleanSession = true
            mqttClient.connect(connectionOptions)
            Log.d(TAG, "Mqtt connection success")
        } catch (e: Exception) {
            Log.e(TAG, "Mqtt connection failure")
            Log.e(TAG, "Exception stack trace: ", e)
        }
    }
}