package com.libs.beheiri.ahmed.edittextlibjava;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ValidEditText extends android.support.v7.widget.AppCompatEditText {


    private boolean hasFallen = false;
    private ErrorPopup mPopup;
    private Drawables mDrawables;
    private CharSequence mError;
    private boolean mShowErrorAfterAttach;
    Drawable mErrorIcon;
    Drawable mErrorBackgroundAbove;
    Drawable mErrorBackground;
    private ValidEditText confirmPassword;
    private boolean required;
    int mErrorTextColor=0;
    private boolean mErrorWasChanged;
    Context mContext;
    int id;

    public ValidEditText(Context context) {
        super(context);
        init(context, null);
        mContext = context;
    }

    public ValidEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        mContext = context;
    }

    public ValidEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
        mContext = context;
    }

    private void init(final Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EditTextWithCustomError);


        if (a.hasValue(R.styleable.EditTextWithCustomError_ErrorDefaultIcon)) {
            mErrorIcon = a.getDrawable(R.styleable.EditTextWithCustomError_ErrorDefaultIcon);
        }
        if (mErrorIcon == null) {
            mErrorIcon=getResources().getDrawable(R.drawable.ic_error_red);
        }
        if (a.hasValue(R.styleable.EditTextWithCustomError_ErrorDefaultBackground)) {
            mErrorBackground = a.getDrawable(R.styleable.EditTextWithCustomError_ErrorDefaultBackground);
        }
        if (mErrorBackground == null) {
            mErrorBackground=getResources().getDrawable(R.drawable.error_text);
        }
        if (a.hasValue(R.styleable.EditTextWithCustomError_ErrorDefaultBackgroundAbove)) {
            mErrorBackgroundAbove = a.getDrawable(R.styleable.EditTextWithCustomError_ErrorDefaultBackgroundAbove);
        }
        if (mErrorBackgroundAbove == null) {
            mErrorBackgroundAbove=getResources().getDrawable(R.drawable.error_text);
        }
        if (a.hasValue(R.styleable.EditTextWithCustomError_ErrorTextColor)) {
            mErrorTextColor = a.getColor(R.styleable.EditTextWithCustomError_ErrorTextColor, 0);
        }
        if(mErrorTextColor==0){
            mErrorTextColor=getResources().getColor(android.R.color.white);
        }


        if (a.hasValue(R.styleable.EditTextWithCustomError_Required)) {
            setRequired(a.getBoolean(R.styleable.EditTextWithCustomError_Required, true));
        }else {
            setRequired(true);
        }
        if (a.hasValue(R.styleable.EditTextWithCustomError_ConfirmedTo)) {
             id = a.getResourceId(R.styleable.EditTextWithCustomError_ConfirmedTo, 0);

        }

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                doCheck();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ValidatorHelper.addEdittextWithInstance(this, context);
    }

    @Override
    public void setError(CharSequence error) {
        if (error == null) {
            setError(null, null);
        } else {
            Drawable dr = mErrorIcon;

            if (dr != null)
                dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
            setError(error, dr);
        }
    }

    private void setTheError(CharSequence error) throws IllegalArgumentException {
        error = TextUtils.stringOrSpannedString(error);
        mError = error;
//		Class<?> c = TextView.class;
//		Field f = c.getDeclaredField("mError");
//		f.setAccessible(true);
//		f.set(this, error);
    }

    private void setTheErrorWasChanged(boolean value) throws IllegalArgumentException {
        mErrorWasChanged = value;
//		Class<?> c = TextView.class;
//		Field f = c.getDeclaredField("mErrorWasChanged");
//		f.setAccessible(true);
//		f.setBoolean(this, value);
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        try {
            setTheError(error);
            setTheErrorWasChanged(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            hasFallen = true;
            //let us fallback
            super.setError(error, icon);
            return;
        }
        final Drawables dr = mDrawables;
        if (dr != null) {
            setCompoundDrawables(dr.mDrawableLeft, dr.mDrawableTop,
                    icon, dr.mDrawableBottom);
        } else {
            setCompoundDrawables(null, null, icon, null);
        }

        if (error == null) {
            if (mPopup != null) {
                if (mPopup.isShowing()) {
                    mPopup.dismiss();

                }

                mPopup = null;
            }
        } else {
            if (isFocused()) {
                showError();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!hasFallen && mShowErrorAfterAttach) {
            showError();
            mShowErrorAfterAttach = false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!hasFallen && mError != null) {
            hideError();
        }
    }

    private void hideError() {

        if (mPopup != null) {
            if (mPopup.isShowing()) {
                mPopup.dismiss();

            }
        }

        mShowErrorAfterAttach = false;

    }

    private void showError() {
        if (mError != null) {
            if (getWindowToken() == null) {
                mShowErrorAfterAttach = true;
                return;
            }

            if (mPopup == null) {
                final TextView err = new TextView(getContext());
                err.setTextColor(mErrorTextColor);
                err.setPadding(20, 15, 20, 15);
                final float scale = getResources().getDisplayMetrics().density;
                mPopup = new ErrorPopup(err, (int) (500 * scale + 1.5f),
                        (int) (150 * scale + 1.5f), mErrorBackground, mErrorBackgroundAbove);
                mPopup.setFocusable(false);
                // The user is entering text, so the input method is needed.  We
                // don't want the popup to be displayed on top of it.
                mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            }

            TextView tv = (TextView) mPopup.getContentView();
            chooseSize(mPopup, mError, tv);
            tv.setText(mError);
            Drawable dr = mErrorIcon;
            setCompoundDrawables(null, null, dr, null);
            mPopup.showAsDropDown(this, getErrorX(), getErrorY());
            mPopup.fixDirection(mPopup.isAboveAnchor());
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            doCheck();
        } else{
            hideError();
    }

}

    /**
     * Returns the Y offset to make the pointy top of the error point
     * at the middle of the error icon.
     */
    private int getErrorX() {
        /*
         * The "25" is the distance between the point and the right edge
         * of the background
         */
        final float scale = getResources().getDisplayMetrics().density;

        final Drawables dr = mDrawables;
        return getWidth() - mPopup.getWidth()
                - getPaddingRight()
                - (dr != null ? dr.mDrawableSizeRight : 0) / 2 + (int) (25 * scale + 0.5f);
    }

    /**
     * Returns the Y offset to make the pointy top of the error point
     * at the bottom of the error icon.
     */
    private int getErrorY() {
        /*
         * Compound, not extended, because the icon is not clipped
         * if the text height is smaller.
         */
        int vspace = getBottom() - getTop() -
                getCompoundPaddingBottom() - getCompoundPaddingTop();

        final Drawables dr = mDrawables;
        int icontop = getCompoundPaddingTop()
                + (vspace - (dr != null ? dr.mDrawableHeightRight : 0)) / 2;

        /*
         * The "2" is the distance between the point and the top edge
         * of the background.
         */

        return icontop + (dr != null ? dr.mDrawableHeightRight : 0)
                - getHeight() - 2;
    }

    private void chooseSize(PopupWindow pop, CharSequence text, TextView tv) {
        int wid = tv.getPaddingLeft() + tv.getPaddingRight();
        int ht = tv.getPaddingTop() + tv.getPaddingBottom();

        /*
         * Figure out how big the text would be if we laid it out to the
         * full width of this view minus the border.
         */
        int cap = getWidth() - wid;
        if (cap < 0) {
            cap = 200; // We must not be measured yet -- setFrame() will fix it.
        }

        Layout l = new StaticLayout(text, tv.getPaint(), cap,
                Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
        float max = 0;
        for (int i = 0; i < l.getLineCount(); i++) {
            max = Math.max(max, l.getLineWidth(i));
        }

        /*
         * Now set the popup size to be big enough for the text plus the border.
         */
        pop.setWidth(wid + (int) Math.ceil(max));
        pop.setHeight(ht + l.getHeight());
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean result = super.setFrame(l, t, r, b);

        if (!hasFallen && mPopup != null) {
            TextView tv = (TextView) mPopup.getContentView();
            chooseSize(mPopup, mError, tv);
            mPopup.update(this, getErrorX(), getErrorY(),
                    mPopup.getWidth(), mPopup.getHeight());
        }


        return result;
    }

    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawables(left, top, right, bottom);
        if (hasFallen)
            return;
        Drawables dr = mDrawables;

        final boolean drawables = left != null || top != null || right != null || bottom != null;

        if (!drawables) {
            // Clearing drawables...  can we free the data structure?
            if (dr != null) {
                if (dr.mDrawablePadding == 0) {
                    mDrawables = null;
                } else {
                    // We need to retain the last set padding, so just clear
                    // out all of the fields in the existing structure.
                    if (dr.mDrawableLeft != null) dr.mDrawableLeft.setCallback(null);
                    dr.mDrawableLeft = null;
                    if (dr.mDrawableTop != null) dr.mDrawableTop.setCallback(null);
                    dr.mDrawableTop = null;
                    if (dr.mDrawableRight != null) dr.mDrawableRight.setCallback(null);
                    dr.mDrawableRight = null;
                    if (dr.mDrawableBottom != null) dr.mDrawableBottom.setCallback(null);
                    dr.mDrawableBottom = null;
                    dr.mDrawableSizeLeft = dr.mDrawableHeightLeft = 0;
                    dr.mDrawableSizeRight = dr.mDrawableHeightRight = 0;
                    dr.mDrawableSizeTop = dr.mDrawableWidthTop = 0;
                    dr.mDrawableSizeBottom = dr.mDrawableWidthBottom = 0;
                }
            }
        } else {
            if (dr == null) {
                mDrawables = dr = new Drawables();
            }

            if (dr.mDrawableLeft != left && dr.mDrawableLeft != null) {
                dr.mDrawableLeft.setCallback(null);
            }
            dr.mDrawableLeft = left;

            if (dr.mDrawableTop != top && dr.mDrawableTop != null) {
                dr.mDrawableTop.setCallback(null);
            }
            dr.mDrawableTop = top;

            if (dr.mDrawableRight != right && dr.mDrawableRight != null) {
                dr.mDrawableRight.setCallback(null);
            }
            dr.mDrawableRight = right;

            if (dr.mDrawableBottom != bottom && dr.mDrawableBottom != null) {
                dr.mDrawableBottom.setCallback(null);
            }
            dr.mDrawableBottom = bottom;

            final Rect compoundRect = dr.mCompoundRect;
            int[] state;

            state = getDrawableState();

            if (left != null) {
                left.setState(state);
                left.copyBounds(compoundRect);
                left.setCallback(this);
                dr.mDrawableSizeLeft = compoundRect.width();
                dr.mDrawableHeightLeft = compoundRect.height();
            } else {
                dr.mDrawableSizeLeft = dr.mDrawableHeightLeft = 0;
            }

            if (right != null) {
                right.setState(state);
                right.copyBounds(compoundRect);
                right.setCallback(this);
                dr.mDrawableSizeRight = compoundRect.width();
                dr.mDrawableHeightRight = compoundRect.height();
            } else {
                dr.mDrawableSizeRight = dr.mDrawableHeightRight = 0;
            }

            if (top != null) {
                top.setState(state);
                top.copyBounds(compoundRect);
                top.setCallback(this);
                dr.mDrawableSizeTop = compoundRect.height();
                dr.mDrawableWidthTop = compoundRect.width();
            } else {
                dr.mDrawableSizeTop = dr.mDrawableWidthTop = 0;
            }

            if (bottom != null) {
                bottom.setState(state);
                bottom.copyBounds(compoundRect);
                bottom.setCallback(this);
                dr.mDrawableSizeBottom = compoundRect.height();
                dr.mDrawableWidthBottom = compoundRect.width();
            } else {
                dr.mDrawableSizeBottom = dr.mDrawableWidthBottom = 0;
            }
        }
        // I do not think this is needed anymore
        //		invalidate();
        //		requestLayout();
    }

    private static boolean isNullOrEmpty(String string) {
        return TextUtils.isEmpty(string);
    }

    private boolean isValidEmail(String string) {
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }


    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    private void doCheck(){
        if (getInputType() == (InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT)) {
            if (isNullOrEmpty(getText().toString()) && isRequired()) {
                setError(mContext.getString(R.string.this_field_empty));
            } else {
                if (!isValidEmail(getText().toString()) && isRequired()) {
                    setError(mContext.getString(R.string.invalid_email));
                } else {
                    hideError();
                    setCompoundDrawables(null, null,
                            null, null);
                }
            }
        } else if (getInputType() == (InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT)) {
            if (isNullOrEmpty(getText().toString()) && isRequired()) {
                setError(mContext.getString(R.string.this_field_empty));
            } else {
                if (id != 0) {
                    confirmPassword= getRootView().findViewById(id);
                    if (!getText().toString().equals(confirmPassword.getText().toString())) {
                        setError(mContext.getString(R.string.password_dont_match));
                    } else {

                        hideError();
                        setCompoundDrawables(null, null,
                                null, null);
                    }
                }else {
                    Log.d("CONFIRMPASSWORD", "null");
                    hideError();
                    setCompoundDrawables(null, null,
                            null, null);
                }
            }

        }else if(isNullOrEmpty(getText().toString()) && isRequired()) {
            setError(mContext.getString(R.string.this_field_empty));

        }else {
            hideError();
            setCompoundDrawables(null, null,
                    null, null);
        }
    }
}

