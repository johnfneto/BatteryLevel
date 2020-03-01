package appsolve.batterylevel

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.RemoteViews
import kotlin.math.roundToInt


class BatteryWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray) {

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun getBatteryLevel(context: Context): String {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, iFilter)
        val level = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = level / scale.toFloat()
        val p = batteryPct * 100

        return p.roundToInt().toString() + "%"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val appWidgetManager = AppWidgetManager.getInstance(context!!.applicationContext)
        val thisWidget = ComponentName(context.applicationContext, BatteryWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int) {

    val views = RemoteViews(context.packageName, R.layout.battery_widget)
    views.setTextViewText(R.id.appwidget_text, Util.getBatteryLevel(context))

    val x: Int = Util.getBatteryLevel(context).replace("%", "").toInt()
    when {
        x > 50  -> views.setInt(R.id.main_layout, "setBackgroundResource", R.drawable.ic_battery_green)
        x <= 20 -> views.setInt(R.id.main_layout, "setBackgroundResource", R.drawable.ic_battery_red)
        else    -> views.setInt(R.id.main_layout, "setBackgroundResource", R.drawable.ic_battery_orange)
    }

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}