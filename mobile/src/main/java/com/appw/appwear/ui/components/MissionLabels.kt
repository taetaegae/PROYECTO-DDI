package com.appw.appwear.ui.components

import androidx.annotation.StringRes
import com.appw.appwear.R
import com.appw.appwear.shared.model.Category
import com.appw.appwear.shared.model.Difficulty
import com.appw.appwear.shared.model.Recurrence

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
