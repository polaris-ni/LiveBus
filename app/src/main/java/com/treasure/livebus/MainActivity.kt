package com.treasure.livebus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.treasure.bus.core.LiveBus

class MainActivity : AppCompatActivity() {

    companion object {
        var isFirst = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LiveBus.enableLog()
        setContentView(R.layout.activity_main)
        if (isFirst){
            LiveBus.get<Int>("test").observe(this) {
                Log.e("TAG", "onCreate: $it")
                startActivity(Intent(this, MainActivity::class.java))
            }
            findViewById<Button>(R.id.btnTest).setOnClickListener {
                LiveBus.get<Int>("test").post(123)
            }
            isFirst = false
        } else {
            LiveBus.get<Int>("test").observeSticky(this){
                Log.e("TAG", "onCreate: sticky $it")
            }
        }
    }
}