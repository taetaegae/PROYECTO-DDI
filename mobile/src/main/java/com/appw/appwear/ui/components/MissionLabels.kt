package com.appw.appwear.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.appw.appwear.R
import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Mission
import com.appw.appwear.shared.model.Recurrence
import com.appw.appwear.shared.model.Verification

@StringRes
fun Category.labelRes(): Int = when (this) {
    Category.STUDY -> R.string.category_study
    Category.EXERCISE -> R.string.category_exercise
    Category.HABITS -> R.string.category_habits
    Category.CREATIVE -> R.string.category_creative
}

@StringRes
fun Difficulty.labelRes(): Int = when (this) {
    Difficulty.EASY -> R.string.difficulty_easy
    Difficulty.MEDIUM -> R.string.difficulty_medium
    Difficulty.HARD -> R.string.difficulty_hard
    Difficulty.EPIC -> R.string.difficulty_epic
}

@StringRes
fun Recurrence.labelRes(): Int = when (this) {
    Recurrence.ONCE -> R.string.recurrence_once
    Recurrence.DAILY -> R.string.recurrence_daily
    Recurrence.WEEKLY -> R.string.recurrence_weekly
}

@StringRes
fun Verification.labelRes(): Int = when (this) {
    Verification.MANUAL -> R.string.verification_manual
    Verification.TIMER -> R.string.verification_timer
    Verification.STEPS -> R.string.verification_steps
}

/** "Media · Diaria", y con temporizador "Media · Diaria · 25 min". */
@Composable
fun missionSummary(mission: Mission): String {
    val summary = stringResource(
        R.string.mission_summary_format,
        stringResource(mission.difficulty.labelRes()),
        stringResource(mission.recurrence.labelRes()),
    )
    val minutes = mission.target
    return if (mission.verification == Verification.TIMER && minutes != null) {
        stringResource(R.string.timer_summary_format, summary, minutes)
    } else {
        summary
    }
}
