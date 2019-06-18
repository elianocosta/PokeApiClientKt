package com.example.PokedexKotlin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.PokedexKotlin.R
import com.example.PokedexKotlin.adapters.PokiListAdp
import com.example.PokedexKotlin.api.PokiApi
import com.example.PokedexKotlin.model.Base
import com.example.PokedexKotlin.model.Pokemon
import kotlinx.android.synthetic.main.poki_list_frg.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PokiListFrg(private val frgMng: FragmentManager) : Fragment() {
    private lateinit var rvList:      RecyclerView
    private lateinit var prgBar:      ProgressBar
    private lateinit var base:        Base
    private val restClient = PokiApi.create()
    private var isReady  = true
    private var pokiList = arrayListOf<Pokemon>()
    private var offset   = 0
    private val limit    = 30

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.poki_list_frg, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prgBar      = progressBar
        rvCreate()
        requestList()

    }
    fun rvCreate() {
        val gridLayoutMng = GridLayoutManager(context, 3)
        rvList = recyclerViewList
        rvList.layoutManager = gridLayoutMng
        rvList.setHasFixedSize(true)
        rvList.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val vsblItem = gridLayoutMng.childCount
                val pastItem = gridLayoutMng.findFirstVisibleItemPosition()
                val totlItem = gridLayoutMng.itemCount
                if ((dy > 0) && isReady && ((vsblItem + pastItem) >= totlItem)) {
                    isReady = false
                    offset += 30
                    if (offset == base.count) offset = 0
                    requestList()
                }
            }
        })
    }

    fun requestList() {
        prgBar.visibility = View.VISIBLE
        restClient.getPokiList(limit, offset)
        .enqueue(object: Callback<Base> {
            override fun onResponse(call: Call<Base>, response: Response<Base>) {
                if (response.isSuccessful) {
                    base = response.body()!!
                    pokiList = base.list
                    for (i in 0 until limit) requestStat(pokiList.get(i).name)
                }
            }
            override fun onFailure(call: Call<Base>, t: Throwable) {
                Log.e("Debug", "requestListFail")
            }
        })
    }

    fun requestStat(pokemonName: String) {
        restClient.getPokiStats(pokemonName)
        .enqueue(object: Callback<Pokemon> {
            override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                if (response.isSuccessful) {
                    isReady = true
                    val pokemon = response.body()
                    for (i in 0 until pokiList.size) {
                        if (pokiList.get(i).name.equals((pokemon!!.name))) pokiList.set(i, pokemon)
                        if (pokiList.get(i).id == 0) isReady = false
                    }
                    if (isReady) {
                        val list = pokiList.toList()
                        rvList.adapter = PokiListAdp(pokiList, frgMng)
                        prgBar.visibility = View.GONE

                    }
                }
            }
            override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                Log.e("Debug", "requestStatFail")
            }
        })
    }
}

