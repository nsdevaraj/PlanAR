package com.adams.awcoe.spaceplanar.room;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

import com.adams.awcoe.spaceplanar.app.R;

public class ViewRoomMap3D extends Activity{
	// Android Variables
		private ScaleGestureDetector mScaleDetector;
		/*ImageButton translateLEFT;
		ImageButton translateUP;
		ImageButton translateCENTER;
		ImageButton translateDOWN;		
		ImageButton translateRIGHT;*/
		GLSurfaceView surfaceView;

		// Members Variables
		private String height;
		ViewRoomMap3DRenderer mRenderer;
		private float mScaleFactor = 1.0f;
		private final float TOUCH_ROTATE_FACTOR = 180.0f / 320;
		private static final int INVALID_POINTER_ID = -1;
		private int mActivePointerId = INVALID_POINTER_ID;
		private float mAngleX;
		private float mAngleY;
		private float mPosX=0.0f;
		private float mPosY=0.0f;
		static float mCPosX=0;
		static float mCPosY=0;
		static float mCPosZ=-20f;
		
		private float mLastTouchX;
		private float mLastTouchY;
		
		// Member Methods
		private class ScaleListener extends
				ScaleGestureDetector.SimpleOnScaleGestureListener {

			public boolean onScale(ScaleGestureDetector detector) {
				mScaleFactor *= detector.getScaleFactor();
				mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor,2.0f));
				return true;
			}
		}
		
		private void init(){
		    height=ViewRoomMap.readMapInfo.get(4);
			/*translateLEFT = (ImageButton) findViewById(R.id.room_newmap_leftTranslateMap);
			translateUP = (ImageButton) findViewById(R.id.room_newmap_upTranslateMap);
			translateCENTER = (ImageButton) findViewById(R.id.room_newmap_centerTranslateMap);
			translateDOWN = (ImageButton) findViewById(R.id.room_newmap_downTranslateMap);			
			translateRIGHT = (ImageButton) findViewById(R.id.room_newmap_rightTranslateMap);*/
			surfaceView = (GLSurfaceView) ViewRoomMap3D.this.findViewById(R.id.room_newmap_3dviewsurface);			
			if (surfaceView != null) {
				mRenderer = new ViewRoomMap3DRenderer(ViewRoomMap3D.this,height);
				mPosX = 0.0f;
				mPosY = -3.0f;
				//mPosY = 0.0f;
				mRenderer.setTranslate(mPosX,mPosY);
				surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
				surfaceView.setRenderer(mRenderer);
				surfaceView.setZOrderOnTop(true);
				surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
				surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
				mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());
				surfaceView.setOnTouchListener(new OnTouchListener() {					
					public boolean onTouch(View v, final MotionEvent ev) {
						mScaleDetector.onTouchEvent(ev);
						mCPosY = mCPosY + .2f;
						mCPosX = mCPosX + .2f;
						mCPosZ = mCPosZ + .2f;
						final int action = ev.getAction();
						switch (action & MotionEvent.ACTION_MASK) {
						case MotionEvent.ACTION_DOWN: {
							final float x = ev.getX();
							final float y = ev.getY();
							mLastTouchX = x;
							mLastTouchY = y;
							mActivePointerId = ev.getPointerId(0);
							break;
						}

						case MotionEvent.ACTION_MOVE: {
							final int pointerIndex = ev
									.findPointerIndex(mActivePointerId);
							final float x = ev.getX(pointerIndex);
							final float y = ev.getY(pointerIndex);

							// Only rotate if the ScaleGestureDetector isn't
							// processing a gesture.
							if (!mScaleDetector.isInProgress()) {
								float dx = x - mLastTouchX;
								float dy = y - mLastTouchY;

								mAngleX += dx * TOUCH_ROTATE_FACTOR;
								mAngleY += dy * TOUCH_ROTATE_FACTOR;

							}

							mLastTouchX = x;
							mLastTouchY = y;

							break;
						}

						case MotionEvent.ACTION_UP: {
							mActivePointerId = INVALID_POINTER_ID;
							break;
						}

						case MotionEvent.ACTION_CANCEL: {
							mActivePointerId = INVALID_POINTER_ID;
							break;
						}

						case MotionEvent.ACTION_POINTER_UP: {
							final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
							final int pointerId = ev.getPointerId(pointerIndex);
							if (pointerId == mActivePointerId) {
								final int newPointerIndex = pointerIndex == 0 ? 1
										: 0;
								mLastTouchX = ev.getX(newPointerIndex);
								mLastTouchY = ev.getY(newPointerIndex);
								mActivePointerId = ev.getPointerId(newPointerIndex);
							}
							break;
						}
					}
					//mRenderer.setScaleFactor(mScaleFactor);
					mRenderer.setRotation(mAngleX, mAngleY);
					
					return true;
				}
			});
		}
	}
		
		private void startListeners() {
			/*translateLEFT.setOnClickListener(new OnClickListener() {				
				public void onClick(View v) {
					mPosX = mPosX - 1;
					mRenderer.setTranslate(mPosX - 1, mPosY);
				}
			});
			
			translateUP.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mPosY = mPosY + 1;
					mRenderer.setTranslate(mPosX, mPosY + 1);
				}
			});
			
			translateCENTER.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mPosX = 0.0f;
					mPosY = -3.0f;
					mRenderer.setTranslate(mPosX, mPosY);
				}
			});

			translateDOWN.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mPosY = mPosY - 1;
					mRenderer.setTranslate(mPosX, mPosY - 1);
				}
			});			

			translateRIGHT.setOnClickListener(new OnClickListener() {				
				public void onClick(View v) {
					mPosX = mPosX + 1;
					mRenderer.setTranslate(mPosX + 1, mPosY);
				}
			});*/
		}
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.room_newmap_generate3dview);
	        init();
	        startListeners();
		}

		@Override
		public void onBackPressed() {
			finish();
		}
}
