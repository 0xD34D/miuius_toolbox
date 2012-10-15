package us.miui.helpers;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created with IntelliJ IDEA.
 * User: Clark Scheff
 * Date: 10/15/12
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class DisplayHelper {
    public static int getPixelSize(Context context, int dp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int)((float)dp * dm.density);
    }
}
