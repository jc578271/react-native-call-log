package com.calllog

import com.facebook.react.bridge.ReactContext

interface CustomReactNativeApplication {
  fun getReactContext() : ReactContext?
}
