package com.example.casinoapp.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.net.UnknownHostException

sealed interface RemoteMessageUiState {
    data class Success(val remoteMessage: List<User>) : RemoteMessageUiState

    object Error : RemoteMessageUiState
    object Loading : RemoteMessageUiState
}

sealed interface LoginMessageUiState {
    data class Success(val loginMessage: User) : LoginMessageUiState
    object Error : LoginMessageUiState
    object Loading : LoginMessageUiState
}

sealed interface GetUserMessageUiState {
    object Loading : GetUserMessageUiState, RemoteMessageUiState
    data class Success(val getUserMessage: User) : GetUserMessageUiState, RemoteMessageUiState
    object Error : GetUserMessageUiState, RemoteMessageUiState
}

sealed interface DeleteMessageUiState {
    object Loading : DeleteMessageUiState, RemoteMessageUiState
    data class Success(val deleteMessage: Boolean) : DeleteMessageUiState, RemoteMessageUiState
    object Error : DeleteMessageUiState, RemoteMessageUiState
}

sealed interface UpdateMessageUiState {
    object Loading : UpdateMessageUiState, RemoteMessageUiState
    data class Success(val updateMessage: User) : UpdateMessageUiState, RemoteMessageUiState
    object Error : UpdateMessageUiState, RemoteMessageUiState
}

interface RemoteUserInterface {
    @GET("user/index")
    suspend fun getRemoteUsers(): List<User>

    //@POST("user/login")
    //suspend fun login(@Body loginRequest: LoginRequest): User

    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<User>

    @POST("user/new")
    suspend fun register(@Body registerRequest: RegisterRequest): User

    @DELETE("user/{userId}")
    suspend fun deleteUser(@Path("userId") id: Int): Boolean

    @GET("user/{userId}")
    suspend fun getUserById(@Path("userId") userId: Int): User

    @PUT("user/{userId}")
    suspend fun updateUser(@Path("userId") userId: Int, @Body updatedUser: User): User
}

class RemoteViewModel : ViewModel() {

    private val _remoteMessageUiState =
        MutableStateFlow<RemoteMessageUiState>(RemoteMessageUiState.Loading)
    var remoteMessageUiState: StateFlow<RemoteMessageUiState> = _remoteMessageUiState

    private val _loginMessageUiState =
        MutableStateFlow<LoginMessageUiState>(LoginMessageUiState.Loading)
    var loginMessageUiState: StateFlow<LoginMessageUiState> = _loginMessageUiState

    private val _getUserMessageUiState =
        MutableStateFlow<GetUserMessageUiState>(GetUserMessageUiState.Loading)
    var getUserMessageUiState: StateFlow<GetUserMessageUiState> = _getUserMessageUiState


    private val _updateUserUiState = MutableStateFlow<UpdateMessageUiState>(UpdateMessageUiState.Loading)
    val updateUserUiState: StateFlow<UpdateMessageUiState> = _updateUserUiState

    var deleteUserState = mutableStateOf<Boolean?>(null)

    val connection = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val remoteService = connection.create(RemoteUserInterface::class.java)

