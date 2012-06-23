/**
 * 
 */
package us.miui.toolbox;

import android.app.Dialog;
import android.content.Context;

/**
 * @author Clark Scheff
 *
 */
public class PresetSelectionDialog extends Dialog {

	public PresetSelectionDialog(Context context, int theme) {
		super(context, theme);
		setContentView(R.layout.dialog_color_presets);
	}

	public PresetSelectionDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		setContentView(R.layout.dialog_color_presets);
	}

	public PresetSelectionDialog(Context context) {
		super(context);
		setContentView(R.layout.dialog_color_presets);
	}

}
