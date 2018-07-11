package com.zamanak.testone.objects;

import java.io.Serializable;

public class OperatorsObj implements Serializable {

    private String mText;
    private int mPosition;

    public OperatorsObj(int position,String mText) {
        this.mText = mText;
        this.mPosition = position;
    }

    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }
}
