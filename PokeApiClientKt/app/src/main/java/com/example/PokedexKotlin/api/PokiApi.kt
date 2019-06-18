package com.example.PokedexKotlin.api

import com.example.PokedexKotlin.model.Base
import com.example.PokedexKotlin.model.Pokemon
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
//retrofit para iniciar uma requisição http e transformar em json
interface PokiApi {
    @GET("pokemon")
    fun getPokiList(@Query("limit") limit: Int, @Query("offset") offset: Int) : Call<Base>
    @GET("pokemon/{pokemonName}")
    fun getPokiStats(@Path("pokemonName") pokemonName: String) : Call<Pokemon>
    // singleton
    companion object retrofit {
        private val baseUrl = "https://pokeapi.co/api/v2/"
        fun create(): PokiApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(PokiApi::class.java)
        }
    }
}


