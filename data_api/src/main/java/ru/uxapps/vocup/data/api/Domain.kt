package ru.uxapps.vocup.data.api

import kotlin.random.Random

data class Word(
    val id: Long,
    val text: String,
    val translations: List<String>,
    val pron: String?,
    val progress: Int = Random.nextInt(100),
    val created: Long = System.nanoTime()
)

data class Def(val text: String, val translations: List<String>)

data class Kit(val id: Long, val title: String, val words: List<String>)

enum class Language(val code: String, val nativeName: String) {
    Afrikaans("af", "Afrikaans"),
    Arabic("ar", "العربية"),
    Bulgarian("bg", "Български"),
    Bangla("bn", "বাংলা"),
    BosnianLatin("bs", "bosanski (latinica)"),
    Catalan("ca", "Català"),
    ChineseSimplified("zh-Hans", "简体中文"),
    Czech("cs", "Čeština"),
    Welsh("cy", "Welsh"),
    Danish("da", "Dansk"),
    German("de", "Deutsch"),
    Greek("el", "Ελληνικά"),
    Spanish("es", "Español"),
    Estonian("et", "Eesti"),
    Persian("fa", "Persian"),
    Finnish("fi", "Suomi"),
    Faroese("ht", "Haitian Creole"),
    French("fr", "Français"),
    Hebrew("he", "עברית"),
    Hindi("hi", "हिंदी"),
    Croatian("hr", "Hrvatski"),
    Hungarian("hu", "Magyar"),
    Indonesian("id", "Indonesia"),
    Icelandic("is", "Íslenska"),
    Italian("it", "Italiano"),
    Japanese("ja", "日本語"),
    Korean("ko", "한국어"),
    Lithuanian("lt", "Lietuvių"),
    Latvian("lv", "Latviešu"),
    Maltese("mt", "Il-Malti"),
    Malay("ms", "Melayu"),
    HmongDaw("mww", "Hmong Daw"),
    Dutch("nl", "Nederlands"),
    Norwegian("nb", "Norsk"),
    Polish("pl", "Polski"),
    PortugueseBrazil("pt", "Português (Brasil)"),
    Romanian("ro", "Română"),
    Russian("ru", "Русский"),
    Slovak("sk", "Slovenčina"),
    Slovenian("sl", "Slovenščina"),
    SerbianLatin("sr-Latn", "srpski (latinica)"),
    Swedish("sv", "Svenska"),
    Swahili("sw", "Kiswahili"),
    Tamil("ta", "தமிழ்"),
    Thai("th", "ไทย"),
    KlingonLatin("tlh-Latn", "Klingon (Latin)"),
    Turkish("tr", "Türkçe"),
    Ukrainian("uk", "Українська"),
    Urdu("ur", "اردو"),
    Vietnamese("vi", "Tiếng Việt")
}