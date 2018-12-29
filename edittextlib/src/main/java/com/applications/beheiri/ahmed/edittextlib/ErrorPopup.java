package com.applications.beheiri.ahmed.edittextlib;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ErrorPopup extends PopupWindow {

        private boolean mAbove = false;
        private TextView mView;
    Drawable mErrorBackgroundAbove;
    Drawable mErrorBackground;
    Context context;

        ErrorPopup(TextView v, int width, int height,Drawable mErrorBackground,Drawable mErrorBackgroundAbove) {
            super(v, width, height);
            mView = v;
            this.mErrorBackground=mErrorBackground;
            this.mErrorBackgroundAbove=mErrorBackgroundAbove;
        }


        void fixDirection(boolean above) {
            mAbove = above;

            if (above) {
                    mView.setBackground(mErrorBackgroundAbove);
            } else {
                    mView.setBackground(mErrorBackground);

            }
        }

        @Override
        public void update(int x, int y, int w, int h, boolean force) {
            super.update(x, y, w, h, force);

            boolean above = isAboveAnchor();
            if (above != mAbove) {
                fixDirection(above);
            }
        }
    }

