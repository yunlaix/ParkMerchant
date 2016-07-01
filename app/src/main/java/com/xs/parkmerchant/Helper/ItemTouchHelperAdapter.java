package com.xs.parkmerchant.Helper;

/**
 * Created by Man on 2016/7/1.
 */
public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

}
