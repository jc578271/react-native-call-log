# react-native-call-log

react-native-call-log

## Installation

```sh
npm install react-native-call-log
```


## Install

### Add receiver and permissions
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

  <uses-permission android:name="android.permission.INTERNET" />
  <!-- add permission -->
  <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
  <uses-permission android:name="android.permission.READ_CALL_LOG"/>
  <uses-permission android:name="android.permission.ANSWER_PHONE_CALLS" />

  <application
    android:name=".MainApplication"
    android:label="@string/app_name"
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:allowBackup="false"
    android:theme="@style/AppTheme">
    <activity
      android:name=".MainActivity"
      android:label="@string/app_name"
      android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|screenSize|smallestScreenSize|uiMode"
      android:launchMode="singleTask"
      android:windowSoftInputMode="adjustResize"
      android:exported="true">
      <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
      </intent-filter>
    </activity>
    <!-- add receiver -->
    <receiver android:name="com.calllog.CallReceiver" android:exported="true">
      <intent-filter>
        <action android:name="android.intent.action.PHONE_STATE"/>
      </intent-filter>
    </receiver>
  </application>
</manifest>

```

## Usage

```js
import * as React from 'react';

import { PermissionsAndroid } from 'react-native';
import CallLog from 'react-native-call-log';

export default function App() {
  React.useEffect(() => {
    /* request permission */
    PermissionsAndroid.requestMultiple([
      'android.permission.READ_PHONE_STATE',
      'android.permission.READ_CALL_LOG',
    ]).then();

    /* Event Listeners */
    CallLog.onIncomingCallEventListener((data) => {
      console.log('incoming', data);
    });
    CallLog.onStartCallEventListener((data) => {
      console.log('start', data);
    });
    CallLog.onEndCallEventListener((data) => {
      console.log('end', data);
    });

    return () => {
      /* Remove Event Listeners */
      CallLog.removeEventListeners();
    };
  }, []);

  return null;
}

```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
