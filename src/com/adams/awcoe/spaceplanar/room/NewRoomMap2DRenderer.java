package com.adams.awcoe.spaceplanar.room;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.utils.WindowResolution;

public class NewRoomMap2DRenderer extends View{	
	//Member Variables
	final static int INVALID_POINTER_ID = -1;
	int c_height, c_width;
	int c_width_scale, c_height_scale;
	float x_cord;
	float y_cord;
	float x_pixel_cord;
	float y_pixel_cord;
	//Drawing the Distances between Flags
	float x_cord_1;
	float y_cord_1;
	float x_pixel_cord_1;
	float y_pixel_cord_1;
	float x_cord_2;
	float y_cord_2;
	float x_pixel_cord_2;
	float y_pixel_cord_2;
	float x_scale;
	float y_scale;
    float mLastTouchX;
    float mLastTouchY;
    int mActivePointerId = INVALID_POINTER_ID;
    double area_sum;
    ArrayList<Double> points_distances;
	double a, b, c, s;
	boolean mouse_down=false;
	int mouse_down_flag=0;
	int touched_edge_left;
	int touched_edge_right;
	int deviceWidth, deviceHeight; 
    
	//Android Variables
	Bitmap flags;
	Bitmap flag_selected;
	Paint mPaint;
	Paint circlePaint;
	Paint backPaint;
	Paint textPaint;
	Paint labelStrokePaint;
	Paint labelTextPaint;
	Paint edgeTouchPaint;
	Context con;
	
	//1 arg Constructor 
	public NewRoomMap2DRenderer(Context context) {
		super(context);		
		calc_area_map();
		calc_distaces();
		con=context;
		mPaint=new Paint();
		backPaint = new Paint();
		backPaint.setColor(Color.WHITE);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(3);
		textPaint=new Paint();
		textPaint.setColor(Color.RED);
		edgeTouchPaint=new Paint();
		edgeTouchPaint.setColor(Color.GREEN);
		edgeTouchPaint.setStrokeWidth(5);
		circlePaint=new Paint();
		circlePaint.setColor(Color.BLUE);
		circlePaint.setStyle(Style.FILL_AND_STROKE);
		flags=BitmapFactory.decodeResource(getResources(),
				R.drawable.room_newmap_marker_flag);
		flag_selected=BitmapFactory.decodeResource(getResources(),
				R.drawable.room_newmap_marker_flag_selected);
		touched_edge_left=-1;
		touched_edge_right=-1;
		
		WindowResolution window_resolution = new WindowResolution((Activity) context);
        deviceWidth = window_resolution.getDeviceWidth();
        deviceHeight = window_resolution.getDeviceHeight();
		labelStrokePaint=new Paint();
		labelTextPaint=new Paint();
		labelStrokePaint.setARGB(255, 0, 0, 0);
		labelStrokePaint.setTextAlign(Paint.Align.CENTER);		
		labelStrokePaint.setTypeface(Typeface.DEFAULT_BOLD);
		labelStrokePaint.setStyle(Paint.Style.STROKE);
		labelStrokePaint.setStrokeWidth(2);	    
		labelTextPaint.setARGB(255, 21, 148, 36);
	    labelTextPaint.setTextAlign(Paint.Align.CENTER);	    
	    labelTextPaint.setTypeface(Typeface.DEFAULT_BOLD);		
	}
	//2 arg Constructor 
	public NewRoomMap2DRenderer(Context context, AttributeSet set) {
		super(context, set);
		
		calc_area_map();
		calc_distaces();
		con=context;
		mPaint=new Paint();
		backPaint = new Paint();
		backPaint.setColor(Color.WHITE);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(3);
		textPaint=new Paint();		
		textPaint.setColor(Color.RED);
		circlePaint=new Paint();
		circlePaint.setColor(Color.BLUE);
		edgeTouchPaint=new Paint();
		edgeTouchPaint.setColor(Color.GREEN);
		edgeTouchPaint.setStrokeWidth(5);
		circlePaint.setStyle(Style.FILL_AND_STROKE);
		flags=BitmapFactory.decodeResource(getResources(),
				R.drawable.room_newmap_marker_flag);
		flag_selected=BitmapFactory.decodeResource(getResources(),
				R.drawable.room_newmap_marker_flag_selected);
		touched_edge_left=-1;
		touched_edge_right=-1;
		
		WindowResolution window_resolution = new WindowResolution((Activity) context);
		deviceWidth = window_resolution.getDeviceWidth();
        deviceHeight = window_resolution.getDeviceHeight();
		labelStrokePaint=new Paint();
		labelTextPaint=new Paint();
		labelStrokePaint.setARGB(255, 112, 22, 33);
		labelStrokePaint.setTextAlign(Paint.Align.CENTER);		
		labelStrokePaint.setTypeface(Typeface.DEFAULT_BOLD);
		labelStrokePaint.setStyle(Paint.Style.STROKE);
		labelStrokePaint.setStrokeWidth(2);	    
		labelTextPaint.setARGB(255, 21, 148, 36);
	    labelTextPaint.setTextAlign(Paint.Align.CENTER);	    
	    labelTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
	}
	
	
	//Private methods
	
