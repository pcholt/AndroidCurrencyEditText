package com.toong.androidcurrencyedittext.behaviour;

import static timber.log.Timber.d;
import static timber.log.Timber.v;

public class CursorBehaviourImpl implements CursorBehaviour {

    private static final int STATE_SKIP_INITIAL_ZERO = 1;
    private static final int STATE_COUNT_DIGITS = 2;
    private static final int STATE_EXIT = 3;
    private char decimalSeparator;

    @Override
    public int digitCount(String string, int selectionEnd) {
        int i = 0;
        int count = 0;
        int state = STATE_SKIP_INITIAL_ZERO;
        while (i < selectionEnd) {
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
        v("digitCount=%s", count);
        return count;
    }

    @Override
    public int cursorPositionAfterCount(int count, String string) {
        d("cursorPositionAfterCount(%d, %s)", count, string);
        int pos = 0;

        String regex0 = "[1-9" + decimalSeparator + "]";
        String regex1 = "[0-9" + decimalSeparator + "]";
        String regex = regex0;
        for (int i = 0; i < string.length(); i++) {
            pos = i;
            char c = string.charAt(i);
            d("regexes=%s | %s", regex0, regex1);
            if (String.valueOf(c).matches(regex)) {
                d("%c matches => decrement", c);
                count--;
                regex = regex1;
            }
            if (count < 0) {
                d("%c match fail => return %s", c, pos);
                return pos;
            }
        }
        return string.length();
    }

    @Override
    public void setDecimalSeparator(char decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }


}