    // Login
    fun login(username: String, password: String, context: Context) {
        viewModelScope.launch {
            _loginMessageUiState.value = LoginMessageUiState.Loading
            try {
                val response = remoteService.login(username, password)
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putInt("user_id", user.userId)
                            apply()
                        }
                        _loginMessageUiState.value = LoginMessageUiState.Success(user)
                    } else {
                        Log.e("Login", "Invalid username or password")
                        _loginMessageUiState.value = LoginMessageUiState.Error
                    }
                } else {
                    Log.e("Login", "HTTP error: ${response.code()} - ${response.message()}")
                    _loginMessageUiState.value = LoginMessageUiState.Error
                }
            } catch (e: HttpException) {
                Log.e("Login", "HTTP error: ${e.code()} - ${e.message}", e)
                _loginMessageUiState.value = LoginMessageUiState.Error
            } catch (e: UnknownHostException) {
                Log.e("Login", "Network error: Unable to resolve host", e)
                _loginMessageUiState.value = LoginMessageUiState.Error
            } catch (e: Exception) {
                Log.e("Login", "Unexpected error: ${e.message}", e)
                _loginMessageUiState.value = LoginMessageUiState.Error
            }
        }
    }


    // Registro
    fun register(name: String, username: String, password: String, context: Context) {
        viewModelScope.launch {
            _loginMessageUiState.value = LoginMessageUiState.Loading
            try {
                Log.d("Registro", "Attempting to log in user: $username")
                val registerRequest =
                    RegisterRequest(name = name, username = username, password = password)
                val user = remoteService.register(registerRequest)

                // Guardar el ID del usuario en SharedPreferences
                val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putInt("user_id", user.userId)
                    apply()
                }

                Log.d("Registro", "Login successful: $user")
                _loginMessageUiState.value = LoginMessageUiState.Success(user)
            } catch (e: HttpException) {
                Log.e("Registro", "HTTP error: ${e.code()} - ${e.message}", e)
                _loginMessageUiState.value = LoginMessageUiState.Error
            } catch (e: UnknownHostException) {
                Log.e("Registro", "Network error: Unable to resolve host", e)
                _loginMessageUiState.value = LoginMessageUiState.Error
            } catch (e: Exception) {
                Log.e("Registro", "Unexpected error: ${e.message}", e)
                _loginMessageUiState.value = LoginMessageUiState.Error
            }
        }
    }

    // Get all Users
    fun getAllUsers() {
        viewModelScope.launch {
            _remoteMessageUiState.value = RemoteMessageUiState.Loading
            try {
                Log.d("GetUsers", "Fetching users from server...")
                val response = remoteService.getRemoteUsers()
                Log.d("GetUsers", "Successfully fetched users: $response")
                _remoteMessageUiState.value = RemoteMessageUiState.Success(response)
            } catch (e: Exception) {
                Log.e("GetUsers", "Error fetching users: ${e.message}", e)
                _remoteMessageUiState.value = RemoteMessageUiState.Error
            }
        }
    }

    fun getUserById(userId: Int) {
        viewModelScope.launch {
            _getUserMessageUiState.value = GetUserMessageUiState.Loading
            try {
                Log.d("GetUser", "Fetching user from server...")
                val response = remoteService.getUserById(userId)
                Log.d("GetUser", "Successfully fetched user: $response")
                _getUserMessageUiState.value = GetUserMessageUiState.Success(response)
            } catch (e: Exception) {
                Log.e("GetUser", "Error fetching user: ${e.message}", e)
                _getUserMessageUiState.value = GetUserMessageUiState.Error
            }
        }
    }


    //Update
    fun updateUser(userId: Int, updatedUser: User) {
        viewModelScope.launch {
            _updateUserUiState.value = UpdateMessageUiState.Loading
            try {
                Log.d("UpdateUser", "Updating user with ID: $userId...")
                val response = remoteService.updateUser(userId, updatedUser)
                Log.d("UpdateUser", "Successfully updated user: $response")
                _updateUserUiState.value = UpdateMessageUiState.Success(response)
            } catch (e: HttpException) {
                Log.e("UpdateUser", "HTTP error: ${e.code()} - ${e.message}", e)
                _updateUserUiState.value = UpdateMessageUiState.Error
            } catch (e: UnknownHostException) {
                Log.e("UpdateUser", "Network error: Unable to resolve host", e)
                _updateUserUiState.value = UpdateMessageUiState.Error
            } catch (e: Exception) {
                Log.e("UpdateUser", "Unexpected error: ${e.message}", e)
                _updateUserUiState.value = UpdateMessageUiState.Error
            }
        }
    }

    //Delete
    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            try {
                deleteUserState.value = null
                val response = remoteService.deleteUser(userId)
                deleteUserState.value = response

                if(response) {
                    Log.d("DeleteUser", "NUser with ID $userId deleted successfully")
                    _loginMessageUiState.value = LoginMessageUiState.Loading // Reiniciar sesión
                } else {
                    Log.e("DeleteUser", "Failed to delete user with ID $userId")

                }

            } catch (e: Exception) {
                Log.e("DeleteUser", "Error deleting user: ${e.message}", e)
                deleteUserState.value = false
            }

        }
    }
}