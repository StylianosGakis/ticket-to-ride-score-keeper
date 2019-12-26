package se.hkr.tickettoride.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_player.view.*
import kotlinx.android.synthetic.main.list_item_single.view.*
import se.hkr.tickettoride.R
import se.hkr.tickettoride.model.Player
import se.hkr.tickettoride.util.Constants
import se.hkr.tickettoride.util.MyGson

class PlayerListAdapter(
    private val interaction: Interaction
) : RecyclerView.Adapter<PlayerListAdapter.PlayerViewHolder>() {
    companion object {
        const val TAG = "AppDebug"
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldPlayer: Player, newPlayer: Player): Boolean {
            return oldPlayer.id == newPlayer.id
        }

        override fun areContentsTheSame(oldPlayer: Player, newPlayer: Player): Boolean {
            return oldPlayer == newPlayer
        }
    }
    private val differ = object : AsyncListDiffer<Player>(this, diffCallback) {
        override fun submitList(newList: List<Player>?, commitCallback: Runnable?) {
            super.submitList(newList?.toList(), commitCallback)
        }

        override fun submitList(newList: List<Player>?) {
            super.submitList(newList?.toList())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        return PlayerViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_player, parent, false
            ), interaction
        )
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Player>) {
        differ.submitList(list.sortedBy { it.id })
    }

    inner class PlayerViewHolder(
        itemView: View,
        private val interaction: Interaction
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(player: Player) {
            Log.d(TAG, "Binding Player viewHolder with id: ${player.id}")
            itemView.nameText.setText(player.name)
            itemView.oneLayout.value.text = player.one.toString()
            itemView.twoLayout.value.text = player.two.toString()
            itemView.threeLayout.value.text = player.three.toString()
            itemView.fourLayout.value.text = player.four.toString()
            itemView.sixLayout.value.text = player.six.toString()
            itemView.eightLayout.value.text = player.eight.toString()
            itemView.stationsLayout.value.text = player.stations.toString()
            itemView.destinations.setText(player.destinations.toString())
            itemView.maxRoad.isChecked = player.maxRoad
            itemView.trainsLeft.text = player.trainsLeft.toString()
            itemView.finalScore.text = player.finalScore.toString()
            setupListeners(player)
        }

        private fun setupListeners(player: Player) {
            itemView.nameTextButton.setOnClickListener {
                Log.d(TAG, "name changed")
                updatePlayer(player.copy(name = itemView.nameText.text.toString()))
            }
            itemView.oneLayout.apply {
                buttonMinus.setOnClickListener {
                    updatePlayer(player.copy(one = player.one.minus(1)))
                }
                buttonPlus.setOnClickListener {
                    updatePlayer(player.copy(one = player.one.plus(1)))
                }
            }
            itemView.twoLayout.apply {
                buttonMinus.setOnClickListener {
                    updatePlayer(player.copy(two = player.two.minus(1)))
                }
                buttonPlus.setOnClickListener {
                    updatePlayer(player.copy(two = player.two.plus(1)))
                }
            }
            itemView.threeLayout.apply {
                buttonMinus.setOnClickListener {
                    updatePlayer(player.copy(three = player.three.minus(1)))
                }
                buttonPlus.setOnClickListener {
                    updatePlayer(player.copy(three = player.three.plus(1)))
                }
            }
            itemView.fourLayout.apply {
                buttonMinus.setOnClickListener {
                    updatePlayer(player.copy(four = player.four.minus(1)))
                }
                buttonPlus.setOnClickListener {
                    updatePlayer(player.copy(four = player.four.plus(1)))
                }
            }
            itemView.sixLayout.apply {
                buttonMinus.setOnClickListener {
                    updatePlayer(player.copy(six = player.six.minus(1)))
                }
                buttonPlus.setOnClickListener {
                    updatePlayer(player.copy(six = player.six.plus(1)))
                }
            }
            itemView.eightLayout.apply {
                buttonMinus.setOnClickListener {
                    updatePlayer(player.copy(eight = player.eight.minus(1)))
                }
                buttonPlus.setOnClickListener {
                    updatePlayer(player.copy(eight = player.eight.plus(1)))
                }
            }
            itemView.stationsLayout.apply {
                buttonMinus.setOnClickListener {
                    updatePlayer(player.copy(stations = player.stations.minus(1)))
                }
                buttonPlus.setOnClickListener {
                    updatePlayer(player.copy(stations = player.stations.plus(1)))
                }
            }
            itemView.destinationsButton.setOnClickListener {
                updatePlayer(player.copy(destinations = itemView.destinations.text.toString().toInt()))
            }
            itemView.maxRoad.apply {
                setOnClickListener {
                    updatePlayer(player.copy(maxRoad = isChecked))
                }
            }
        }

        private fun updatePlayer(player: Player) {
            interaction.onPlayerStateChanged(
                "${Constants.BASE_TOPIC}/${player.id}",
                MyGson.gson.toJson(player, Player::class.java)
            )
        }
    }

    interface Interaction {
        fun onPlayerStateChanged(topic: String, message: String)
    }
}
