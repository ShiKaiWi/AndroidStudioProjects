package com.example.xkwei.draganddraw;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xkwei on 26/12/2016.
 */

public class DragAndDrawFragment extends Fragment {



    public static Fragment newInstance(){
        return new DragAndDrawFragment();
    }



    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container,Bundle savedInstanceState){
        View v = lif.inflate(R.layout.fragment_drag_and_draw,container,false);
        return v;
    }
}
