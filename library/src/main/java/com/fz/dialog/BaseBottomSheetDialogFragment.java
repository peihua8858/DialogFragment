package com.fz.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fz.toast.ToastCompat;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.socks.library.KLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {
    /**
     * 请求码 {@link Activity#onActivityResult(int, int, Intent)}
     */
    protected static final String KEY_REQUEST_CODE = "REQUEST_CODE";
    /**
     * 弹窗提示消息
     */
    protected static final String KEY_TEXT_MESSAGE = "TEXT_MESSAGE";
    /**
     * 弹窗标题
     */
    protected static final String KEY_TEXT_TITLE = "TEXT_TITLE";
    /**
     * 弹窗确定按钮文案
     */
    protected static final String KEY_TEXT_POSITIVE_BUTTON = "TEXT_POSITIVE_BUTTON";
    /**
     * 弹窗取消按钮文案
     */
    protected static final String KEY_TEXT_NEGATIVE_BUTTON = "TEXT_NEGATIVE_BUTTON";
    /**
     * 弹窗中性按钮文案
     */
    protected static final String KEY_TEXT_NEUTRAL_BUTTON = "TEXT_NEUTRAL_BUTTON";
    /**
     * 弹窗多选列表数据
     */
    protected static final String KEY_MULTI_CHOICE_ITEMS = "MULTI_CHOICE_ITEMS";
    /**
     * 弹窗单选列表数据
     */
    protected static final String KEY_SINGLE_CHOICE_ITEM = "SINGLE_CHOICE_ITEM";
    protected static final String KEY_LIST_ITEMS = "LIST_ITEMS";
    /**
     * 弹窗是否可取消
     */
    protected static final String KEY_CANCELABLE = "CANCELABLE";
    /**
     * 设置此对话框是否在窗口的边界之外点击被取消。 如果设置为true，该对话框设置如果尚未设置为取消
     */
    protected static final String KEY_CANCELABLE_ON_TOUCH_OUTSIDE = "CANCELABLE_ON_TOUCH_OUTSIDE ";
    /**
     * 对话框进出场动画
     */
    protected static final String KEY_DIALOG_ANIMATION = "DIALOG_ANIMATION";
    /**
     * 对话框主题
     */
    protected static final String KEY_DIALOG_THEME = "DIALOG_THEME";

    protected int mRequestCode;
    protected Context mContext;
    protected Activity mActivity;
    protected DialogInterface.OnDismissListener onDismissListener;
    private int backStackId = -1;
    /**
     * 顶部向下偏移量
     */
    protected BottomSheetBehavior mBehavior;
    private float bottomSheetSlideOffset = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = (Activity) context;
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            mRequestCode = getTargetRequestCode();
        } else {
            Bundle args = getArguments();
            if (args != null) {
                mRequestCode = args.getInt(KEY_REQUEST_CODE, 0);
            }
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        int animation = getAnimationRes();
        if (window != null) {
            window.setWindowAnimations(animation == 0 ? R.style.BottomInDialogAnimation : animation);
        }
        dialog.setOnDismissListener(onDismissListener);
        dialog.setCancelable(isCancelable());
        dialog.setCanceledOnTouchOutside(isCancelableOnTouchOutside());
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = getDialogHeight();
        layoutParams.height = height;
        view.setLayoutParams(layoutParams);
        mBehavior.setPeekHeight(0);
        mBehavior.addBottomSheetCallback(getBottomSheetCallback());
        mBehavior.setMaxHeight(height);
    }

    protected BottomSheetBehavior.BottomSheetCallback getBottomSheetCallback() {
        return new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                final float offsetSlop = 0.2f;
                if (bottomSheetSlideOffset > slideOffset && slideOffset < offsetSlop
                        && mBehavior.getState() != BottomSheetBehavior.STATE_DRAGGING) {
                    // 当是向下滑动 && 滑动偏移小于0.2 && 手指离开屏幕时直接关闭弹窗，避免出现 STATE_COLLAPSED 折叠状态
                    dismissAllowingStateLoss();
                }
                bottomSheetSlideOffset = slideOffset;
            }
        };
    }

    protected int getDialogHeight() {
        return getDefaultHeight();
    }


    private int getDefaultHeight() {
        return (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
    }

    public final void setRequestCode(int mRequestCode) {
        this.mRequestCode = mRequestCode;
    }

    @StyleRes
    public int getAnimationRes() {
        return checkBundle(KEY_DIALOG_ANIMATION, 0);
    }

    @StyleRes
    public int getDialogThemeRes() {
        return checkBundle(KEY_DIALOG_THEME, 0);
    }

    @Override
    public int getTheme() {
        int theme = getDialogThemeRes();
        theme = (theme == 0 ? super.getTheme() : theme);
        return theme;
    }

    protected CharSequence getMessage() {
        return checkBundle(KEY_TEXT_MESSAGE, "");
    }

    protected String getTitle() {
        return checkBundle(KEY_TEXT_TITLE, "");
    }

    protected String getPositiveButtonText() {
        return checkBundle(KEY_TEXT_POSITIVE_BUTTON, "");
    }

    protected String getNegativeButtonText() {
        return checkBundle(KEY_TEXT_NEGATIVE_BUTTON, "");
    }

    protected String getNeutralButtonText() {
        return checkBundle(KEY_TEXT_NEUTRAL_BUTTON, "");
    }

    protected boolean[] getMultiChoiceItems() {
        return checkBundle(KEY_MULTI_CHOICE_ITEMS, null);
    }

    protected int getSingleChoiceItem() {
        return checkBundle(KEY_SINGLE_CHOICE_ITEM, -1);
    }

    @Override
    public boolean isCancelable() {
        Bundle bundle = getBundle();
        if (bundle.containsKey(KEY_CANCELABLE)) {
            return checkBundle(bundle, KEY_CANCELABLE, true);
        }
        return super.isCancelable();
    }

    protected boolean isCancelableOnTouchOutside() {
        return checkBundle(KEY_CANCELABLE_ON_TOUCH_OUTSIDE, true);
    }

    protected String[] getItems() {
        return checkBundle(KEY_LIST_ITEMS, null);
    }

    protected final <T> T checkBundle(String key, T defaultValue) {
        return checkBundle(getBundle(), key, defaultValue);
    }

    protected final <T> T checkBundle(Bundle bundle, String key, T defaultValue) {
        T value = (T) getBundle().get(key);
        return value == null ? defaultValue : value;
    }

    /**
     * 获取Bundle对象
     *
     * @author dingpeihua
     * @date 2019/12/11 11:09
     * @version 1.0
     */
    @NonNull
    protected final Bundle getBundle() {
        Bundle bundle;
        return (bundle = getArguments()) != null ? bundle : new Bundle();
    }

    protected final DialogInterface.OnClickListener getOnClickListener(DialogInterface.OnClickListener listener) {
        if (listener == null) {
            return getOnClickListener();
        }
        return listener;
    }

    protected DialogInterface.OnClickListener getOnClickListener() {
        return getDialogListener(DialogInterface.OnClickListener.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mContext = null;
    }

    /**
     * 获取dialog监听器
     *
     * @param listenerInterface 监听器类型
     * @author dingpeihua
     * @date 2016/9/18 15:11
     * @version 1.0
     */
    @Nullable
    protected final <T> T getDialogListener(Class<T> listenerInterface) {
        final Fragment targetFragment = getTargetFragment();
        if (targetFragment != null && listenerInterface.isAssignableFrom(targetFragment.getClass())) {
            return (T) targetFragment;
        }
        if (getActivity() != null && listenerInterface.isAssignableFrom(getActivity().getClass())) {
            return (T) getActivity();
        }
        return null;
    }

    protected final DialogInterface.OnMultiChoiceClickListener getOnMultiChoiceClickListener() {
        return getDialogListener(DialogInterface.OnMultiChoiceClickListener.class);
    }

    protected final DialogInterface.OnMultiChoiceClickListener getOnMultiChoiceClickListener(DialogInterface.OnMultiChoiceClickListener listener) {
        if (listener == null) {
            return getOnMultiChoiceClickListener();
        }
        return listener;
    }

    @CallSuper
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @CallSuper
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    public final boolean isShowing() {
        Dialog dialog = getDialog();
        return !isRemoving() && dialog != null && dialog.isShowing();
    }

    @CallSuper
    @Override
    public void dismiss() {
        // 捕获异常不至于造成崩溃
        dismissAllowingStateLoss();
    }

    @CallSuper
    @Override
    public void dismissAllowingStateLoss() {
        // 捕获异常不至于造成崩溃
        try {
            super.dismissAllowingStateLoss();
            if (onDismissListener != null) {
                onDismissListener.onDismiss(getDialog());
            }
        } catch (Exception e) {
            KLog.e("message:" + e.getMessage());
        }
    }


    @Override
    public final void show(FragmentManager manager, String tag) {
        try {
            showAllowingStateLoss(manager, tag);
        } catch (Exception e) {
            KLog.d("message", e.getMessage());
        }
    }


    @Override
    public final int show(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        try {
            setFieldValue(this, "mDismissed", false);
            setFieldValue(this, "mShownByMe", true);
            transaction.remove(this);
            transaction.add(this, tag);
            backStackId = transaction.commitAllowingStateLoss();
            setFieldValue(this, "mBackStackId", backStackId);
            return backStackId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 显示dialogfragment ，规避Can not perform this action after onSaveInstanceState 程序奔溃的问题；
     * 增加isAdded判断防止Fragment already added错误
     *
     * @author dingpeihua
     * @date 2016/4/7 9:45
     * @version 1.0
     */
    public final void showAllowingStateLoss(FragmentManager manager, String tag) {
        show(manager.beginTransaction(), tag);
    }

    /**
     * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @param value     : 将要设置的值
     */
    void setFieldValue(Object object, String fieldName, Object value) {
        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
        Field field = getDeclaredField(object, fieldName);
        try {
            if (field != null) {
                //抑制Java对其的检查
                field.setAccessible(true);
                //将 object 中 field 所代表的值 设置为 value
                field.set(object, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 反射获取对象的私有属性
     *
     * @param object    需要反射的对象
     * @param fieldName 属性名称
     * @return 返回当前对象的私有属性
     * @author dingpeihua
     * @date 2016/5/5 8:51
     * @version 1.0
     */
    Field getDeclaredField(Object object, String fieldName) {
        Field field = null;
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * Utility method for acquiring all listeners of some type for current instance of DialogFragment
     *
     * @param listenerInterface Interface of the desired listeners
     * @return Unmodifiable list of listeners
     * @since 1.2.0
     */
    @SuppressWarnings("unchecked")
    protected final <T> List<T> getDialogListeners(Class<T> listenerInterface) {
        final Fragment targetFragment = getTargetFragment();
        List<T> listeners = new ArrayList<>(2);
        if (targetFragment != null && listenerInterface.isAssignableFrom(targetFragment.getClass())) {
            listeners.add((T) targetFragment);
        }
        if (getActivity() != null && listenerInterface.isAssignableFrom(getActivity().getClass())) {
            listeners.add((T) getActivity());
        }
        return Collections.unmodifiableList(listeners);
    }

    public void showToast(@NonNull String message) {
        ToastCompat.makeText(mContext).setText(message).show();
    }

    public void showLongToast(@NonNull String message) {
        ToastCompat.makeText(mContext).setText(message).setDuration(ToastCompat.LENGTH_LONG).show();
    }

    public void showToast(@NonNull String message, int duration) {
        ToastCompat.makeText(mContext).setText(message).setDuration(duration).show();
    }

    public void showToast(@StringRes int resId) {
        ToastCompat.makeText(mContext).setText(resId).show();
    }

    public void showLongToast(@StringRes int resId) {
        ToastCompat.makeText(mContext).setText(resId).show();
    }

    public void showToast(@StringRes int resId, int duration) {
        ToastCompat.makeText(mContext).setText(resId).setDuration(duration).show();
    }

    /**
     * 解析当前上下文主题，获取主题样式
     *
     * @param context    当前上下文
     * @param resId      资源ID
     * @param defaultRes 默认主题样式
     * @author dingpeihua
     * @date 2020/7/7 10:31
     * @version 1.0
     */
    protected static int resolveAttribute(@NonNull Context context, int resId, @StyleRes int defaultRes) {
        int resourceId = resolveAttribute(context, resId);
        if (resourceId != 0) {
            return resourceId;
        }
        return defaultRes;
    }

    /**
     * 解析当前上下文主题，获取主题样式
     *
     * @param context 当前上下文
     * @param resId   资源ID
     * @author dingpeihua
     * @date 2020/7/7 10:31
     * @version 1.0
     */
    protected static int resolveAttribute(@NonNull Context context, int resId) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(resId, outValue, true);
        return outValue.resourceId;
    }

    protected final ActivityResultContract<Intent, ActivityResult> intentResultContract = new ActivityResultContract<Intent, ActivityResult>() {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Intent input) {
            return input;
        }

        @Override
        public ActivityResult parseResult(int resultCode, @Nullable Intent intent) {
            return new ActivityResult(resultCode, intent);
        }
    };

    protected ActivityResultLauncher<Intent> registerForActivityResult(ActivityResultCallback<ActivityResult> callback) {
        return registerForActivityResult(intentResultContract, callback);
    }

    protected <T extends FragmentActivity> ActivityResultLauncher<Void> registerForActivityResult(Class<T> clazz, ActivityResultCallback<ActivityResult> callback) {
        return registerForActivityResult(new ActivityResultContract<Void, ActivityResult>() {

            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, Void input) {
                return new Intent(context, clazz);
            }

            @Override
            public ActivityResult parseResult(int resultCode, @Nullable Intent intent) {
                return new ActivityResult(resultCode, intent);
            }

        }, callback);
    }
}
