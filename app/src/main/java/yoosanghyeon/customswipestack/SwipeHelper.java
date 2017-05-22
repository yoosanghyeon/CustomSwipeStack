package yoosanghyeon.customswipestack;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.OvershootInterpolator;
import yoosanghyeon.customswipestack.util.AnimationUtils.AnimationEndListener;

public class SwipeHelper implements OnTouchListener {
    private final SwipeStack mSwipeStack;
    private View mObservedView;
    private boolean mListenForTouchEvents;
    private float mDownX;
    private float mDownY;
    private float mInitialX;
    private float mInitialY;
    private int mPointerId;
    private float mRotateDegrees = 30.0F;
    private float mOpacityEnd = 1.0F;
    private int mAnimationDuration = 400;

    public SwipeHelper(SwipeStack swipeStack) {
        this.mSwipeStack = swipeStack;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
        case 0:
            if(this.mListenForTouchEvents && this.mSwipeStack.isEnabled()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                this.mSwipeStack.onSwipeStart();
                this.mPointerId = event.getPointerId(0);
                this.mDownX = event.getX(this.mPointerId);
                this.mDownY = event.getY(this.mPointerId);
                return true;
            }

            return false;
        case 1:
            v.getParent().requestDisallowInterceptTouchEvent(false);
            this.mSwipeStack.onSwipeEnd();
            this.checkViewPosition();
            return true;
        case 2:
            int pointerIndex = event.findPointerIndex(this.mPointerId);
            if(pointerIndex < 0) {
                return false;
            }

            float dx = event.getX(pointerIndex) - this.mDownX;
            float dy = event.getY(pointerIndex) - this.mDownY;
            float newX = this.mObservedView.getX() + dx;
            float newY = this.mObservedView.getY() + dy;
            this.mObservedView.setX(newX);
            this.mObservedView.setY(newY);
            float dragDistanceX = newX - this.mInitialX;
            float swipeProgress = Math.min(Math.max(dragDistanceX / (float)this.mSwipeStack.getWidth(), -1.0F), 1.0F);
            this.mSwipeStack.onSwipeProgress(swipeProgress);
            float alpha;
            if(this.mRotateDegrees > 0.0F) {
                alpha = this.mRotateDegrees * swipeProgress;
                this.mObservedView.setRotation(alpha);
            }

            if(this.mOpacityEnd < 1.0F) {
                alpha = 1.0F - Math.min(Math.abs(swipeProgress * 2.0F), 1.0F);
                this.mObservedView.setAlpha(alpha);
            }

            return true;
        default:
            return false;
        }
    }

    private void checkViewPosition() {
        if(!this.mSwipeStack.isEnabled()) {
            this.resetViewPosition();
        } else {
            float viewCenterHorizontal = this.mObservedView.getX() + (float)(this.mObservedView.getWidth() / 2);
            float viewCenterVertical = this.mObservedView.getY() + (float)(this.mObservedView.getHeight() / 2);
            float parentFirstThird = (float)this.mSwipeStack.getWidth() / 3.0F;
            float parentFirstThirdTop = (float)this.mSwipeStack.getHeight() / 3.0F;
            float parentLastThird = parentFirstThird * 2.0F;
            float parentLastThirdTop = parentFirstThirdTop * 2.0F;
            if(viewCenterHorizontal < parentFirstThird && this.mSwipeStack.getAllowedSwipeDirections() != 2) {
                this.swipeViewToLeft(this.mAnimationDuration / 2);
            } else if(viewCenterHorizontal > parentLastThird && this.mSwipeStack.getAllowedSwipeDirections() != 1) {
                this.swipeViewToRight(this.mAnimationDuration / 2);
            } else if(viewCenterVertical > parentLastThirdTop && this.mSwipeStack.getAllowedSwipeDirections() != 4) {
                this.swipeViewToBottom(this.mAnimationDuration / 2);
            } else if(viewCenterVertical < parentFirstThirdTop && this.mSwipeStack.getAllowedSwipeDirections() != 3) {
                this.swipeViewToTop(this.mAnimationDuration / 2);
            } else {
                this.resetViewPosition();
            }

        }
    }

    private void resetViewPosition() {
        this.mObservedView.animate().x(this.mInitialX).y(this.mInitialY).rotation(0.0F).alpha(1.0F).setDuration((long)this.mAnimationDuration).setInterpolator(new OvershootInterpolator(1.4F)).setListener((AnimatorListener)null);
    }

    private void swipeViewToLeft(int duration) {
        if(this.mListenForTouchEvents) {
            this.mListenForTouchEvents = false;
            this.mObservedView.animate().cancel();
            this.mObservedView.animate().x((float)(-this.mSwipeStack.getWidth()) + this.mObservedView.getX()).rotation(-this.mRotateDegrees).alpha(0.0F).setDuration((long)duration).setListener(new AnimationEndListener() {
                public void onAnimationEnd(Animator animation) {
                    SwipeHelper.this.mSwipeStack.onViewSwipedToLeft();
                }
            });
        }
    }

    private void swipeViewToRight(int duration) {
        if(this.mListenForTouchEvents) {
            this.mListenForTouchEvents = false;
            this.mObservedView.animate().cancel();
            this.mObservedView.animate().x((float)this.mSwipeStack.getWidth() + this.mObservedView.getX()).rotation(this.mRotateDegrees).alpha(0.0F).setDuration((long)duration).setListener(new AnimationEndListener() {
                public void onAnimationEnd(Animator animation) {
                    SwipeHelper.this.mSwipeStack.onViewSwipedToRight();
                }
            });
        }
    }

    private void swipeViewToTop(int duration) {
        if(this.mListenForTouchEvents) {
            this.mListenForTouchEvents = false;
            this.mObservedView.animate().cancel();
            this.mObservedView.animate().y((float)(-this.mSwipeStack.getHeight()) + this.mObservedView.getY()).rotation(0.0F).alpha(0.0F).setDuration((long)duration).setListener(new AnimationEndListener() {
                public void onAnimationEnd(Animator animation) {
                    SwipeHelper.this.mSwipeStack.onViewSwipedToTop();
                }
            });
        }
    }

    private void swipeViewToBottom(int duration) {
        if(this.mListenForTouchEvents) {
            this.mListenForTouchEvents = false;
            this.mObservedView.animate().cancel();
            this.mObservedView.animate().y((float)this.mSwipeStack.getHeight() + this.mObservedView.getY()).rotation(this.mRotateDegrees).alpha(0.0F).setDuration((long)duration).setListener(new AnimationEndListener() {
                public void onAnimationEnd(Animator animation) {
                    SwipeHelper.this.mSwipeStack.onViewSwipedToBottom();
                }
            });
        }
    }

    public void registerObservedView(View view, float initialX, float initialY) {
        if(view != null) {
            this.mObservedView = view;
            this.mObservedView.setOnTouchListener(this);
            this.mInitialX = initialX;
            this.mInitialY = initialY;
            this.mListenForTouchEvents = true;
        }
    }

    public void unregisterObservedView() {
        if(this.mObservedView != null) {
            this.mObservedView.setOnTouchListener((OnTouchListener)null);
        }

        this.mObservedView = null;
        this.mListenForTouchEvents = false;
    }

    public void setAnimationDuration(int duration) {
        this.mAnimationDuration = duration;
    }

    public void setRotation(float rotation) {
        this.mRotateDegrees = rotation;
    }

    public void setOpacityEnd(float alpha) {
        this.mOpacityEnd = alpha;
    }

    public void swipeViewToLeft() {
        this.swipeViewToLeft(this.mAnimationDuration);
    }

    public void swipeViewToRight() {
        this.swipeViewToRight(this.mAnimationDuration);
    }

    public void swipeViewToTop() {
        this.swipeViewToTop(this.mAnimationDuration);
    }

    public void swipeViewToBottom() {
        this.swipeViewToBottom(this.mAnimationDuration);
    }
}