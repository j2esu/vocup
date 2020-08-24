package ru.uxapps.vocup.core

import ru.uxapps.vocup.core.data.Repo

interface RepoProvider {
    fun provideRepo(): Repo
}