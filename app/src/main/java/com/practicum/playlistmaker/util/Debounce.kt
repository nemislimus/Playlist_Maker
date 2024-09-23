package com.practicum.playlistmaker.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> jobWithDebounce(
    delayMillis: Long,
    coroutineScope: CoroutineScope,
    useNextIncomingParam: Boolean,
    actionOnDebounce: (T) -> Unit
) : (T) -> Unit {

    var debounceJob: Job? = null

    return { param: T ->
        if (useNextIncomingParam) {
            debounceJob?.cancel()
        }

        if (debounceJob?.isCompleted != false || useNextIncomingParam) {
            debounceJob = coroutineScope.launch {
                delay(delayMillis)
                actionOnDebounce(param)
            }
        }
    }
}