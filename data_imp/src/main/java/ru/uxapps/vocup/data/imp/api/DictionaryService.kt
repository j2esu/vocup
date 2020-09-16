package ru.uxapps.vocup.data.imp.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * https://docs.microsoft.com/en-us/azure/cognitive-services/translator/reference/v3-0-reference
 */
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
    data class Translation(val displayTarget: String, val backTranslations: List<BackTranslation>)
    data class BackTranslation(val displayText: String)
}