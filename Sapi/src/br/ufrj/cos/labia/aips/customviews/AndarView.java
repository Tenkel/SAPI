package br.ufrj.cos.labia.aips.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class AndarView extends SurfaceView implements Callback {

	private GameThread mThread;

	public AndarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AndarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
	}

	public AndarView(Context context) {
		super(context);
	}
	
	public void onLoopDraw(Canvas canvas) {
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mThread = new GameThread(getHolder());
		mThread.setRunning(true);
		mThread.start();
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		boolean retry = true;
		while (retry) {
			try {
				mThread.setRunning(false);
				mThread.join();
				retry = false;
			} catch (InterruptedException e) {
				// Try again until thread ends
			}
		}
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	class GameThread extends Thread {

		private boolean mRunning;
		
		private SurfaceHolder mHolder;

		public GameThread(SurfaceHolder holder) {
			mHolder = holder;
		}

		public void setRunning(boolean r) {
			mRunning = r;
		}

		@Override
		public void run() {
			
			long frameTime = 1000 / 30;
			long start, ellapsed, sleepTime;
			
			Canvas canvas;
			while (mRunning) {
				canvas = null;
				
				try {
					start = System.currentTimeMillis();
					
					canvas = mHolder.lockCanvas();
					if (canvas != null) {
						try {
							onLoopDraw(canvas);
						} finally {
							mHolder.unlockCanvasAndPost(canvas);
						}
					}
					
					// Controla FPS
					ellapsed = System.currentTimeMillis() - start;
					sleepTime = frameTime - ellapsed;
					if (sleepTime > 0) sleep(sleepTime);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}
	
}
