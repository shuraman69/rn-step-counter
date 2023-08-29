import {NativeModules, DeviceEventEmitter} from 'react-native';

const {StepCounterModule} = NativeModules;

class StepCounter {
  public start() {
    StepCounterModule.startStepCounter();
  }

  public stop() {
    StepCounterModule.stopStepCounter();
  }

  public addStepCountListener(callback: any) {
    return DeviceEventEmitter.addListener('onStepCountChange', callback);
  }
}

export const StepCountera = new StepCounter();
export const a = 1;
