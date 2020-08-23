package ru.uxapps.vocup

import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.data.FakeApi
import ru.uxapps.vocup.data.FakeDb
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.data.RepoImp

val AndroidViewModel.repo: Repo get() = RepoImp(getApplication(), FakeDb, FakeApi)