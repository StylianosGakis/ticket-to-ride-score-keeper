package se.hkr.tickettoride.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.main_fragment.mainFragmentRecyclerView
import se.hkr.tickettoride.R
import se.hkr.tickettoride.model.Player
import se.hkr.tickettoride.mqtt.MqttConnection
import se.hkr.tickettoride.util.Constants
import se.hkr.tickettoride.util.MyGson
import se.hkr.tickettoride.util.TopSpacingItemDecoration

class MainFragment : Fragment(), PlayerListAdapter.Interaction {
    private val TAG = "AppDebug"
    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerAdapter: PlayerListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        setHasOptionsMenu(true)
        initializeRecyclerView()
        subscribeObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.new_game_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_game -> {
                showNewGameOptions()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeRecyclerView() {
        recyclerAdapter = PlayerListAdapter(this@MainFragment)
        val topSpacingDecoration = TopSpacingItemDecoration(topPadding = 30)
        mainFragmentRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@MainFragment.context,
                RecyclerView.HORIZONTAL,
                false
            )
            adapter = recyclerAdapter
            addItemDecoration(topSpacingDecoration)
        }
    }

    private fun subscribeObservers() {
        viewModel.playersList.observe(viewLifecycleOwner, Observer { playerList ->
            Log.d(TAG, "MainFragment: new playerList $playerList")
            Log.d(TAG, "MainFragment: updating player list with: $playerList")
            recyclerAdapter.apply {
                submitList(playerList)
            }
        })
    }

    private fun showNewGameOptions() {
        activity?.let {
            MaterialDialog(it).show {
                title(text = "New game?")
                message(text = "This may take a while")
                negativeButton(text = "Cancel")
                positiveButton(text = "New Game!") { populatePlayers(5) }
            }
        }
    }

    private fun populatePlayers(numberOfPlayers: Int) {
        for (i in 1..numberOfPlayers) {
            MqttConnection.publish(
                "${Constants.BASE_TOPIC}/$i",
                MyGson.gson.toJson(Player(id = i, stations = 3))
            )
        }
    }

    override fun onPlayerStateChanged(topic: String, message: String) {
        MqttConnection.publish(topic, message)
    }
}
