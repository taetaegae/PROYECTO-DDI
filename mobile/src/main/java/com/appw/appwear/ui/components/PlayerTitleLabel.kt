package com.appw.appwear.ui.components

import androidx.annotation.StringRes
import com.appw.appwear.R
import com.appw.appwear.shared.engine.PlayerTitle

@StringRes
fun PlayerTitle.labelRes(): Int = when (this) {
    PlayerTitle.NOVICE -> R.string.player_title_novice
    PlayerTitle.ADVENTURER -> R.string.player_title_adventurer
    PlayerTitle.VETERAN -> R.string.player_title_veteran
    PlayerTitle.HERO -> R.string.player_title_hero
    PlayerTitle.LEGEND -> R.string.player_title_legend
}
