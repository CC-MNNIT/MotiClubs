package com.mnnit.moticlubs.domain.util

import android.util.Log
import com.google.gson.Gson
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.data.network.dto.ErrorDto
import com.mnnit.moticlubs.domain.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.Response

const val TAG = "Resource"

sealed class Resource<T>(val d: T? = null, val errorCode: Int = -1, val errorMsg: String = "Error") {
    data class Success<T>(val data: T) : Resource<T>(data)
    data class Error<T>(val errCode: Int = -1, val errMsg: String = "Error") : Resource<T>(null, errCode, errMsg)
    data class Loading<T>(val data: T? = null) : Resource<T>(data)
}

inline fun <reified ReqT, ResT> Repository.networkResource(
    errorMsg: String,
    crossinline query: suspend () -> ResT,
    crossinline apiCall: suspend (apiService: ApiService, auth: String?) -> Response<ReqT?>,
    crossinline saveResponse: suspend (ResT, ReqT) -> Unit,
    shouldFetch: Boolean = true
): Flow<Resource<ResT>> = flow {
    val data = query()
    emit(Resource.Loading(data))

    if (!shouldFetch) {
        emit(if (data == null) Resource.Error(-1, errorMsg) else Resource.Success(data))
        return@flow
    }

    if (!getApplication().connectionAvailable()) {
        emit(Resource.Success(data))
        return@flow
    }

    val flow = try {
        val apiResponse = apiInvoker { apiCall(getAPIService(), getApplication().getAuthToken()) }
        if (apiResponse is Resource.Success) {
            saveResponse(data, apiResponse.data)
            Resource.Success(query())
        } else {
            Resource.Error(apiResponse.errorCode, apiResponse.errorMsg)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Resource.Error(-1, errorMsg)
    }
    emit(flow)
}

suspend inline fun <reified T> apiInvoker(crossinline invoke: (suspend () -> Response<T?>)): Resource<T> {
    try {
        val response = withContext(Dispatchers.IO) { invoke() }
        val body = response.body()
        if (!response.isSuccessful || body == null) {
            val message = response.errorBody()?.string()
                ?.let { error -> Gson().fromJson(error, ErrorDto::class.java) }
                ?.getErrorMessage()
                ?: response.message()
            return Resource.Error(response.code(), message)
        }
        return Resource.Success(body)
    } catch (e: Exception) {
        e.printStackTrace()
        Log.d(TAG, "apiInvoker: ${T::class.simpleName} - ${e.message}")
        return Resource.Error(-1, e.localizedMessage ?: "Error")
    }
}
