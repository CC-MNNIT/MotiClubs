package com.mnnit.moticlubs.domain.util

import android.util.Log
import com.google.gson.Gson
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.data.network.dto.ErrorDto
import com.mnnit.moticlubs.domain.model.Stamp
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.util.Constants.STAMP_HEADER
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
    stampKey: ResponseStamp.StampKey,
    crossinline query: suspend () -> ResT,
    crossinline apiCall: suspend (
        apiService: ApiService,
        auth: String?,
        stamp: Long,
    ) -> Response<ReqT?>,
    crossinline saveResponse: suspend (ResT, ReqT) -> Unit,
    remoteRequired: Boolean = false
): Flow<Resource<ResT>> = flow {
    val data = query()
    emit(Resource.Loading(data))

    if (!getApplication().connectionAvailable()) {
        emit(
            if (remoteRequired) {
                Log.w(TAG, "networkResource: remoteRequired but connection unavailable")
                Resource.Error(errMsg = "You're Offline")
            } else Resource.Success(data)
        )
        return@flow
    }

    val flow = try {
        val stampObj = getStampByKey(stampKey.getKey())
        val stamp = stampObj?.stamp ?: 0

        val apiResponse = apiInvoker {
            apiCall(
                getAPIService(),
                getApplication().getAuthToken(),
                stamp
            )
        }
        if (apiResponse is Resource.Success) {
            insertOrUpdateStamp(Stamp(stampKey.getKey(), apiResponse.data.second))

            val result = apiResponse.data.first
            if (result != null) {
                saveResponse(data, result)
            }
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

suspend inline fun <reified T> apiInvoker(
    crossinline invoke: (suspend () -> Response<T?>)
): Resource<Pair<T?, Long>> {
    try {
        val response = withContext(Dispatchers.IO) { invoke() }
        val body = response.body()
        val stampHeaderValue = response.headers()[STAMP_HEADER]?.toLong() ?: 0

        if (response.code() == 304) {
            return Resource.Success(Pair(body, stampHeaderValue))
        }

        if (!response.isSuccessful || body == null) {
            val message = response.errorBody()?.string()
                ?.let { error -> Gson().fromJson(error, ErrorDto::class.java) }
                ?.getErrorMessage()
                ?: response.message()
            return Resource.Error(response.code(), message)
        }
        return Resource.Success(Pair(body, stampHeaderValue))
    } catch (e: Exception) {
        e.printStackTrace()
        Log.d(TAG, "apiInvoker: ${T::class.simpleName} - ${e.message}")
        return Resource.Error(-1, e.localizedMessage ?: "Error")
    }
}
