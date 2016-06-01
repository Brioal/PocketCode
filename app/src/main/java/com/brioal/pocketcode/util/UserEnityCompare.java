package com.brioal.pocketcode.util;

import com.brioal.pocketcode.entiy.AttentionEnity;

import java.util.Comparator;

/**
 * Created by Brioal on 2016/6/1.
 */

public class UserEnityCompare implements Comparator<AttentionEnity> {


    @Override
    public int compare(AttentionEnity lhs, AttentionEnity rhs) {
        String lhTime = lhs.getCreatedAt();
        String rhTime = rhs.getCreatedAt();
        return rhTime.compareTo(lhTime);
    }
}
