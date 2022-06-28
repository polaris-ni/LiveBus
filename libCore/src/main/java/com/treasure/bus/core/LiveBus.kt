package com.treasure.bus.core

import androidx.lifecycle.Lifecycle
import com.treasure.bus.log.Logger

/**
 * @author Liangyong Ni
 * @date 2022/6/26
 * description LiveBus
 */
object LiveBus {

    /**
     * 存放LiveEvent与事件名的映射
     */
    internal var bus = HashMap<String, LiveEvent<Any>>()

    /**
     * Set logger
     *
     * @param logger 日志打印器
     */
    fun setLogger(logger: Logger) = Logger.setLogger(logger)

    /**
     * 启用日志
     */
    fun enableLog() = Logger.enableLog()

    /**
     * 根据事件名称拿到[LiveEvent]，以便订阅或发送事件
     *
     * @param T             泛型
     * @param key           事件名称
     * @param autoClear     是否自动清除，默认为false
     * @param targetState   事件回调时机
     * @return [LiveEvent]
     */
    @Suppress("UNCHECKED_CAST")
    @Synchronized
    fun <T> get(
        key: String,
        autoClear: Boolean = false,
        targetState: Lifecycle.State = Lifecycle.State.STARTED,
    ): LiveEvent<T> {
        if (!bus.containsKey(key)) {
            bus[key] = LiveEvent(key, targetState, autoClear)
        }
        return bus[key]!! as LiveEvent<T>
    }
}