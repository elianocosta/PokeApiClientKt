package com.example.PokedexKotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.PokedexKotlin.fragments.PokiListFrg

class MainActivity : AppCompatActivity() {
    private val frgMng      = supportFragmentManager;
    private val pokiListFrg = PokiListFrg(frgMng)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        frgMng.beginTransaction().add(R.id.contentLayout, pokiListFrg).commit()
    }
}