package com.toong.androidcurrencyedittext;

import android.support.annotation.NonNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import static timber.log.Timber.d;

class CleanStringImpl implements CleanString {
    public static final int STATE_SKIP_INITIAL_ZERO = 1;
    public static final int STATE_COUNT_DIGITS = 2;
    public static final int STATE_EXIT = 3;
    private final String prefix;
    private final NumberFormat numberFormat;
    private final String suffix;
    private final char groupingSeparator;
    private final char decimalSeparator;
    private String displayText;
    private int selection;

    CleanStringImpl(Locale locale) {
        numberFormat = NumberFormat.getCurrencyInstance(locale);
        if (numberFormat instanceof DecimalFormat) {
            prefix = ((DecimalFormat) numberFormat).getPositivePrefix();
            suffix = ((DecimalFormat) numberFormat).getPositiveSuffix();
            groupingSeparator = ((DecimalFormat) numberFormat).getDecimalFormatSymbols().getGroupingSeparator();
            decimalSeparator = ((DecimalFormat) numberFormat).getDecimalFormatSymbols().getDecimalSeparator();
        } else {
            prefix = "";
            suffix = "";
            groupingSeparator = ',';
            decimalSeparator = '.';
        }
    }

    @Override
    public boolean update(int selectionStart, int selectionEnd, String string, String changeText) {
        if (string.length() < prefix.length() + suffix.length()) {
            displayText = prefix + suffix;
            selection = prefix.length();
            return true;
        }

        if (string.equals(prefix + suffix)) {
            return false;
        }

        int digitCountUntilSelection = digitCount(string, selectionEnd);
        d("digitCountUntilSelection=%d (%s, %d)", digitCountUntilSelection, string, selectionEnd);

        String stripped = stripString(string);

        double aDouble;
        try {
            aDouble = Double.parseDouble(stripped);
        } catch (NumberFormatException e) {
            aDouble = 0;
        }

        displayText = numberFormat.format(aDouble);
        selection = cursorPositionAfterCount(digitCountUntilSelection, displayText);

        return true;
    }

    public boolean update1(int selectionStart, int selectionEnd, String string, String changeText) {

        StringBuilder builder = new StringBuilder(string);

        for (char c : changeText.toCharArray()) {
            if (Objects.equals(c, decimalSeparator)) {
                builder.insert(selectionStart, decimalSeparator);
                break;
            }
        }

//        string = s.toString().replaceFirst("^[^1-9"+decimalSeparator+"]*0", "");
        string = builder.toString();

        if (string.length() < prefix.length() + suffix.length()) {
            displayText = prefix + suffix;
            selection = prefix.length();
            return true;
        }

        if (string.equals(prefix + suffix)) {
            return false;
        }

        int digitCountUntilSelection = digitCount(string, selectionEnd);

        String stripped = stripString(string);

        double aDouble;
        try {
            aDouble = Double.parseDouble(stripped);
        } catch (NumberFormatException e) {
            aDouble = 0;
        }
        displayText = numberFormat.format(aDouble);
        selection = digitsForward(digitCountUntilSelection, displayText);

        return true;
    }

    /**
     * Calculate the position of the cursor after the given number of digits.
     *
     * @param digitCountUntilSelection number of digits between the start of the string
     *                                 and the intended position of the cursor
     * @param displayText              the display string containing the digits
     * @return the final position of the cursor.
     */
    private int digitsForward(int digitCountUntilSelection, String displayText) {
        int i;
        char c;
        for (i = 0; digitCountUntilSelection > 0 && i < displayText.length(); i++) {
            if (String.valueOf(displayText.charAt(i)).matches(getDigitMatcherRegex()))
                digitCountUntilSelection--;
        }

        if (i == 0) {
            while (i < displayText.length() && !String.valueOf(displayText.charAt(i)).matches("[.1-9]"))
                i++;
        }
        return i;
    }

    private int digitCount(String string, int selectionEnd) {
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

    private int cursorPositionAfterCount(int count, String string) {
        d("cursorPositionAfterCount(%d, %s)", count, string);
        int state = STATE_SKIP_INITIAL_ZERO;
        int i = 0;
        int pos=0;
        while (count >= 0 && state != STATE_EXIT) {
            char c = string.charAt(i);
            d("     c=%c, count=%d, i=%d, state=%d", c, count, i, state);
            switch (state) {
                case STATE_SKIP_INITIAL_ZERO:
                    if (String.valueOf(c).matches("[1-9" + decimalSeparator + "]")) {
                        d("SIZ  c=%c count=%d", c,count-1);
                        count--;
                        pos++;
                        state = STATE_COUNT_DIGITS;
                    }
                    break;
                case STATE_COUNT_DIGITS:
                    if (String.valueOf(c).matches("[0-9" + decimalSeparator + "]")) {
                        d("CD   c=%c count=%d", c,count-1);
                        count--;
                    }
                    else {
                        pos++;
                    }
                    break;
                default:
            }
            i++;
        }
        return pos;
    }

    private String _regex;

    @NonNull
    private String getDigitMatcherRegex() {
        if (_regex == null) {
            _regex = "[0-9]";
            if (numberFormat instanceof DecimalFormat) {
                DecimalFormat decimalFormat = (DecimalFormat) this.numberFormat;
                _regex = "[0-9" + decimalFormat.getDecimalFormatSymbols().getDecimalSeparator() + "]";
            }
        }
        return _regex;
    }

    private String stripString(String string) {
        return string
                .replace(groupingSeparator, ' ')
                .replace(decimalSeparator, '.')
                .replaceAll("[^-0-9.]", "")
                .replaceAll("\\.\\.", ".");
    }

    @Override
    public String getDisplayText() {
        return displayText;
    }

    @Override
    public int getSelection() {
        return selection;
    }
}
