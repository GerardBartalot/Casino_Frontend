package com.example.hospitalapp.classes

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
    data class Success(val remoteMessage: List<Nurse>) : RemoteMessageUiState
    object Loading : RemoteMessageUiState
    object Error : RemoteMessageUiState

}

sealed interface LoginMessageUiState {
    data class Success(val loginMessage: User?) : LoginMessageUiState
    object Loading : LoginMessageUiState
    object Error : LoginMessageUiState

}

sealed interface RegisterMessageUiState {
    data class Success(val nurse: Nurse?) : RegisterMessageUiState
    object Loading : RegisterMessageUiState
    object Error : RegisterMessageUiState
}

interface RemoteNurseInterface {

    @GET("/nurse/index")
    suspend fun getAllNurses(): List<Nurse>

    @FormUrlEncoded
    @POST("/nurse/login")
    suspend fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<User>

    @POST("/nurse/create")
    suspend fun registerUser(@Body nurse: Nurse): Response<Nurse>

}

class RemoteViewModel : ViewModel() {

    private val _remoteMessageUiState = MutableStateFlow<RemoteMessageUiState>(RemoteMessageUiState.Loading)
    val remoteMessageUiState: StateFlow<RemoteMessageUiState> = _remoteMessageUiState

    private val _loginMessageUiState = MutableStateFlow<LoginMessageUiState>(LoginMessageUiState.Loading)
    val loginMessageUiState: StateFlow<LoginMessageUiState> = _loginMessageUiState

    private val _registerMessageUiState = MutableStateFlow<RegisterMessageUiState>(RegisterMessageUiState.Loading)
    val registerMessageUiState: StateFlow<RegisterMessageUiState> = _registerMessageUiState

    fun getAllNurses() {
        viewModelScope.launch {
            _remoteMessageUiState.value = RemoteMessageUiState.Loading
            try {
                Log.d("RemoteViewModel", "Iniciando conexión Retrofit con base URL: http://10.0.2.2:8080")
                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteNurseInterface::class.java)
                val response = endpoint.getAllNurses()
                Log.d("RemoteViewModel", "Datos recibidos: $response")
                _remoteMessageUiState.value = RemoteMessageUiState.Success(response)
            } catch (e: Exception) {
                Log.e("RemoteViewModel", "Error en la conexión o procesamiento: ${e.message}", e)
                _remoteMessageUiState.value = RemoteMessageUiState.Error
            }
        }
    }

    fun loginUser(username: String, password: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            _loginMessageUiState.value = LoginMessageUiState.Loading
            try {
                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteNurseInterface::class.java)
                val response = endpoint.loginUser(username, password)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _loginMessageUiState.value = LoginMessageUiState.Success(responseBody)
                        Log.d("RemoteViewModel", "Login exitoso. Datos recibidos: $responseBody")
                        onResult("Login exitoso")
                    } else {
                        _loginMessageUiState.value = LoginMessageUiState.Error
                        onResult("Error: Datos no recibidos")
                    }
                } else {
                    _loginMessageUiState.value = LoginMessageUiState.Error
                    onResult("Error: Credenciales incorrectas")
                }
            } catch (e: Exception) {
                _loginMessageUiState.value = LoginMessageUiState.Error
                onResult("Error: Problema de conexión")
            }
        }
    }

    fun registerUser(nurse: Nurse, onResult: (String) -> Unit) {
        viewModelScope.launch {
            _registerMessageUiState.value = RegisterMessageUiState.Loading
            try {
                Log.d("RemoteViewModel", "Intentando registrar usuario: ${nurse.username}")

                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteNurseInterface::class.java)
                val response = endpoint.registerUser(nurse)

                Log.d("RemoteViewModel", "Código de respuesta: ${response.code()}")
                Log.d("RemoteViewModel", "Mensaje de respuesta: ${response.message()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _registerMessageUiState.value = RegisterMessageUiState.Success(responseBody)
                        Log.d("RemoteViewModel", "Registro exitoso para usuario: ${nurse.username}")
                        onResult("Registro exitoso")
                    } else {
                        _registerMessageUiState.value = RegisterMessageUiState.Error
                        Log.e("RemoteViewModel", "Registro fallido: respuesta vacía")
                        onResult("Error: Datos no recibidos")
                    }
                } else {
                    _registerMessageUiState.value = RegisterMessageUiState.Error
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("RemoteViewModel", "Error en registro: $errorMessage")
                    onResult("Error: $errorMessage")
                }
            } catch (e: Exception) {
                _registerMessageUiState.value = RegisterMessageUiState.Error
                Log.e("RemoteViewModel", "Excepción durante el registro: ${e.localizedMessage}", e)
                onResult("Error: Problema de conexión")
            }
        }
    }

}