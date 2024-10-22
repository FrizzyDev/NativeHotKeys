package com.github.frizzy.nhk.HotKey;

/**
 * A listener interface used to notify an application when a user
 * has pressed a Hotkey.
 *
 * @see Hotkey
 * @author Frizzy
 * @version 0.1
 * @since 0.1
 */
public interface HotKeyListener {

    /**
     * hotKeyPressed is called when the HotKeyEventBus has determined
     * a Hotkey expression and located a matching Hotkey binding./
     */
    void hotkeyPressed ( HotKeyEvent hke );
}
