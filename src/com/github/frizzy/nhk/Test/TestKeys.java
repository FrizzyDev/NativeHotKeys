package com.github.frizzy.nhk.Test;

import com.github.frizzy.nhk.HotKey.HotKeyEventBus;
import com.github.frizzy.nhk.HotKey.Hotkey;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

/**
 * Creates a HotKeyEventBus in debug mode and any keys pressed will be printed
 * to the console.
 */
public class TestKeys {

    private TestKeys ( ) {
        HotKeyEventBus bus = new HotKeyEventBus ( true );
        Hotkey hotkey = new Hotkey ( "open-window", 29, 56, 24 );
        System.out.println ( hotkey.toJSON () );

        GlobalScreen.addNativeKeyListener ( bus );
        try {
            GlobalScreen.registerNativeHook ();
        } catch ( NativeHookException e ) {
            throw new RuntimeException ( e );
        }
    }

    public static void main ( String[] args) {
        new TestKeys ();
    }


}
