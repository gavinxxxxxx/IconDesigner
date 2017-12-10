package me.gavin.widget.color.picker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * ColorPickerDialogBuilder
 *
 * @author gavin.xiong 2017/11/29
 */
public class ColorPickerDialogBuilder {

    private final Context mContext;
    private final AlertDialog.Builder builder;

    private HSLColorPicker picker;

    private ColorPickerDialogBuilder(Context context, int themeResId) {
        this.mContext = context;
        FrameLayout parent = new FrameLayout(context);
        int padding = DisplayUtil.dp2px(24);
        parent.setPadding(padding, padding, padding, padding);
        picker = new HSLColorPicker(context);
        parent.addView(picker);
        this.builder = new AlertDialog.Builder(context, themeResId)
                .setView(parent);
    }

    public static ColorPickerDialogBuilder with(Context context) {
        return with(context, 0);
    }

    public static ColorPickerDialogBuilder with(Context context, int themeResId) {
        return new ColorPickerDialogBuilder(context, themeResId);
    }

    public ColorPickerDialogBuilder setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public ColorPickerDialogBuilder setTitle(int titleRes) {
        builder.setTitle(titleRes);
        return this;
    }

    public ColorPickerDialogBuilder withAlpha(boolean enable) {
        picker.withAlpha(enable);
        return this;
    }

    public ColorPickerDialogBuilder setColor(int color) {
        picker.setColor(color);
        return this;
    }

    public ColorPickerDialogBuilder setInputButton(int text, final OnColorInputListener listener) {
        return setInputButton(mContext.getString(text), listener);
    }

    public ColorPickerDialogBuilder setInputButton(CharSequence text, final OnColorInputListener listener) {
        builder.setNeutralButton(text, (dialog, which) -> {
            FrameLayout parent = new FrameLayout(mContext);
            int padding = DisplayUtil.dp2px(24);
            parent.setPadding(padding, padding, padding, padding);
            final EditText editText = new EditText(mContext);
            editText.setTextColor(0xFFFFFFFF);
            editText.setHintTextColor(0xFF8899AA);
            editText.setHint("#26A69A  |  #80000000");
            editText.setFilters(new InputFilter[]{new ColorInputFilter()});
            parent.addView(editText);
            final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                    .setTitle(text)
                    .setView(parent)
                    .setPositiveButton(android.R.string.ok, (dialog1, which1)
                            -> listener.onColorSelected(dialog, editText.getText().toString()))
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
            editText.postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    editText.requestFocus();
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
            editText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    alertDialog.dismiss();
                    listener.onColorSelected(alertDialog, editText.getText().toString());
                }
                return true;
            });
        });
        return this;
    }

    public ColorPickerDialogBuilder setPositiveButton(CharSequence text, final OnColorSelectedListener listener) {
        builder.setPositiveButton(text, (dialog, which) -> listener.onColorSelected(dialog, picker.getColor()));
        return this;
    }

    public ColorPickerDialogBuilder setPositiveButton(int text, OnColorSelectedListener listener) {
        return setPositiveButton(mContext.getString(text), listener);
    }

    public ColorPickerDialogBuilder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        builder.setNegativeButton(text, listener);
        return this;
    }

    public ColorPickerDialogBuilder setNegativeButton(int text, DialogInterface.OnClickListener listener) {
        return setNegativeButton(mContext.getString(text), listener);
    }

    public ColorPickerDialogBuilder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
        builder.setNeutralButton(text, listener);
        return this;
    }

    public AlertDialog show() {
        return builder.show();
    }

}
