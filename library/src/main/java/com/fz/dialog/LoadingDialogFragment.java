package com.fz.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * 加载中...
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2017/11/8 10:42
 */
public class LoadingDialogFragment extends BaseDialogFragment {

    private Boolean mCancelable = null;
    private Boolean mCanceledOnTouchOutside = null;
    private boolean mOnBackCancelable = true;
    private boolean isFinishActivity = false;
    private int mDialogThemeRes = 0;
    public static final String EXTRA_BACK_CANCELABLE = "BACK_CANCELABLE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        mCanceledOnTouchOutside = cancel;
    }

    public void setDialogThemeRes(int mDialogThemeRes) {
        this.mDialogThemeRes = mDialogThemeRes;
    }

    @Override
    public int getDialogThemeRes() {
        if (mDialogThemeRes == 0) {
            return super.getDialogThemeRes();
        }
        return mDialogThemeRes;
    }

    @Override
    public void setCancelable(boolean cancel) {
        super.setCancelable(cancel);
        mCancelable = cancel;
    }

    public void setCanceledOnBackPressed(boolean onBackCancelable) {
        this.mOnBackCancelable = onBackCancelable;
    }

    /**
     * 按下返回按钮是否关闭当前activity
     *
     * @param finishActivity true 按下返回按钮关闭当前activity，否则不关闭
     * @author dingpeihua
     * @date 2019/12/9 11:34
     * @version 1.0
     */
    public void setFinishActivity(boolean finishActivity) {
        isFinishActivity = finishActivity;
    }

    @Override
    protected boolean isCancelableOnTouchOutside() {
        if (mCanceledOnTouchOutside == null) {
            return super.isCancelableOnTouchOutside();
        }
        return mCanceledOnTouchOutside;
    }

    @Override
    public boolean isCancelable() {
        if (mCancelable == null) {
            return super.isCancelable();
        }
        return mCancelable;
    }

    public boolean isOnBackCancelable() {
        Bundle bundle = getBundle();
        if (bundle.containsKey(EXTRA_BACK_CANCELABLE)) {
            return checkBundle(bundle, EXTRA_BACK_CANCELABLE, true);
        }
        return mOnBackCancelable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_loading, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        if (activity != null) {
            int resourceId = getDialogThemeRes();
            if (resourceId == 0) {
                resourceId = resolveDialogTheme(activity, R.style.DefaultLoadingDialogTheme);
            }
            Dialog dialog = new Dialog(activity, resourceId);
            dialog.setCancelable(isCancelable());
            dialog.setCanceledOnTouchOutside(isCancelableOnTouchOutside());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setOnKeyListener((dialog1, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Activity activity1 = getActivity();
                    if (isFinishActivity && activity1 != null) {
                        activity1.finish();
                        return true;
                    }
                    return !isOnBackCancelable();
                }
                return false;
            });
            Window window = dialog.getWindow();
            if (window != null) {
                int animation = getAnimationRes();
                if (animation != 0) {
                    window.setWindowAnimations(animation);
                }
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            return dialog;
        }
        return super.onCreateDialog(savedInstanceState);
    }

    public static LoadingDialogFragmentBuilder createBuilder(Context context) {
        if (!(context instanceof FragmentActivity)) {
            throw new IllegalArgumentException("Context must be extends FragmentActivity");
        }
        return createBuilder(context, ((FragmentActivity) context).getSupportFragmentManager());
    }

    public static LoadingDialogFragmentBuilder createBuilder(Context context, FragmentManager fragmentManager) {
        return new LoadingDialogFragmentBuilder(context, fragmentManager, LoadingDialogFragment.class);
    }

    public final static class LoadingDialogFragmentBuilder extends BaseDialogBuilder<LoadingDialogFragment,
            LoadingDialogFragmentBuilder> {
        private boolean onBackCancelable = true;

        public LoadingDialogFragmentBuilder(@NonNull Context context, @NonNull FragmentManager fragmentManager,
                                            @NonNull Class<LoadingDialogFragment> clazz) {
            super(context, fragmentManager, clazz);
        }

        public LoadingDialogFragmentBuilder setOnBackCancelable(boolean mOnBackCancelable) {
            this.onBackCancelable = mOnBackCancelable;
            return self();
        }

        @NonNull
        @Override
        protected LoadingDialogFragmentBuilder self() {
            return this;
        }

        @NonNull
        @Override
        protected Bundle prepareArguments() {
            Bundle bundle = new Bundle();
            bundle.putBoolean(EXTRA_BACK_CANCELABLE, onBackCancelable);
            return bundle;
        }
    }
    /**
     * 解析当前上下文主题，获取对话框主题样式
     *
     * @param context    当前上下文
     * @param defaultRes 默认主题样式
     * @author dingpeihua
     * @date 2020/7/7 10:31
     * @version 1.0
     */
    static int resolveDialogTheme(@NonNull Context context, @StyleRes int defaultRes) {
        return resolveAttribute(context, R.attr.LoadingDialogTheme, defaultRes);
    }
}
