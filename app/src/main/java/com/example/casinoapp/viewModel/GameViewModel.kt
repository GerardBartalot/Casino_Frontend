package com.example.casinoapp.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casinoapp.entity.Game
import com.example.casinoapp.entity.User
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

interface RemoteGameInterface {

    @GET("/user/{id}/fondocoins")
    suspend fun getUserFondoCoins(@Path("id") id: Int): Response<Int>

    @GET("/games/allGames")
    suspend fun getAllGames(): List<Game>

    @PUT("/user/{id}/fondocoins")
    suspend fun updateUserFondoCoins(
        @Path("id") id: Int,
        @Body newFondoCoins: Int
    ): Response<Map<String, String>>

    @GET("/user/{id}/experience")
    suspend fun getUserExperience(@Path("id") id: Int): Response<Int>

    @PUT("/user/{id}/experience")
    suspend fun updateUserExperience(
        @Path("id") id: Int,
        @Body newExperience: Int
    ): Response<Map<String, String>>
}

class GameViewModel : ViewModel() {
    private val _fondocoins = MutableStateFlow(0)
    val fondocoins: StateFlow<Int> get() = _fondocoins
    private val _experience = MutableStateFlow(0)
    val experience: StateFlow<Int> get() = _experience
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> get() = _games

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val endpoint: RemoteGameInterface = retrofit.create(RemoteGameInterface::class.java)

    fun placeBet(betAmount: Int): Boolean {
        return if (_fondocoins.value >= betAmount) {
            _fondocoins.value -= betAmount
            true
        } else {
            false
        }
    }

    fun getUserFondoCoins(userId: Int) {
        viewModelScope.launch {
            try {
                val response = endpoint.getUserFondoCoins(userId)
                if (response.isSuccessful) {
                    _fondocoins.value = response.body() ?: 0
                } else {
                    Log.e("GameViewModel", "Error en la respuesta: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error obteniendo fondocoins: ${e.message}", e)
            }
        }
    }

    fun updateUserFondoCoins(userId: Int, newFondoCoins: Int) {
        viewModelScope.launch {
            try {
                val response = endpoint.updateUserFondoCoins(userId, newFondoCoins)
                if (response.isSuccessful) {
                    _fondocoins.value = newFondoCoins
                } else {
                    Log.e("GameViewModel", "Error actualizando fondocoins: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error actualizando fondocoins: ${e.message}", e)
            }
        }
    }

    fun getUserExperience(userId: Int) {
        viewModelScope.launch {
            try {
                val response = endpoint.getUserExperience(userId)
                if (response.isSuccessful) {
                    _experience.value = response.body() ?: 0
                } else {
                    Log.e("GameViewModel", "Error en la respuesta: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error obteniendo experiencia: ${e.message}", e)
            }
        }
    }

    fun updateUserExperience(userId: Int, newExperience: Int) {
        viewModelScope.launch {
            try {
                val response = endpoint.updateUserExperience(userId, newExperience)
                if (response.isSuccessful) {
                    _experience.value = newExperience
                    checkLevelUp(newExperience)
                } else {
                    Log.e("GameViewModel", "Error actualizando experiencia: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error actualizando experiencia: ${e.message}", e)
            }
        }
    }

    fun getAllGames() {
        viewModelScope.launch {
            try {
                val gamesList = endpoint.getAllGames()
                _games.value = gamesList
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error obteniendo juegos: ${e.message}", e)
                _games.value = emptyList()
            }
        }
    }

    //Logica LevelUpPopUp
    var showLevelUpPopup by mutableStateOf(false)
        private set

    var currentPopupLevel by mutableStateOf(0)
        private set

    private var previousLevel by mutableStateOf(1)

    fun checkLevelUp(currentExperience: Int) {
        val newLevel = (currentExperience / 1000) + 1
        if (newLevel > previousLevel) {
            previousLevel = newLevel
            currentPopupLevel = newLevel
            showLevelUpPopup = true
        }
    }

    fun dismissLevelUpPopup() {
        showLevelUpPopup = false
    }


}
