package com.appw.appwear.notify

import com.appw.appwear.shared.engine.Badge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Insignias que avisó el teléfono y que el reloj todavía no celebra.
 * Vive en memoria: si el sistema cierra la app antes de mostrarlas, se pierden, como permite el plan.
 */
object BadgeInbox {

    private val _pending = MutableStateFlow<List<Badge>>(emptyList())
    val pending: StateFlow<List<Badge>> = _pending.asStateFlow()

    fun add(badge: Badge) = _pending.update { if (badge in it) it else it + badge }

    fun consume(badge: Badge) = _pending.update { it - badge }
}
