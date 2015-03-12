package br.ufrj.cos.labia.aips.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class AndarGridView extends View {

	private int[][] mGrid;
	private float[][] mConfidences;
	private int mPredX;
	private int mPredY;
	private int mCurX;
	private int mCurY;
	private int mWidth;
	private int mHeight;
	private Paint mCircleFill;
	private Paint mCircleStroke;
	private RectF mRectF;
	private Rect mRect;
	private Paint mText;
	private Paint mSelected;
	private Paint mPredicted;
	private Paint mConfidenceStroke;
	private DisplayHelper mDH;
	private Paint mRoomStroke;
	private Paint mRoomFill;
	private boolean mShowConfidence;
	private boolean mShowSelected;
	private boolean mShowPredicted;

	public AndarGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		Log.i("RoomView", "1");
	}

	public AndarGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i("RoomView", "2");

		mShowPredicted = false;
		mShowSelected = true;
		mPredX = -1;
		mPredY = -1;
		
		mRect = new Rect();
		mRectF = new RectF();
		mDH = new DisplayHelper(context);
		
		mText = new Paint();
		mText.setTextSize(25);
		mText.setTextAlign(Align.CENTER);

		mCircleStroke = new Paint();
		mCircleStroke.setAntiAlias(true);
		mCircleStroke.setStyle(Style.STROKE);
		mCircleStroke.setColor(Color.BLACK);
		mCircleStroke.setStrokeWidth(mDH.dpToPx(3));

		mCircleFill = new Paint();
		mCircleFill.setStyle(Style.FILL);
		mCircleFill.setColor(Color.BLACK);

		mSelected = new Paint();
		mSelected.setStrokeWidth(mDH.dpToPx(3));
		mSelected.setAntiAlias(true);
		mSelected.setStyle(Style.STROKE);
		mSelected.setColor(Color.rgb(0, 0, 255));
		mSelected.setPathEffect(new DashPathEffect(
				new float[] { mDH.dpToPx(5), mDH.dpToPx(5) }, 0));

		mPredicted = new Paint();
		mPredicted.setStrokeWidth(mDH.dpToPx(3));
		mPredicted.setAntiAlias(true);
		mPredicted.setStyle(Style.STROKE);
		mPredicted.setColor(Color.rgb(255, 0, 0));
		mPredicted.setPathEffect(new DashPathEffect(
				new float[] { mDH.dpToPx(5), mDH.dpToPx(5) }, 0));

		mConfidenceStroke = new Paint();
		mConfidenceStroke.setStrokeWidth(mDH.dpToPx(3));
		mConfidenceStroke.setAntiAlias(true);
		mConfidenceStroke.setStyle(Style.STROKE);
		mConfidenceStroke.setColor(Color.rgb(0, 255, 0));
		
		mRoomStroke = new Paint();
		mRoomStroke.setStyle(Style.STROKE);
		mRoomStroke.setStrokeWidth(mDH.dpToPx(3));
		mRoomStroke.setColor(Color.rgb(120, 150, 190));
		
		mRoomFill = new Paint();
		mRoomFill.setStyle(Style.FILL);
		mRoomFill.setColor(Color.rgb(220, 220, 240));
	}

	public AndarGridView(Context context) {
		super(context);
		Log.i("RoomView", "3");
	}

	public void setSize(int width, int height) {
		mGrid = new int[width][height];
		mConfidences = new float[width][height];
		mHeight = height;
		mWidth = width;
		mPredX = -1;
		mPredY = -1;
		mCurX = 0;
		mCurY = 0;
	}

	public void setPredicted(int x, int y) {
		mPredX = x;
		mPredY = y;
		invalidate();
	}

	public void addCollected(int x, int y) {
		mGrid[x][y]++;
		invalidate();
	}

	public void setConfidence(int x, int y, float c) {
		mConfidences[x][y] = c;
		invalidate();
	}

	public void clearConfidences() {
		for (int i=0;i<mConfidences.length;++i) {
			for (int j=0;j<mConfidences[i].length;++j) {
				mConfidences[i][j] = 0f;
			}
		}
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (mWidth == 0 || mHeight == 0)
			return;

		float r = mDH.dpToPx(3);
		mRectF.set(r, r, canvas.getWidth()-r, canvas.getHeight()-r);
		canvas.drawRoundRect(mRectF, r, r, mRoomFill);
		canvas.drawRoundRect(mRectF, r, r, mRoomStroke);
		
		int spaceX = canvas.getWidth() / mWidth;
		int spaceY = canvas.getHeight() / mHeight;

		int startX = spaceX / 2;
		int startY = spaceY / 2;

		int wx, wy;
		for (int y = 0; y < mHeight; ++y) {
			for (int x = 0; x < mWidth; ++x) {
				wx = startX + x * spaceX;
				wy = startY + y * spaceY;

				String msg = Integer.toString(mGrid[x][y]);
				mText.getTextBounds(msg, 0, msg.length(), mRect);
				canvas.drawText(msg, wx, wy-(mRect.top-mRect.bottom)/2, mText);
				canvas.drawCircle(wx, wy, mDH.dpToPx(20), mCircleStroke);
				//drawCollected(canvas, mDH.dpToPx(10), wx, wy, mGrid[x][y]);

				if (mShowPredicted && x == mPredX && y == mPredY) {
					canvas.drawCircle(wx, wy, mDH.dpToPx(25), mPredicted);
				}

				if (mShowSelected && x == mCurX && y == mCurY) {
					canvas.drawCircle(wx, wy, mDH.dpToPx(30), mSelected);
				}

				if (mShowPredicted && mShowConfidence) {
					drawConfidence(canvas, mDH.dpToPx(35), wx, wy, mConfidences[x][y]);
				}
			}
		}
		
		if (!isEnabled()) {
			canvas.drawARGB(128, 255, 255, 255);
		}
	}

	private void drawConfidence(Canvas canvas, int radius, int wx, int wy,
			float c) {
		mRectF.set(wx - radius, wy - radius, wx + radius, wy + radius);
		canvas.drawArc(mRectF, 0, 360 * c, false, mConfidenceStroke);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (super.onTouchEvent(event))
			return true;

		if (!isEnabled())
			return false;
		
		int mx = (int) event.getX();
		int my = (int) event.getY();

		int spaceX = getWidth() / mWidth;
		int spaceY = getHeight() / mHeight;

		int startX = spaceX / 2;
		int startY = spaceY / 2;

		int bestDistance = Integer.MAX_VALUE;
		int wx, wy, distance, bx = 0, by = 0;
		for (int x = 0; x < mWidth; ++x) {
			for (int y = 0; y < mHeight; ++y) {
				wx = startX + x * spaceX;
				wy = startY + y * spaceY;

				distance = (wx - mx) * (wx - mx) + (wy - my) * (wy - my);

				if (distance < bestDistance) {
					bestDistance = distance;
					bx = x;
					by = y;
				}
			}
		}

		if (bx != mCurX || by != mCurY)
			invalidate();

		mCurX = bx;
		mCurY = by;

		return true;
	}

	public int getSelectedX() {
		return mCurX;
	}

	public int getSelectedY() {
		return mCurY;
	}

	public void setShowConfidence(boolean show) {
		mShowConfidence = show;
	}

	public void setShowSelected(boolean b) {
		mShowSelected = b;
		invalidate();
	}

	public void setShowPredicted(boolean b) {
		mShowPredicted = b;
		invalidate();
	}

}
