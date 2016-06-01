package com.brioal.pocketcode.interfaces;

/**
 * Created by Brioal on 2016/5/18.
 */
public interface onItemChangeListener {
    boolean onItemMove(int from, int to);

    void onItemDismiss(int position);
}
