@file:Suppress("unused")

package com.treasure.bus.core

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.CustomLiveData
import androidx.lifecycle.Observer
import com.treasure.bus.log.Logger
import java.util.logging.Level

/**
 * @author Liangyong Ni
 * @date 2022/6/26
 * description LiveEvent
 */
class LiveEvent<T> constructor(
    private val key: String,
    private val targetState: Lifecycle.State = Lifecycle.State.CREATED,
    private val autoClear: Boolean = false
) {

    private val liveData: LiveData4Bus<T> = LiveData4Bus(key)

    /**
     * 发送事件
     *
     * @param value     发送的事件
     * @return [Result]
     */
    fun post(value: T) = mainLaunch {
        postInternal(value)
    }

    /**
     * 延迟发送事件
     *
     * @param value     事件
     * @param duration  延迟
     * @return [Result]
     */
    fun postDelay(value: T, duration: Long) = mainLaunch(duration) {
        postInternal(value)
    }

    /**
     * 订阅事件
     *
     * @param owner     LifecycleOwner
     * @param observer  观察者
     * @return [Result]
     */
    fun observe(owner: LifecycleOwner, observer: Observer<T>) = mainLaunch {
        observeInternal(owner, observer, false)
    }

    /**
     * 订阅一个粘性事件
     *
     * @param owner     LifecycleOwner
     * @param observer  观察者
     * @return [Result]
     */
    fun observeSticky(owner: LifecycleOwner, observer: Observer<T>) = mainLaunch {
        observeInternal(owner, observer, true)
    }

    /**
     * 注册一个Observer，需要手动解除绑定
     *
     * @param observer  观察者
     * @return [Result]
     */
    fun observeManual(observer: Observer<T>) = mainLaunch {
        observeManualInternal(observer, false)
    }

    /**
     * 粘性地注册一个Observer，需要手动解除绑定
     *
     * @param observer  观察者
     * @return [Result]
     */
    fun observeManualSticky(observer: Observer<T>) = mainLaunch {
        observeManualInternal(observer, true)
    }

    /**
     * 取消订阅
     *
     * @param observer  观察者
     * @return [Result]
     */
    fun removeObserver(observer: Observer<T>) = mainLaunch {
        liveData.removeObserver(observer)
    }

    /**
     * 发送事件的真正实现
     *
     * @param value 事件
     */
    @MainThread
    private fun postInternal(value: T) {
        Logger.log(Level.INFO, "post: $value to $key")
        liveData.value = value
    }

    /**
     * 订阅事件的真正实现
     *
     * @param owner     [LifecycleOwner]
     * @param observer  观察者
     * @param isSticky  是否粘性
     */
    @MainThread
    private fun observeInternal(owner: LifecycleOwner, observer: Observer<T>, isSticky: Boolean) {
        val observerWrapper = ObserverWrapper(observer)
        if (!isSticky) {
            observerWrapper.shouldCallback = liveData.version > CustomLiveData.START_VERSION
        }
        liveData.observe(owner, observerWrapper)
        Logger.log(Level.INFO, "observe${if (isSticky) " sticky" else ""}: $key by $owner $observer")
    }

    /**
     * 脱离生命周期的订阅的实现
     *
     * @param observer  观察者
     * @param isSticky  是否粘性
     */
    @MainThread
    private fun observeManualInternal(observer: Observer<T>, isSticky: Boolean) {
        val observerWrapper = ObserverWrapper(observer)
        if (!isSticky) {
            observerWrapper.shouldCallback = liveData.version > CustomLiveData.START_VERSION
        }
        liveData.observeForever(observerWrapper)
        Logger.log(Level.INFO, "observe forever${if (isSticky) " sticky" else ""}: $key by $observer")
    }

    /**
     * @author Liangyong Ni
     * @date 2022/6/26
     * description LifecycleLiveData
     */
    private inner class LiveData4Bus<T>(private val key: String) : CustomLiveData<T>(targetState) {
        override fun removeObserver(observer: Observer<in T>) {
            super.removeObserver(observer)
            if (autoClear && !liveData.hasObservers()) {
                LiveBus.bus.remove(key)
            }
            Logger.log(Level.INFO, "observer removed: $observer on $key")
        }
    }
}