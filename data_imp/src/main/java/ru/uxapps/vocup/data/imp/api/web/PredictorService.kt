package ru.uxapps.vocup.data.imp.api.web

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * https://yandex.com/dev/predictor/doc/dg/concepts/api-overview-docpage/
 */
internal interface PredictorService {

    companion object {
        val SUPPORTED_LANGUAGES = arrayOf(
            "af", "ar", "az", "ba", "be", "bg", "bs", "ca", "cs", "cy", "da", "de", "el", "en", "es", "et", "eu",
            "fi", "fr", "ga", "gl", "he", "hr", "ht", "hu", "hy", "id", "is", "it", "ka", "kk", "ky", "la", "lb",
            "lt", "lv", "mg", "mhr", "mk", "mn", "mrj", "ms", "mt", "nl", "no", "pl", "pt", "ro", "ru", "sk",
            "sl", "sq", "sr", "sv", "sw", "tg", "tl", "tr", "tt", "udm", "uk", "uz", "vi"
        )
    }

    @GET("complete?key=pdct.1.1.20200812T134518Z.0d0be6862667e89f.df9b165743a57de04d4a7e24ea15ba580dc900a8")
    suspend fun complete(
        @Query("q") text: String,
        @Query("lang") lang: String
    ): CompleteResponse
}

internal data class CompleteResponse(
    val endOfWord: Boolean,
    val pos: Int,
    val text: List<String>
)