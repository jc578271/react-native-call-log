package com.calllog

import android.content.IntentFilter
import android.util.Log
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule


class CallLogModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {
  private var broadcastReceiver = CallReceiver()

  init {
    cReactContext = reactContext
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
    filter.addAction("android.intent.action.PHONE_STATE")
    filter.addAction("android.intent.action.NEW_OUTGOING_CALL")

    getReactContext().registerReceiver(broadcastReceiver, filter)
  }

  private fun unregisterBroadcastReceiver() {
    getReactContext().unregisterReceiver(broadcastReceiver)
  }
  //endregion
}
