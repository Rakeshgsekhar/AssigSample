package com.webatron.rakesh.assignsample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rakesh on 13/3/18.
 */

public class ViewAdaptor extends FragmentPagerAdapter {


    List<Fragment> frag = new ArrayList<Fragment>();
    List<String> fragtittle = new ArrayList<String>();



    public List<Fragment> getFrag() {
        return frag;
    }

    public List<String> getFragtittle() {
        return fragtittle;
    }


    public ViewAdaptor(FragmentManager fm) {
        super(fm);
    }

    public void AddItem(Fragment fragment,String tittle){

        frag.add(fragment);
        fragtittle.add(tittle);
    }

    @Override
    public Fragment getItem(int position) {
        return frag.get(position);
    }

    @Override
    public int getCount() {
        return frag.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragtittle.get(position);

    }
}
