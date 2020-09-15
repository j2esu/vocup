package ru.uxapps.vocup.data.imp.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

internal interface DictionaryApi {

    @Headers(
        "Ocp-Apim-Subscription-Key: 673f55500b8a4ee79466b96e28727e45",
        "Ocp-Apim-Subscription-Region: global"
    )
    @POST("lookup?api-version=3.0")
    suspend fun lookup(
        @Query("from") srcLang: String,
        @Query("to") targetLang: String,
        @Body request: List<LookupRequest>
    ): List<LookupResponse>
}

data class LookupRequest(val text: String)

internal data class LookupResponse(
    val displaySource: String,
    val translations: List<Translation>
) {
    data class Translation(val displayTarget: String)
}