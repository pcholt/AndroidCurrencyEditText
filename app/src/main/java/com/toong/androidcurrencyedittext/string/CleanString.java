package com.toong.androidcurrencyedittext.string;

public interface CleanString {
    boolean update(int selectionStart, int selectionEnd, String string, String changeText);
    String getDisplayText();
    int getSelection();
    int getDigitCountUntilSelection();
}
