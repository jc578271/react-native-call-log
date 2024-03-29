interface BroadcastEventData {
  /**
   * Scanned barcode data from harware scanners
   */
  duration?: number;
  endTime?: number;
  phoneNumber?: string;
  startTime?: number;
}
type BroadcastEventCallback = (d: BroadcastEventData) => void;

interface NativeModuleType {
  registerReceiver(): Promise<boolean>;
}

interface BroadcastReceiverInterface {
  /**
   * Get device harware id
   */
  // getPhoneID(): Promise<string[]>;
  /**
   *
   * @description
   *  - `intentAction` is the actions name that'd be registered for `android.BroadcastReceiver`
   *  - `intentExtrasDataKey` will be used to extract data from the intent
   * @param cb
   */
  // setIntentActionConfig(args: IntentActionConfig): Promise<boolean>;
  onStartCallEventListener(cb: BroadcastEventCallback): void;
  onEndCallEventListener(cb: BroadcastEventCallback): void;
  onIncomingCallEventListener(cb: BroadcastEventCallback): void;
  removeEventListeners(): void;
}

export type {
  NativeModuleType,
  BroadcastReceiverInterface,
  BroadcastEventData,
  BroadcastEventCallback,
};
