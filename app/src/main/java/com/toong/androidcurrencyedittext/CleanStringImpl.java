package com.toong.androidcurrencyedittext;

import android.support.annotation.NonNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import static java.text.MessageFormat.*;
import static timber.log.Timber.d;

class CleanStringImpl implements CleanString {
    private final String prefix;
    private final NumberFormat numberFormat;
    private final String suffix;
    private final char groupingSeparator;
    private final char decimalSeparator;
    private String displayText;
    private int selection;
    private String previousStripped;

    public CleanStringImpl(Locale locale) {
        numberFormat = NumberFormat.getCurrencyInstance(locale);
        if (numberFormat instanceof DecimalFormat) {
            prefix = ((DecimalFormat) numberFormat).getPositivePrefix();
            suffix = ((DecimalFormat) numberFormat).getPositiveSuffix();
            groupingSeparator = ((DecimalFormat) numberFormat).getDecimalFormatSymbols().getGroupingSeparator();
            decimalSeparator = ((DecimalFormat) numberFormat).getDecimalFormatSymbols().getDecimalSeparator();
        }
        else {
            prefix = "";
            suffix = "";
            groupingSeparator = ',';
            decimalSeparator = '.';
        }
        previousStripped = "";
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

        d(format("Unstripped {0}", string));
        String stripped = stripString(string);
        d(format("Stripped   {0}", stripped));

        if (Objects.equals(stripped, previousStripped) || stripped.isEmpty()) {
            return false;
        }
        previousStripped = stripped;

        double aDouble = Double.parseDouble(stripped);
        d(format("double={0}", aDouble));
        displayText = numberFormat.format(aDouble);
        d(format("display={0}", displayText));
        selection = digitsForward(digitCountUntilSelection, displayText);

        return true;
    }

    /**
     * Calculate the position of the cursor after the given number of digits.
     * @param digitCountUntilSelection number of digits between the start of the string
     *                                and the intended position of the cursor
     * @param displayText the display string containing the digits
     * @return the final position of the cursor.
     *
     */
    private int digitsForward(int digitCountUntilSelection, String displayText) {
        int i;
        char c;
        for(i=0; digitCountUntilSelection > 0 && i < displayText.length(); i++) {
            if (String.valueOf(displayText.charAt(i)).matches(getDigitMatcherRegex()))
                digitCountUntilSelection --;
        }


        return i;
    }

    private int digitCount(String string, int selectionEnd) {
        int position = 0;
        for (int i = 0; i < selectionEnd; i++) {
            position += String.valueOf(string.charAt(i)).matches(getDigitMatcherRegex()) ? 1 : 0;
        }
        return position;
    }

    private String _regex;

    @NonNull
    private String getDigitMatcherRegex() {
        if (_regex == null) {
            _regex = "[0-9]";
            if (numberFormat instanceof DecimalFormat) {
                DecimalFormat decimalFormat = (DecimalFormat) this.numberFormat;
                _regex = "[0-9" + decimalFormat.getDecimalFormatSymbols().getDecimalSeparator()+"]";
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
