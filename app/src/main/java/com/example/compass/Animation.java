package com.example.compass;

import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Math.abs;

/**
 * This class deals with the animation of the compass
 * @author James Hanratty
 */
public class Animation {
    // UI
    ImageView CompassIV;
    TextView DirectionTV;

    // current orientation degree between [-180,180]
    float currentDegree = 0.0f;
    // the last time the animation was performed
    long lastUpdatedTime = 0;
    // Animation Constants
    final static int animationInterval = 1000;   // time in ms before animation moves again
    final static int animationDuration = 250;    // 250ms

    public Animation(ImageView compass_iv, TextView direction_tv){
        CompassIV = compass_iv;
        DirectionTV = direction_tv;
    }

    /**
     * Animates the compass
     * @param degree Degree of orientation between [-180,180]
     * @see Animation
     */
    public void animateCompass(float degree){
        // create and start animation
        RotateAnimation compass_rotation = new RotateAnimation(currentDegree, -degree, android.view.animation.Animation.RELATIVE_TO_SELF,0.5f, android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f );
        compass_rotation.setDuration(animationDuration);
        compass_rotation.setFillAfter(true);
        CompassIV.startAnimation(compass_rotation);
        currentDegree = -degree;

        // change DirectionTv
        int degree_360 = Orientation.convertTo360Degrees(degree);
        DirectionTV.setText(degree_360 + "°");
    }
}
