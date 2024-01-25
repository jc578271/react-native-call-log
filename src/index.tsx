import { NativeEventEmitter, NativeModules } from 'react-native';
import type * as t from './types';
import type { NativeModuleType } from './types';

const nativeEventEmitter = new NativeEventEmitter();

class BroadcastReceiver implements t.BroadcastReceiverInterface {
  private _nativeModule: NativeModuleType;

  constructor() {
    this._nativeModule = NativeModules.CallLog
      ? NativeModules.CallLog
      : new Proxy(
          {},
          {
            get() {
              // throw new Error(LINKING_ERROR);
            },
          }
        );
    this.registerReceiver().then();
  }

  onStartCallEventListener(listener: t.BroadcastEventCallback) {
    return nativeEventEmitter.addListener('startCall', listener);
  }

  onEndCallEventListener(listener: t.BroadcastEventCallback) {
    return nativeEventEmitter.addListener('endCall', listener);
  }

  onIncomingCallEventListener(listener: t.BroadcastEventCallback) {
    return nativeEventEmitter.addListener('incomingCall', listener);
  }

  removeEventListeners() {
    nativeEventEmitter.removeAllListeners('startCall');
    nativeEventEmitter.removeAllListeners('endCall');
    nativeEventEmitter.removeAllListeners('incomingCall');
  }

  registerReceiver() {
    return this._nativeModule.registerReceiver();
  }
}

export default new BroadcastReceiver();
