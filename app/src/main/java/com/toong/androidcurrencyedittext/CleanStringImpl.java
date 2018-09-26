package com.toong.androidcurrencyedittext;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

class CleanStringImpl implements CleanString {
    private final String prefix;
    private final NumberFormat format;
    private final String suffix;
    private final char groupingSeparator;
    private final char decimalSeparator;
    private String displayText;
    private int selection;
    private String previousStripped;

    public CleanStringImpl(Locale locale) {
        format = NumberFormat.getCurrencyInstance(locale);
        if (format instanceof DecimalFormat) {
            prefix = ((DecimalFormat) format).getPositivePrefix();
            suffix = ((DecimalFormat) format).getPositiveSuffix();
            groupingSeparator = ((DecimalFormat) format).getDecimalFormatSymbols().getGroupingSeparator();
            decimalSeparator = ((DecimalFormat) format).getDecimalFormatSymbols().getDecimalSeparator();
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
    public boolean update(int selectionStart, int selectionEnd, String string) {

        if (string.length() < prefix.length() + suffix.length()) {
            displayText = prefix + suffix;
            selection = prefix.length();
            return true;
        }

        if (string.equals(prefix + suffix)) {
            return false;
        }

        String stripped = stripString(string);

        if (Objects.equals(stripped, previousStripped) || stripped.isEmpty()) {
            return false;
        }
        previousStripped = stripped;

        displayText = format.format(Double.parseDouble(stripped));
        selection = displayText.length();

        return true;
    }

    private String stripString(String string) {
        return string
                .replace(groupingSeparator, ' ')
                .replace(decimalSeparator, '.')
                .replaceAll("[^-0-9.]", "");
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
