package com.wzx.wzxfoundation.widget.clipimage;

import android.content.Context;
import android.os.SystemClock;
import android.view.animation.Interpolator;

import com.wzx.wzxfoundation.util.LogHelper;


public class MyScroller {
	private static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t + 1.0f;
//			return t * t * t * t * t + 1.0f;
		}
	};

//	private static final Interpolator sInterpolator = new DecelerateInterpolator();

	private Context mContext;
	public MyScroller(Context context){
		mContext = context;
	}
	
	private int mStartX;
	private int mStartY;
	private int mDistanceX;
	private int mDistanceY;
	
	private int mCurrentX;
	private int mCurrentY;
	
	private long mStartTime;
	private long mDuration = 1200l;
	private boolean mIsfinish = true;

	public void setDuration(long duration) {
		mDuration = duration;
	}

	public void startScroll(int startX, int startY, int distanceX, int distanceY) {
		mStartX = startX;
		mStartY = startY;
		this.mDistanceX = distanceX;
		this.mDistanceY = distanceY;
		mIsfinish = false;
		mStartTime = SystemClock.uptimeMillis();
	}

	public boolean computeScrollOffset() {
		if (mIsfinish) {
			return false;
		}

		long timePassed = SystemClock.uptimeMillis()- mStartTime;

		if (timePassed < mDuration) {
			float rate = timePassed / (float) mDuration;
			float interpolation = sInterpolator.getInterpolation(rate);
			LogHelper.d("zoomview", "rate, interpolation " + rate + ", " +interpolation);
			mCurrentX = (int) (mStartX + mDistanceX * interpolation);
			mCurrentY = (int) (mStartY + mDistanceY * interpolation);
		} else if (timePassed >= mDuration) {
			mCurrentX = mStartX + mDistanceX;
			mCurrentY = mStartY + mDistanceY;
			mIsfinish = true;
		}
		return true;
	}

	public int getCurrentX() {
		return mCurrentX;
	}

	public void setCurrentX(int currentX) {
		this.mCurrentX = currentX;
	}

	public int getCurrentY() {
		return mCurrentY;
	}

	public void setCurrentY(int currentY) {
		this.mCurrentY = currentY;
	}

	public final void forceFinished(boolean finished) {
		mIsfinish = finished;
	}

	public boolean isFinish() {
		return mIsfinish;
	}

	static class ViscousFluidInterpolator implements Interpolator {
		/** Controls the viscous fluid effect (how much of it). */
		private static final float VISCOUS_FLUID_SCALE = 8.0f;

		private static final float VISCOUS_FLUID_NORMALIZE;
		private static final float VISCOUS_FLUID_OFFSET;

		static {

			// must be set to 1.0 (used in viscousFluid())
			VISCOUS_FLUID_NORMALIZE = 1.0f / viscousFluid(1.0f);
			// account for very small floating-point error
			VISCOUS_FLUID_OFFSET = 1.0f - VISCOUS_FLUID_NORMALIZE * viscousFluid(1.0f);
		}

		private static float viscousFluid(float x) {
			x *= VISCOUS_FLUID_SCALE;
			if (x < 1.0f) {
				x -= (1.0f - (float)Math.exp(-x));
			} else {
				float start = 0.36787944117f;   // 1/e == exp(-1)
				x = 1.0f - (float)Math.exp(1.0f - x);
				x = start + x * (1.0f - start);
			}
			return x;
		}

		@Override
		public float getInterpolation(float input) {
			final float interpolated = VISCOUS_FLUID_NORMALIZE * viscousFluid(input);
			if (interpolated > 0) {
				return interpolated + VISCOUS_FLUID_OFFSET;
			}
			return interpolated;
		}
	}
}
