package com.treasure.livebus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.treasure.bus.core.LiveBus

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LiveBus.with<Int>("test").observe(this, false) {
            Log.e("TAG", "onCreate: $it")
        }
        findViewById<Button>(R.id.btnTest).setOnClickListener {
            LiveBus.with<Int>("test").post(123)
        }
    }
}