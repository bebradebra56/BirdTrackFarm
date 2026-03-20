package com.birdtracks.farmbird.grf.data.repo

import android.util.Log
import com.birdtracks.farmbird.grf.domain.model.BirdTrackFarmEntity
import com.birdtracks.farmbird.grf.domain.model.BirdTrackFarmParam
import com.birdtracks.farmbird.grf.presentation.app.BirdTrackFarmApplication.Companion.BIRD_TRACK_FARM_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface BirdTrackFarmApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun birdTrackFarmGetClient(
        @Body jsonString: JsonObject,
    ): Call<BirdTrackFarmEntity>
}


private const val BIRD_TRACK_FARM_MAIN = "https://birdtrackfarm.com/"
class BirdTrackFarmRepository {

    suspend fun birdTrackFarmGetClient(
        birdTrackFarmParam: BirdTrackFarmParam,
        birdTrackFarmConversion: MutableMap<String, Any>?
    ): BirdTrackFarmEntity? {
        val gson = Gson()
        val api = birdTrackFarmGetApi(BIRD_TRACK_FARM_MAIN, null)

        val birdTrackFarmJsonObject = gson.toJsonTree(birdTrackFarmParam).asJsonObject
        birdTrackFarmConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            birdTrackFarmJsonObject.add(key, element)
        }
        return try {
            val birdTrackFarmRequest: Call<BirdTrackFarmEntity> = api.birdTrackFarmGetClient(
                jsonString = birdTrackFarmJsonObject,
            )
            val birdTrackFarmResult = birdTrackFarmRequest.awaitResponse()
            Log.d(BIRD_TRACK_FARM_MAIN_TAG, "Retrofit: Result code: ${birdTrackFarmResult.code()}")
            if (birdTrackFarmResult.code() == 200) {
                Log.d(BIRD_TRACK_FARM_MAIN_TAG, "Retrofit: Get request success")
                Log.d(BIRD_TRACK_FARM_MAIN_TAG, "Retrofit: Code = ${birdTrackFarmResult.code()}")
                Log.d(BIRD_TRACK_FARM_MAIN_TAG, "Retrofit: ${birdTrackFarmResult.body()}")
                birdTrackFarmResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(BIRD_TRACK_FARM_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(BIRD_TRACK_FARM_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun birdTrackFarmGetApi(url: String, client: OkHttpClient?) : BirdTrackFarmApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
