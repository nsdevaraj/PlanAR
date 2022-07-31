package com.adams.awcoe.spaceplanar.room;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.utils.AngleOfViewData;
import com.adams.awcoe.spaceplanar.utils.WindowResolution;

public class NewRoomMapMakersView extends View {	
	//Android Variables
	Bitmap room_newmap_marker, room_newmap_crosshair;
	int deviceWidth = 0, deviceHeight = 0;
	private Paint strokePaint;
	private Paint textPaint;
	private Paint linePaint;
	int bottom_infobar_height = 35;
	
	//Member Variables
	float x_tilt, y_tilt, y_tiltcorrection;
	int current_tilt_position, current_y_tilt_position, diff_x, diff_y;
	
	public NewRoomMapMakersView(Context context) {
		super(context);
		room_newmap_marker = BitmapFactory.decodeResource(getResources(), R.drawable.room_newmap_marker);
        room_newmap_crosshair = BitmapFactory.decodeResource(getResources(), R.drawable.symbol_newmap_crosshair_4);
        WindowResolution window_resolution = new WindowResolution((Activity) context);
        deviceWidth = window_resolution.getDeviceWidth();
        deviceHeight = window_resolution.getDeviceHeight();
        strokePaint = new Paint();
    	textPaint = new Paint();
    	linePaint = new Paint();
	}
	
	//Member Methods
	public void draw_markers_inView(float current_tilt, float current_ytilt, float y_tiltcorrection) {
		this.x_tilt = current_tilt;
		this.y_tilt = current_ytilt;
		this.y_tiltcorrection = y_tiltcorrection;
		invalidate();
	}
	double roundDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("###.###");
		return Double.valueOf(twoDForm.format(d));
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
		
	    canvas.drawText("NEW ROOM MAP", deviceWidth/2, deviceHeight/4, strokePaint);
		canvas.drawText("NEW ROOM MAP", deviceWidth/2, deviceHeight/4, textPaint);	
		
		strokePaint.setTextSize(16);
		textPaint.setTextSize(16);
		
	    linePaint.setColor(Color.TRANSPARENT);
		canvas.drawPaint(linePaint);
		linePaint.setColor(Color.RED);
		linePaint.setStrokeWidth(2);		
		//canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
		canvas.translate(deviceWidth / 2, deviceHeight / 2);
		canvas.drawLine(0, 0, 0, deviceHeight/2 - bottom_infobar_height, linePaint);
		canvas.drawBitmap(room_newmap_crosshair,0-room_newmap_crosshair.getWidth()/2,0-room_newmap_crosshair.getHeight()/2, linePaint);
		
		// Display the distance
		canvas.drawText("Projection: ", -50, deviceHeight / 5, strokePaint);
		canvas.drawText("Projection: ", -50, deviceHeight / 5, textPaint);
		if(this.y_tilt-y_tiltcorrection < 0.1 || this.y_tilt-y_tiltcorrection > 90.0){
			canvas.drawText("Invalid", 50, deviceHeight / 5, strokePaint);
			canvas.drawText("Invalid", 50, deviceHeight / 5, textPaint);
		} else {
			canvas.drawText(roundDecimals(NewRoomMap.current_projection) + " fts",
					50, deviceHeight / 5, strokePaint);
			canvas.drawText(roundDecimals(NewRoomMap.current_projection) + " fts",
					50, deviceHeight / 5, textPaint);
		}
		// Display Angle of View
		canvas.drawText("Angle of View: ", -60, deviceHeight / 3, strokePaint);
		canvas.drawText("Angle of View: ", -60, deviceHeight / 3, textPaint);
		if(y_tiltcorrection <= 2){
			canvas.drawText(y_tilt+" dgrs", 50, deviceHeight / 3, strokePaint);
			canvas.drawText(y_tilt+" dgrs", 50, deviceHeight / 3, textPaint);
		} else {
			canvas.drawText(y_tilt-y_tiltcorrection+" dgrs", 50, deviceHeight / 3, strokePaint);
			canvas.drawText(y_tilt-y_tiltcorrection+" dgrs", 50, deviceHeight / 3, textPaint);
		}
		
		
		if(NewRoomMap.mark_start) {
			AngleOfViewData fov=new AngleOfViewData();			
			for(int i=0;i<NewRoomMap.points_augmented.size();i++) {                
				current_tilt_position = fov.find_position((int) x_tilt);
				current_y_tilt_position = (int) NewRoomMap.points_augmented.get(i).getYAngle();

				diff_x = fov.find_position((int) NewRoomMap.points_augmented.get(i)
						.getTilt()) - current_tilt_position;
				diff_y = (int) (current_y_tilt_position - y_tilt);
				if ((Math.abs(diff_x) <= 30)) {
					float x = (diff_x * deviceWidth / 60) - room_newmap_marker.getWidth() / 2;
					float y = 0 - room_newmap_marker.getHeight()
							- (diff_y * deviceHeight / 30);
					canvas.drawBitmap(room_newmap_marker, x, y, strokePaint);
					canvas.drawText(NewRoomMap.points_augmented.get(i).getCordPosition(),
							x, y + 30, strokePaint);
					canvas.drawText(NewRoomMap.points_augmented.get(i).getCordPosition(),
							x, y + 30, textPaint);
				}
			}
		}
		
	}
	
	
}
