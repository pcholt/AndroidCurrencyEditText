package com.toong.androidcurrencyedittext;

import android.util.Log;

import com.squareup.burst.BurstJUnit4;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import timber.log.Timber;

import static org.junit.Assert.*;

@RunWith(BurstJUnit4.class)
public class CleanStringImplTest {

    @Before
    public void setupLogging() {
        Timber.plant(new Timber.Tree() {
            @Override
            protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
                if (priority >= Log.WARN)
                    System.out.println(message);
            }
        });
    }

    @Test
    public void update(TestCase testCase) {
        CleanString cleanString = new CleanStringImpl(Locale.forLanguageTag(testCase.languageTag));
        cleanString.update(testCase.selectionStart, testCase.selectionEnd, testCase.inputText, testCase.changeText);
        assertEquals(testCase.outputText, cleanString.getDisplayText());
        assertEquals(testCase.newSelectionPosition, cleanString.getSelection());
    }

    enum TestCase {
        CASE1a("fr-FR", "1,^ €", "1,^00 €"),
        CASE1b("en-IE", "€1^", "€1^.00"),
        CASE1c("en-IE", "€1.^.00", "€1.^00"),
        CASE1d("en-IE", "€1.0^00", "€1.0^0"),
        CASE2a("en-GB", "12,921.138^", "£12,921.14^"),
        CASE3A("en-US", "$^.00", "$0^.00"),
        CASE3B("en-US", "$0.^.00", "$0.^00"),
        CASE3C("en-US", "$^.00", "$0^.00"),
        CASE3D("en-US", "$^.00", "$0^.00"),
        CASE3E("en-US", "$2^00", "$2^.00", "."),
        CASE3F("fr-FR", "1^00 €", "1^,00 €", ","),
        CASE4E("vi-VN", "1229^.345.678 đ", "1.229^.345.678 đ"),
        CASE4a("vi-VN", "1^229.345.678 đ", "1^.229.345.678 đ"),
        CASE4b("vi-VN", "1229^.345.678 đ", "1.229^.345.678 đ"),
        CASE4c("vi-VN", "1229^.345.678 đ", "1.229^.345.678 đ"),
        CASE4d("vi-VN", "1229^.345.678 đ", "1.229^.345.678 đ"),
        CASE4e("vi-VN", "122.345.3^678 đ", "1.223.453^.678 đ");

        final int selectionStart;
        final int selectionEnd;
        private String languageTag;
        final String inputText;
        final String outputText;
        final int newSelectionPosition;
        String changeText;

        TestCase(String languageTag, String inputText, String outputText, String changeText) {
            this(languageTag, inputText, outputText);
            this.changeText = changeText;
        }

        TestCase(String languageTag, String inputText, String outputText) {
            this.languageTag = languageTag;
            selectionStart = inputText.indexOf('^');
            selectionEnd = selectionStart;
            newSelectionPosition = outputText.indexOf('^');
            this.inputText = inputText.substring(0, selectionStart) + inputText.substring(selectionStart + 1);
            this.outputText = outputText.substring(0, newSelectionPosition) + outputText.substring(newSelectionPosition + 1);
            changeText = "";
        }
    }
}