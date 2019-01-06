/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for string splitting.
 */
public class CamelCaseSplitter {

    private enum State {
        EMPTY, LOWER, UPPER, DIGIT
    }

    private CamelCaseSplitter() {/* no instantiation */}

    /**
     * Split string, where characters change from lower to upper case or on digits.
     * <br/>
     * Example: getIDFromProdukt => get ID From Produkt
     * <br/>
     * Example: update4Invoice => update 4 Invoice
     *
     * @param string the input string
     * @return the resulting string array
     */
    public static String[] split(String inputString) {
        State state = determineInitialState(inputString);
        if (state == State.EMPTY) {
            return new String[0];
        }

        final String string = inputString.replaceAll("_", " "); // split on underscores
        final StringBuilder current = new StringBuilder();
        final List<String> resultList = new ArrayList<String>();

        for (int i = 0; i < string.length(); i++) {
            final char ch = string.charAt(i);

            switch (state) {
            case UPPER:
                state = handleUpperState(resultList, current, ch, state, string, i);
                break;
            case LOWER:
                state = handleLowerState(resultList, current, ch, state);
                break;
            case DIGIT:
                state = handleDigitState(resultList, current, ch, state);
                break;
            default:
                break;
            }
        }
        addAndClearCurrent(resultList, current);
        return resultList.toArray(new String[resultList.size()]);
    }

    private static State handleDigitState(final List<String> resultList, final StringBuilder current,
            final char ch, final State state) {
        State newState = state;

        if (Character.isDigit(ch)) {
            addCharToCurrent(resultList, current, ch);
        }  else {
            addAndClearCurrent(resultList, current);
            addCharToCurrent(resultList, current, ch);
            newState = Character.isUpperCase(ch) ? State.UPPER : State.LOWER;
        }
        return newState;
    }

    private static State handleLowerState(final List<String> resultList, final StringBuilder current,
            final char ch, final State state) {
        State newState = state;

        if (!Character.isDigit(ch) && !Character.isUpperCase(ch)) {
            addCharToCurrent(resultList, current, ch);
        } else {
            addAndClearCurrent(resultList, current);
            addCharToCurrent(resultList, current, ch);
            newState = Character.isDigit(ch) ? State.DIGIT : State.UPPER;
        }
        return newState;
    }

    private static State handleUpperState(final List<String> resultList, final StringBuilder current,
            final char ch, final State state, final String string, final int index) {
        State newState = state;

        if (Character.isUpperCase(ch)) {
            if (index == string.length() - 1
                    || Character.isDigit(string.charAt(index + 1))
                    || Character.isUpperCase(string.charAt(index + 1))) {
                addCharToCurrent(resultList, current, ch);
            } else {
                addAndClearCurrent(resultList, current);
                addCharToCurrent(resultList, current, ch);
                newState = State.LOWER;
            }
        } else {
            if (Character.isDigit(ch)) {
                addAndClearCurrent(resultList, current);
            }
            addCharToCurrent(resultList, current, ch);
            newState = Character.isDigit(ch) ? State.DIGIT : State.LOWER;
        }
        return newState;
    }

    private static void addAndClearCurrent(final List<String> resultList, final StringBuilder current) {
        if (current.length() > 0) {
            resultList.add(current.toString());
            current.setLength(0);
        }
    }

    private static void addCharToCurrent(final List<String> resultList, final StringBuilder current, final char ch) {
        if (Character.isWhitespace(ch)) {
            addAndClearCurrent(resultList, current);
        } else {
            current.append(ch);
        }
    }

    private static State determineInitialState(final String string) {
        if (string != null && string.length() > 0) {
            final char ch = string.charAt(0);
            return Character.isDigit(ch) ? State.DIGIT : (Character.isUpperCase(ch) ? State.UPPER : State.LOWER);
        }
        return State.EMPTY;
    }
}
