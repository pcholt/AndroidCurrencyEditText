package com.toong.androidcurrencyedittext;

interface CleanString {
    boolean update(int selectionStart, int selectionEnd, String string, String changeText);
    String getDisplayText();
    int getSelection();
}
