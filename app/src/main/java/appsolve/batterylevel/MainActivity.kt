package appsolve.batterylevel

import android.appwidget.AppWidgetManager
import android.content.*
import android.os.BatteryManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt
import android.content.Intent





class MainActivity : AppCompatActivity() {

    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startService(Intent(this, BatteryService::class.java))

        textView = findViewById(R.id.text)
        textView.text = getBatteryLevel()

        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        //val batteryStatus = applicationContext.registerReceiver(broadcastReceiver, iFilter)

    }

    private fun getBatteryLevel(): String {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = applicationContext.registerReceiver(null, iFilter)
        val level = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = level / scale.toFloat()
        val p = batteryPct * 100

        return p.roundToInt().toString() + "%"
    }

    /*private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctxt: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = level / scale.toFloat()
            val p = batteryPct * 100

            textView.text = p.roundToInt().toString() + "%"

            val intent = Intent(this@MainActivity, BatteryWidget::class.java)
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
            // since it seems the onUpdate() is only fired on that:
            val ids = AppWidgetManager.getInstance(application)
                .getAppWidgetIds(ComponentName(getApplication(), BatteryWidget::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            sendBroadcast(intent)
        }
    }*/
}
