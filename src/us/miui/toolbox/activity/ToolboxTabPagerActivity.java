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
package us.miui.toolbox.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import java.io.IOException;
import java.util.ArrayList;

import us.miui.Toolbox;
import us.miui.carrierlogo.CarrierLogoHelper;
import us.miui.helpers.CPUHelper;
import us.miui.helpers.SystemHelper;
import us.miui.toolbox.R;
import us.miui.toolbox.R.id;
import us.miui.toolbox.R.layout;
import us.miui.toolbox.fragment.*;

/**
 * Demonstrates combining a TabHost with a ViewPager to implement a tab UI
 * that switches between tabs and also allows the user to perform horizontal
 * flicks to move between the tabs.
 */
public class ToolboxTabPagerActivity extends FragmentActivity {
    public static final String ACTION_ADB_WIFI_SETTINGS = "adb_wifi_setings";
    public static final String ACTION_EXPERIMENTAL_SETTINGS = "experimental_setings";


    TabHost mTabHost;
    ViewPager  mViewPager;
    TabsAdapter mTabsAdapter;
    HorizontalScrollView mTabScroller;
    boolean mShowAdbTab = false;
    RelativeLayout mTipOverlay;
    ImageView mLeftSwipe;
    ImageView mRightSwipe;
    Button mDismiss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_tabs_pager);
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        
        mTabScroller = (HorizontalScrollView)findViewById(R.id.tab_scroller);

        mViewPager = (ViewPager)findViewById(R.id.pager);

        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager, mTabScroller);
        
        mTabsAdapter.addTab(mTabHost.newTabSpec("status_bar").setIndicator("Status Bar"),
                StatusbarPrefFragment.class, null);
        
        mTabsAdapter.addTab(mTabHost.newTabSpec("colors").setIndicator("Colors"),
                CustomColorsPrefFragment.class, null);
        
        mTabsAdapter.addTab(mTabHost.newTabSpec("home").setIndicator("Launcher"),
                MiuiHomePrefFragment.class, null);
        
        if (SystemHelper.hasNavigationBar(this))
            mTabsAdapter.addTab(mTabHost.newTabSpec("navbar").setIndicator("Navbar"),
                    NavbarPrefFragment.class, null);
        
        if (CPUHelper.cpuSettingsExist())
        	mTabsAdapter.addTab(mTabHost.newTabSpec("cpu").setIndicator("CPU"),
        			CPUPrefFragment.class, null);
        
        mTabsAdapter.addTab(mTabHost.newTabSpec("adb").setIndicator("ADB WiFi"),
                AdbWifiPrefFragment.class, null);
        
        mTabsAdapter.addTab(mTabHost.newTabSpec("system").setIndicator("Extras"),
                SystemSettingsPrefFragment.class, null);

        if (Toolbox.ENABLE_DEVELOPMENT_OPTIONS)
            mTabsAdapter.addTab(mTabHost.newTabSpec("development").setIndicator("Development"),
                    DevelopmentOptionsFragment.class, null);

        if (Toolbox.ENABLE_EXPERIMENTAL_OPTIONS)
            mTabsAdapter.addTab(mTabHost.newTabSpec("experimental").setIndicator("Experimental"),
                    ExperimentalPrefFragment.class, null);

        mTabsAdapter.addTab(mTabHost.newTabSpec("changes").setIndicator("Change log"),
                ChangeLogFragment.class, null);
        

		Intent intent = getIntent();
		if (intent.getAction().equals(ACTION_ADB_WIFI_SETTINGS))
			mTabsAdapter.onPageSelected(5);
		else {
			if (!CarrierLogoHelper.carrierLogoExists()) {
				try {
					CarrierLogoHelper.copyDefaultCarrierLogo(this);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			CarrierLogoHelper.makeCarrierLogoWorldReadable();
		}
        
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        
        SharedPreferences prefs = getSharedPreferences(Toolbox.PREFS, 0);
        boolean showTips = prefs.getBoolean("pref_key_show_tips", true);
        mTipOverlay = (RelativeLayout) findViewById(R.id.tip_layout);
        if (showTips == false)
        	mTipOverlay.setVisibility(View.GONE);
        mLeftSwipe = (ImageView) findViewById(R.id.tip_image);
        mRightSwipe = (ImageView) findViewById(R.id.tip_image_right);
        mDismiss = (Button) findViewById(R.id.dismiss_button);
        mDismiss.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTipOverlay.setVisibility(View.GONE);
		        SharedPreferences prefs = getSharedPreferences(Toolbox.PREFS, 0);
		        Editor editor = prefs.edit();
		        editor.putBoolean("pref_key_show_tips", false);
		        editor.commit();
			}
		});
    }
    
    public void updatePage(int position) {
    	if (position == 0) {
    		mLeftSwipe.setVisibility(View.VISIBLE);
    		mRightSwipe.setVisibility(View.GONE);
    	} else if (position == mTabsAdapter.getCount() - 1) {
    		mLeftSwipe.setVisibility(View.GONE);
    		mRightSwipe.setVisibility(View.VISIBLE);
    	}
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between pages.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct paged in the ViewPager whenever the selected
     * tab changes.
     */
    public static class TabsAdapter extends FragmentPagerAdapter
            implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final TabHost mTabHost;
        private final ViewPager mViewPager;
        private final Activity mActivity;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
        private final HorizontalScrollView mTabScroller;

        static final class TabInfo {
            @SuppressWarnings("unused")
			private final String tag;
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager, HorizontalScrollView tabScroller) {
            super(activity.getFragmentManager());
            mContext = activity;
            mActivity = activity;
            mTabHost = tabHost;
            mViewPager = pager;
            mTabHost.setOnTabChangedListener(this);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
            mTabScroller = tabScroller;
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mContext));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);
            mTabs.add(info);
            mTabHost.addTab(tabSpec);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onTabChanged(String tabId) {
            int position = mTabHost.getCurrentTab();
            mViewPager.setCurrentItem(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            // Unfortunately when TabHost changes the current tab, it kindly
            // also takes care of putting focus on it when not in touch mode.
            // The jerk.
            // This hack tries to prevent this from pulling focus out of our
            // ViewPager.
            TabWidget widget = mTabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);
            int x = mTabScroller.getScrollX();
            int w = widget.getChildAt(0).getWidth();
            int mid = x + (mTabHost.getWidth() - w) / 2;
            int tabX = position * w;
            if (tabX < mid || tabX > mid)
            	mTabScroller.scrollBy(tabX-mid, 0);
            
            ((ToolboxTabPagerActivity)mActivity).updatePage(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
