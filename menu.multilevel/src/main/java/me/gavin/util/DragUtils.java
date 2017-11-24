package me.gavin.util;

import android.content.ClipData;
import android.content.ClipDescription;

/**
 * DragUtils
 *
 * @author st028 12/7/15
 */
public class DragUtils {

    private static final String DRAG_LABEL = "me.gavin.widget.menu.multilevel";

    public static boolean isDragForMe(CharSequence dragLabel) {
        return DRAG_LABEL.equals(dragLabel);
    }

    public static ClipData getClipData() {
         return new ClipData(DRAG_LABEL, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, new ClipData.Item(DRAG_LABEL));
    }
}
