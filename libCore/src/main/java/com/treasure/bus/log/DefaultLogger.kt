package com.treasure.bus.log

import android.util.Log
import java.util.logging.Level

/**
 * @author Liangyong Ni
 * @date 2022/6/26
 * description DefaultLogger
 */
class DefaultLogger : ILogger {
    companion object {
        private const val TAG = "[LiveBus]"
    }

    override fun log(level: Level, msg: String?, e: Throwable?) {
        if (level === Level.SEVERE) {
            Log.e(TAG, msg, e)
        } else if (level === Level.WARNING) {
            Log.w(TAG, msg, e)
        } else if (level === Level.INFO) {
            Log.i(TAG, msg, e)
        } else if (level === Level.CONFIG) {
            Log.d(TAG, msg, e)
        } else if (level !== Level.OFF) {
            Log.v(TAG, msg, e)
        }
    }
}