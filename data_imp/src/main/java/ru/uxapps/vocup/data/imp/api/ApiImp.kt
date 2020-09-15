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
        val requests = words.map { LookupRequest(it) }
        val responses = dictionary.lookup("en", lang.code, requests)
        return responses.map { response ->
            Def(response.displaySource, response.translations.map { it.displayTarget })
        }
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