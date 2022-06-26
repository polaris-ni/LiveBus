package com.treasure.bus.log

import java.util.logging.Level

/**
 * @author Liangyong Ni
 * @date 2022/6/26
 * description Logger
 */
interface ILogger {
    fun log(level: Level, msg: String?, e: Throwable? = null)
}