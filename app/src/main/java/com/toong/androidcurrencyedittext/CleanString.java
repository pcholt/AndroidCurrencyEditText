package com.toong.androidcurrencyedittext;

interface CleanString {
    public boolean update(int selectionStart, int selectionEnd, String string);

    String getDisplayText();

    int getSelection();
}
