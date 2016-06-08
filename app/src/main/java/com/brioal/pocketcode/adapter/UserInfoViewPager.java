package com.brioal.pocketcode.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.brioal.pocketcode.fragment.AttentionFragment;
import com.brioal.pocketcode.fragment.CollectListFragment;
import com.brioal.pocketcode.fragment.FansFragment;
import com.brioal.pocketcode.fragment.ShareListFragment;

/**默认是"我"的
 * 用户信息的viewpager
 */
public class UserInfoViewPager extends FragmentPagerAdapter {
    public static int TYPE_MINE = 0;
    public static int TYPE_OTHER = 1;
    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[]{"分享", "收藏", "关注", "关注者"};
    private int mType = TYPE_MINE;


    public UserInfoViewPager(FragmentManager fm , int type) {
        super(fm);
        this.mType = type;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ShareListFragment.getInstance();
            case 1:
                return CollectListFragment.getInstance();
            case 2:
                return AttentionFragment.getInstance();
            case 3:
                return FansFragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        if (mType == TYPE_MINE) {
            return 4;
        } else if (mType == TYPE_OTHER) {
            return 1;
        }
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}