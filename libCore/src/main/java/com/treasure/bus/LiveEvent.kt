package com.treasure.bus

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData4Bus
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import com.treasure.bus.log.Logger
import java.util.logging.Level

/**
 * @author Liangyong Ni
 * @date 2022/6/26
 * description LiveEvent
 */
class LiveEvent<T> constructor(
    private val key: String,
    private val lifecycleObserverAlwaysActive: Boolean,
    private val autoClear: Boolean = false
) : Observable<T> {
    private val liveData: LifecycleLiveData<T> = LifecycleLiveData(key)

    /**
     * 进程内发送消息
     *
     * @param value 发送的消息
     */
    override fun post(value: T) {
        if (isMainThread()) {
            postInternal(value)
        } else {
            mainLaunch {
                postInternal(value)
            }
        }
    }

    /**
     * 进程内发送消息，延迟发送
     *
     * @param value 发送的消息
     * @param duration 延迟毫秒数
     */
    override fun postDelay(value: T, duration: Long) {
        mainLaunch(duration) {
            postInternal(value)
        }
    }

    /**
     * 进程内发送消息，延迟发送，带生命周期
     * 如果延时发送消息的时候sender处于非激活状态，消息取消发送
     *
     * @param sender 消息发送者
     * @param value 发送的消息
     * @param duration 延迟毫秒数
     */
    override fun postDelay(sender: LifecycleOwner, value: T, duration: Long) {
        mainLaunch(duration) {
            if (sender.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                postInternal(value)
            }
        }
    }

    /**
     * 注册一个Observer，生命周期感知，自动取消订阅
     *
     * @param owner    LifecycleOwner
     * @param observer 观察者
     */
    override fun observe(owner: LifecycleOwner, isSticky: Boolean, observer: Observer<T>) {
        if (isMainThread()) {
            if (!isSticky) {
                observeInternal(owner, observer)
            } else {
                observeStickyInternal(owner, observer)
            }
        } else {
            mainLaunch {
                if (!isSticky) {
                    observeInternal(owner, observer)
                } else {
                    observeStickyInternal(owner, observer)
                }
            }
        }
    }

    /**
     * 注册一个Observer，需手动解除绑定
     *
     * @param observer 观察者
     */
    override fun observeForever(observer: Observer<T>, isSticky: Boolean) {
        if (isMainThread()) {
            if (!isSticky) {
                observeForeverInternal(observer)
            } else {
                observeStickyForeverInternal(observer)
            }
        } else {
            mainLaunch {
                if (!isSticky) {
                    observeForeverInternal(observer)
                } else {
                    observeStickyForeverInternal(observer)
                }
            }
        }
    }

    /**
     * 通过observeForever或observeStickyForever注册的，需要调用该方法取消订阅
     *
     * @param observer 观察者
     */
    override fun removeObserver(observer: Observer<T>) {
        if (isMainThread()) {
            removeObserverInternal(observer)
        } else {
            mainLaunch {
                removeObserverInternal(observer)
            }
        }
    }

    @MainThread
    private fun postInternal(value: T) {
        Logger.log(Level.INFO, "post: $value to $key")
        liveData.value = value
    }

    @MainThread
    private fun observeInternal(owner: LifecycleOwner, observer: Observer<T>) {
        val observerWrapper = ObserverWrapper(observer)
        observerWrapper.preventNextEvent = liveData.version > LiveData4Bus.START_VERSION
        liveData.observe(owner, observerWrapper)
        Logger.log(Level.INFO, "observe: $key by $owner $observer")
    }

    @MainThread
    private fun observeStickyInternal(owner: LifecycleOwner, observer: Observer<T>) {
        val observerWrapper = ObserverWrapper(observer)
        liveData.observe(owner, observerWrapper)
        Logger.log(Level.INFO, "sticky observe: $key by $owner $observer")
    }

    @MainThread
    private fun observeForeverInternal(observer: Observer<T>) {
        val observerWrapper = ObserverWrapper(observer)
        observerWrapper.preventNextEvent = liveData.version > LiveData4Bus.START_VERSION
        liveData.observeForever(observerWrapper)
        Logger.log(Level.INFO, "observe forever: $key by $observer")
    }

    @MainThread
    private fun observeStickyForeverInternal(observer: Observer<T>) {
        val observerWrapper = ObserverWrapper(observer)
        liveData.observeForever(observerWrapper)
        Logger.log(Level.INFO, "sticky observe forever: $key by $observer")
    }

    @MainThread
    private fun removeObserverInternal(observer: Observer<T>) {
        liveData.removeObserver(observer)
    }

    /**
     * @author Liangyong Ni
     * @date 2022/6/26
     * description LifecycleLiveData
     */
    inner class LifecycleLiveData<T>(private val key: String) :
        LiveData4Bus<T>(if (lifecycleObserverAlwaysActive) Lifecycle.State.CREATED else Lifecycle.State.STARTED) {

        override fun removeObserver(observer: Observer<T>) {
            super.removeObserver(observer)
            if (autoClear && !liveData.hasObservers()) {
                LiveBus.bus.remove(key)
            }
            Logger.log(Level.INFO, "observer removed: $observer on $key")
        }
    }
}