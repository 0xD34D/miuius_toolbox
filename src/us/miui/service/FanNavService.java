/**
 *
 */
package us.miui.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import us.miui.toolbox.activity.ToolboxTabPagerActivity;
import us.miui.view.OverlayPanel;
import us.miui.view.QuickNavbarPanel;

import us.miui.toolbox.R;

/**
 * @author lithium
 */
public class FanNavService extends Service {
    private static final String TAG = "FanNavService";
    private static final int MSG_CLOSE_QUICKNAV_PANEL = 1001;

    public static final int FANNAV_NOTIFICATION_ID = 2000;

    static OverlayHandler mHandler;
    static QuickNavbarPanel mQuicknavPanel;
    static boolean mServiceEnabled = false;
    private final IBinder mBinder = new FanNavServiceBinder();
    private static View mTrigger;

    /**
     *
     */
    public FanNavService() {
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
      * @see android.app.Service#onBind(android.content.Intent)
      */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mBinder;
    }

    /* (non-Javadoc)
      * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
      */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        super.onStartCommand(intent, flags, startId);
        if (intent.getBooleanExtra("enable", false)) {
            Log.d(TAG, "Starting service with overlay enabled");
            int height = intent.getIntExtra("height", 10);
            int color = intent.getIntExtra("color", 0x80FFFFFF);
            if (mHandler == null) {
                mHandler = new OverlayHandler();
            }
            if (mQuicknavPanel == null)
                createFanNav();
            enableOverlay(height, color);
        }
        return Service.START_STICKY;
    }

    /* (non-Javadoc)
      * @see android.app.Service#onCreate()
      */
    @Override
    public void onCreate() {
        super.onCreate();
        if (mQuicknavPanel == null)
            createFanNav();
        if (mHandler == null)
            mHandler = new OverlayHandler();
    }

    private void createFanNav() {
        int flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE |
                LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        LayoutParams params = new LayoutParams(
                300, 150,
                LayoutParams.TYPE_SYSTEM_ALERT,
                flags,
                PixelFormat.TRANSLUCENT);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mQuicknavPanel = (QuickNavbarPanel) inflater.inflate(R.layout.fannav_panel, null);
        mQuicknavPanel.setOnTouchListener(new TouchOutsideListener(MSG_CLOSE_QUICKNAV_PANEL, mQuicknavPanel));
        mQuicknavPanel.show(false, false);
        wm.addView(mQuicknavPanel, params);
    }

    public void enableOverlay(int height, int color) {
        //int flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        int flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE |
                LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_SYSTEM_ALERT,
                flags,
                PixelFormat.TRANSLUCENT);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mTrigger = inflater.inflate(R.layout.hot_spot, null);
        ImageView button = (ImageView) mTrigger.findViewById(R.id.trigger);
        int gravity = Gravity.LEFT | Gravity.BOTTOM;
        params.gravity = gravity;
        button.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams trigger_params = (LinearLayout.LayoutParams) button.getLayoutParams();
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        trigger_params.width = dm.widthPixels > dm.heightPixels ? dm.widthPixels : dm.heightPixels;
        trigger_params.height = height;
        button.setLayoutParams(trigger_params);
        button.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        final int g = gravity;

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mQuicknavPanel.isShowing())
                        mQuicknavPanel.show(false, false);
                    int flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE |
                            LayoutParams.FLAG_ALT_FOCUSABLE_IM
                            | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                    LayoutParams params = new LayoutParams(
                            300, 150, (int) event.getX() - 150, 0,
                            LayoutParams.TYPE_SYSTEM_ALERT,
                            flags,
                            PixelFormat.TRANSLUCENT);
                    params.gravity = g;
                    params.windowAnimations = android.R.style.Animation;
                    WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);

                    wm.updateViewLayout(mQuicknavPanel, params);
                    mQuicknavPanel.show(true, true);
                    return true;
                }
                return false;
            }
        });
        // Add layout to window manager
        wm.addView(mTrigger, params);
        mServiceEnabled = true;
        createNotification();
    }

    public void disableOverlay() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.removeView(mTrigger);
        mTrigger = null;
        mQuicknavPanel.show(false, false);
        mServiceEnabled = false;
        removeNotification();
    }

    public void setTriggerSize(int width, int height) {
        ImageView button = (ImageView) mTrigger.findViewById(R.id.trigger);
        LinearLayout.LayoutParams trigger_params = (LinearLayout.LayoutParams) button.getLayoutParams();
        trigger_params.width = width;
        trigger_params.height = height;
        button.setLayoutParams(trigger_params);
    }

    public boolean serviceEnabled() {
        return mServiceEnabled;
    }

    public class TouchOutsideListener implements View.OnTouchListener {
        private int mMsg;
        private OverlayPanel mPanel;

        public TouchOutsideListener(int msg, OverlayPanel panel) {
            mMsg = msg;
            mPanel = panel;
        }

        public boolean onTouch(View v, MotionEvent ev) {
            final int action = ev.getAction();
            if (action == MotionEvent.ACTION_OUTSIDE
                    || (action == MotionEvent.ACTION_DOWN
                    && !mPanel.isInContentArea((int) ev.getX(), (int) ev.getY()))) {
                mHandler.removeMessages(mMsg);
                mHandler.sendEmptyMessage(mMsg);
                return true;
            }
            return false;
        }
    }

    public class OverlayHandler extends Handler {

        /* (non-Javadoc)
           * @see android.os.Handler#handleMessage(android.os.Message)
           */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CLOSE_QUICKNAV_PANEL:
                    mQuicknavPanel.show(false, true);
                    break;
            }
        }
    }

    public class FanNavServiceBinder extends Binder {
        public FanNavService getService() {
            return FanNavService.this;
        }
    }

    private Notification createNotification() {
        Intent intent = new Intent(this, ToolboxTabPagerActivity.class);
        intent.setAction(ToolboxTabPagerActivity.ACTION_EXPERIMENTAL_SETTINGS);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notice = new Notification.Builder(this)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentTitle("Fan-Nav service")
                .setSmallIcon(R.drawable.ic_fannav)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .build();
        nm.notify(FANNAV_NOTIFICATION_ID, notice);
        return notice;

    }

    private void removeNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(FANNAV_NOTIFICATION_ID);
    }

}
