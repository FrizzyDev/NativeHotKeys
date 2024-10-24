package com.github.frizzy.nhk.HotKey;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.util.Objects;

/**
 * A Hotkey represents a key expression that can be pressed by the user to
 * quickly activate a process or window in an application, from anywhere
 * within the system.
 *
 * @author Frizzy
 * @version 0.1
 * @since 0.1
 */
public class Hotkey implements Comparable< Hotkey > {

    /**
     * <p>
     * The ID of the process that should be started when
     * this HotKey is dispatched.
     * </p>
     * <p>
     * This is essentially the name for the Hotkey.
     * </p>
     */
    private final String commandID;

    /**
     * The text representation of the hotkey. For example,
     * this could appear as "Control + Shift + Space"
     */
    private final String expressionText;

    /**
     * The first modifier for this Hotkey.
     */
    private final int mod1;

    /**
     * The second modifier for this Hotkey.
     */
    private final int mod2;

    /**
     * The keycode for this hotkey.
     */
    private final int key;

    /**
     * Constructs the Hotkey with the provided commandID, first and second key modifiers,
     * and the key code. The default values for the modifiers and the keycode is -1. Any modifiers or
     * key codes represented by -1 means that it has not been set.
     *
     * @param commandID The commandID of the process that should be started when this hotkey is dispatched.
     * @param mod1      The first modifier of the hotkey expression, such as Shift or Control.
     * @param mod2      The second modifier of the hotkey expression, such as Shift or Control.
     * @param key       The key code of the hotkey expression, such as letters, numbers, or function buttons.
     */
    public Hotkey ( String commandID , int mod1 , int mod2 , int key ) {
        this.commandID = commandID;
        this.mod1 = mod1;
        this.mod2 = mod2;
        this.key = key;
        expressionText = buildStringRepresentation ( new int[] { mod1, mod2, key} );
    }

    /**
     * <p>
     * Checks if the provided expression array matches this Hotkeys
     * binding.
     * </p>
     * <p>
     * Used in determining if a built expression array in HotKeyEventBus matches
     * a Hotkey registered with the bus Hotkey Set.
     * </p>
     */
    public boolean matchesKeyExpression ( int[] expression ) {
        return expression[ HotKeyEventBus.MODIFIER_1 ] == mod1 &&
                expression[ HotKeyEventBus.MODIFIER_2 ] == mod2 &&
                expression[ HotKeyEventBus.KEYCODE ] == key;
    }

    /**
     * <p>
     * Checks if the provided expression text matches this Hotkey expression.
     * </p>
     * <p>
     * The expression text is a String representation of the modifiers and keycode of the
     * Hotkey binding.
     * </p>
     */
    @SuppressWarnings ( "unused" )
    public boolean matchesExpressionText ( String expText ) {
        return expText.equals ( expressionText );
    }

    /**
     * <p>
     * Returns the commandID of this Hotkey.
     * </p>
     * <p>
     * The commandID is essentially the name of a Hotkey and should be
     * used as an identifier in one a Hotkey is supposed to do.
     * </p>
     */
    public String getCommandID ( ) {
        return commandID;
    }

    /**
     * Returns the first modifier of the HotKey expression, if there is one.
     * If there is not a modifier, the default value returned is -1.
     */
    @SuppressWarnings ( "unused" )
    public int getFirstModifier ( ) {
        return mod1;
    }

    /**
     * Returns the second modifier of the HotKey expression, if there is one.
     * If there is not a modifier, the default value returned is -1.
     */
    @SuppressWarnings ( "unused" )
    public int getSecondModifier ( ) {
        return mod2;
    }

    /**
     * Returns the key code of the HotKey expression, if there is one.
     * If there is not a modifier, the default value returned is -1.
     */
    @SuppressWarnings ( "unused" )
    public int getKey ( ) {
        return key;
    }

    /**
     * Converts the Hotkey into a String of valid JSON for export to a
     * .JSON file.
     */
    public String toJSON ( ) {
        return "{" + "\"commandID\": \"" + commandID + "\", " +
                "\"firstModifier\": " + mod1 + ", " +
                "\"secondModifier\": " + mod2 + ", " +
                "\"keyCode\": " + key + ", " +
                "\"text\": " + expressionText +
                "}";
    }

    /**
     * Builds a String representation of the provided Hotkey expression.
     */
    public static String buildStringRepresentation ( final int[] expression ) {
        StringBuilder builder = new StringBuilder ( );

        if ( expression[ HotKeyEventBus.MODIFIER_1 ] != -1 ) {
            builder.append ( NativeKeyEvent.getKeyText ( expression [ HotKeyEventBus.MODIFIER_1 ] ) ).append ( " + " );
        }

        if ( expression[ HotKeyEventBus.MODIFIER_2 ] != -1 ) {
            builder.append ( NativeKeyEvent.getKeyText ( expression [ HotKeyEventBus.MODIFIER_2 ] ) ).append ( " + " );
        }

        if ( expression [ HotKeyEventBus.KEYCODE ] != -1 ) {
            builder.append ( NativeKeyEvent.getKeyText ( expression [ HotKeyEventBus.KEYCODE ] ) );
        }

        return builder.toString ();
    }

    /**
     * Compares the commandID's of this Hotkey and the provided Hotkey
     * to see if they are the same Hotkey.
     */
    @Override
    public int compareTo ( Hotkey o ) {
        return o.getCommandID ( ).compareTo ( commandID );
    }

    @Override
    public boolean equals ( Object o ) {
        if ( this == o )
            return true;
        if ( o == null || getClass ( ) != o.getClass ( ) )
            return false;
        Hotkey hotkey = ( Hotkey ) o;
        return mod1 == hotkey.mod1 && mod2 == hotkey.mod2 && key == hotkey.key && Objects.equals ( commandID , hotkey.commandID ) && Objects.equals ( expressionText ,
                hotkey.expressionText );
    }

    @Override
    public int hashCode ( ) {
        return Objects.hash ( commandID , expressionText , mod1 , mod2 , key );
    }
}
