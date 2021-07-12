package com.fz.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * 从底部弹出显示的弹窗
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2020/6/18 14:25
 */
public class BottomDialogFragment extends BaseDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnDismissListener(onDismissListener);
        setWindowAttributes(dialog);
        return dialog;
    }

    private void setWindowAttributes(Dialog alertDialog) {
        if (alertDialog != null) {
            Window window = alertDialog.getWindow();
            if (window != null) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                int animation = getAnimationRes();
                animation = (animation == 0 ? R.style.BottomInDialogAnimation : animation);
                window.setWindowAnimations(animation);
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.BOTTOM;
                window.getDecorView().setPadding(0, 0, 0, 0);
                wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wlp.horizontalMargin = 0f;
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setAttributes(wlp);
            }
        }
    }
}
