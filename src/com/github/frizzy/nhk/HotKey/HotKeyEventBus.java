package com.github.frizzy.nhk.HotKey;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>
 * The HotKeyEventBus can have HotKeyListener's attached to it to notify other
 * parts of an application when a user has successfully input a known/set hotkey
 * for that application. The HotKeyEventBus should be registered with GlobalScreen at application start,
 * but adding additional HotKeyListener's can be done at anytime.
 * </p>
 * <p>
 * The idea behind the HotKeyEventBus is its used globally for an application in a set-it and forget-it manner.
 * At the startup of an application, all Hotkeys can be registered with the EventBus and any number of listeners
 * can be attached to dispatch HotKeyEvents to every part of an application, as needed.
 * </p>
 * <p>
 * Additionally, Hotkeys can be added to or removed from a HotKeyEventBus at anytime.
 * </p>
 *
 * @author Frizzy
 * @version 0.1
 * @since 0.1
 */
public class HotKeyEventBus implements NativeKeyListener {

    /**
     * Logger for the HotKeyEventBus.
     */
    protected static Logger log = Logger.getLogger ( HotKeyEventBus.class.getName () );

    /**
     * <p>
     * A Set of HotKeyListeners that will be notified when a hotkey is dispatched.
     * </p>
     * <p>
     * A Set is used to ensure listeners added are unique.
     * </p>
     */
    private Set< HotKeyListener > listeners;

    /**
     * <p>
     * A Set of Hotkeys key events are compared against to determine if a registered
     * Hotkey has been pressed.
     * </p>
     * <p>
     * A Set is used to ensure Hotkeys added are unique.
     * </p>
     */
    private Set< Hotkey > hotkeys;

    /**
     * The index of the first key modifier in the expression array.
     */
    protected static final int MODIFIER_1 = 0;

    /**
     * The index of the second key modifier in the expression array.
     */
    protected static final int MODIFIER_2 = 1;

    /**
     * The index of the keycode in the expression array.
     */
    protected static final int KEYCODE = 2;

    /**
     * The value used in the expression array for invalid or not set codes.
     */
    protected static final int INVALID_CODE = -1;

    /**
     * Index 0 is the first modifier, or -1 if there isn't a modifier. <br>
     * Index 1 is the second modifier, or -1 if there isn't a modifier. <br>
     * Index 3 is the key code, or -1 if there isn't one. <br>
     */
    private final int[] expression = new int[] { INVALID_CODE , INVALID_CODE , INVALID_CODE };

    /**
     * If true, the event bus will output key events to the console.
     */
    private boolean debug = false;

    /**
     * Creates a HotKeyEventBus with no HotKeys.
     */
    public HotKeyEventBus ( ) {
        this ( Collections.emptySet () );
    }

    /**
     * <p>
     * Passing true will create a HotKeyEventBus in debug mode and no Hotkeys.
     * </p>
     * <p>
     * This should only be used in a development environment.
     * </p>
     */
    public HotKeyEventBus ( boolean debug ) {
        this ( Collections.emptySet () , debug );
    }

    /**
     * Creates a HotKeyEventBus and registers the list of hotkeys.
     */
    public HotKeyEventBus ( final Set< Hotkey > hotkeys ) {
        this ( hotkeys , false );
    }

    /**
     * Creates a HotKeyEventBus with a list of valid HotKeys.
     */
    private HotKeyEventBus ( final Set< Hotkey > hotkeys , boolean debug ) {
        this.hotkeys = hotkeys;
        listeners = Collections.emptySet ();
        this.debug = debug;
    }

