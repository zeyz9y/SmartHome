package com.example.akllev.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    secondary = AppColors.Secondary,
    onSecondary = AppColors.OnSecondary,
    background = AppColors.Background,
    onBackground = AppColors.OnBackground,
    surface = AppColors.Surface,
    onSurface = AppColors.OnSurface,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.OnSurfaceVariant
)

private val DarkColors = darkColorScheme(
    primary = AppColors.PrimaryDark,
    onPrimary = AppColors.OnPrimaryDark,
    secondary = AppColors.SecondaryDark,
    onSecondary = AppColors.OnSecondaryDark,
    background = AppColors.BackgroundDark,
    onBackground = AppColors.OnBackgroundDark,
    surface = AppColors.SurfaceDark,
    onSurface = AppColors.OnSurfaceDark,
    surfaceVariant = AppColors.SurfaceVariantDark,
    onSurfaceVariant = AppColors.OnSurfaceVariantDark
)

// Seçili/seleksiyon durumları için yardımcılar (mevcudu korudum)
val ColorScheme.selectedContainer get() = secondary
val ColorScheme.selectedContent   get() = onSecondary
val ColorScheme.unselectedContainer get() = surface
val ColorScheme.unselectedContent   get() = onSurface

@Composable
fun AkıllıEvTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColors
        else      -> LightColors
    }

    // >>> Spacing tokenlarını MaterialTheme'ye enjekte et
    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colors,
            typography  = Typography,
            shapes      = Shapes(
                small  = RoundedCornerShape(4.dp),
                medium = RoundedCornerShape(8.dp),
                large  = RoundedCornerShape(12.dp)
            ),
            content     = content
        )
    }
}
