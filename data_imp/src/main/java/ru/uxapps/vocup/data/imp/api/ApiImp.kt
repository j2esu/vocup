package ru.uxapps.vocup.data.imp.api

import android.content.Context
import androidx.room.Room
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.uxapps.vocup.data.api.Def
import ru.uxapps.vocup.data.api.Kit
import ru.uxapps.vocup.data.api.Language
import ru.uxapps.vocup.data.imp.api.local.KitRes
import ru.uxapps.vocup.data.imp.api.local.LocalApiDb
import ru.uxapps.vocup.data.imp.api.web.DictionaryApi
import ru.uxapps.vocup.data.imp.api.web.LookupRequest
import ru.uxapps.vocup.data.imp.api.web.PredictorService
import java.io.IOException
import java.util.*

class ApiImp(private val context: Context) : Api {

    private val dictionary = Retrofit.Builder()
        .baseUrl("https://api.cognitive.microsofttranslator.com/dictionary/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(createOkHttpWithLogging())
        .build()
        .create(DictionaryApi::class.java)

    private val predictor = Retrofit.Builder()
        .baseUrl("https://predictor.yandex.net/api/v1/predict.json/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(createOkHttpWithLogging())
        .build()
        .create(PredictorService::class.java)

    private val localApi = Room.databaseBuilder(context, LocalApiDb::class.java, "local_api.db")
        .fallbackToDestructiveMigration()
        .createFromAsset("local_api.db")
        .build()

    private var kitsCache: List<Kit>? = null

    private fun createOkHttpWithLogging(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    override suspend fun getDefinitions(words: List<String>, userLang: Language): List<Def> = withHttp {
        val engWords = words.filter { it.isAscii() }
        val nonEngWords = words - engWords
        val defs = mutableListOf<Def>()
        if (engWords.isNotEmpty()) {
            val requests = engWords.map { LookupRequest(it) }
            val responses = dictionary.lookup("en", userLang.code, requests)
            defs.addAll(responses.map { response ->
                Def(response.displaySource, response.translations.map { it.displayTarget })
            })
        }
        if (nonEngWords.isNotEmpty()) {
            val requests = nonEngWords.map { LookupRequest(it) }
            val responses = dictionary.lookup(userLang.code, "en", requests)
            defs.addAll(responses.flatMap { response ->
                response.translations.map { trans ->
                    Def(trans.displayTarget, trans.backTranslations.map { it.displayText })
                }
            })
        }
        return defs
    }

    override suspend fun getPredictions(input: String, userLang: Language): List<String> = withHttp {
        val inputLang = if (input.isAscii()) "en" else userLang.code
        if (PredictorService.SUPPORTED_LANGUAGES.contains(inputLang)) {
            val response = predictor.complete(input, inputLang)
            if (!response.endOfWord && response.pos <= 0) {
                return response.text.map { input.substring(0, input.length + response.pos) + it }
            }
        }
        return emptyList()
    }

    override suspend fun getPronunciations(word: String): List<String> {
        return localApi.dict().findPron(word).map { it.pron }
    }

    override suspend fun getCompletions(input: String): List<String> {
        return if (input.isAscii()) {
            val lowerInput = input.toLowerCase(Locale.ROOT)
            localApi.frequentWords().findCompletions(input, 10)
                .map { it.text }
                .toMutableList().apply {
                    if (!contains(lowerInput)) {
                        add(lowerInput)
                    }
                }
        } else {
            listOf(input)
        }
    }

    override suspend fun getWordKits(userLang: Language): List<Kit> {
        return kitsCache ?: KitRes.values().map {
            val words = context.resources.getStringArray(it.words).asList()
            Kit(it.id, context.getString(it.title), words)
        }.also { kitsCache = it }
    }

    private fun String.isAscii() = toCharArray().all { it.toInt() < 128 }

    private inline fun <T> withHttp(action: () -> T): T {
        return try {
            action()
        } catch (e: HttpException) {
            throw IOException("Http error ${e.code()}: ${e.message()}", e)
        }
    }
}