    /**
     * <p>
     * On keyPressed events, the event code is checked to determine if it is a modifier code.
     * </p>
     * <p>
     * Only modifier keys are processed in keyPressed events. The keycode of the expression is
     * determined in keyReleased events.
     * </p>
     */
    @Override
    public void nativeKeyPressed ( NativeKeyEvent nativeEvent ) {
        int code = nativeEvent.getKeyCode ( );
        debug (  "Received event key code on keyPressed: " + code + ". Code text: " + NativeKeyEvent.getKeyText ( code ) + "."  );

        if ( isModifierCode ( code ) ) {
            debug ( "Received event key code is a modifier code." );

            if ( expression[ MODIFIER_1 ] == INVALID_CODE && expression[ MODIFIER_2 ] == INVALID_CODE ) {
                debug ( "Setting MODIFIER_1 to code: " + code + ". Code text: " + NativeKeyEvent.getKeyText ( code ) + "." );

                expression[ MODIFIER_1 ] = code;
            } else if ( expression[ MODIFIER_1 ] != -1 && expression[ MODIFIER_2 ] == -1 ) {
                debug ( "Setting MODIFIER_2 to code: " + code + ". Code text: " + NativeKeyEvent.getKeyText ( code ) + "." );

                expression[ MODIFIER_2 ] = code;
            }
        }
    }

    /**
     * <p>
     * On keyReleased events, the event code is checked to determine if previously pressed modifiers
     * were released before a key code could be detected, I.E. any key that is valid and is NOT a modifier code.
     * </p>
     * <p>
     * Modifier keys released before a key code could be registered will be removed from the Hotkey expression array,
     * and ultimately will not be used in determining the hotkey pressed.
     * </p>
     */
    @Override
    public void nativeKeyReleased ( NativeKeyEvent nativeEvent ) {
        int code = nativeEvent.getKeyCode ( );
        debug ( "Received event key code on keyReleased: " + code + ". Key text: " + NativeKeyEvent.getKeyText ( code ) + "." );

        if ( isModifierCode ( code ) ) {
            debug ( "Received event key code is a modifier code." );

            if ( expression[ MODIFIER_1 ] == code ) {
                debug ( "Expression code at index of MODIFIER_1 is keyReleased event code. MODIFIER_1 is set to INVALID_CODE." );

                expression[ MODIFIER_1 ] = INVALID_CODE;
            } else if ( expression[ MODIFIER_2 ] == code ) {
                debug ( "Expression code at index of MODIFIER_2 is keyReleased event code. MODIFIER_2 is set to INVALID_CODE." );

                expression[ MODIFIER_2 ] = INVALID_CODE;
            }
        } else {
            debug ( "Received event key code on keyReleased is NOT a modifier code." );

            if ( checkCodeValidity ( code ) ) {
                debug ( "Received event key code on keyReleased is a valid binding code. Setting expression code at index KEYCODE to code: " + code + ". Code text: " + NativeKeyEvent.getKeyText ( code ) + "." );

                expression[ KEYCODE ] = code;
                checkExpression ( );
            }
        }
    }

    /**
     * Sets the hotkeys list to the provided list.
     */
    public void setHotkeys ( final Set< Hotkey > hotkeys ) {
        this.hotkeys = hotkeys;
        log.info ( "Provided Hotkeys Set was set to the HotKeyEventBus.");
    }

    /**
     * Adds the provided Hotkey to the HotKeyEventBus.
     */
    public void addHotkey ( final Hotkey hotkey ) {
        boolean changed = hotkeys.add ( hotkey );

        if ( changed ) {
            log.info ( "Provided Hotkey was added to the HotKeyEventBus Hotkey list." );
        } else {
            log.warning ( "Provided Hotkey was not added to the HotKeyEventBus Hotkey list." );
        }
    }

    /**
     * Removes the provided Hotkey from the HotKeyEventBus.
     */
    public void removeHotKey ( final Hotkey hotkey ) {
        boolean contained = hotkeys.remove ( hotkey );

        if ( contained ) {
            log.info ( "HotKeyEventBus contained the provided Hotkey and was removed from the Hotkey list." );
        } else {
            log.warning ( "HotKeyEventBus did not contain the provided Hotkey and it was not removed from the Hotkey list." );
        }
    }

    /**
     * Adds the provided HotKeyListener to the HotKeyEventBus.
     */
    public void addHotKeyListener ( HotKeyListener listener ) {
        boolean contained = listeners.add ( listener );

        if ( contained ) {
            log.info ( "Provided HotKeyListener was registered with the HotKeyEventBus." );
        } else {
            log.warning ( "Provided HotKeyListener already exists in the HotKeyEventBus listener Set." );
        }
    }

