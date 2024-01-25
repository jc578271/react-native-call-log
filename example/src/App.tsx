import * as React from 'react';

import { PermissionsAndroid } from 'react-native';
import CallLog from 'react-native-call-log';

export default function App() {
  React.useEffect(() => {
    PermissionsAndroid.check('android.permission.READ_PHONE_STATE').then(
      (e) => {
        console.log('permission', e);
      }
    );
    PermissionsAndroid.requestMultiple([
      'android.permission.READ_PHONE_STATE',
      'android.permission.READ_CALL_LOG',
    ]).then();
    // PermissionsAndroid.request('android.permission.READ_PHONE_STATE').then();
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
      CallLog.removeEventListeners();
    };
  }, []);

  return null;
}
