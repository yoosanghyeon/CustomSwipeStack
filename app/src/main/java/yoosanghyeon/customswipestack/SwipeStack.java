package yoosanghyeon.customswipestack;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import yoosanghyeon.customswipestack.R.id;
import yoosanghyeon.customswipestack.R.styleable;

import java.util.Random;


public class SwipeStack extends ViewGroup {
    public static final int SWIPE_DIRECTION_BOTH = 0;
    public static final int SWIPE_DIRECTION_ONLY_LEFT = 1;
    public static final int SWIPE_DIRECTION_ONLY_RIGHT = 2;
    public static final int SWIPE_DIRECTION_ONLY_TOP = 3;
    public static final int SWIPE_DIRECTION_ONLY_BOTTOM = 4;
    public static final int DEFAULT_ANIMATION_DURATION = 400;
    public static final int DEFAULT_STACK_SIZE = 3;
    public static final int DEFAULT_STACK_ROTATION = 0;
    public static final float DEFAULT_SWIPE_ROTATION = 30.0F;
    public static final float DEFAULT_SWIPE_OPACITY = 1.0F;
    public static final float DEFAULT_SCALE_FACTOR = 1.0F;
    public static final boolean DEFAULT_DISABLE_HW_ACCELERATION = true;
    private static final String KEY_SUPER_STATE = "superState";
    private static final String KEY_CURRENT_INDEX = "currentIndex";
    private Adapter mAdapter;
    private Random mRandom;
    private int mAllowedSwipeDirections;
    private int mAnimationDuration;
    private int mCurrentViewIndex;
    private int mNumberOfStackedViews;
    private int mViewSpacing;
    private int mViewRotation;
    private float mSwipeRotation;
    private float mSwipeOpacity;
    private float mScaleFactor;
    private boolean mDisableHwAcceleration;
    private boolean mIsFirstLayout;
    private View mTopView;
    private SwipeHelper mSwipeHelper;
    private DataSetObserver mDataObserver;
    private SwipeStack.SwipeStackListener mListener;
    private SwipeStack.SwipeProgressListener mProgressListener;

    public SwipeStack(Context context) {
        this(context, (AttributeSet) null);
    }

    public SwipeStack(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeStack(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIsFirstLayout = true;
        this.readAttributes(attrs);
        this.initialize();
    }

    private void readAttributes(AttributeSet attributeSet) {
        TypedArray attrs = this.getContext().obtainStyledAttributes(attributeSet, styleable.SwipeStack);

        try {
            mAllowedSwipeDirections =
                    attrs.getInt(R.styleable.SwipeStack_allowed_swipe_directions,
                            SWIPE_DIRECTION_BOTH);
            mAnimationDuration =
                    attrs.getInt(R.styleable.SwipeStack_animation_duration,
                            DEFAULT_ANIMATION_DURATION);
            mNumberOfStackedViews =
                    attrs.getInt(R.styleable.SwipeStack_stack_size, DEFAULT_STACK_SIZE);
            mViewSpacing =
                    attrs.getDimensionPixelSize(R.styleable.SwipeStack_stack_spacing,
                            getResources().getDimensionPixelSize(R.dimen.default_stack_spacing));
            mViewRotation =
                    attrs.getInt(R.styleable.SwipeStack_stack_rotation, DEFAULT_STACK_ROTATION);
            mSwipeRotation =
                    attrs.getFloat(R.styleable.SwipeStack_swipe_rotation, DEFAULT_SWIPE_ROTATION);
            mSwipeOpacity =
                    attrs.getFloat(R.styleable.SwipeStack_swipe_opacity, DEFAULT_SWIPE_OPACITY);
            mScaleFactor =
                    attrs.getFloat(R.styleable.SwipeStack_scale_factor, DEFAULT_SCALE_FACTOR);
            mDisableHwAcceleration =
                    attrs.getBoolean(R.styleable.SwipeStack_disable_hw_acceleration,
                            DEFAULT_DISABLE_HW_ACCELERATION);
        } finally {
            attrs.recycle();
        }

    }

    private void initialize() {
        this.mRandom = new Random();
        this.setClipToPadding(false);
        this.setClipChildren(false);
        this.mSwipeHelper = new SwipeHelper(this);
        this.mSwipeHelper.setAnimationDuration(this.mAnimationDuration);
        this.mSwipeHelper.setRotation(this.mSwipeRotation);
        this.mSwipeHelper.setOpacityEnd(this.mSwipeOpacity);
        this.mDataObserver = new DataSetObserver() {
            public void onChanged() {
                super.onChanged();
                SwipeStack.this.invalidate();
                SwipeStack.this.requestLayout();
            }
        };
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("currentIndex", this.mCurrentViewIndex - this.getChildCount());
        return bundle;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.mCurrentViewIndex = bundle.getInt("currentIndex");
            state = bundle.getParcelable("superState");
        }

        super.onRestoreInstanceState(state);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (this.mAdapter != null && !this.mAdapter.isEmpty()) {
            for (int x = this.getChildCount(); x < this.mNumberOfStackedViews && this.mCurrentViewIndex < this.mAdapter.getCount(); ++x) {
                this.addNextView();
            }

            this.reorderItems();
            this.mIsFirstLayout = false;
        } else {
            this.mCurrentViewIndex = 0;
            this.removeAllViewsInLayout();
        }
    }

