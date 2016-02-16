package com.transapps.callprotector.UI.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.transapps.callprotector.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainSearchActivityFragment extends Fragment {

    public MainSearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_search, container, false);
    }
}
