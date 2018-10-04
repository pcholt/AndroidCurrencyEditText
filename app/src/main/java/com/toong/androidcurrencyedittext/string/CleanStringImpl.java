package com.toong.androidcurrencyedittext.string;

import com.toong.androidcurrencyedittext.behaviour.CursorBehaviour;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static timber.log.Timber.d;

public class CleanStringImpl implements CleanString {
    public static final char DEFAULT_GROUPING_SEPARATOR = ',';
    public static final char DEFAULT_DECIMAL_SEPARATOR = '.';
    private static final String EMPTY_STRING = "";
    private final String prefix;
    private final NumberFormat numberFormat;
    private final CursorBehaviour cursorBehaviour;
    private final String suffix;
    private final char groupingSeparator;
    private String displayText;
    private int selection;
    private int digitCountUntilSelection;
    private final char decimalSeparator;

    public CleanStringImpl(Locale locale, CursorBehaviour cursorBehaviour) {
        numberFormat = NumberFormat.getCurrencyInstance(locale);
        this.cursorBehaviour = cursorBehaviour;
        if (numberFormat instanceof DecimalFormat) {
            DecimalFormat decimalFormat = (DecimalFormat) this.numberFormat;
            prefix = decimalFormat.getPositivePrefix();
            suffix = decimalFormat.getPositiveSuffix();
            groupingSeparator = decimalFormat.getDecimalFormatSymbols().getGroupingSeparator();
            decimalSeparator = decimalFormat.getDecimalFormatSymbols().getDecimalSeparator();
        } else {
            prefix = EMPTY_STRING;
            suffix = EMPTY_STRING;
            groupingSeparator = DEFAULT_GROUPING_SEPARATOR;
            decimalSeparator = DEFAULT_DECIMAL_SEPARATOR;
        }
        this.cursorBehaviour.setDecimalSeparator(decimalSeparator);
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

        if (changeText.length() > 0)
            if (changeText.charAt(0) == decimalSeparator) {
                // Trying to delete the decimal separator - return the decimal separator to the string.
                string = new StringBuffer(string).insert(selectionEnd, decimalSeparator).toString();
            }
            else if (changeText.charAt(0) == groupingSeparator) {
            // Trying to delete the grouping separator - delete the next character to the left.
                selectionEnd--;
                string = new StringBuffer(string).deleteCharAt(selectionEnd).toString();
            }

        digitCountUntilSelection = cursorBehaviour.digitCount(string, selectionEnd);
        d("digitCountUntilSelection=%d (%s, %d)", digitCountUntilSelection, string, selectionEnd);

        String stripped = stripString(string);

        double aDouble;
        try {
            aDouble = Double.parseDouble(stripped);
        } catch (NumberFormatException e) {
            aDouble = 0;
        }

        displayText = numberFormat.format(aDouble);
        d("digitCountUntilSelection:%d", digitCountUntilSelection);
        d("displayText:%s", displayText);
        selection = cursorBehaviour.cursorPositionAfterCount(digitCountUntilSelection, displayText);

        return true;
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

    @Override
    public int getDigitCountUntilSelection() {
        return digitCountUntilSelection;
    }
}
