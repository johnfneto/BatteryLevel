package appsolve.batterylevel

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.*
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import org.jetbrains.annotations.Nullable


class BatteryService : Service() {

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        applicationContext.registerReceiver(broadcastReceiver, iFilter)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            //val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(NOTIF_CHANNEL_ID, CHANNEL_NAME, importance)
            //mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        startForeground()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForeground() {
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )

        startForeground(
            NOTIF_ID, NotificationCompat.Builder(
                this,
                NOTIF_CHANNEL_ID
            ) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_battery)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running in background")
                .setContentIntent(pendingIntent)
                .build()
        )
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = level / scale.toFloat()
            val p = batteryPct * 100


            val intent = Intent(context, BatteryWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
            // since it seems the onUpdate() is only fired on that:
            val ids = AppWidgetManager.getInstance(application)
                .getAppWidgetIds(ComponentName(application, BatteryWidget::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            sendBroadcast(intent)
        }
    }

    companion object {

        private val NOTIF_ID = 1
        private val NOTIF_CHANNEL_ID = "Channel_Id"
        private val CHANNEL_NAME = "Battery Channel"
    }
}
