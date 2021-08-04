package ru.uxapps.vocup.feature.addword.model

import ru.uxapps.vocup.data.api.Repo

internal class SaveDef(private val repo: Repo) {
    suspend fun run(item: DefItem) {
        if (item.wordId == null) {
            repo.addWord(item.text, item.trans?.map { it.first } ?: emptyList())
        } else {
            repo.addTranslations(
                item.wordId, (item.trans ?: emptyList()).filter { !it.second }.map { it.first }
            )
        }
    }
}
