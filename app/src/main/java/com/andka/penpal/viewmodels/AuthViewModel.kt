package com.andka.penpal.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andka.penpal.domain.LoginResponse
import com.andka.penpal.domain.RegisterResponse
import com.andka.penpal.network.APIConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {
    val isLoading = MutableLiveData(false)
    val errorContent = MutableLiveData<String?>()

    val loginResponse = MutableLiveData<LoginResponse>()
    val registerResponse = MutableLiveData<RegisterResponse>()

    fun doLogin(email: String, password: String) {
        val client = APIConfig.getApiService().login(email, password)
        makeApiCall(client) { response -> loginResponse.postValue(response!!) }
    }

    fun doRegister(name: String, email: String, password: String) {
        val client = APIConfig.getApiService().register(email, password, name)
        makeApiCall(client) { response -> registerResponse.postValue(response!!) }
    }

    // Simplify API call with a generic function
    private fun <T> makeApiCall(call: Call<T>, onSuccess: (T?) -> Unit) {
        isLoading.postValue(true)
        errorContent.postValue(null)
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    onSuccess(response.body())
                } else {
                    response.errorBody()?.let {
                        val errorMessage = JSONObject(it.string()).getString("message")
                        errorContent.postValue(errorMessage)
                    }
                }
                isLoading.postValue(false)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                isLoading.postValue(false)
                Log.e("AuthViewModel", "API call failed: ${t.message}")
                errorContent.postValue(t.message)
            }
        })
    }
}