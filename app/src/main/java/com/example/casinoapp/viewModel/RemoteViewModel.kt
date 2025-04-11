package com.example.casinoapp.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.casinoapp.entity.GameSession
import com.example.casinoapp.entity.User
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
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @POST("/sessionGame/save")
    suspend fun saveGameSession(@Body gameSession: GameSession): Response<GameSession>

    @PUT("/user/update/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body user: User
    ): Response<Map<String, String>>

    @GET("/sessionGame/{userId}/sessions")
    suspend fun getUserGameHistory(@Path("userId") userId: Int): List<GameSession>

    @PUT("/user/{id}/profile-picture")
    suspend fun updateProfilePicture(
        @Path("id") id: Int,
        @Body profilePicture: String
    ): Response<Map<String, String>>

    @GET("/user/{id}/profile-picture")
    suspend fun getProfilePicture(
        @Path("id") id: Int
    ): Response<String>
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

    private val _gameHistory = MutableStateFlow<List<GameSession>>(emptyList())
    val gameHistory: StateFlow<List<GameSession>> = _gameHistory

    fun login(username: String, password: String, onResult: (Any) -> Any) {
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

    fun register(user: User, onResult: (String) -> Unit) {
        viewModelScope.launch {
            _registerMessageUiState.value = RegisterMessageUiState.Loading
            try {
                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteUserInterface::class.java)
                val response = endpoint.registerUser(user)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _loggedInUser.value = responseBody
                        _registerMessageUiState.value = RegisterMessageUiState.Success(responseBody)
                        onResult("Registro exitoso")
                    } else {
                        _registerMessageUiState.value = RegisterMessageUiState.Error
                        onResult("Error: Respuesta vacía")
                    }
                } else {
                    _registerMessageUiState.value = RegisterMessageUiState.Error
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    onResult("Error: $errorMessage")
                }
            } catch (e: Exception) {
                _registerMessageUiState.value = RegisterMessageUiState.Error
                onResult("Error: Problema de conexión")
            }
        }
    }

    fun logout(onResult: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loggedInUser.value = null
                _loginMessageUiState.value = LoginMessageUiState.Loading
                onResult("Sesión cerrada exitosamente")
            } catch (e: Exception) {
                onResult("Error al cerrar sesión: ${e.message}")
            }
        }
    }

    fun saveGameSession(gameSession: GameSession, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteUserInterface::class.java)
                val response = endpoint.saveGameSession(gameSession)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onResult("Sesión de juego guardada exitosamente")
                    } else {
                        onResult("Error: Respuesta vacía del servidor")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    onResult("Error: $errorMessage")
                }
            } catch (e: Exception) {
                Log.e("RemoteViewModel", "Error al guardar la sesión de juego: ${e.message}", e)
                onResult("Error: Problema de conexión")
            }
        }
    }

    fun updateUser(user: User, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteUserInterface::class.java)
                val response = endpoint.updateUser(user.userId, user)

                if(response.isSuccessful) {
                    response.body()?.let {
                        _loggedInUser.value = user
                        onResult("Perfil actualizado con éxito")
                    }
                } else {
                    onResult("Error al actualizar: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onResult("Error de conexión: ${e.message}")
            }
        }
    }

    // Nueva función para obtener el historial de sesiones de juego del usuario
    fun getUserGameHistory(userId: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteUserInterface::class.java)
                val response = endpoint.getUserGameHistory(userId)

                if (response.isNotEmpty()) {
                    _gameHistory.value = response
                    onResult("Historial de partidas cargado exitosamente")
                } else {
                    onResult("No hay partidas para mostrar")
                }
            } catch (e: Exception) {
                Log.e("RemoteViewModel", "Error al obtener el historial de partidas: ${e.message}", e)
                onResult("Error al cargar el historial de partidas")
            }
        }
    }



    fun updateProfilePicture(userId: Int, imageBase64: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteUserInterface::class.java)
                val response = endpoint.updateProfilePicture(userId, imageBase64)

                if(response.isSuccessful) {
                    response.body()?.let {
                        // Actualizar el usuario local con la nueva imagen
                        _loggedInUser.value?.let { currentUser ->
                            _loggedInUser.value = currentUser.copy(profilePicture = imageBase64)
                        }
                        onResult("Foto de perfil actualizada con éxito")
                    }
                } else {
                    onResult("Error al actualizar la foto: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onResult("Error de conexión: ${e.message}")
            }
        }
    }

    fun getProfilePicture(userId: Int, onResult: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val connection = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val endpoint = connection.create(RemoteUserInterface::class.java)
                val response = endpoint.getProfilePicture(userId)

                if(response.isSuccessful) {
                    response.body()?.let { base64Image ->
                        _loggedInUser.value?.let { currentUser ->
                            _loggedInUser.value = currentUser.copy(profilePicture = base64Image)
                        }
                        onResult(base64Image)
                    }
                }
            } catch (e: Exception) {
                Log.e("RemoteViewModel", "Error al obtener foto de perfil", e)
            }
        }
    }
}
