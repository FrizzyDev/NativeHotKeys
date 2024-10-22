package com.github.frizzy.nhk.HotKey;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * A HotKeyEvent is used with {@link HotKeyListener} to communicate
 * when a user dispatches a Hotkey. An event will contain the
 * dispatched Hotkey and the time the event was dispatched.
 *
 * @author Frizzy
 * @version 0.1
 * @since 0.1
 */
public class HotKeyEvent {

    /**
     * <p>
     * The Hotkey instance that matches an expression detected by
     * the HotKeyEventBus.
     * </p>
     */
    private final Hotkey hotkey;

    /**
     * <p>
     * The dispatched time of the HotKeyEvent as an LocalDateTime
     * instance.
     * </p>
     */
    private final LocalDateTime dispatchedTime;

    /**
     * Creates the HotKeyEvent with the dispatched hotkey.
     */
    public HotKeyEvent ( Hotkey hotkey ) {
        this.hotkey = hotkey;
        dispatchedTime = LocalDateTime.now ( ZoneId.systemDefault () );
    }

    /**
     * Returns the Hotkey that dispatched this event.
     */
    public final Hotkey getDispatchedHotkey ( ) {
        return hotkey;
    }

    /**
     * Returns the time this HotKeyEvent was dispatched.
     */
    public final LocalDateTime getDispatchedTime ( ) {
        return dispatchedTime;
    }
 }