	//Rounding up to 3decimal point
	private double roundDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("###.###");
		return Double.valueOf(twoDForm.format(d));
	}
	
	
	//Area Calculation for Map
	void calc_area_map() {
		area_sum = 0;
		for (int i = 2; i < NewRoomMap.points3dCordinates_augmented.size(); i++) {
			a = Math.sqrt(Math.pow((NewRoomMap.points3dCordinates_augmented.get(i)
					.getXMark() - NewRoomMap.points3dCordinates_augmented.get(i - 1)
					.getXMark()), 2)
					+ Math.pow(
							(NewRoomMap.points3dCordinates_augmented.get(i).getYMark() - NewRoomMap.points3dCordinates_augmented
									.get(i - 1).getYMark()), 2));

			b = Math.sqrt(Math
					.pow((NewRoomMap.points3dCordinates_augmented.get(i - 1).getXMark() - NewRoomMap.points3dCordinates_augmented
							.get(0).getXMark()), 2)
					+ Math.pow((NewRoomMap.points3dCordinates_augmented.get(i - 1)
							.getYMark() - NewRoomMap.points3dCordinates_augmented.get(0)
							.getYMark()), 2));

			c = Math.sqrt(Math
					.pow((NewRoomMap.points3dCordinates_augmented.get(i).getXMark() - NewRoomMap.points3dCordinates_augmented
							.get(0).getXMark()), 2)
					+ Math.pow(
							(NewRoomMap.points3dCordinates_augmented.get(i).getYMark() - NewRoomMap.points3dCordinates_augmented
									.get(0).getYMark()), 2));

			s = (a + b + c) / 2;

			area_sum = area_sum + (Math.sqrt(s * (s - a) * (s - b) * (s - c)));
		}
	}
	
	//Calculate Wall Distances defined by two adjacent Nodes or Markers
	void calc_distaces() {
		points_distances=new ArrayList<Double>();
		double distance ;
		int index;
		for(index=1;index<NewRoomMap.points3dCordinates_augmented.size();index++)
		{
			 distance=
					  Math.sqrt(Math.pow((NewRoomMap.points3dCordinates_augmented.get(index).getXMark() -
					  NewRoomMap.points3dCordinates_augmented.get(index-1).getXMark()), 2) +
					  Math.pow((NewRoomMap.points3dCordinates_augmented.get(index).getYMark() -
					  NewRoomMap.points3dCordinates_augmented.get(index-1).getYMark()), 2));
			 points_distances.add(distance);
		}
		index=index-1;
	    distance =
			  Math.sqrt(Math.pow((NewRoomMap.points3dCordinates_augmented.get(index).getXMark() -
			  NewRoomMap.points3dCordinates_augmented.get(0).getXMark()), 2) +
			  Math.pow((NewRoomMap.points3dCordinates_augmented.get(index).getYMark() -
			  NewRoomMap.points3dCordinates_augmented.get(0).getYMark()), 2));
	    points_distances.add(distance);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		c_width = canvas.getWidth();
		c_height = canvas.getHeight();
		x_scale=c_width/60;
		y_scale=c_height/60;
		if(deviceWidth < 500){
	    	labelStrokePaint.setTextSize(15);
	    	labelTextPaint.setTextSize(15);
	    	c_width_scale = c_width/3;
			c_height_scale = c_height/3;	    	
	    } else if(deviceWidth > 500 && deviceWidth < 1000){
	    	labelStrokePaint.setTextSize(15);
	    	labelTextPaint.setTextSize(15);
	    	c_width_scale = c_width/2;
			c_height_scale = c_height/2;
	    } else if(deviceWidth > 1000){
	    	labelStrokePaint.setTextSize(20);
	    	labelTextPaint.setTextSize(20);
	    	c_width_scale = c_width/2;
			c_height_scale = c_height/2;
			x_scale=c_width/70;
			y_scale=c_height/70;
	    }			
		
		// Equalizing x Scale and y Scale for same feet-to-pixels conversion in XY axis
		if(x_scale<=y_scale)
			y_scale=x_scale;
		else
			x_scale=y_scale;		
		
		canvas.drawPaint(backPaint);
		Path p = new Path();
		
		// Displaying Location and Room Height Information
		/*canvas.drawText("HEIGHT: " + NewRoomMap2D.room_canvas_height, c_width/2 - 10, 20, labelStrokePaint);
		canvas.drawText("HEIGHT: " + NewRoomMap2D.room_canvas_height, c_width/2 - 10, 20, labelTextPaint);
		
		canvas.drawText("LOCATION: " + NewRoomMap2D.room_canvas_location, c_width/2 - 10, 40, labelStrokePaint);
		canvas.drawText("LOCATION: " + NewRoomMap2D.room_canvas_location, c_width/2 - 10, 40, labelTextPaint);*/
		
		if(deviceWidth < 500){
			canvas.drawText(NewRoomMap2D.room_canvas_area, 50, 20, labelStrokePaint);
			canvas.drawText(NewRoomMap2D.room_canvas_area, 50, 20, labelTextPaint);
			/*canvas.drawText(NewRoomMap2D.room_canvas_location , c_width/2 + 65, 20, labelStrokePaint);
			canvas.drawText(NewRoomMap2D.room_canvas_location, c_width/2 + 65, 20, labelTextPaint);*/
			canvas.drawText("Untitled", c_width/2 + 65, 20, labelStrokePaint);
			canvas.drawText("Untitled", c_width/2 + 65, 20, labelTextPaint);
	    } else if(deviceWidth > 500 && deviceWidth < 1000){
	    	canvas.drawText(NewRoomMap2D.room_canvas_area, 70, 20, labelStrokePaint);
			canvas.drawText(NewRoomMap2D.room_canvas_area, 70, 20, labelTextPaint);
	    	/*canvas.drawText(NewRoomMap2D.room_canvas_location, c_width/2 + c_width/3, 20, labelStrokePaint);
			canvas.drawText(NewRoomMap2D.room_canvas_location, c_width/2 + c_width/3, 20, labelTextPaint);*/
			canvas.drawText("Untitled", c_width/2 + c_width/3, 20, labelStrokePaint);
			canvas.drawText("Untitled", c_width/2 + c_width/3, 20, labelTextPaint);
	    } else if(deviceWidth > 1000){
	    	canvas.drawText(NewRoomMap2D.room_canvas_area, 80, 20, labelStrokePaint);
			canvas.drawText(NewRoomMap2D.room_canvas_area, 80, 20, labelTextPaint);
	    	/*canvas.drawText(NewRoomMap2D.room_canvas_location, c_width/2 + c_width/3, 20, labelStrokePaint);
			canvas.drawText(NewRoomMap2D.room_canvas_location, c_width/2 + c_width/3, 20, labelTextPaint);*/
			canvas.drawText("Untitled", c_width/2 + c_width/3, 20, labelStrokePaint);
			canvas.drawText("Untitled", c_width/2 + c_width/3, 20, labelTextPaint);
	    }
		
		//Drawing the Basic Map Path
		for(int i=0;i<NewRoomMap.points3dCordinates_augmented.size();i++) {
			x_cord=NewRoomMap.points3dCordinates_augmented.get(i).getXMark();
			y_cord=NewRoomMap.points3dCordinates_augmented.get(i).getYMark();
			x_pixel_cord=(c_width_scale+(x_cord*x_scale));
			y_pixel_cord=(c_height_scale+(y_cord*y_scale));
			
			p.setLastPoint(x_pixel_cord, y_pixel_cord);
			p.lineTo(x_pixel_cord, y_pixel_cord);
		}
		//Drawing that Map Path defined by Last Point and First Point
		x_cord=NewRoomMap.points3dCordinates_augmented.get(0).getXMark();
		y_cord=NewRoomMap.points3dCordinates_augmented.get(0).getYMark();
		x_pixel_cord=(c_width_scale+(x_cord*x_scale));
		y_pixel_cord=(c_height_scale+(y_cord*y_scale));
		p.setLastPoint(x_pixel_cord, y_pixel_cord);
		p.lineTo(x_pixel_cord, y_pixel_cord);
		canvas.drawPath(p, mPaint);
		
		//Drawing Flag Bitmap on Map Markers or Nodes
		//Drawing Selected Flag Bitmap if Marker or Node is Selected
		for(int i=0;i<NewRoomMap.points3dCordinates_augmented.size();i++) {
			x_cord=NewRoomMap.points3dCordinates_augmented.get(i).getXMark();
			y_cord=NewRoomMap.points3dCordinates_augmented.get(i).getYMark();
			x_pixel_cord=(c_width_scale+(x_cord*x_scale));
			y_pixel_cord=(c_height_scale+(y_cord*y_scale));
			
			if(mouse_down==true&&i==mouse_down_flag) {
				canvas.drawCircle(x_pixel_cord, y_pixel_cord, 25, circlePaint); 
				canvas.drawBitmap(flag_selected, x_pixel_cord-flags.getWidth()/2,y_pixel_cord-flags.getHeight()/2, backPaint);
			} else {
				canvas.drawBitmap(flags, x_pixel_cord-flags.getWidth()/2,y_pixel_cord-flags.getHeight()/2, backPaint);
				canvas.drawText((i+1)+"", x_pixel_cord-flags.getWidth()/2, y_pixel_cord-flags.getHeight()/2, circlePaint);
			}
		}

		
		//Drawing Distances of Walls on Map Lines
		for(int i=0;i<NewRoomMap.points3dCordinates_augmented.size()-1;i++) {
			x_cord_1=NewRoomMap.points3dCordinates_augmented.get(i).getXMark();
			y_cord_1=NewRoomMap.points3dCordinates_augmented.get(i).getYMark();
			x_pixel_cord_1=(c_width_scale+(x_cord_1*x_scale));
			y_pixel_cord_1=(c_height_scale+(y_cord_1*y_scale));
			x_cord_2=NewRoomMap.points3dCordinates_augmented.get(i+1).getXMark();
			y_cord_2=NewRoomMap.points3dCordinates_augmented.get(i+1).getYMark();
			x_pixel_cord_2=(c_width_scale+(x_cord_2*x_scale));
			y_pixel_cord_2=(c_height_scale+(y_cord_2*y_scale));			
			
			canvas.drawText(roundDecimals(points_distances.get(i))+" fts ",
					(x_pixel_cord_1+x_pixel_cord_2)/2,
				    (y_pixel_cord_1+y_pixel_cord_2)/2, 
				    textPaint);
		}
		//Drawing the Distance for Wall defined by Last Node and First Node
		x_cord_1=NewRoomMap.points3dCordinates_augmented.get(0).getXMark();
		y_cord_1=NewRoomMap.points3dCordinates_augmented.get(0).getYMark();
		x_pixel_cord_1=(c_width_scale+(x_cord_1*x_scale));
		y_pixel_cord_1=(c_height_scale+(y_cord_1*y_scale));
		
		x_cord_2=NewRoomMap.points3dCordinates_augmented.get(NewRoomMap.points3dCordinates_augmented.size()-1).getXMark();
		y_cord_2=NewRoomMap.points3dCordinates_augmented.get(NewRoomMap.points3dCordinates_augmented.size()-1).getYMark();
		x_pixel_cord_2=(c_width_scale+(x_cord_2*x_scale));
		y_pixel_cord_2=(c_height_scale+(y_cord_2*y_scale));
		
		canvas.drawText(roundDecimals(points_distances.get(points_distances.size()-1))+" fts",
				(x_pixel_cord_1+x_pixel_cord_2)/2,
			    (y_pixel_cord_1+y_pixel_cord_2)/2, 
			    textPaint);		
		
		//Marking the Touched Edge with Green Color
		if((touched_edge_left!=-1)&&(touched_edge_right!=-1)) {
				x_cord_1=NewRoomMap.points3dCordinates_augmented.get(this.touched_edge_left).getXMark();
				y_cord_1=NewRoomMap.points3dCordinates_augmented.get(this.touched_edge_left).getYMark();
				x_pixel_cord_1=(c_width_scale+(x_cord_1*x_scale));
				y_pixel_cord_1=(c_height_scale+(y_cord_1*y_scale));
				
				x_cord_2=NewRoomMap.points3dCordinates_augmented.get(this.touched_edge_right).getXMark();
				y_cord_2=NewRoomMap.points3dCordinates_augmented.get(this.touched_edge_right).getYMark();
				x_pixel_cord_2=(c_width_scale+(x_cord_2*x_scale));
				y_pixel_cord_2=(c_height_scale+(y_cord_2*y_scale));
			canvas.drawLine(x_pixel_cord_1,y_pixel_cord_1, x_pixel_cord_2,y_pixel_cord_2,edgeTouchPaint);			
			touched_edge_left=touched_edge_right=-1;
			NewRoomMap2DRenderer.this.invalidate();
		}
	}
}
