package com.toong.androidcurrencyedittext;

import com.squareup.burst.Burst;
import com.squareup.burst.BurstJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static org.junit.Assert.*;

@RunWith(BurstJUnit4.class)
public class CleanStringImplTest {


    @Test
    public void update(TestCase testCase) {
        CleanString cleanString = new CleanStringImpl(Locale.forLanguageTag(testCase.languageTag));
        cleanString.update(testCase.selectionStart,testCase.selectionEnd,testCase.inputText);
        assertEquals(testCase.outputText, cleanString.getDisplayText());
        assertEquals(testCase.newSelectionPosition, cleanString.getSelection());
    }

    enum TestCase {
//        CASE1(7,7,"fr-FR","$ 12921.1233", "12 921,12 €",6),
//        CASE11(7,7,"en-IE","$ 12921.1233", "12 921,12 €",6),
//        CASE2(1,1,"en-GB","12921.1233", "12 921,12",6),
//        CASE3(7,7,"en-US","$ 12921.1233", "12 921,12 €",6),
        CASE4(5,5,"vi-VN","12 9231,123 đ", "129 231,123 đ",5);

        final int selectionStart;
        final int selectionEnd;
        private String languageTag;
        final String inputText;
        final String outputText;
        final int newSelectionPosition;

        TestCase(int selectionStart, int selectionEnd, String languageTag, String inputText, String outputText, int newSelectionPosition) {
            this.selectionStart = selectionStart;
            this.selectionEnd = selectionEnd;
            this.languageTag = languageTag;
            this.inputText = inputText;
            this.outputText = outputText;
            this.newSelectionPosition = newSelectionPosition;
        }
    }
}