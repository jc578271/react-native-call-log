import {
  AppState,
  Linking,
  NativeEventEmitter,
  type NativeEventSubscription,
  NativeModules,
  Platform,
} from 'react-native';
import type * as t from './types';
import type { NativeModuleType } from './types';

const nativeEventEmitter = new NativeEventEmitter(NativeModules.CallLog);

class BroadcastReceiver implements t.BroadcastReceiverInterface {
  private _nativeModule: NativeModuleType;
  private phoneNumber: string = '';
  private startTime: number = 0;
  private state: 'readyCall' | 'startCall' | 'endCall' | null = null;
  private path: string = '';
  private pathWithTime: string = '';
  private prevTime: number = 0;

  private startCallAppStateHandler: NativeEventSubscription | null = null;
  private endCallAppStateHandler: NativeEventSubscription | null = null;

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
    if (Platform.OS === 'android') this.registerReceiver().then();
  }

  onStartCallEventListener(listener: t.BroadcastEventCallback) {
    if (Platform.OS === 'ios') {
      this.startCallAppStateHandler = AppState.addEventListener(
        'change',
        (state) => {
          const currentTime = new Date().getTime();
          this.path = this.path + '/' + state;

          this.pathWithTime =
            this.pathWithTime +
            '/' +
            state +
            '-' +
            (currentTime - this.prevTime);

          this.prevTime = currentTime;

          let valid = true;
          const list = this.pathWithTime.split('/');
          list.forEach((item) => {
            const [_state, _time] = item.split('-');
            if (_state === 'inactive' && _time && parseInt(_time, 10) > 150) {
              valid = valid && false;
            }

            if (_state === 'background' && _time && parseInt(_time, 10) > 500) {
              valid = valid && false;
            }
          });

          if (
            state === 'background' &&
            this.state === 'readyCall' &&
            this.path === '/inactive/active/inactive/background' &&
            valid
          ) {
            this.state = 'startCall';
            this.startTime = new Date().getTime();
            listener({
              phoneNumber: this.phoneNumber,
              startTime: this.startTime,
            });
          }
        }
      );
    } else {
      nativeEventEmitter.addListener('startCall', listener);
    }
  }

  onEndCallEventListener(listener: t.BroadcastEventCallback) {
    if (Platform.OS === 'ios') {
      this.endCallAppStateHandler = AppState.addEventListener(
        'change',
        (state) => {
          if (
            state === 'active' &&
            this.state === 'startCall' &&
            this.path === '/inactive/active/inactive/background/active'
          ) {
            this.state = 'endCall';
            const endTine = new Date().getTime();
            listener({
              phoneNumber: this.phoneNumber,
              startTime: this.startTime,
              endTime: endTine,
              duration: endTine - this.startTime,
            });
          }
        }
      );
    } else {
      nativeEventEmitter.addListener('endCall', listener);
    }
  }

  onIncomingCallEventListener(listener: t.BroadcastEventCallback) {
    if (Platform.OS === 'android') {
      nativeEventEmitter.addListener('incomingCall', listener);
    }
  }

  removeEventListeners() {
    if (Platform.OS === 'ios') {
      this.startCallAppStateHandler?.remove();
      this.endCallAppStateHandler?.remove();
    } else {
      nativeEventEmitter.removeAllListeners('startCall');
      nativeEventEmitter.removeAllListeners('endCall');
      nativeEventEmitter.removeAllListeners('incomingCall');
    }
  }

  registerReceiver() {
    return this._nativeModule.registerReceiver();
  }

  async startCall(phoneNumber: string) {
    if (Platform.OS === 'ios') {
      this.prevTime = new Date().getTime();
      this.state = 'readyCall';
      this.path = '';
      this.pathWithTime = '';
      this.phoneNumber = phoneNumber;
    }

    return Linking.openURL('tel://' + phoneNumber);
  }
}

export default new BroadcastReceiver();
