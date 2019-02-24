# TouchToUnlock
CPSC 581 - Project 1 - Touch to Unlock

Group Members:
- Brandon Slack
- Lamess Kharfan
- Shaheed Murji
- Noor Hammad
---
## Build:
1) Clone/download the repo
2) Open the project in Android Studio
3) If opened for the first time, Android Studio will sync the project and configure the build
4) Build > Clean Project

## Run:
1) Run > Run 'app'
2) Ensure ADB is configured 
- Set up an emulator, or
- Plug in an Android device (follow https://developer.android.com/training/basics/firstapp/running-app for setting up a real Android device)
3) Wait until app has been installed on emulator or Android device
4) When app is open, a prompt will appear to set an unlock pattern, press Okay to set
5) Drag a color from the palette into desired shape to set a color pattern
6) Press the checkmark button when done coloring
7) Enter the color pattern into the canvas
- A Red 'X' will appear if the incorrect color pattern was entered
- A Green checkmark will appear if the pattern is correct, and the phone will unlock
