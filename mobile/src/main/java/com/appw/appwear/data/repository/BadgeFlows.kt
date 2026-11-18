package com.appw.appwear.data.repository

import com.appw.appwear.shared.engine.Badge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Emite las insignias que aparecen respecto al valor anterior.
 * El primer valor solo sirve de punto de partida: no anuncia lo que ya estaba desbloqueado.
 */
fun Flow<Set<Badge>>.newlyUnlocked(): Flow<List<Badge>> = flow {
    var previous: Set<Badge>? = null
    collect { current ->
        val before = previous
        previous = current
        if (before != null) {
            val added = Badge.entries.filter { it in current && it !in before }
            if (added.isNotEmpty()) emit(added)
        }
    }
}
