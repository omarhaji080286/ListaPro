package winservices.com.listapro.utils;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationManager {

    private Context context;

    public AnimationManager(Context context) {
        this.context = context;
    }

    public void animateItem(final View v, final int animationId ,int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Animation anim = AnimationUtils.loadAnimation(context, animationId);
                if (v != null) {
                    v.startAnimation(anim);
                }

            }
        }, delay);
    }

}
