package com.subspace.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback, GestureDetector.OnGestureListener  {

	Arena arena;
	PanelThread _thread;
	private GestureDetector gestureScanner;
	
	int x;
	int y;
	Paint paint = new Paint(); 
	Rect viewPort = new Rect();

	public GamePanel(Context context) {
		super(context);
		gestureScanner = new GestureDetector(context,this);
	    
		paint.setColor(Color.GREEN); 
		getHolder().addCallback(this);
		
		//centre it
		x = 1024*16 / 2;
		y = 1024*16/2;			
		
	}

	public void setArena(Arena arena) {
		this.arena = arena;
	}

	@Override
	public void onDraw(Canvas canvas) {
		// draw map if its loaded
		if (arena != null) {
			
			
			int screenWidth = getWidth();
		    int screenHeight = getHeight();
		    	
			canvas.drawText("x " + x + " y " + y, 100,100, paint);			
			viewPort.set(x,y,x+screenWidth,y+screenHeight);
			arena.Lvl.Draw(canvas,viewPort);
			
		}
	}

	
	@Override
    public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
    }
	
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        return true;
    }
    @Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		
        x+=distanceX;
        y+=distanceY;
		
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false); // Allows us to use invalidate() to call onDraw()

		_thread = new PanelThread(getHolder(), this); // Start the thread that
		_thread.setRunning(true); // will make calls to
		_thread.start(); // onDraw()
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			_thread.setRunning(false); // Tells thread to stop
			_thread.join(); // Removes thread from mem.
		} catch (InterruptedException e) {
		}
	}

	// thread to invalidate drawing
	class PanelThread extends Thread {
		private SurfaceHolder _surfaceHolder;
		private GamePanel _panel;
		private boolean _run = false;

		public PanelThread(SurfaceHolder surfaceHolder, GamePanel panel) {
			_surfaceHolder = surfaceHolder;
			_panel = panel;
		}

		public void setRunning(boolean run) { // Allow us to stop the thread
			_run = run;
		}

		@Override
		public void run() {
			Canvas c;
			while (_run) { // When setRunning(false) occurs, _run is
				c = null; // set to false and loop ends, stopping thread
				try {
					c = _surfaceHolder.lockCanvas(null);
					synchronized (_surfaceHolder) {

						// Insert methods to modify positions of items in
						// onDraw()
						postInvalidate();
					}
				} finally {
					if (c != null) {
						_surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}

	
}
