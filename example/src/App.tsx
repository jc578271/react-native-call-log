import * as React from 'react';

import { Button, Linking, PermissionsAndroid, View } from 'react-native';
import CallLog from 'react-native-call-log';

export default function App() {
  React.useEffect(() => {
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
    <View>
      <Button
        title={'call'}
        onPress={() => {
          Linking.openURL('tel://0123456789').then();
        }}
      />
    </View>
  );
}
