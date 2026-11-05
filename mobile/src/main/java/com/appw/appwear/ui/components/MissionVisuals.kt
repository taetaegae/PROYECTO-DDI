package com.appw.appwear.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.appw.appwear.R
import com.appw.appwear.shared.model.Category
import com.appw.appwear.ui.theme.CategoryCreative
import com.appw.appwear.ui.theme.CategoryExercise
import com.appw.appwear.ui.theme.CategoryHabits
import com.appw.appwear.ui.theme.CategoryStudy
import com.appw.appwear.ui.theme.XpGold

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

/** Cuadro redondeado con el ícono y el color de la categoría. */
@Composable
fun CategoryIcon(category: Category, modifier: Modifier = Modifier, size: Dp = 44.dp) {
    val color = category.color()
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.3f))
            .background(color.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(category.iconRes()),
            contentDescription = stringResource(category.labelRes()),
            tint = color,
            modifier = Modifier.size(size * 0.55f),
        )
    }
}

/** Ícono pequeño de categoría para los chips de filtro y del formulario. */
@Composable
fun CategoryChipIcon(category: Category) {
    Icon(
        painter = painterResource(category.iconRes()),
        contentDescription = null,
        tint = category.color(),
        modifier = Modifier.size(FilterChipDefaults.IconSize),
    )
}

/** Insignia dorada con la XP que da una misión. */
@Composable
fun XpBadge(xp: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(XpGold.copy(alpha = 0.25f))
            .padding(start = 6.dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
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
