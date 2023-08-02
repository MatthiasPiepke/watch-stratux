# WatchStratux
WatchStratux is an Android App for Smartwatches and displays traffic information from your [DIY Stratux receiver](https://github.com/b3nn0/stratux) (tested on Europe Edition only). It works of course alongside all other EFIS and display devices.

<img src="screenshots/screenshot_2.png" alt="drawing" width="200"/> <img src="screenshots/screenshot_1.png" alt="drawing" width="200"/> <img src="screenshots/screenshot_4.png" alt="drawing" width="200"/> <img src="screenshots/screenshot_3.png" alt="drawing" width="200"/>

WatchStratux connects to your Stratux receiver via WiFi and processes TCP and Websocket broadcasts.

:warning: **Caution:** To run this app properly, you need a running Stratux receiver.

WatchStratux App has been tested on Galaxy Watch 4 (40mm), but it should run also on all other Galaxy Watch 4 and 5 versions.

This app is 100% free, 100% open source, 100% non-commercial and does not collect any user data.

## Features
 * radar view with zoom in/out function to display air traffic
 * vibration alert in case of traffic warning
 * adjustable IP configuration
 * adjustable vertical radar range
 * selectable metric and nautical units
 * system information of the Stratux receiver

## App Installation
This app can not be found and installed from Play Store.  
It needs to be installed by using `adb` command-line tool from Android.

1. **Enable the ADB debugging interface on your watch**
	* navigate under system settings to About (*Info zur Uhr*) -> Versions (*Softwareinfo*)
	* tap the Build Number option (*Softwareversion*) seven times until you see the message: You are now a developer! This enables developer options on your device.
	* navigate under system settings to developer options (*Entwickleroptionen*) and enable ADB debugging and debugging via WiFi

1. **Download latest WatchStratux APK file**  
   https://github.com/MatthiasPiepke/watch-stratux/releases

1. **Download SDK Platform-Tools from here:**  
   https://developer.android.com/tools/releases/platform-tools

1. **Connect ADB to your watch via WiFi:**  
   Open command prompt and navigate to the platform-tools folder  
   Command: `adb connect 192.168.xx.xx:5555`

1. **Install APK file on your watch:**  
   Command: `adb install /your-path/WatchStratux_vx-x-x.apk`

   **Now WatchStratux is installed!**  

1. **Disconnect from ADB**  
   Command: `adb disconnect 192.168.xx.xx:5555`

## Best Practice
 * Before starting the app: enable WiFi on your watch and connect to your Stratux access point or to the WiFi network, where your Stratux is connected to. Your watch may not automatically connect to the Stratux WiFi.
 * The default IP configuration of Stratux access point is `192.168.0.1:2000`
 
## Disclaimer
Never trust and rely on any electronic system in your airplane. It can always fail at any time.

This app does not replace your responsibility to watch the sky for traffic with your own eyes!!

We do not recommend to use it during flight. We do not guarantee that traffic information will be displayed correctly.

This repository offers code and binaries that helps you to bring your Stratux traffic information to your smartwatch. We do not take any responsibility for what you do with this code. There is no warranty of any kind provided with the information, code and binaries you can find here.