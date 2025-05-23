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
import retrofit2.http.*



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

    @DELETE("/user/{id}/profile-picture")
    suspend fun deleteProfilePicture(@Path("id") id: Int): Response<Map<String, String>>

    @DELETE("/user/delete/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Map<String, String>>
}



class RemoteViewModel : ViewModel() {

    private val baseUrl = "http://10.0.2.2:8080"
    private val connection = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val endpoint = connection.create(RemoteUserInterface::class.java)

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
                val response = endpoint.loginUser(username, password)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _loggedInUser.value = it
                        _loginMessageUiState.value = LoginMessageUiState.Success(it)
                        Log.d("RemoteViewModel", "Login exitós. Dades rebudes: $it")
                        onResult("Login exitós")
                    } ?: run {
                        _loginMessageUiState.value = LoginMessageUiState.Error
                        onResult("Error: Dades no rebudes")
                    }
                } else {
                    _loginMessageUiState.value = LoginMessageUiState.Error
                    onResult("Error: Credencials incorrectes")
                }
            } catch (e: Exception) {
                _loginMessageUiState.value = LoginMessageUiState.Error
                onResult("Error: Problema de connexió")
            }
        }
    }

    fun register(user: User, onResult: (String) -> Unit) {
        viewModelScope.launch {
            _registerMessageUiState.value = RegisterMessageUiState.Loading
            try {
                val response = endpoint.registerUser(user)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _loggedInUser.value = it
                        _registerMessageUiState.value = RegisterMessageUiState.Success(it)
                        onResult("Registre exitós")
                    } ?: run {
                        _registerMessageUiState.value = RegisterMessageUiState.Error
                        onResult("Error: Resposta buida")
                    }
                } else {
                    _registerMessageUiState.value = RegisterMessageUiState.Error
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody?.contains("Duplicate entry") == true &&
                        errorBody.contains("users.username")) {
                        "Aquest nom d'usuari ja existeix"
                    } else {
                        "Error: $errorBody"
                    }
                    onResult(errorMessage ?: "Error desconegut")
                }
            } catch (e: Exception) {
                _registerMessageUiState.value = RegisterMessageUiState.Error
                onResult("Error: Problema de connexió")
            }
        }
    }

    fun logout(onResult: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loggedInUser.value = null
                _loginMessageUiState.value = LoginMessageUiState.Loading
                onResult("Sessió tancada exitosament")
            } catch (e: Exception) {
                onResult("Error al tancar sessió: ${e.message}")
            }
        }
    }

    fun saveGameSession(gameSession: GameSession, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = endpoint.saveGameSession(gameSession)
                if (response.isSuccessful) {
                    response.body()?.let {
                        onResult("Sessió del joc guardada exitósament")
                    } ?: run {
                        onResult("Error: Resposta buida del servidor")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconegut"
                    onResult("Error: $errorMessage")
                }
            } catch (e: Exception) {
                Log.e("RemoteViewModel", "Error al guardar la sessió del joc", e)
                onResult("Error: Problema de connexió")
            }
        }
    }

    fun updateUser(user: User, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = endpoint.updateUser(user.userId, user)
                if (response.isSuccessful) {
                    _loggedInUser.value = user
                    onResult("Perfil actualitzat amb èxit")
                } else {
                    onResult("Error al actualitzar: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onResult("Error de connexió: ${e.message}")
            }
        }
    }

    fun getUserGameHistory(userId: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = endpoint.getUserGameHistory(userId)
                if (response.isNotEmpty()) {
                    _gameHistory.value = response
                    onResult("Historial de partides carregat exitósament")
                } else {
                    onResult("No hi ha partides per mostrar")
                }
            } catch (e: Exception) {
                Log.e("RemoteViewModel", "Error al obtenir l’historial", e)
                onResult("Error al carregar l’historial de partides")
            }
        }
    }

    fun updateProfilePicture(userId: Int, imageBase64: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = endpoint.updateProfilePicture(userId, imageBase64)
                if (response.isSuccessful) {
                    _loggedInUser.value = _loggedInUser.value?.copy(profilePicture = imageBase64)
                    onResult("Foto de perfil actualitzada amb èxit")
                } else {
                    onResult("Error al actualitzar la foto: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onResult("Error de connexió: ${e.message}")
            }
        }
    }

    fun getProfilePicture(userId: Int, onResult: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = endpoint.getProfilePicture(userId)
                if (response.isSuccessful) {
                    response.body()?.let { base64Image ->
                        _loggedInUser.value = _loggedInUser.value?.copy(profilePicture = base64Image)
                        onResult(base64Image)
                    }
                }
            } catch (e: Exception) {
                Log.e("RemoteViewModel", "Error al obtenir la foto de perfil", e)
            }
        }
    }

    fun deleteProfilePicture(userId: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = endpoint.deleteProfilePicture(userId)
                if (response.isSuccessful) {
                    _loggedInUser.value = _loggedInUser.value?.copy(profilePicture = null)
                    onResult("Foto de perfil eliminada")
                } else {
                    onResult("Error al eliminar: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onResult("Error de connexió: ${e.message}")
            }
        }
    }

    fun deleteAccount(onResult: (String) -> Unit = {}) {
        val userId = _loggedInUser.value?.userId
        if (userId != null) {
            viewModelScope.launch {
                try {
                    val response = endpoint.deleteUser(userId)
                    if (response.isSuccessful) {
                        _loggedInUser.value = null
                        _loginMessageUiState.value = LoginMessageUiState.Loading
                        _registerMessageUiState.value = RegisterMessageUiState.Loading
                        onResult("Compte eliminat correctament")
                    } else {
                        onResult("Error al eliminar: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    onResult("Error de connexió: ${e.message}")
                }
            } catch (e: Exception) {
                onResult("Error: ${e.message}")
            }
        }
    }
}
