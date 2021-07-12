package com.fz.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;

import android.text.Html;
import android.text.SpannedString;
import android.text.TextUtils;

/**
 * 构建dialog弹窗，为所有对话框片段构建器保存公共值的内部基本构建器。
 *
 * @param <FRAGMENT>      {@link DialogFragment}
 * @param <BUILDER>{@link BaseDialogBuilder}
 * @author dingpeihua
 * @version 1.0
 * @date 2017/11/8 10:41
 */
public abstract class BaseDialogBuilder<FRAGMENT extends DialogFragment, BUILDER extends BaseDialogBuilder<FRAGMENT, BUILDER>> {

    public final static String DEFAULT_TAG = "simple_dialog";
    public final static int DEFAULT_REQUEST_CODE = -42;

    protected final Context mContext;
    protected final FragmentManager mFragmentManager;
    protected final Class<FRAGMENT> mClass;

    private Fragment mTargetFragment;
    private boolean mCancelable = true;
    private boolean mCancelableOnTouchOutside = true;

    private String mTag = DEFAULT_TAG;
    private int mRequestCode = DEFAULT_REQUEST_CODE;

    private CharSequence mTitle;
    private CharSequence mMessage;
    private CharSequence mPositiveButtonText;
    private CharSequence mNegativeButtonText;
    private CharSequence mNeutralButtonText;

    private CharSequence[] items;
    //多选
    private boolean[] mMultiChoiceItems;
    //单选
    private int mCheckedItemIdx = -1;
    @StyleRes
    private int mDialogTheme = 0;
    @StyleRes
    private int mDialogAnimation = 0;
    protected DialogInterface.OnDismissListener onDismissListener;

    public BaseDialogBuilder(@NonNull Context context, @NonNull FragmentManager fragmentManager, @NonNull Class<FRAGMENT> clazz) {
        mFragmentManager = fragmentManager;
        mContext = context;
        mClass = clazz;
    }

    @NonNull
    protected abstract BUILDER self();

    /**
     * @author dingpeihua
     * @date 2016/9/18 12:06
     * @version 1.0
     */
    @NonNull
    protected abstract Bundle prepareArguments();

