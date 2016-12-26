package com.example.xkwei.draganddraw;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xkwei on 26/12/2016.
 */

public class Box implements Parcelable {
    private PointF mOrigin;
    private PointF mCurrent;

    public Box(PointF origin) {
        mCurrent = mOrigin = origin;
    }

    private Box(Parcel in){
        mOrigin = new PointF(in.readFloat(),in.readFloat());
        mCurrent = new PointF(in.readFloat(),in.readFloat());
    }
    public PointF getOrigin() {
        return mOrigin;
    }

    public PointF getCurrent() {

        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags){
        out.writeFloat(mOrigin.x);
        out.writeFloat(mOrigin.y);
        out.writeFloat(mCurrent.x);
        out.writeFloat(mCurrent.y);
    }

    public static final Parcelable.Creator<Box> CREATOR =
            new Parcelable.Creator<Box>(){
                public Box createFromParcel(Parcel in){
                    return new Box(in);
                }
                public Box[] newArray(int size){
                    return new Box[size];
                }
            };

}
