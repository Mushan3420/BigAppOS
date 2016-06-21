package com.wjwu.wpmain.uzwp;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.wjwu.wpmain.drag_tab.CatalogActivity;
import com.wjwu.wpmain.lib_base.BaseFragmentWithTitleBar;
import com.wjwu.wpmain.net.RequestUrl;
import com.wjwu.wpmain.uzwp.detail.FragmentDetailsCommon;
import com.wjwu.wpmain.uzwp.search.ActivitySearch;
import com.wjwu.wpmain.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import event.UserCatalogChangedEvent;
import model.NavCatalog;

public class FragmentMainHome extends BaseFragmentWithTitleBar {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private List<SamplePagerItem> mTabs = new ArrayList<SamplePagerItem>();

    @Override
    public int initContentView() {
        return R.layout.v_fragment_main_home;
    }

    @Override
    public void findAndBindViews(View contentView) {
        setDefaultImageLeftVisible(true);
        setDefaultImageRightVisible(true);
        setTitleText(R.string.app_name);
        contentView.findViewById(R.id.iv_add).setOnClickListener(this);
        initSlidingTabs(contentView);
    }

    private void initSlidingTabs(View view) {
        ArrayList<NavCatalog> listNavCatalog = (ArrayList<NavCatalog>) getArguments().getSerializable("userNavList");
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        if (listNavCatalog == null || listNavCatalog.size() == 0) {
            view.findViewById(R.id.ll_tab).setVisibility(View.GONE);
            view.findViewById(R.id.frame_none).setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
            NavCatalog navCatalog = new NavCatalog();
            navCatalog.name = "none_title";
            navCatalog.banner_list = null;
            navCatalog.link = RequestUrl.base_content_list;
            getChildFragmentManager().beginTransaction().replace(R.id.frame_none, FragmentTabContent.newInstance(navCatalog)).commit();
            return;
        }

        for (NavCatalog catalog : listNavCatalog) {
            mTabs.add(new SamplePagerItem(catalog));
        }
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SampleFragmentPagerAdapter(getChildFragmentManager()));
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColorStateList(R.color.tv_catalog_p).getDefaultColor());
        mSlidingTabLayout.setDividerColors(Color.parseColor("#00FFFFFF"));
    }

    @Override
    public void onViewClick(View v) {
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.iv_left:
                ((ActivityMainSliding) getActivity()).getSlidingMenu().showMenu();
                break;
            case R.id.iv_right:
                ActivitySearch.gotoFragmentSearchHistory(mContext);
                break;
            case R.id.iv_add:
                CatalogActivity.gotoCatalogActivity(mContext);
                break;
        }
    }

    static class SamplePagerItem {
        private final NavCatalog mNavCatalog;

        SamplePagerItem(NavCatalog catalog) {
            mNavCatalog = catalog;
        }

        Fragment createFragment() {
            if ("taxonomy".equals(mNavCatalog.type)) {
                return FragmentTabContent.newInstance(mNavCatalog);
            }
            if ("post_type".equals(mNavCatalog.type)) {
                return FragmentDetailsCommon.newInstance(mNavCatalog.link, false, false, 1);
            }
            if ("custom".equals(mNavCatalog.type)) {
                return FragmentTabContentCustom.newInstance(mNavCatalog);
            }
            return FragmentTabContent.newInstance(mNavCatalog);
        }

        CharSequence getTitle() {
            return mNavCatalog.name;
        }
    }

    class SampleFragmentPagerAdapter extends FragmentStatePagerAdapter {

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mTabs.get(position).createFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getTitle();
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UserCatalogChangedEvent event = EventBus.getDefault().getStickyEvent(UserCatalogChangedEvent.class);
        if (event == null) {
            return;
        }
        EventBus.getDefault().removeStickyEvent(UserCatalogChangedEvent.class);
        freshTabs(event);
    }

    public void freshTabs(UserCatalogChangedEvent event) {
        mTabs.clear();
        for (NavCatalog catalog : event.catalogList) {
            mTabs.add(new SamplePagerItem(catalog));
        }
        mSlidingTabLayout.freshTabs();
        mViewPager.getAdapter().notifyDataSetChanged();
    }

}
