package com.brioal.pocketcode.util;

import com.brioal.pocketcode.entiy.ContentModel;

import java.util.Comparator;

/**
 * Created by Brioal on 2016/5/23.
 */
public class ContentModelCompare implements Comparator<ContentModel> {


    @Override
    public int compare(ContentModel lhs, ContentModel rhs) {
        String lhTime = lhs.getmTime() + "";
        String rhTime = rhs.getmTime() + "";
        return rhTime.compareTo(lhTime);
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}
