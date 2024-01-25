package com.calllog

import android.content.IntentFilter
import android.util.Log
import com.facebook.react.BuildConfig
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule


class CallLogModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), LifecycleEventListener {
  private var broadcastReceiver = CallReceiver()

  init {
    cReactContext = reactContext
    cReactContext.addLifecycleEventListener(
      this
    )
  }

  override fun getName(): String {
    return "CallLog"
  }

  private fun getReactContext(): ReactApplicationContext {
    return cReactContext
  }


  @ReactMethod
  fun registerReceiver(promise: Promise) {
    registerBroadcastReceiver()
    promise.resolve(true)
  }

  companion object {
    lateinit var cReactContext: ReactApplicationContext
    private var isCallActive: Boolean = false;
    private var isCallRinging: Boolean = false;

    fun sendEvent(eventName: String, params: WritableMap) {
      if (cReactContext.hasCatalystInstance()) {
        cReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
          .emit(eventName, params)
      }
    }

    fun setCallActive(value: Boolean) {
      isCallActive = value
    }
    fun getCallActive(): Boolean {
      return isCallActive
    }
    fun setCallRinging(value: Boolean) {
      isCallRinging = value
    }
    fun getCallRinging(): Boolean {
      return isCallRinging
    }
  }

  private fun registerBroadcastReceiver() {
    if (BuildConfig.DEBUG) Log.d(name, "register receiver")

    val filter = IntentFilter()
    filter.addCategory("android.intent.category.DEFAULT")

    getReactContext().registerReceiver(broadcastReceiver, filter)
  }

  private fun unregisterBroadcastReceiver() {
    getReactContext().unregisterReceiver(broadcastReceiver)
  }

  //region: LifecycleEventListener
  override fun onHostResume() {
    if (BuildConfig.DEBUG) Log.d(name, "onHostResume: register Application receivers")
    registerBroadcastReceiver()
  }

  override fun onHostPause() {
    if (BuildConfig.DEBUG) Log.d(name, "onHostPause: unregister receivers")
    unregisterBroadcastReceiver()
  }

  override fun onHostDestroy() {
    if (BuildConfig.DEBUG) Log.d(name, "onHostDestroy: Destroy host")
  }
  //endregion
}
