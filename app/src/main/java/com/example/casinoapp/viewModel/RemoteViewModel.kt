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
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

sealed interface RemoteMessageUiState {
    data class Success(val remoteMessage: List<User>) : RemoteMessageUiState
    object Loading : RemoteMessageUiState
    object Error : RemoteMessageUiState

}

sealed interface LoginMessageUiState {
    data class Success(val loginMessage: User?) : LoginMessageUiState
    object Loading : LoginMessageUiState
    object Error : LoginMessageUiState

}

sealed interface RegisterMessageUiState {
    data class Success(val user: User) : RegisterMessageUiState
    object Loading : RegisterMessageUiState
    object Error : RegisterMessageUiState
}

interface RemoteUserInterface {

    @GET("/user/index")
    suspend fun getAllUsers(): List<User>

    @FormUrlEncoded
    @POST("/user/login")
    suspend fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<User>

    @POST("/user/register")
    suspend fun registerUser(@Body user: User): Response<User>

}

class RemoteViewModel : ViewModel() {

    private val _remoteMessageUiState = MutableStateFlow<RemoteMessageUiState>(RemoteMessageUiState.Loading)
    val remoteMessageUiState: StateFlow<RemoteMessageUiState> = _remoteMessageUiState

    private val _loginMessageUiState = MutableStateFlow<LoginMessageUiState>(LoginMessageUiState.Loading)
    val loginMessageUiState: StateFlow<LoginMessageUiState> = _loginMessageUiState

    private val _registerMessageUiState = MutableStateFlow<RegisterMessageUiState>(RegisterMessageUiState.Loading)
    val registerMessageUiState: StateFlow<RegisterMessageUiState> = _registerMessageUiState

    private val _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser: StateFlow<User?> = _loggedInUser

    fun login(username: String, password: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            _loginMessageUiState.value = LoginMessageUiState.Loading
            try {
                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteUserInterface::class.java)
                val response = endpoint.loginUser(username, password)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _loggedInUser.value = responseBody
                        _loginMessageUiState.value = LoginMessageUiState.Success(responseBody)
                        Log.d("RemoteViewModel", "Login exitoso. Datos recibidos: $responseBody")
                        onResult("Login exitoso")
                    } else {
                        _loginMessageUiState.value = LoginMessageUiState.Error
                        onResult("Error: Datos no recibidos")
                    }
                } else {
                    _loginMessageUiState.value = LoginMessageUiState.Error
                    onResult("Error: Credenciales incorrectas. Datos recibidos: $response")
                }
            } catch (e: Exception) {
                _loginMessageUiState.value = LoginMessageUiState.Error
                onResult("Error: Problema de conexión")
            }
        }
    }

    fun register(user: User, function: (String) -> Unit) {
    viewModelScope.launch {
            _registerMessageUiState.value = RegisterMessageUiState.Loading
            try {
                Log.d("RemoteViewModel", "Intentando registrar usuario: ${user.username}")

                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteUserInterface::class.java)
                val response = endpoint.registerUser(user)

                Log.d("RemoteViewModel", "Código de respuesta: ${response.code()}")
                Log.d("RemoteViewModel", "Mensaje de respuesta: ${response.message()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _registerMessageUiState.value = RegisterMessageUiState.Success(responseBody)
                        Log.d("RemoteViewModel", "Registro exitoso para usuario: ${user.username}")
                    } else {
                        _registerMessageUiState.value = RegisterMessageUiState.Error
                        Log.e("RemoteViewModel", "Registro fallido: respuesta vacía")
                    }
                } else {
                    _registerMessageUiState.value = RegisterMessageUiState.Error
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("RemoteViewModel", "Error en registro: $errorMessage")
                }
            } catch (e: Exception) {
                _registerMessageUiState.value = RegisterMessageUiState.Error
                Log.e("RemoteViewModel", "Excepción durante el registro: ${e.localizedMessage}", e)
            }
        }
    }

    fun logout() {
        _loginMessageUiState.value = LoginMessageUiState.Loading
    }

    fun getAllUsers() {
        viewModelScope.launch {
            _remoteMessageUiState.value = RemoteMessageUiState.Loading
            try {
                Log.d("RemoteViewModel", "Iniciando conexión Retrofit con base URL: http://10.0.2.2:8080")
                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteUserInterface::class.java)
                val response = endpoint.getAllUsers()
                Log.d("RemoteViewModel", "Datos recibidos: $response")
                _remoteMessageUiState.value = RemoteMessageUiState.Success(response)
            } catch (e: Exception) {
                Log.e("RemoteViewModel", "Error en la conexión o procesamiento: ${e.message}", e)
                _remoteMessageUiState.value = RemoteMessageUiState.Error
            }
        }
    }

    // En RemoteViewModel.kt
    fun updateLoggedInUserFondocoins(newFondoCoins: Int) {
        _loggedInUser.value = _loggedInUser.value?.copy(fondocoins = newFondoCoins)
    }
}