import * as React from 'react';

import { Button, PermissionsAndroid, Platform, View } from 'react-native';
import CallLog from 'react-native-call-log';

export default function App() {
  React.useEffect(() => {
    if (Platform.OS === 'android')
      PermissionsAndroid.requestMultiple([
        'android.permission.READ_PHONE_STATE',
        'android.permission.READ_CALL_LOG',
      ]).then();
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

  return (
    <View style={{ paddingTop: 100 }}>
      <Button
        title={'call'}
        onPress={() => {
          CallLog.startCall('0123456789').then();
        }}
      />
    </View>
  );
}
