package ru.uxapps.vocup

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import ru.uxapps.vocup.data.InMemoryRepo
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.screen.Navigation

val Fragment.repo: Repo get() = InMemoryRepo
val ViewModel.repo: Repo get() = InMemoryRepo
val Fragment.nav get() = activity as Navigation