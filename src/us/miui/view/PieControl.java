/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import us.miui.toolbox.R;

/**
 * Controller for Quick Controls pie menu
 */
public class PieControl implements PieMenu.PieController, OnClickListener,
        OnLongClickListener {
    public static final String BACK_BUTTON = "##back##";
    public static final String HOME_BUTTON = "##home##";
    public static final String MENU_BUTTON = "##menu##";
    public static final String RECENT_BUTTON = "##recent##";
    public static final String SEARCH_BUTTON = "##search##";
    public static final String NOTIFICATION_BUTTON = "##notification##";
    public static final String SETTINGS_BUTTON = "##settings##";
    public static final String SCREENSHOT_BUTTON = "##screenshot##";

    protected Context mContext;
    protected PieMenu mPie;
    protected int mItemSize;
    protected TextView mNotificationsCount;
    private PieItem mBack;
    private PieItem mHome;
    private PieItem mMenu;
    private PieItem mRecent;
    private PieItem mSearch;
    private OnNavButtonPressedListener mListener;
    private int mNumNotifications = 0;

    public PieControl(Context context) {
        mContext = context;
        mItemSize = (int) context.getResources().getDimension(R.dimen.fannav_item_size);
    }

    public void attachToContainer(FrameLayout container) {
        if (mPie == null) {
            mPie = new PieMenu(mContext);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            mPie.setLayoutParams(lp);
            populateMenu();
            mPie.setController(this);
        }
        container.addView(mPie);
    }

    public void removeFromContainer(FrameLayout container) {
        container.removeView(mPie);
    }

    public void forceToTop(FrameLayout container) {
        if (mPie.getParent() != null) {
            container.removeView(mPie);
            container.addView(mPie);
        }
    }

    protected void setClickListener(OnClickListener listener, PieItem... items) {
        for (PieItem item : items) {
            item.getView().setOnClickListener(listener);
        }
    }

    protected void setLongClickListener(OnLongClickListener listener, PieItem... items) {
        for (PieItem item : items) {
            item.getView().setOnLongClickListener(listener);
        }
    }

    public void setNotificationsCount(int count) {
        mNumNotifications = count;
    }

    @Override
    public boolean onOpen() {
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return mPie.onTouchEvent(event);
    }

    protected void populateMenu() {
        mBack = makeItem(R.drawable.ic_sysbar_back, 1);
        mHome = makeItem(R.drawable.ic_sysbar_home, 1);
        mRecent = makeItem(R.drawable.ic_sysbar_recent, 1);
        mMenu = makeItem(R.drawable.ic_sysbar_menu, 1);
        mSearch = makeItem(R.drawable.ic_sysbar_search, 1);

        setClickListener(this, mBack, mHome, mRecent, mMenu, mSearch);
        setLongClickListener(this, mBack);
        // level 1
        mPie.addItem(mSearch);

        mPie.addItem(mMenu);

        mPie.addItem(mRecent);
        
        mPie.addItem(mHome);

        mPie.addItem(mBack);
    }

    @Override
    public void onClick(View v) {
        String buttonName = "";
        if (v == mBack.getView()) {
            buttonName = BACK_BUTTON;
        } else if (v == mHome.getView()) {
            buttonName = HOME_BUTTON;
        } else if (v == mRecent.getView()) {
            buttonName = RECENT_BUTTON;
        } else if (v == mMenu.getView()) {
            buttonName = MENU_BUTTON;
        } else if (v == mSearch.getView()) {
            buttonName = SEARCH_BUTTON;
        }

        if (mListener != null)
            mListener.onNavButtonPressed(buttonName);
    }

    @Override
    public boolean onLongClick(View v) {
        String buttonName = "";
        if (v == mBack.getView()) {
            buttonName = BACK_BUTTON;
        } else if (v == mHome.getView()) {
            buttonName = HOME_BUTTON;
        } else if (v == mRecent.getView()) {
            buttonName = RECENT_BUTTON;
        } else if (v == mMenu.getView()) {
            buttonName = MENU_BUTTON;
        } else if (v == mSearch.getView()) {
            buttonName = SEARCH_BUTTON;
        }

        if (mListener != null) {
            mListener.onNavButtonLongPressed(buttonName);
            return true;
        }
        return false;
    }

    protected PieItem makeItem(int image, int l) {
        ImageView view = new ImageView(mContext);
        view.setImageResource(image);
        view.setMinimumWidth(mItemSize);
        view.setMinimumHeight(mItemSize);
        view.setScaleType(ScaleType.CENTER);
        LayoutParams lp = new LayoutParams(mItemSize, mItemSize);
        view.setLayoutParams(lp);
        return new PieItem(view, l);
    }

    protected PieItem makeFiller() {
        return new PieItem(null, 1);
    }

    public void show(boolean show) {
        mPie.show(show);
    }

    public void setCenter(int x, int y) {
        mPie.setCenter(x, y);
    }

	@Override
	public void stopEditingUrl() {
		// TODO Auto-generated method stub
		
	}

    public void setOnNavButtonPressedListener(OnNavButtonPressedListener listener) {
        mListener = listener;
    }

    public interface OnNavButtonPressedListener {
        public void onNavButtonPressed(String buttonName);
        public void onNavButtonLongPressed(String buttonName);
    }

}
