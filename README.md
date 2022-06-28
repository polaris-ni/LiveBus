# LiveBus
Event Bus for Android based on LiveData
# 依赖
[![](https://jitpack.io/v/polaris-ni/LiveBus.svg)](https://jitpack.io/#polaris-ni/LiveBus)

**setting.gradle** in **Project**
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
**build.gradle** in **Module**

```
dependencies {
    implementation 'com.github.polaris-ni:LiveBus:Tag'
}
```
需要依赖 **LiveData** 和 **Lifecycle**
