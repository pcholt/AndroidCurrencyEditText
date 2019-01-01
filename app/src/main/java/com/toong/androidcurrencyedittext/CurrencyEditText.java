package com.toong.androidcurrencyedittext;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.widget.EditText;

import com.toong.androidcurrencyedittext.behaviour.CursorBehaviourImpl;
import com.toong.androidcurrencyedittext.string.CleanString;
import com.toong.androidcurrencyedittext.string.CleanStringImpl;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

import static timber.log.Timber.d;


/**
 * Created by PhanVanLinh on 25/07/2017.
 * phanvanlinh.94vn@gmail.com
 * <p>
 * Some note <br/>
 * <li>Always use locale US instead of default to make DecimalFormat work well in all language</li>
 */
public class CurrencyEditText extends androidx.appcompat.widget.AppCompatEditText {
    private static final int MAX_LENGTH = 20;
    private CurrencyTextWatcher currencyTextWatcher;
    private String prefix;

    public CurrencyEditText(Context context) {
        this(context, null);
    }

    public CurrencyEditText(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.editTextStyle);
    }

    public CurrencyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTextLocale(Locale.GERMAN);
        this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        this.setKeyListener(new DigitsKeyListener(getTextLocale()) {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                CharSequence result = super.filter(source, start, end, dest, dstart, dend);
                if (Objects.equals(source.toString(), ".")) {
                    return source;
                }
                return result;
            }
        });
        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(getTextLocale());
        prefix = format.getCurrency().getSymbol();
        setHint(prefix);
        currencyTextWatcher = new CurrencyTextWatcher(this, getTextLocale());
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            this.addTextChangedListener(currencyTextWatcher);
        } else {
            this.removeTextChangedListener(currencyTextWatcher);
        }
        handleCaseCurrencyEmpty(focused);
    }

    /**
     * When currency empty <br/>
     * + When focus EditText, set the default text = prefix (ex: VND) <br/>
     * + When EditText lose focus, set the default text = "", EditText will display hint (ex:VND)
     */
    private void handleCaseCurrencyEmpty(boolean focused) {
        if (focused) {
            if (getText().toString().isEmpty()) {
                setText(prefix);
            }
        } else {
            if (getText().toString().equals(prefix)) {
                setText("");
            }
        }
    }

    private static class CurrencyTextWatcher implements TextWatcher {
        private final EditText editText;
        CleanString cleanString;
        private String changeText;

        CurrencyTextWatcher(EditText editText, Locale locale) {
            this.editText = editText;
            cleanString = new CleanStringImpl(locale, new CursorBehaviourImpl());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            d(MessageFormat.format("BEFORECHANGED [{0}] s{1} c{2} a{3}", s, start, count, after));
            changeText = s.toString().substring(start, start + count);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            d(MessageFormat.format("ONCHANGED     [{0}]  s{1} c{2}", s, start, count));
        }

        @Override
        public void afterTextChanged(Editable editable) {
            d(MessageFormat.format("AFTERCHANGED  [{0}]", editable));

            if (cleanString.update(editText.getSelectionStart(), editText.getSelectionEnd(), editable.toString(), changeText)) {
                editText.removeTextChangedListener(this);
                editText.setText(cleanString.getDisplayText());
                editText.setSelection(cleanString.getSelection());
                editText.addTextChangedListener(this);
            }

        }

    }
}
