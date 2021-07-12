package com.fz.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * 基础的Dialog（DialogFragment实现）
 *
 * @author dingpeihua
 * @date 2016/6/10
 * @since 1.0
 */
public abstract class BaseAlertDialogFragment extends BaseDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int theme = getDialogThemeRes();
        theme = (theme == 0 ? R.style.EightyFivePercentDialogStyle : theme);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(mContext, theme);
        Builder builder = new Builder(this, mContext, inflater);
        View view = build(builder).create();
        if (view != null) {
            adBuilder.setView(view);
        }
        if (!TextUtils.isEmpty(builder.mTitle)) {
            adBuilder.setTitle(builder.mTitle);
        }
        if (!TextUtils.isEmpty(builder.mMessage)) {
            adBuilder.setMessage(builder.mMessage);
        }
        if (mRequestCode != 0) {
            setRequestCode(mRequestCode);
        }
        if (view == null && builder.mItems != null && builder.mItems.length > 0) {
            if (builder.mCheckedItemIdx > -1) {//单选
                adBuilder.setSingleChoiceItems(builder.mItems, builder.mCheckedItemIdx, getOnClickListener(getOnClickListener(builder.onSingleChooseClickListener)));
            } else if (builder.mCheckedItems != null && builder.mCheckedItems.length > 0) {//多选
                adBuilder.setMultiChoiceItems(builder.mItems, builder.mCheckedItems, getOnMultiChoiceClickListener(builder.onMultiChoiceClickListener));
            } else {
                adBuilder.setItems(builder.mItems, getOnClickListener());
            }
        }
        if (!TextUtils.isEmpty(builder.mPositiveButtonText)) {
            adBuilder.setPositiveButton(builder.mPositiveButtonText, getOnClickListener(builder.onPositiveButtonClickListener));
        }
        if (!TextUtils.isEmpty(builder.mNegativeButtonText)) {
            adBuilder.setNegativeButton(builder.mNegativeButtonText, getOnClickListener(builder.onNegativeButtonClickListener));
        }
        if (!TextUtils.isEmpty(builder.mNeutralButtonText)) {
            adBuilder.setNeutralButton(builder.mNeutralButtonText, getOnClickListener(builder.onNeutralButtonClickListener));
        }
        adBuilder.setCancelable(builder.mCancelable);
        AlertDialog alertDialog = adBuilder.create();
        final Window window = alertDialog.getWindow();
        final WindowManager.LayoutParams wlp = window.getAttributes();
        if (builder.width == -1 || builder.width > 0) {
            wlp.width = builder.width;//设置宽度
        }
        if (builder.height == -1 || builder.height > 0) {
            wlp.height = builder.height;//设置高度
        }
        if (builder.gravity != -1) {
            wlp.gravity = builder.gravity;
        }
        int animation = getAnimationRes();
        if (animation != 0) {
            window.setWindowAnimations(animation);
        }
        if (builder.backgroundDrawable > 0) {
            window.setBackgroundDrawableResource(builder.backgroundDrawable);
        } else if (builder.backgroundColor != Integer.MAX_VALUE) {
            window.setBackgroundDrawable(new ColorDrawable(builder.backgroundColor));
        }
        window.setAttributes(wlp);
        alertDialog.setOnDismissListener(onDismissListener);
        alertDialog.setCanceledOnTouchOutside(builder.mCancelableOnTouchOutside);
        return alertDialog;
    }

    @CallSuper
    protected Builder build(Builder initialBuilder) {
        if (TextUtils.isEmpty(initialBuilder.mTitle)) {
            initialBuilder.setTitle(getTitle());
        }
        initialBuilder.setMessage(getMessage());
        if (TextUtils.isEmpty(initialBuilder.mPositiveButtonText)) {
            initialBuilder.setPositiveButton(getPositiveButtonText());
        }
        if (TextUtils.isEmpty(initialBuilder.mNegativeButtonText)) {
            initialBuilder.setNegativeButton(getNegativeButtonText());
        }
        if (TextUtils.isEmpty(initialBuilder.mNeutralButtonText)) {
            initialBuilder.setNeutralButton(getNeutralButtonText());
        }
        initialBuilder.setCancelable(isCancelable());
        initialBuilder.setCancelableOnTouchOutside(isCancelableOnTouchOutside());
        String[] items = getItems();
        if (items != null) {
            if (getSingleChoiceItem() > -1) {
                initialBuilder.setItems(items, getSingleChoiceItem());
            } else if (getMultiChoiceItems() != null) {
                initialBuilder.setItems(items, getMultiChoiceItems());
            } else {
                initialBuilder.setItems(items);
            }
        }
        return initialBuilder;
    }

    /**
     * Custom dialog builder
     */
    protected static class Builder {

        private final Context mContext;

        private final LayoutInflater mInflater;
        private final DialogFragment mDialogFragment;

        private CharSequence mTitle = null;

        private CharSequence mPositiveButtonText;

        private CharSequence mNegativeButtonText;

        private CharSequence mNeutralButtonText;

        private CharSequence mMessage;
        private boolean mCancelable = true;
        private boolean mCancelableOnTouchOutside = true;
        private View mView;
        private CharSequence[] mItems;
        //多选
        private boolean[] mCheckedItems;
        //单选
        private int mCheckedItemIdx = -1;
        private int gravity = -1;
        private int width;
        private int height;
        private int backgroundDrawable;
        private DialogInterface.OnClickListener onNeutralButtonClickListener;
        private DialogInterface.OnClickListener onPositiveButtonClickListener;
        private DialogInterface.OnClickListener onNegativeButtonClickListener;
        private DialogInterface.OnClickListener onSingleChooseClickListener;
        private DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener;
        @ColorInt
        private int backgroundColor = Integer.MAX_VALUE;

        public View inflater(@LayoutRes int layoutRes) {
            if (layoutRes == 0) {
                throw new IllegalArgumentException("layoutRes == 0");
            }
            mView = mInflater.inflate(layoutRes, null);
            return mView;
        }

        public View inflater(@LayoutRes int layoutRes, ViewGroup parent) {
            if (layoutRes == 0) {
                throw new IllegalArgumentException("layoutRes < 0");
            }
            return mInflater.inflate(layoutRes, parent, false);
        }

        public Builder(@NonNull DialogFragment dialogFragment, @NonNull Context context, @NonNull LayoutInflater inflater) {
            this.mDialogFragment = dialogFragment;
            this.mContext = context;
            this.mInflater = inflater;
        }

        public LayoutInflater getLayoutInflater() {
            return mInflater;
        }

        public Builder setTitle(@StringRes int titleId) {
            this.mTitle = mContext.getText(titleId);
            return this;
        }

        public Builder setTitle(@NonNull CharSequence title) {
            this.mTitle = title;
            return this;
        }

        public Builder setCancelable(boolean mCancelable) {
            this.mCancelable = mCancelable;
            return this;
        }

        public Builder setCancelableOnTouchOutside(boolean mCancelableOnTouchOutside) {
            this.mCancelableOnTouchOutside = mCancelableOnTouchOutside;
            return this;
        }

        public Builder setPositiveButton(@StringRes int textId) {
            mPositiveButtonText = mContext.getText(textId);
            return this;
        }

        public Builder setPositiveButton(@StringRes int textId, DialogInterface.OnClickListener onClickListener) {
            mPositiveButtonText = mContext.getText(textId);
            onPositiveButtonClickListener = onClickListener;
            return this;
        }

        public Builder setPositiveButton(@NonNull CharSequence text) {
            mPositiveButtonText = text;
            return this;
        }

        public Builder setPositiveButton(@NonNull CharSequence text, DialogInterface.OnClickListener onClickListener) {
            mPositiveButtonText = text;
            onPositiveButtonClickListener = onClickListener;
            return this;
        }

        public Builder setNegativeButton(@StringRes int textId, DialogInterface.OnClickListener onClickListener) {
            mNegativeButtonText = mContext.getText(textId);
            onNegativeButtonClickListener = onClickListener;
            return this;
        }

        public Builder setNegativeButton(@StringRes int textId) {
            mNegativeButtonText = mContext.getText(textId);
            return this;
        }

        public Builder setNegativeButton(@NonNull CharSequence text, DialogInterface.OnClickListener onClickListener) {
            mNegativeButtonText = text;
            onNegativeButtonClickListener = onClickListener;
            return this;
        }

        public Builder setNegativeButton(@NonNull CharSequence text) {
            mNegativeButtonText = text;
            return this;
        }

        public Builder setNeutralButton(@StringRes int textId, DialogInterface.OnClickListener onClickListener) {
            mNeutralButtonText = mContext.getText(textId);
            onNeutralButtonClickListener = onClickListener;
            return this;
        }

        public Builder setNeutralButton(@StringRes int textId) {
            mNeutralButtonText = mContext.getText(textId);
            return this;
        }

        public Builder setNeutralButton(@NonNull CharSequence text, DialogInterface.OnClickListener onClickListener) {
            mNeutralButtonText = text;
            onNeutralButtonClickListener = onClickListener;
            return this;
        }

        public Builder setNeutralButton(@NonNull CharSequence text) {
            mNeutralButtonText = text;
            return this;
        }

        public Builder setMessage(@StringRes int messageId) {
            mMessage = mContext.getText(messageId);
            return this;
        }

        public Builder setMessage(@NonNull CharSequence message) {
            mMessage = message;
            return this;
        }

        /**
         * Set list
         *
         * @param checkedItemIdx Item check by default, -1 if no item should be checked
         */
        public Builder setItems(@NonNull CharSequence[] mItems, int checkedItemIdx) {
            this.mItems = mItems;
            this.mCheckedItemIdx = checkedItemIdx;
            return this;
        }

        /**
         * Set list
         *
         * @param checkedItemIdx Item check by default, -1 if no item should be checked
         */
        public Builder setItems(@NonNull CharSequence[] mItems, int checkedItemIdx, DialogInterface.OnClickListener onClickListener) {
            this.mItems = mItems;
            this.mCheckedItemIdx = checkedItemIdx;
            this.onSingleChooseClickListener = onClickListener;
            return this;
        }

        public Builder setItems(@NonNull CharSequence[] mItems, boolean[] mCheckedItems) {
            this.mItems = mItems;
            this.mCheckedItems = mCheckedItems;
            return this;
        }

        public Builder setItems(@NonNull CharSequence[] mItems, boolean[] mCheckedItems, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
            this.mItems = mItems;
            this.mCheckedItems = mCheckedItems;
            this.onMultiChoiceClickListener = onMultiChoiceClickListener;
            return this;
        }

        public Builder setItems(@NonNull CharSequence[] mItems) {
            this.mItems = mItems;
            return this;
        }

        public Builder setView(@NonNull View view) {
            mView = view;
            return this;
        }

        public Builder setView(@LayoutRes int viewRes) {
            mView = getLayoutInflater().inflate(viewRes, null);
            return this;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setBackgroundDrawable(int backgroundDrawable) {
            this.backgroundDrawable = backgroundDrawable;
            return this;
        }

        public Builder setBackgroundColor(@ColorInt int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        @NonNull
        public View create() {
            return mView;
        }
    }
}