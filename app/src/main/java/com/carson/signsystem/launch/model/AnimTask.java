package com.carson.signsystem.launch.model;

import android.animation.AnimatorSet;

public class AnimTask {

    private AnimatorSet animatorSet;
//    private long duration;
    private static AnimTask INSTANCE = null;

    private AnimTask () {}

    public static AnimTask getINstance() {
        if (INSTANCE == null) {
            INSTANCE = new AnimTask();
        }
        return INSTANCE;
    }
//
//    public void setTranslateAnim(int direction, )
}