    private void addNextView() {
        if (this.mCurrentViewIndex < this.mAdapter.getCount()) {
            View bottomView = this.mAdapter.getView(this.mCurrentViewIndex, (View) null, this);
            bottomView.setTag(id.new_view, Boolean.valueOf(true));
            if (!this.mDisableHwAcceleration) {
                bottomView.setLayerType(2, (Paint) null);
            }

            if (this.mViewRotation > 0) {
                bottomView.setRotation((float) (this.mRandom.nextInt(this.mViewRotation) - this.mViewRotation / 2));
            }

            int width = this.getWidth() - (this.getPaddingLeft() + this.getPaddingRight());
            int height = this.getHeight() - (this.getPaddingTop() + this.getPaddingBottom());
            LayoutParams params = bottomView.getLayoutParams();
            if (params == null) {
                params = new LayoutParams(-2, -2);
            }

            int measureSpecWidth = -2147483648;
            int measureSpecHeight = -2147483648;
            if (params.width == -1) {
                measureSpecWidth = 1073741824;
            }

            if (params.height == -1) {
                measureSpecHeight = 1073741824;
            }

            bottomView.measure(measureSpecWidth | width, measureSpecHeight | height);
            this.addViewInLayout(bottomView, 0, params, true);
            ++this.mCurrentViewIndex;
        }

    }

    private void reorderItems() {
        for (int x = 0; x < this.getChildCount(); ++x) {
            View childView = this.getChildAt(x);
            int topViewIndex = this.getChildCount() - 1;
            int distanceToViewAbove = topViewIndex * this.mViewSpacing - x * this.mViewSpacing;
            int newPositionX = (this.getWidth() - childView.getMeasuredWidth()) / 2;
            int newPositionY = distanceToViewAbove + this.getPaddingTop();
            childView.layout(newPositionX, this.getPaddingTop(), newPositionX + childView.getMeasuredWidth(), this.getPaddingTop() + childView.getMeasuredHeight());
            if (VERSION.SDK_INT >= 21) {
                childView.setTranslationZ((float) x);
            }

            boolean isNewView = ((Boolean) childView.getTag(id.new_view)).booleanValue();
            float scaleFactor = (float) Math.pow((double) this.mScaleFactor, (double) (this.getChildCount() - x));
            if (x == topViewIndex) {
                this.mSwipeHelper.unregisterObservedView();
                this.mTopView = childView;
                this.mSwipeHelper.registerObservedView(this.mTopView, (float) newPositionX, (float) newPositionY);
            }

            if (!this.mIsFirstLayout) {
                if (isNewView) {
                    childView.setTag(id.new_view, Boolean.valueOf(false));
                    childView.setAlpha(0.0F);
                    childView.setY((float) newPositionY);
                    childView.setScaleY(scaleFactor);
                    childView.setScaleX(scaleFactor);
                }

                childView.animate().y((float) newPositionY).scaleX(scaleFactor).scaleY(scaleFactor).alpha(1.0F).setDuration((long) this.mAnimationDuration);
            } else {
                childView.setTag(id.new_view, Boolean.valueOf(false));
                childView.setY((float) newPositionY);
                childView.setScaleY(scaleFactor);
                childView.setScaleX(scaleFactor);
            }
        }

    }