    public final BUILDER setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        return self();
    }

    public final BUILDER setCancelableOnTouchOutside(boolean cancelable) {
        mCancelableOnTouchOutside = cancelable;
        if (cancelable) {
            mCancelable = cancelable;
        }
        return self();
    }

    public final BUILDER setTargetFragment(@NonNull Fragment fragment, int requestCode) {
        mTargetFragment = fragment;
        mRequestCode = requestCode;
        return self();
    }

    public final BUILDER setTargetFragment(@NonNull Fragment fragment) {
        mTargetFragment = fragment;
        mRequestCode = DEFAULT_REQUEST_CODE;
        return self();
    }

    public final BUILDER setRequestCode(int requestCode) {
        mRequestCode = requestCode;
        return self();
    }

    public final BUILDER setTag(String tag) {
        mTag = tag;
        return self();
    }

    public final BUILDER setTitle(@NonNull CharSequence mTitle) {
        this.mTitle = mTitle;
        return self();
    }

    public final BUILDER setTitle(@StringRes int titleId) {
        this.mTitle = mContext.getString(titleId);
        return self();
    }

    public final BUILDER setMessage(@NonNull CharSequence mMessage) {
        this.mMessage = mMessage;
        return self();
    }

    /**
     * Allow to set resource string with HTML formatting and bind %s,%i.
     * This is workaround for https://code.google.com/p/android/issues/detail?id=2923
     */
    public BUILDER setMessage(int resourceId, Object... formatArgs) {
        mMessage = Html.fromHtml(String.format(Html.toHtml(new SpannedString(mContext.getText(resourceId))), formatArgs));
        return self();
    }

    public final BUILDER setMessage(@StringRes int messageId) {
        this.mMessage = mContext.getString(messageId);
        return self();
    }

    public final BUILDER setPositiveButtonText(@NonNull CharSequence mPositiveButtonText) {
        this.mPositiveButtonText = mPositiveButtonText;
        return self();
    }

    public final BUILDER setPositiveButtonText(@StringRes int positiveButtonId) {
        this.mPositiveButtonText = mContext.getString(positiveButtonId);
        return self();
    }

    public final BUILDER setNegativeButtonText(@NonNull CharSequence mNegativeButtonText) {
        this.mNegativeButtonText = mNegativeButtonText;
        return self();
    }

    public final BUILDER setNegativeButtonText(@StringRes int negativeButtonId) {
        this.mNegativeButtonText = mContext.getString(negativeButtonId);
        return self();
    }

    public final BUILDER setNeutralButtonText(@NonNull CharSequence mNeutralButtonText) {
        this.mNeutralButtonText = mNeutralButtonText;
        return self();
    }

    public final BUILDER setNeutralButtonText(@StringRes int neutralButtonId) {
        this.mNeutralButtonText = mContext.getString(neutralButtonId);
        return self();
    }

    public final BUILDER setMultiChoiceItems(boolean... mMultiChoiceItems) {
        this.mMultiChoiceItems = mMultiChoiceItems;
        return self();
    }

    public final BUILDER setCheckedItemIdx(int mCheckedItemIdx) {
        this.mCheckedItemIdx = mCheckedItemIdx;
        return self();
    }

    public final BUILDER setDialogThemeRes(@StyleRes int themeRes) {
        this.mDialogTheme = themeRes;
        return self();
    }

    public final BUILDER setDialogAnimationRes(@StyleRes int animationRes) {
        this.mDialogAnimation = animationRes;
        return self();
    }

    public final BUILDER setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return self();
    }

    public final FRAGMENT create() {
        Bundle args = prepareArguments();
        if (args == null) {
            args = new Bundle();
        }
        FragmentFactory factory = mFragmentManager.getFragmentFactory();
        final FRAGMENT fragment = (FRAGMENT) factory.instantiate(mClass.getClassLoader(), mClass.getName());
        if (mTargetFragment != null) {
            fragment.setTargetFragment(mTargetFragment, mRequestCode);
        } else {
            args.putInt(BaseDialogFragment.KEY_REQUEST_CODE, mRequestCode);
        }
        if (!TextUtils.isEmpty(mTitle)) {
            args.putCharSequence(BaseDialogFragment.KEY_TEXT_TITLE, mTitle);
        }
        if (!TextUtils.isEmpty(mMessage)) {
            args.putCharSequence(BaseDialogFragment.KEY_TEXT_MESSAGE, mMessage);
        }
        if (!TextUtils.isEmpty(mPositiveButtonText)) {
            args.putCharSequence(BaseDialogFragment.KEY_TEXT_POSITIVE_BUTTON, mPositiveButtonText);
        }
        if (!TextUtils.isEmpty(mNegativeButtonText)) {
            args.putCharSequence(BaseDialogFragment.KEY_TEXT_NEGATIVE_BUTTON, mNegativeButtonText);
        }
        if (!TextUtils.isEmpty(mNeutralButtonText)) {
            args.putCharSequence(BaseDialogFragment.KEY_TEXT_NEUTRAL_BUTTON, mNeutralButtonText);
        }
        if (mCheckedItemIdx != -1) {
            args.putInt(BaseDialogFragment.KEY_SINGLE_CHOICE_ITEM, mCheckedItemIdx);
        }
        if (mMultiChoiceItems != null) {
            args.putBooleanArray(BaseDialogFragment.KEY_MULTI_CHOICE_ITEMS, mMultiChoiceItems);
        }
        args.putBoolean(BaseDialogFragment.KEY_CANCELABLE, mCancelable);
        args.putBoolean(BaseDialogFragment.KEY_CANCELABLE_ON_TOUCH_OUTSIDE, mCancelableOnTouchOutside);
        fragment.setCancelable(mCancelable);
        if (mDialogTheme != 0) {
            args.putInt(BaseDialogFragment.KEY_DIALOG_THEME, mDialogTheme);
        }
        if (mDialogAnimation != 0) {
            args.putInt(BaseDialogFragment.KEY_DIALOG_ANIMATION, mDialogAnimation);
        }
        if (onDismissListener != null && fragment instanceof BaseDialogFragment) {
            ((BaseDialogFragment) fragment).setOnDismissListener(onDismissListener);
        }
        fragment.setArguments(args);
        return fragment;
    }

    public FRAGMENT show() {
        return show(create());
    }

    protected FRAGMENT show(@NonNull FRAGMENT fragment) {
        fragment.show(mFragmentManager, mTag);
        return fragment;
    }
}
