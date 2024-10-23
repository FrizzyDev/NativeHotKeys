# About
NativeHotKeys uses JNativeHook to allow developers to listen for key events anywhere on the computer and dispatch them to a Java application. You are able to register a keybind, including up to two modifiers, to determine when you need to start a process.

# Usage
```java
HotKeyEventBus bus = new HotKeyEventBus ( hotkeySet );

Hotkey openHotkey = new Hotkey ( "open-window", 29, 56, 24); //Control + alt + O
OpenWindowListener listener = new OpenWindowListener ( );

bus.registerHotkeyWithListener ( openHotkey, listener );

GlobalScreen.addNativeKeyListener ( bus );
GlobalScreen.registerNativeHook ( );

```

Now, whenever the user presses Control + Alt + 0, the OpenWindowListener will be notified, and whatever process or action defined will be ran. The Hotkey can be detected whether a Java GUI is active or not. Perfect for those background applications or overlays.
