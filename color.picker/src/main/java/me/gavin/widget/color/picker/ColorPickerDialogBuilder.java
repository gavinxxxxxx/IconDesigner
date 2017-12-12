package me.gavin.widget.color.picker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

    public ColorPickerDialogBuilder setNeutralButton(int text, DialogInterface.OnClickListener listener) {
        return setNeutralButton(mContext.getString(text), listener);
    }

    public AlertDialog show() {
        return builder.show();
    }

}
