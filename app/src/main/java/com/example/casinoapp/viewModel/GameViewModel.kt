package com.example.casinoapp.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

data class FondoCoinsRequest(val fondocoins: Int)

interface RemoteGameInterface {

    @GET("/user/{id}/fondocoins")
    suspend fun getUserFondoCoins(@Path("id") id: Int): Response<Int>

    @PUT("/user/{id}/fondocoins")
    suspend fun updateUserFondoCoins(
        @Path("userId") userId: Int,
        @Body fondoCoinsRequest: FondoCoinsRequest
    ): Response<Map<String, String>>
}

class GameViewModel : ViewModel() {
    private val _fondocoins = MutableStateFlow(0)

    fun getUserFondoCoins(userId: Int) {
        viewModelScope.launch {
            try {
                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteGameInterface::class.java)
                val response = endpoint.getUserFondoCoins(userId)

                if (response.isSuccessful) {
                    _fondocoins.value = response.body() ?: 0
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error obteniendo fondocoins: ${e.message}")
            }
        }
    }

    fun updateUserFondoCoins(userId: Int, newFondoCoins: Int) {
        viewModelScope.launch {
            try {
                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val endpoint = connection.create(RemoteGameInterface::class.java)
                val fondoCoinsRequest = FondoCoinsRequest(newFondoCoins)
                val response = endpoint.updateUserFondoCoins(userId, fondoCoinsRequest)

                if (response.isSuccessful) {
                    _fondocoins.value = newFondoCoins
                } else {
                    Log.e("GameViewModel", "Error actualizando fondocoins: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error actualizando fondocoins: ${e.message}")
            }
        }
    }

}