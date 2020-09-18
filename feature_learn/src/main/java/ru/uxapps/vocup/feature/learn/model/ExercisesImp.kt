package ru.uxapps.vocup.feature.learn.model

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.data.api.Word

internal class ExercisesImp(repo: Repo, scope: CoroutineScope) : Exercises {

    private val requirementsMap = mapOf<Exercise, (List<Word>) -> Boolean>(
        Exercise.WordToTranslation to { words ->
            words.size > 2
        }
    )

    override val exercises = repo.getAllWords().map { words ->
        requirementsMap.map { (ex, req) ->
            ExerciseItem(ex, req(words))
        }
    }.asLiveData(scope.coroutineContext + Dispatchers.IO)
}