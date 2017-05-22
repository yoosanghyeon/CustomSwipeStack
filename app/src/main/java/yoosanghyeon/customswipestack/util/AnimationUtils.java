package yoosanghyeon.customswipestack.util;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;

public class AnimationUtils {
    public AnimationUtils() {
    }

    public abstract static class AnimationEndListener implements AnimatorListener {
        public AnimationEndListener() {
        }

        public void onAnimationStart(Animator animation) {
        }

        public void onAnimationCancel(Animator animation) {
        }

        public void onAnimationRepeat(Animator animation) {
        }
    }
}