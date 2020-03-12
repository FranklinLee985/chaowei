package com.eduhdsdk.tools;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 键盘控制器
 */
public class KeyBoardUtil {

	/**
	 * 显示键盘
	 * @param context
	 * @param et
	 */
	public static void showKeyBoard(Context context, EditText et) {
		if (context == null || et == null) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
		imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
	}

	/**
	 * 隐藏键盘
	 * @param context
	 * @param et
	 */
	public static void hideKeyBoard(Context context, EditText et) {
		if (context == null || et == null) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}

	public static boolean inputMethodIsActive(Context mContext) {
		return getInputMethodManager(mContext).isActive();
	}

	// 隐藏软键盘
	public static void hideInputMethod(Context mContext) {
		if (!(mContext instanceof Activity)) {
			return;
		}
		View token = ((Activity)mContext).getCurrentFocus();
		if (token == null) {
			return;
		}
		getInputMethodManager(mContext).hideSoftInputFromWindow(token.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static void showInputMethod(Context mContext) {
		if (!(mContext instanceof Activity)) {
			return;
		}
		View token = ((Activity)mContext).getCurrentFocus();
		if (token == null) {
			return;
		}
		getInputMethodManager(mContext).showSoftInput(token, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static InputMethodManager getInputMethodManager(Context mContext) {
		return (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	/**
	 * convert px to its equivalent dp
	 * 将px转换为与之相等的dp
	 */
	public static int px2dp(Context context, float pxValue) {
		final float scale =  context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dp转换为与之相等的px
	 */
	public static int dp2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

}
