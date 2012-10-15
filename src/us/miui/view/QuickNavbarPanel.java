/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.miui.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.internal.statusbar.IStatusBarService;

import java.io.IOException;
import java.util.Calendar;

import us.miui.toolbox.R;
import us.miui.toolbox.RootUtils;


public class QuickNavbarPanel extends FrameLayout implements OverlayPanel, PieControl.OnNavButtonPressedListener {
    private static final boolean DEBUG = true;
    static QuickNavbarPanel mQuickNavbarPanel;
    private Handler mHandler;
    boolean mShowing;
    private PieControl mPieControl;
    private int mInjectKeycode;
    private long mDownTime;
    private Context mContext;
    private boolean mHideOnPress = false;
    private boolean mLongpressKillsApp = false;
    long mHideTime;
    boolean mAutoHide;

    ViewGroup mContentFrame;
    Rect mContentArea = new Rect();

    public QuickNavbarPanel(Context context) {
        this(context, null);
    }

    public QuickNavbarPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPieControl = new PieControl(context);
        mPieControl.setOnNavButtonPressedListener(this);
        mHandler = new Handler();
        mQuickNavbarPanel = this;
        SharedPreferences prefs = context.getSharedPreferences("us.miui.toolbox_preferences", 0);
        mAutoHide = prefs.getBoolean("pref_key_autohide", false);
        mHideTime = Long.parseLong(prefs.getString("pref_key_autohide_time", "5000"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        updateAutoHideTimer();
        return mPieControl.onTouchEvent(event);
        //return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onAttachedToWindow () {
        super.onAttachedToWindow();
    }

    public void setHandler(Handler h) {
        mHandler = h;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        mContentFrame = (ViewGroup)findViewById(R.id.content_frame);
        setWillNotDraw(false);
        mShowing = false;
        mPieControl.attachToContainer(this);
        mPieControl.forceToTop(this);
    }

    /**
     * Whether the panel is showing, or, if it's animating, whether it will be
     * when the animation is done.
     */
    public boolean isShowing() {
        return mShowing;
    }

    public void show(boolean show, boolean animate) {
        mShowing = show;
        setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            mPieControl.setCenter(this.getWidth() / 2, this.getHeight());
        }

        if (mAutoHide) {
            if (show)
                updateAutoHideTimer();
            else
                cancelAutoHideTimer();
        }

        mPieControl.show(show);
    }

    public boolean isInContentArea(int x, int y) {
        mContentArea.left = mContentFrame.getLeft() + mContentFrame.getPaddingLeft();
        mContentArea.top = mContentFrame.getTop() + mContentFrame.getPaddingTop();
        mContentArea.right = mContentFrame.getRight() - mContentFrame.getPaddingRight();
        mContentArea.bottom = mContentFrame.getBottom() - mContentFrame.getPaddingBottom();

        return mContentArea.contains(x, y);
    }

    public void setHideOnPress(boolean hide) {
        mHideOnPress = hide;
    }

    public void setLongpressKillsApp(boolean kill) {
        mLongpressKillsApp = kill;
    }

    public void onNavButtonPressed(String buttonName) {
        if (buttonName.equals(PieControl.BACK_BUTTON)) {
            injectKeyDelayed(KeyEvent.KEYCODE_BACK);
        } else if (buttonName.equals(PieControl.HOME_BUTTON)) {
            injectKeyDelayed(KeyEvent.KEYCODE_HOME);
        } else if (buttonName.equals(PieControl.MENU_BUTTON)) {
            injectKeyDelayed(KeyEvent.KEYCODE_MENU);
        } else if (buttonName.equals(PieControl.SEARCH_BUTTON)) {
            injectKeyDelayed(KeyEvent.KEYCODE_SEARCH);
        } else if (buttonName.equals(PieControl.RECENT_BUTTON)) {
            try {
                // Using a hidden api to call the statusbar service toggleRecentApps() method
                IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar")).toggleRecentApps();
            } catch (RemoteException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        if (mHideOnPress)
            show(false, false);
    }

    public void onNavButtonLongPressed(String buttonName) {
        if (buttonName.equals(PieControl.BACK_BUTTON)) {
            if (mLongpressKillsApp) {
                // TODO: Implement sending long press key event
            }
        }
        if (mHideOnPress)
            show(false, false);
    }

    public void injectKeyDelayed(int keycode){
    	mInjectKeycode = keycode;
        mDownTime = SystemClock.uptimeMillis();
    	mHandler.removeCallbacks(onInjectKeyDelayed);
      	mHandler.postDelayed(onInjectKeyDelayed, 100);
    }

    final Runnable onInjectKeyDelayed = new Runnable() {
    	public void run() {
            try {
                RootUtils.execute(String.format("input keyevent %d\n", mInjectKeycode));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    };

    public void updateAutoHideTimer() {
        Context context = getContext();
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AutoHideReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            am.cancel(pi);
        } catch (Exception e) {
        }
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis() + mHideTime);
        am.set(AlarmManager.RTC, time.getTimeInMillis(), pi);
    }

    public void cancelAutoHideTimer() {
        Context context = getContext();
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AutoHideReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            am.cancel(pi);
        } catch (Exception e) {
        }
    }

    public static class AutoHideReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mQuickNavbarPanel.show(false, true);
        }
    }
}
