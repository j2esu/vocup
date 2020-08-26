package ru.uxapps.vocup.data

import ru.uxapps.vocup.data.api.Repo

interface RepoProvider {
    fun provideRepo(): Repo
}