package com.toong.androidcurrencyedittext.behaviour;

import static timber.log.Timber.d;

public class CursorBehaviourImpl implements CursorBehaviour {

    public static final int STATE_SKIP_INITIAL_ZERO = 1;
    public static final int STATE_COUNT_DIGITS = 2;
    public static final int STATE_EXIT = 3;
    private char decimalSeparator;

    @Override
    public int digitCount(String string, int selectionEnd) {
        int i = 0;
        int count = 0;
        int state = STATE_SKIP_INITIAL_ZERO;
        while (i < selectionEnd && state != STATE_EXIT) {
            char c = string.charAt(i);
            switch (state) {
                case STATE_SKIP_INITIAL_ZERO:
                    if (String.valueOf(c).matches("[1-9" + decimalSeparator + "]")) {
                        count++;
                        state = STATE_COUNT_DIGITS;
                    }
                    break;
                case STATE_COUNT_DIGITS:
                    if (String.valueOf(c).matches("[0-9" + decimalSeparator + "]")) {
                        count++;
                    }
                    break;
                default:
            }
            i++;
        }
        d("%s", count);
        return count;
    }

    @Override
    public int cursorPositionAfterCount(int count, String string) {
        d("cursorPositionAfterCount(%d, %s)", count, string);
        int pos = 0;

        for (int i = 0; i < string.length(); i++) {
            pos = i;
            char c = string.charAt(i);
            if (String.valueOf(c).matches("[1-9" + decimalSeparator + "]")) {
                count--;
            }
            if (count <= 0) {
                return pos;
            }
        }
        return pos;
    }

    @Override
    public void setDecimalSeparator(char decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }


}
