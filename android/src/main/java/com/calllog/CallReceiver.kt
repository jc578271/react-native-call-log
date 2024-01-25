package com.calllog;

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyCallback.CallStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.facebook.react.bridge.Arguments


class CallReceiver: BroadcastReceiver() {
  private lateinit var telephonyManager: TelephonyManager;
  private var callStartTime: Long = 0;
  private var callEndTime: Long = 0;
  private var incomingPhoneNumber: String = ""; // Add this to store the incoming phone number
  private lateinit var callIntent: Intent; // Add this to store the incoming phone number

  @SuppressLint("UnsafeProtectedBroadcastReceiver")
  override fun onReceive(context: Context, intent: Intent) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
      == PackageManager.PERMISSION_GRANTED) {
      telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        callIntent = intent
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
          == PackageManager.PERMISSION_GRANTED) {
//          telephonyManager.registerTelephonyCallback(
//            context.mainExecutor,
//            callStateListener
//          )
          telephonyManager.listen(object : PhoneStateListener() {
            @Deprecated("Deprecated in Java")
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
              onCallStateChange(state, phoneNumber)
            }
          }, PhoneStateListener.LISTEN_CALL_STATE)
        }
      } else {
        telephonyManager.listen(object : PhoneStateListener() {
          @Deprecated("Deprecated in Java")
          override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            onCallStateChange(state, phoneNumber)
          }
        }, PhoneStateListener.LISTEN_CALL_STATE)
      }
    }
  }

  private val callStateListener =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) object: TelephonyCallback(), CallStateListener {
      override fun onCallStateChanged(state: Int) {
        val phoneNumber: String? = callIntent.extras?.getString("incoming_number");
        onCallStateChange(state, phoneNumber)
      }
      // Handle call state change
    } else null as TelephonyCallback

  private fun onCallStateChange (state: Int, incomingNumber: String?) {
    when (state) {
      TelephonyManager.CALL_STATE_IDLE -> {
        val isCallActive = CallLogModule.getCallActive()
        if (isCallActive) {
          CallLogModule.setCallActive(false);
          callEndTime = System.currentTimeMillis();
          val callDurationMillis: Long = callEndTime - callStartTime;
          // Pass all three values back to React Native
          if (incomingPhoneNumber !== ""){
            val result = Arguments.createMap();
            result.putInt("duration", callDurationMillis.toInt()); // Use putInt
            result.putInt("startTime", callStartTime.toInt());
            result.putInt("endTime", callEndTime.toInt());
            result.putString("phoneNumber", incomingPhoneNumber);
            CallLogModule.sendEvent("endCall", result)
          }
        }
      }

      // Implement your logic for IDLE state
      TelephonyManager.CALL_STATE_OFFHOOK -> {
        val isCallActive = CallLogModule.getCallActive();
        val isCallRinging = CallLogModule.getCallRinging()
        if (isCallRinging && !isCallActive && incomingPhoneNumber !== "") {
          CallLogModule.setCallActive(true);
          CallLogModule.setCallRinging(false);
          callStartTime = System.currentTimeMillis();
          val result = Arguments.createMap();
          result.putInt("startTime", callStartTime.toInt())
          result.putString("phoneNumber", incomingPhoneNumber)
          CallLogModule.sendEvent("startCall", result)
        }
      }

      // Implement your logic for OFFHOOK state
      TelephonyManager.CALL_STATE_RINGING -> {
        val isCallRinging = CallLogModule.getCallRinging()
        if (!isCallRinging && incomingNumber !== null) {
          CallLogModule.setCallRinging(true)
          incomingPhoneNumber = incomingNumber;
          val result = Arguments.createMap();

          callStartTime = System.currentTimeMillis();
          result.putInt("startTime", callStartTime.toInt())
          result.putString("phoneNumber", incomingPhoneNumber)
          CallLogModule.sendEvent("incomingCall", result)
        }
      }
    }
  }

  fun onDestroy() {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//      telephonyManager.unregisterTelephonyCallback(callStateListener)
//    } else {
      telephonyManager.listen(null, PhoneStateListener.LISTEN_NONE);
//    }
  }
}
