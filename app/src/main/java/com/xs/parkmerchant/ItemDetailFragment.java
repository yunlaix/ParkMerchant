package com.xs.parkmerchant;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ItemDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments().containsKey(ARG_ITEM_ID)) {
        }
    }
}
