package ru.uxapps.vocup.data.imp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.uxapps.vocup.data.api.Def
import ru.uxapps.vocup.data.api.Language

class ApiImp : Api {

    private val dictionary = Retrofit.Builder()
        .baseUrl("https://api.cognitive.microsofttranslator.com/dictionary/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .build()
        .create(DictionaryApi::class.java)

    override suspend fun getDefinitions(words: List<String>, lang: Language): List<Def> {
        val engWords = words.filter { word ->
            word.toCharArray().all { it.toInt() < 128 }
        }
        val nonEngWords = words - engWords
        val defs = mutableListOf<Def>()
        if (engWords.isNotEmpty()) {
            val requests = engWords.map { LookupRequest(it) }
            val responses = dictionary.lookup("en", lang.code, requests)
            defs.addAll(responses.map { response ->
                Def(response.displaySource, response.translations.map { it.displayTarget })
            })
        }
        if (nonEngWords.isNotEmpty()) {
            val requests = nonEngWords.map { LookupRequest(it) }
            val responses = dictionary.lookup(lang.code, "en", requests)
            defs.addAll(responses.flatMap { response ->
                response.translations.map { trans ->
                    Def(trans.displayTarget, trans.backTranslations.map { it.displayText })
                }
            })
        }
        return defs
    }

    override suspend fun getPredictions(input: String): List<String> {
        return listOf()
    }

    override suspend fun getPronunciations(word: String): List<String> {
        return listOf()
    }

    override suspend fun getCompletions(input: String): List<String> {
        return listOf(input)
    }
}