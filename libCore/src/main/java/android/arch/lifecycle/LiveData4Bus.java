package android.arch.lifecycle;

import static android.arch.lifecycle.Lifecycle.State.CREATED;
import static android.arch.lifecycle.Lifecycle.State.DESTROYED;

import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Liangyong Ni
 * @date 2022/6/21 15:48
 * description: ExternalLiveData
 */
public class LiveData4Bus<T> extends MutableLiveData<T> {

    private static final String TAG = "LiveData4Bus";

    public static final int START_VERSION = LiveData.START_VERSION;

    /**
     * 何时接受消息 CREATED(default)/STARTED/RESUMED
     */
    private Lifecycle.State targetState = CREATED;

    public LiveData4Bus() {
    }

    public LiveData4Bus(Lifecycle.State targetState) {
        this.targetState = targetState;
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        if (owner.getLifecycle().getCurrentState() == DESTROYED) {
            // 当前的观察者已经被销毁了，则不做任何事
            return;
        }
        try {
            // 替换LifecycleBoundObserver
            LifecycleBoundObserver wrapper = new CustomLifecycleBoundObserver(owner, observer);
            LifecycleBoundObserver existing = (LifecycleBoundObserver) callMethodPutIfAbsent(observer, wrapper);
            if (existing != null && !existing.isAttachedTo(owner)) {
                // 如果监听器已经存在并且与观察者建立关联，则抛出异常
                throw new IllegalStateException("Cannot add the same observer with different lifecycles");
            }
            if (existing != null) {
                // 如果该监听器已经存在，则直接返回
                return;
            }
            // 添加观察者
            owner.getLifecycle().addObserver(wrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getVersion() {
        return super.getVersion();
    }

    /**
     * 修改的LifecycleBoundObserver
     */
    class CustomLifecycleBoundObserver extends LifecycleBoundObserver {

        CustomLifecycleBoundObserver(@NonNull LifecycleOwner owner, Observer<T> observer) {
            super(owner, observer);
        }

        @Override
        boolean shouldBeActive() {
            // 选择最低活跃时机CREATED/STARTED/RESUMED
            return mOwner.getLifecycle().getCurrentState().isAtLeast(targetState);
        }
    }

    private Object callMethodPutIfAbsent(Object observer, Object wrapper) throws Exception {
        Field fieldObservers = LiveData.class.getDeclaredField("mObservers");
        fieldObservers.setAccessible(true);
        Object mObservers = fieldObservers.get(this);
        if (mObservers == null) {
            throw new NullPointerException("the observers of " + TAG + this + " is null");
        }
        Class<?> classOfSafeIterableMap = mObservers.getClass();
        Method putIfAbsent = classOfSafeIterableMap.getDeclaredMethod("putIfAbsent",
                Object.class, Object.class);
        putIfAbsent.setAccessible(true);
        return putIfAbsent.invoke(mObservers, observer, wrapper);
    }
}
