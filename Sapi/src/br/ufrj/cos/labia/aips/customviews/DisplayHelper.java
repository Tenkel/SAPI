package br.ufrj.cos.labia.aips.customviews;

import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayHelper {
	
	private DisplayMetrics mMetrics;

	public DisplayHelper(Context context) {
		mMetrics = context.getResources().getDisplayMetrics();
	}
	
	public int dpToPx(int dp) {
	    return Math.round(dp * (mMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	}
	
	public int pxToDp(int px) {
	    return Math.round(px / (mMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
	}
	
}
