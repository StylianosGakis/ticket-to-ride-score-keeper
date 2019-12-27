package se.hkr.tickettoride.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: Int = 0,
    var name: String = "",
    var one: Int = 0,
    var two: Int = 0,
    var three: Int = 0,
    var four: Int = 0,
    var six: Int = 0,
    var eight: Int = 0,
    var destinations: Int = 0,
    var stations: Int = 0,
    var maxRoad: Boolean = false
) {
    fun trainsLeft(): Int {
        return 45 - (one +
                (two * 2) +
                (three * 3) +
                (four * 4) +
                (six * 6) +
                (eight * 8))
    }

    fun finalScore(): Int {
        return one + (two * 2) +
                (three * 4) +
                (four * 7) +
                (six * 15) +
                (eight * 21) +
                (stations * 4) +
                destinations +
                (if (maxRoad) 10 else 0)
    }
}