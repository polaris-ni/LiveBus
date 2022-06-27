package com.treasure.bus.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * @author Liangyong Ni
 * @date 2022/6/26
 * description Observable
 */
interface Observable<T> {
    /**
     * 进程内发送消息
     *
     * @param value 发送的消息
     */
    fun post(value: T)

    /**
     * 进程内发送消息，延迟发送
     *
     * @param value 发送的消息
     * @param duration 延迟毫秒数
     */
    fun postDelay(value: T, duration: Long)

    /**
     * 进程内发送消息，延迟发送，带生命周期
     * 如果延时发送消息的时候sender处于非激活状态，消息取消发送
     *
     * @param sender 消息发送者
     * @param value  发送的消息
     * @param duration  延迟毫秒数
     */
    fun postDelay(sender: LifecycleOwner, value: T, duration: Long)

    /**
     * 注册一个Observer，生命周期感知，自动取消订阅
     *
     * @param owner    LifecycleOwner
     * @param isSticky 是否是粘性事件
     * @param observer 观察者
     */
    fun observe(owner: LifecycleOwner, isSticky: Boolean, observer: Observer<T>)

    /**
     * 注册一个Observer，需手动解除绑定
     *
     * @param observer 观察者
     * @param isSticky 是否是粘性事件
     */
    fun observeForever(observer: Observer<T>, isSticky: Boolean)

    /**
     * 通过observeForever或observeStickyForever注册的，需要调用该方法取消订阅
     *
     * @param observer 观察者
     */
    fun removeObserver(observer: Observer<T>)
}