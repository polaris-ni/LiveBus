package com.treasure.bus.log

import java.util.logging.Level

/**
 * @author Liangyong Ni
 * @date 2022/6/26
 * description LiveBus
 */
object Logger : ILogger {

    private var logger: ILogger = DefaultLogger()

    private var isEnable = false

    fun enableLog() {
        isEnable = true
    }

    fun setLogger(logger: ILogger) {
        this.logger = logger
    }

    override fun log(level: Level, msg: String?, e: Throwable?) {
        if (isEnable) {
            logger.log(level, msg, e)
        }
    }
}