package com.example.akllev.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

class Notifier(private val ctx: Context) {

    private val ALERT_CH = "alerts"
    private val SYS_CH   = "system"

    init {
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            if (nm.getNotificationChannel(ALERT_CH) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        ALERT_CH, "Alarm/Bildirim", NotificationManager.IMPORTANCE_HIGH
                    ).apply { description = "Güvenlik ve önemli uyarılar" }
                )
            }
            if (nm.getNotificationChannel(SYS_CH) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        SYS_CH, "Sistem", NotificationManager.IMPORTANCE_DEFAULT
                    ).apply { description = "Genel sistem bildirimleri" }
                )
            }
        }
    }

    private fun canNotify(): Boolean {
        return if (Build.VERSION.SDK_INT >= 33) {
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true // <33'te runtime izin yok
        }
    }

    @SuppressLint("MissingPermission") // yukarıda canNotify() ile kontrol edildi
    private fun safeNotify(id: Int, builder: NotificationCompat.Builder) {
        if (!canNotify()) return
        try {
            NotificationManagerCompat.from(ctx).notify(id, builder.build())
        } catch (se: SecurityException) {
            Log.w("Notifier", "POST_NOTIFICATIONS izni yok, bildirim gösterilemedi.", se)
        }
    }

    fun notifyAlert(title: String, msg: String, id: Int = 1001) {
        val n = NotificationCompat.Builder(ctx, ALERT_CH)
            .setSmallIcon(android.R.drawable.stat_sys_warning) // TODO: app ikonunla değiştir
            .setContentTitle(title)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        safeNotify(id, n)
    }

    fun notifySystem(title: String, msg: String, id: Int = 2000) {
        val n = NotificationCompat.Builder(ctx, SYS_CH)
            .setSmallIcon(android.R.drawable.stat_notify_more) // TODO: app ikonunla değiştir
            .setContentTitle(title)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        safeNotify(id, n)
    }
}

/* Snackbar helper aynı kalsın */
@Composable
fun rememberNotifier(): Pair<SnackbarHostState, (String) -> Unit> {
    val host = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val notify: (String) -> Unit = { msg ->
        scope.launch {
            host.showSnackbar(
                message = msg,
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
        }
    }
    return host to notify
}
