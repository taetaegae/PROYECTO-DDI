package com.appw.appwear.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.appw.appwear.R
import com.appw.appwear.shared.engine.Badge
import com.appw.appwear.ui.theme.CategoryCreative
import com.appw.appwear.ui.theme.CategoryExercise
import com.appw.appwear.ui.theme.CategoryHabits
import com.appw.appwear.ui.theme.CategoryStudy
import com.appw.appwear.ui.theme.CoinGold
import com.appw.appwear.ui.theme.HeroEnd
import com.appw.appwear.ui.theme.StreakFlame
import com.appw.appwear.ui.theme.Violet

@StringRes
fun Badge.nameRes(): Int = when (this) {
    Badge.FIRST_STEP -> R.string.badge_first_step
    Badge.STREAK_7 -> R.string.badge_streak_7
    Badge.STREAK_30 -> R.string.badge_streak_30
    Badge.EARLY_BIRD -> R.string.badge_early_bird
    Badge.SCHOLAR -> R.string.badge_scholar
    Badge.ATHLETE -> R.string.badge_athlete
    Badge.EPIC_QUEST -> R.string.badge_epic_quest
    Badge.LEVEL_5 -> R.string.badge_level_5
    Badge.LEVEL_10 -> R.string.badge_level_10
    Badge.TREAT_YOURSELF -> R.string.badge_treat_yourself
}

@StringRes
fun Badge.conditionRes(): Int = when (this) {
    Badge.FIRST_STEP -> R.string.badge_first_step_condition
    Badge.STREAK_7 -> R.string.badge_streak_7_condition
    Badge.STREAK_30 -> R.string.badge_streak_30_condition
    Badge.EARLY_BIRD -> R.string.badge_early_bird_condition
    Badge.SCHOLAR -> R.string.badge_scholar_condition
    Badge.ATHLETE -> R.string.badge_athlete_condition
    Badge.EPIC_QUEST -> R.string.badge_epic_quest_condition
    Badge.LEVEL_5 -> R.string.badge_level_5_condition
    Badge.LEVEL_10 -> R.string.badge_level_10_condition
    Badge.TREAT_YOURSELF -> R.string.badge_treat_yourself_condition
}

@DrawableRes
fun Badge.iconRes(): Int = when (this) {
    Badge.FIRST_STEP -> R.drawable.ic_bolt
    Badge.STREAK_7, Badge.STREAK_30 -> R.drawable.ic_streak
    Badge.EARLY_BIRD -> R.drawable.ic_sun
    Badge.SCHOLAR -> R.drawable.ic_category_study
    Badge.ATHLETE -> R.drawable.ic_category_exercise
    Badge.EPIC_QUEST, Badge.LEVEL_5, Badge.LEVEL_10 -> R.drawable.ic_trophy
    Badge.TREAT_YOURSELF -> R.drawable.ic_coin
}

fun Badge.color(): Color = when (this) {
    Badge.FIRST_STEP -> Violet
    Badge.STREAK_7, Badge.STREAK_30 -> StreakFlame
    Badge.EARLY_BIRD -> CoinGold
    Badge.SCHOLAR -> CategoryStudy
    Badge.ATHLETE -> CategoryExercise
    Badge.EPIC_QUEST -> CategoryCreative
    Badge.LEVEL_5, Badge.LEVEL_10 -> HeroEnd
    Badge.TREAT_YOURSELF -> CategoryHabits
}