    public void removeHotKeyListener ( final HotKeyListener listener ) {
        boolean contained = listeners.remove ( listener );

        if ( contained ) {
            log.info ( "Provided HotKeyListener was registered with the HotKeyEventBus. The listener has been removed." );
        } else {
            log.warning ( "Provided HotKeyListener was not registered with the HotKeyEventBus. No listener was removed." );
        }
    }

    /**
     * Checks the key expression recorded by the NativeKeyListener.
     */
    private void checkExpression ( ) {
        debug ( "Checking completed key event expression for a valid Hotkey." );

        for ( Hotkey key : hotkeys ) {
            if ( key.matchesKeyExpression ( expression ) ) {
                debug ( "Matching Hotkey was determined. Notifying attached listeners of the event." );

                notifyListeners ( new HotKeyEvent ( key ) );
            }
        }

        resetExpression ( );
    }

    /**
     * Notifies any attached HotKeyListeners.
     */
    private void notifyListeners ( HotKeyEvent event ) {
        for ( HotKeyListener hkls : listeners ) {
            hkls.hotkeyPressed ( event );
        }
    }

    /**
     * Resets the expression array to the default values.
     */
    private void resetExpression ( ) {
        debug ( "Expression is now being reset." );

        expression[ 0 ] = INVALID_CODE;
        expression[ 1 ] = INVALID_CODE;
        expression[ 2 ] = INVALID_CODE;
    }

    /**
     * Returns true if the provided code is a modifier key, false otherwise.
     */
    private boolean isModifierCode ( int code ) {

        switch ( code ) {
            case NativeKeyEvent.VC_SHIFT , NativeKeyEvent.VC_ALT , NativeKeyEvent.VC_CONTROL , NativeKeyEvent.VC_TAB -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Returns true if the provided code is a valid key for bindings, false otherwise.
     */
    private boolean checkCodeValidity ( int code ) {
        switch ( code ) {
            case NativeKeyEvent.VC_ESCAPE , NativeKeyEvent.VC_BROWSER_BACK , NativeKeyEvent.VC_BROWSER_FAVORITES ,
                    NativeKeyEvent.VC_BROWSER_HOME , NativeKeyEvent.VC_BROWSER_FORWARD , NativeKeyEvent.VC_BROWSER_REFRESH ,
                    NativeKeyEvent.VC_BROWSER_SEARCH , NativeKeyEvent.VC_BROWSER_STOP , NativeKeyEvent.VC_APP_CALCULATOR ,
                    NativeKeyEvent.VC_APP_MAIL , NativeKeyEvent.VC_APP_MUSIC , NativeKeyEvent.VC_APP_PICTURES ,
                    NativeKeyEvent.VC_CAPS_LOCK , NativeKeyEvent.VC_CONTEXT_MENU , NativeKeyEvent.VC_MEDIA_EJECT ,
                    NativeKeyEvent.VC_MEDIA_NEXT , NativeKeyEvent.VC_MEDIA_PLAY , NativeKeyEvent.VC_MEDIA_PREVIOUS ,
                    NativeKeyEvent.VC_MEDIA_STOP , NativeKeyEvent.VC_MEDIA_SELECT , NativeKeyEvent.VC_NUM_LOCK ,
                    NativeKeyEvent.VC_INSERT , NativeKeyEvent.VC_HOME , NativeKeyEvent.VC_DELETE , NativeKeyEvent.VC_PRINTSCREEN ,
                    NativeKeyEvent.VC_POWER , NativeKeyEvent.VC_SCROLL_LOCK , NativeKeyEvent.VC_VOLUME_DOWN , NativeKeyEvent.VC_VOLUME_UP ,
                    NativeKeyEvent.VC_VOLUME_MUTE , NativeKeyEvent.VC_WAKE , NativeKeyEvent.VC_SLEEP , NativeKeyEvent.VC_ENTER , -1 -> {
                debug ( "Code: " + code + " is not a valid binding code. Code text: " + NativeKeyEvent.getKeyText ( code ) + "." );
                return false;
            }
            default -> {
                return true;
            }
        }
    }

    /**
     * <p>
     * Debug is called at certain points when processing events.
     * It only outputs the message if the value debug is set to true.
     * </p>
     */
    private void debug ( final String message ) {
        if ( debug ) {
            log.info ( message );
        }
    }

}
