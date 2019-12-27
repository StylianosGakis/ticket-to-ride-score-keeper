package se.hkr.tickettoride.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JSON
import se.hkr.tickettoride.model.Player
import se.hkr.tickettoride.mqtt.MqttConnection
import se.hkr.tickettoride.util.Constants

class MainViewModel : ViewModel() {
    init {
        initializeMqttConnection()
        initializeMqttSubscription()
    }

    val TAG = "MainViewModel"
    private val _playersList: MutableLiveData<List<Player>> =
        MutableLiveData(
            listOf(
                Player(id = 1, stations = 3),
                Player(id = 2, stations = 3),
                Player(id = 3, stations = 3),
                Player(id = 4, stations = 3),
                Player(id = 5, stations = 3)
            )
        )
    val playersList: LiveData<List<Player>>
        get() = _playersList

    private fun updatePlayer(player: Player) {
        val myList = playersList.value!!.toMutableList()
        myList.remove(myList.find { it.id == player.id })
        myList.add(player)
        _playersList.value = myList
    }

    private fun initializeMqttConnection() {
        MqttConnection.connect("tcp://broker.mqttdashboard.com")
    }

    private fun initializeMqttSubscription() {
        Log.d(TAG, "Initializing MQTT subscription")
        MqttConnection.mqttClient.subscribe("${Constants.BASE_TOPIC}/#") { topic, message ->
            Log.d(TAG, "MainViewModel: subscription received topic: $topic, message: $message")
            try {
                val newPlayer = JSON.parse(Player.serializer(), message.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    updatePlayer(newPlayer)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "Wrong player formatting")
            }
        }
    }
}
