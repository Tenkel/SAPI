package br.ufrj.cos.labia.aips.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SimpleAndarView extends View {

	public interface OnPointSelectListener {
		public void onPointSelected(SimpleAndarView view, int position);
	}

	public static class Point {
		public int counter;
		public float x;
		public float y;
	}
	
	private Bitmap mBitmap;
	private OnPointSelectListener mListener;
	private Point[] mPoints;
	
	private int mSelected;
	private int mPredicted;
	
	private RectF mDst;
	private Rect mRect;
	
	private DisplayHelper mDH;
	
	private Paint mCircleStroke;
	private Paint mCircleSelectedStroke;
	private Paint mCircleFill;
	private Paint mCircleSelectedFill;
	private Paint mTextSelected;
	private Paint mCirclePredictedStroke;
	private Paint mText;
	private boolean mSelectable;
	
	
	public SimpleAndarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public SimpleAndarView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mDst = new RectF();
		mRect = new Rect();
		mDH = new DisplayHelper(context);
		
		mCircleStroke = new Paint();
		mCircleStroke.setAntiAlias(true);
		mCircleStroke.setStyle(Style.STROKE);
		mCircleStroke.setColor(Color.argb(0, 22, 91, 19));
		mCircleStroke.setStrokeWidth(mDH.dpToPx(2));
		
		mCircleFill = new Paint();
		mCircleFill.setStyle(Style.FILL);
		mCircleFill.setColor(Color.argb(208, 0, 0, 0));
		
		mCircleSelectedStroke = new Paint();
		mCircleSelectedStroke.setAntiAlias(true);
		mCircleSelectedStroke.setStyle(Style.STROKE);
		mCircleSelectedStroke.setColor(Color.argb(0, 19, 22, 91));
		mCircleSelectedStroke.setStrokeWidth(mDH.dpToPx(2));

		mCircleSelectedFill = new Paint();
		mCircleSelectedFill.setStyle(Style.FILL);
		mCircleSelectedFill.setColor(Color.argb(208, 116, 166, 0));
		
		mText = new Paint();
		mText.setColor(Color.WHITE);
		mText.setTextAlign(Align.CENTER);

		mTextSelected = new Paint();
		mTextSelected.setColor(Color.BLACK);
		mTextSelected.setTextAlign(Align.CENTER);

		mCirclePredictedStroke = new Paint();
		mCirclePredictedStroke.setAntiAlias(true);
		mCirclePredictedStroke.setStyle(Style.STROKE);
		mCirclePredictedStroke.setColor(Color.rgb(0, 0, 255));
		mCirclePredictedStroke.setPathEffect(new DashPathEffect(
				new float[] { mDH.dpToPx(5), mDH.dpToPx(5) }, 0));
		mCirclePredictedStroke.setStrokeWidth(mDH.dpToPx(5));

		mPredicted = -1;
		mSelected = -1;
	}

	public SimpleAndarView(Context context) {
		super(context);
	}

	public void setImage(Bitmap bitmap) {
		mBitmap = bitmap;
		invalidate();
	}
	
	public void setPoints(Point[] points) {
		mPoints = points;
		mPredicted = -1;
		mSelected = -1;
		
		if (mListener != null)
			mListener.onPointSelected(this, mSelected);

		invalidate();
	}
	
	public void setSelected(int position) {
		if (mPoints == null)
			return;
		
		if (position < -1 || position >= mPoints.length)
			throw new IllegalArgumentException();
		
		mSelected = position;
		invalidate();
	}

	public void setSelectable(boolean s) {
		mSelectable = s;
		mSelected = -1;
		invalidate();
	}
	
	public void setPredicted(int position) {
		mPredicted = position;
		invalidate();
	}
	
	public void incCounter(int position) {
		if (mPoints == null || position == -1)
			return;
		
		if (position < 0 || position >= mPoints.length)
			throw new IllegalArgumentException();
		
		mPoints[position].counter += 1;
		invalidate();
	}

	public void setCounterAt(int position, int c) {
		if (mPoints == null || position == -1)
			return;
		
		if (position < 0 || position >= mPoints.length)
			throw new IllegalArgumentException();
		
		mPoints[position].counter = c;
		invalidate();
	}

	public void setOnPointSelectListener(OnPointSelectListener listener) {
		mListener = listener;
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		updateTextSize(canvas);
		drawMap(canvas);
		drawPoints(canvas);
		
		if (!isEnabled()) 
			canvas.drawARGB(200, 255, 255, 255);
	}
	
	private void updateTextSize(Canvas canvas) {
		mText.setTextSize(47f);
		mText.getTextBounds("0", 0, 1, mRect);
		float size = 0.02f * getWidth() * 47f / (mRect.right - mRect.left);
		mText.setTextSize(size);
		mTextSelected.setTextSize(size);
	}

	private void drawMap(Canvas canvas) {
		if (mBitmap == null) return;
		mDst.set(0, 0, getWidth(), getHeight());
		canvas.drawBitmap(mBitmap, null, mDst, null);
	}

	private void drawPoints(Canvas canvas) {
		if (mPoints == null) return;
		
		for (int i=0;i<mPoints.length;++i) {
			Point p = mPoints[i];
			float wx = getWidth() * p.x;
			float wy = getHeight() * (1f - p.y);

			String msg = Integer.toString(p.counter);
			mText.getTextBounds(msg, 0, msg.length(), mRect);
			
			if (mSelectable) {
				if (i == mSelected) {
					canvas.drawCircle(wx, wy, mDH.dpToPx(15), mCircleSelectedFill);
					canvas.drawCircle(wx, wy, mDH.dpToPx(15), mCircleSelectedStroke);
					canvas.drawText(msg, wx, wy-(mRect.top-mRect.bottom)/2, mTextSelected);
				} else {
					canvas.drawCircle(wx, wy, mDH.dpToPx(15), mCircleFill);
					canvas.drawCircle(wx, wy, mDH.dpToPx(15), mCircleStroke);
					canvas.drawText(msg, wx, wy-(mRect.top-mRect.bottom)/2, mText);
				}
			} else {
				canvas.drawCircle(wx, wy, mDH.dpToPx(15), mCircleFill);
				canvas.drawCircle(wx, wy, mDH.dpToPx(15), mCircleStroke);
				canvas.drawText(msg, wx, wy-(mRect.top-mRect.bottom)/2, mText);
				
				if (i == mPredicted) {
					canvas.drawCircle(wx, wy, mDH.dpToPx(20), mCirclePredictedStroke);
				}
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (super.onTouchEvent(event))
			return true;
		
		if (!mSelectable || !isEnabled() || mPoints == null)
			return false;
		
		if (mPoints.length == 0)
			return false;
		
		float cx = event.getX() / getWidth();
		float cy = 1f - event.getY() / getHeight();
		
		int minI = 0;
		float minD = sdistance(mPoints[0].x, mPoints[0].y, cx, cy);
		
		for (int i=1;i<mPoints.length;++i) {
			float distance = sdistance(mPoints[i].x, mPoints[i].y, cx, cy);
			if (distance < minD) {
				minD = distance;
				minI = i;
			}
		}
		
		if (minI != mSelected) {
			mSelected = minI;
			if (mListener != null)
				mListener.onPointSelected(this, mSelected);
			invalidate();
		}
		
		return true;
	}
	
	private float sdistance(float x1, float y1, float x2, float y2) {
		float dx = x1 - x2;
		float dy = y1 - y2;
		return dx * dx + dy * dy;
	}

}
