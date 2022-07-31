package com.adams.awcoe.spaceplanar.room;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.utils.WindowResolution;

public class NewRoomMapHeightCrosshairView extends View {
	Bitmap crosshair;
	private Paint linePaint;
	private Paint strokePaint;
	private Paint textPaint;
	int deviceWidth = 0, deviceHeight = 0;
	float y_tilt = 0.0f;
	int bottom_infobar_height = 35;
	
	public NewRoomMapHeightCrosshairView(Context context) {
		super(context);
		this.crosshair = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_newmap_crosshair_4);
		WindowResolution window_resolution = new WindowResolution((Activity) context);
		this.deviceWidth = window_resolution.getDeviceWidth();
		this.deviceHeight = window_resolution.getDeviceHeight();
		linePaint = new Paint();
		strokePaint = new Paint();
		textPaint = new Paint();
	}
	
	public void printYTilt(float y){
		this.y_tilt = y;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		strokePaint.setARGB(255, 0, 0, 0);
	    strokePaint.setTextAlign(Paint.Align.CENTER);
	    strokePaint.setTextSize(22);
	    strokePaint.setTypeface(Typeface.DEFAULT_BOLD);
	    strokePaint.setStyle(Paint.Style.STROKE);
	    strokePaint.setStrokeWidth(2);
	    
	    textPaint.setARGB(255, 255, 255, 255);
	    textPaint.setTextAlign(Paint.Align.CENTER);
	    textPaint.setTextSize(22);
	    textPaint.setTypeface(Typeface.DEFAULT_BOLD);		
		
	    canvas.drawText("SET ROOM HEIGHT", deviceWidth/2, deviceHeight/4, strokePaint);
		canvas.drawText("SET ROOM HEIGHT", deviceWidth/2, deviceHeight/4, textPaint);	
		
		strokePaint.setTextSize(16);
		textPaint.setTextSize(16);
		
		linePaint.setColor(Color.TRANSPARENT);
		canvas.drawPaint(linePaint);
		linePaint.setColor(Color.RED);
		linePaint.setStrokeWidth(2);
		canvas.translate(deviceWidth / 2, deviceHeight / 2);
		canvas.drawLine(0, 0, 0, deviceHeight/2 - bottom_infobar_height, linePaint);
		canvas.drawBitmap(crosshair,0-crosshair.getWidth()/2,0-crosshair.getHeight()/2, linePaint);
		
		canvas.drawText("Angle of View: ", -60, deviceHeight / 4, strokePaint);
		canvas.drawText("Angle of View: ", -60, deviceHeight / 4, textPaint);
		canvas.drawText(this.y_tilt+" dgrs", 50, deviceHeight / 4, strokePaint);
		canvas.drawText(this.y_tilt+" dgrs", 50, deviceHeight / 4, textPaint);
	}
}
