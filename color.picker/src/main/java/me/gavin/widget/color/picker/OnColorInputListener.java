package me.gavin.widget.color.picker;

import android.content.DialogInterface;

public interface OnColorInputListener {
    void onColorSelected(DialogInterface dialog, String color);
}