    private void removeTopView() {
        if (this.mTopView != null) {
            this.removeView(this.mTopView);
            this.mTopView = null;
        }

        if (this.getChildCount() == 0 && this.mListener != null) {
            this.mListener.onStackEmpty();
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(width, height);
    }

    public void onSwipeStart() {
        if (this.mProgressListener != null) {
            this.mProgressListener.onSwipeStart(this.getCurrentPosition());
        }

    }

    public void onSwipeProgress(float progress) {
        if (this.mProgressListener != null) {
            this.mProgressListener.onSwipeProgress(this.getCurrentPosition(), progress);
        }

    }

    public void onSwipeEnd() {
        if (this.mProgressListener != null) {
            this.mProgressListener.onSwipeEnd(this.getCurrentPosition());
        }

    }

    public void onViewSwipedToLeft() {
        if (this.mListener != null) {
            this.mListener.onViewSwipedToLeft(this.getCurrentPosition());
        }

        this.removeTopView();
    }

    public void onViewSwipedToRight() {
        if (this.mListener != null) {
            this.mListener.onViewSwipedToRight(this.getCurrentPosition());
        }

        this.removeTopView();
    }

    public void onViewSwipedToTop() {
        if (this.mListener != null) {
            this.mListener.onViewSwipedToTop(this.getCurrentPosition());
        }

        this.removeTopView();
    }

    public void onViewSwipedToBottom() {
        if (this.mListener != null) {
            this.mListener.onViewSwipedToBottom(this.getCurrentPosition());
        }

        this.removeTopView();
    }

    public int getCurrentPosition() {
        return this.mCurrentViewIndex - this.getChildCount();
    }

    public Adapter getAdapter() {
        return this.mAdapter;
    }

    public void setAdapter(Adapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataObserver);
        }

        this.mAdapter = adapter;
        this.mAdapter.registerDataSetObserver(this.mDataObserver);
    }

    public int getAllowedSwipeDirections() {
        return this.mAllowedSwipeDirections;
    }

    public void setAllowedSwipeDirections(int directions) {
        this.mAllowedSwipeDirections = directions;
    }

    public void setListener(@Nullable SwipeStack.SwipeStackListener listener) {
        this.mListener = listener;
    }

    public void setSwipeProgressListener(@Nullable SwipeStack.SwipeProgressListener listener) {
        this.mProgressListener = listener;
    }

    public View getTopView() {
        return this.mTopView;
    }

    public void swipeTopViewToRight() {
        if (this.getChildCount() != 0) {
            this.mSwipeHelper.swipeViewToRight();
        }
    }

    public void swipeTopViewToLeft() {
        if (this.getChildCount() != 0) {
            this.mSwipeHelper.swipeViewToLeft();
        }
    }

    public void swipeTopViewToTop() {
        if (this.getChildCount() != 0) {
            this.mSwipeHelper.swipeViewToTop();
        }
    }

    public void swipeTopViewToBottom() {
        if (this.getChildCount() != 0) {
            this.mSwipeHelper.swipeViewToBottom();
        }
    }

    public void resetStack() {
        this.mCurrentViewIndex = 0;
        this.removeAllViewsInLayout();
        this.requestLayout();
    }

    public interface SwipeProgressListener {
        void onSwipeStart(int var1);

        void onSwipeProgress(int var1, float var2);

        void onSwipeEnd(int var1);
    }

    public interface SwipeStackListener {
        void onViewSwipedToLeft(int var1);

        void onViewSwipedToRight(int var1);

        void onViewSwipedToTop(int var1);

        void onViewSwipedToBottom(int var1);

        void onStackEmpty();
    }
}