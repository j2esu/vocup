package ru.uxapps.vocup

import androidx.lifecycle.ViewModel
import ru.uxapps.vocup.data.InMemoryRepo
import ru.uxapps.vocup.data.Repo

val ViewModel.repo: Repo get() = InMemoryRepo