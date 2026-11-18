package com.appw.appwear.tile

import android.content.Context
import android.util.Log
import androidx.concurrent.futures.SuspendToFutureAdapter
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.CircularProgressIndicator
import androidx.wear.protolayout.material.ProgressIndicatorColors
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.EdgeContentLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.appw.appwear.LevelUpWearApp
import com.appw.appwear.R
import com.appw.appwear.presentation.MainActivity
import com.appw.appwear.presentation.components.labelRes
import com.appw.appwear.shared.model.PlayerProfile
import com.appw.appwear.shared.model.TodayMission
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first

/**
 * Tile de LevelUp: anillo de XP en el borde, nivel y título, y la siguiente misión pendiente.
 * Lee la misma caché que la app, así que funciona aunque la app esté cerrada.
 */
class LevelTileService : TileService() {

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> =
        SuspendToFutureAdapter.launchFuture(Dispatchers.Default) {
            val cache = (application as LevelUpWearApp).cache
            val layout = tileLayout(
                context = this@LevelTileService,
                device = requestParams.deviceConfiguration,
                profile = cache.profile.first(),
                missions = cache.todayMissions.first(),
            )
            TileBuilders.Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTileTimeline(TimelineBuilders.Timeline.fromLayoutElement(layout))
                .build()
        }

    override fun onTileResourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest,
    ): ListenableFuture<ResourceBuilders.Resources> =
        SuspendToFutureAdapter.launchFuture(Dispatchers.Default) {
            // El Tile no usa imágenes, solo texto y el anillo.
            ResourceBuilders.Resources.Builder().setVersion(RESOURCES_VERSION).build()
        }

    companion object {
        private const val RESOURCES_VERSION = "1"
        private const val TAG = "LevelTileService"

        /** Pide al sistema que vuelva a dibujar el Tile con el estado actual. */
        fun requestUpdate(context: Context) {
            // Si el sistema rechaza la petición, el Tile se queda con la versión anterior; la app no debe cerrarse por eso.
            try {
                getUpdater(context).requestUpdate(LevelTileService::class.java)
            } catch (e: RuntimeException) {
                Log.w(TAG, "No se pudo actualizar el Tile", e)
            }
        }
    }
}

private const val GOLD = 0xFFFFC83D.toInt()
private const val GOLD_TRACK = 0x33FFC83D
private const val WHITE = 0xFFFFFFFF.toInt()
private const val MUTED = 0xFFB9B4C8.toInt()
private const val MINT = 0xFF3DDC97.toInt()

private fun tileLayout(
    context: Context,
    device: DeviceParameters,
    profile: PlayerProfile,
    missions: List<TodayMission>,
): LayoutElementBuilders.LayoutElement {
    val progress = profile.progress
    val next = missions.firstOrNull { !it.isCompleted }
    val done = missions.count { it.isCompleted }

    val level = Text.Builder(
        context,
        context.getString(R.string.tile_level_format, progress.level, context.getString(profile.title.labelRes())),
    )
        .setTypography(Typography.TYPOGRAPHY_CAPTION1)
        .setColor(argb(GOLD))
        .build()

    val content = LayoutElementBuilders.Column.Builder()
        .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
        .apply {
            when {
                missions.isEmpty() -> addContent(text(context, context.getString(R.string.tile_empty), Typography.TYPOGRAPHY_BODY1, WHITE))
                next == null -> addContent(text(context, context.getString(R.string.tile_all_done), Typography.TYPOGRAPHY_TITLE3, MINT))
                else -> {
                    addContent(text(context, context.getString(R.string.tile_next_label), Typography.TYPOGRAPHY_CAPTION2, MUTED))
                    addContent(text(context, next.mission.title, Typography.TYPOGRAPHY_TITLE3, WHITE, maxLines = 2))
                }
            }
        }
        .build()

    val layout = EdgeContentLayout.Builder(device)
        .setResponsiveContentInsetEnabled(true)
        .setEdgeContent(
            CircularProgressIndicator.Builder()
                .setProgress(progress.fraction)
                .setCircularProgressIndicatorColors(ProgressIndicatorColors(argb(GOLD), argb(GOLD_TRACK)))
                .build(),
        )
        .setPrimaryLabelTextContent(level)
        .setContent(content)
        .apply {
            if (missions.isNotEmpty()) {
                setSecondaryLabelTextContent(
                    text(context, context.getString(R.string.today_progress_format, done, missions.size), Typography.TYPOGRAPHY_CAPTION2, MUTED),
                )
            }
        }
        .build()

    // Tocar cualquier parte del Tile abre la app.
    val openApp = ModifiersBuilders.Clickable.Builder()
        .setId("abrir_levelup")
        .setOnClick(
            ActionBuilders.LaunchAction.Builder()
                .setAndroidActivity(
                    ActionBuilders.AndroidActivity.Builder()
                        .setPackageName(context.packageName)
                        .setClassName(MainActivity::class.java.name)
                        .build(),
                )
                .build(),
        )
        .build()

    return LayoutElementBuilders.Box.Builder()
        .setWidth(expand())
        .setHeight(expand())
        .setModifiers(ModifiersBuilders.Modifiers.Builder().setClickable(openApp).build())
        .addContent(layout)
        .build()
}

private fun text(context: Context, value: String, typography: Int, color: Int, maxLines: Int = 1): Text =
    Text.Builder(context, value)
        .setTypography(typography)
        .setColor(argb(color))
        .setMaxLines(maxLines)
        .setOverflow(LayoutElementBuilders.TEXT_OVERFLOW_ELLIPSIZE)
        .build()
