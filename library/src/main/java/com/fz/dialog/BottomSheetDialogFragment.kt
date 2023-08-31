package com.fz.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.fz.common.view.utils.animateIn
import com.fz.common.view.utils.animateOut
import com.fz.common.view.utils.setOnNoDoubleClickListener

/**
 * 适配安卓14底部弹出dialog
 * @author dingpeihua
 * @date 2023/7/25 15:13
 * @version 1.0
 */
abstract class BottomSheetDialogFragment : BaseBottomSheetDialogFragment() {
    private val COLOR_4D000000 = 0x4d000000
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            val params = attributes
            if (isPie) {
                params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            setWindowAnimations(R.style.DialogAnimation)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(if (isUpsideDownCake) COLOR_4D000000 else Color.TRANSPARENT))
        }
        return dialog
    }

    inline val isPie: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    inline val isUpsideDownCake: Boolean
        get() = Build.VERSION.SDK_INT >= 34

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogStyle
    }

    override fun dismissAllowingStateLoss() {
        if (isShowing) {
            animView.animateOut(true) {
                onAnimationEnd {
                    super.dismissAllowingStateLoss()
                }
            }
        }

    }

    abstract val animView: View
    open val animViewHeight: Int
        get() {
            var height = animView.measuredHeight.coerceAtLeast(animView.layoutParams.height)
            if (height == 0) {
                animView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                height = animView.measuredHeight
            }
            return height
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val touchOutside = dialog?.findViewById<View>(R.id.touch_outside)
        animView.y += animViewHeight
        touchOutside.setOnNoDoubleClickListener {
            dismissAllowingStateLoss()
        }
        animView.postDelayed({
            animView.animateIn(true)
        }, 100)
    }
}