package ru.uxapps.vocup.data.imp.api.web

import retrofit2.http.GET
import retrofit2.http.Query
import ru.uxapps.vocup.data.imp.BuildConfig

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

    @GET("complete?limit=5&key=${BuildConfig.PREDICTOR_API_KEY}")
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
