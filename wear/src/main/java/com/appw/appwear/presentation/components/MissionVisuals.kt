package com.appw.appwear.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.appw.appwear.R
import com.appw.appwear.presentation.theme.CategoryCreative
import com.appw.appwear.presentation.theme.CategoryExercise
import com.appw.appwear.presentation.theme.CategoryHabits
import com.appw.appwear.presentation.theme.CategoryStudy
import com.appw.appwear.shared.model.Category

@DrawableRes
fun Category.iconRes(): Int = when (this) {
    Category.STUDY -> R.drawable.ic_category_study
    Category.EXERCISE -> R.drawable.ic_category_exercise
    Category.HABITS -> R.drawable.ic_category_habits
    Category.CREATIVE -> R.drawable.ic_category_creative
}

fun Category.color(): Color = when (this) {
    Category.STUDY -> CategoryStudy
    Category.EXERCISE -> CategoryExercise
    Category.HABITS -> CategoryHabits
    Category.CREATIVE -> CategoryCreative
}

/** Círculo con un ícono sobre un fondo tenue de su mismo color. */
@Composable
fun TintedIcon(
    @DrawableRes iconRes: Int,
    color: Color,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.22f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier.size(size * 0.58f),
        )
    }
}

@Composable
fun CategoryIcon(category: Category, modifier: Modifier = Modifier, size: Dp = 32.dp) {
    TintedIcon(
        iconRes = category.iconRes(),
        color = category.color(),
        contentDescription = stringResource(category.labelRes()),
        modifier = modifier,
        size = size,
    )
}

/** Rayo dorado con la XP que da una misión. */
@Composable
fun XpLabel(xp: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_bolt),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = stringResource(R.string.xp_reward_format, xp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

/** Insignia redondeada de color con la XP, para las pantallas con más espacio. */
@Composable
fun XpBadge(xp: Int, modifier: Modifier = Modifier) {
    XpLabel(
        xp = xp,
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}
