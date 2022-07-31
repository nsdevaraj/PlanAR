package com.adams.awcoe.spaceplanar.floors;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.adams.awcoe.spaceplanar.floors.modeldata.FloorModelPointsCordinate;

public class ViewFloorMap2DRenderer extends View {
	
	private Paint mRoomPaint;
	private Paint paint;
	Paint mFloorBoundaryPaintRect;
	Paint mRoomText;
	Paint mText;
	int c_width;
	int c_height;
	private Object floor_x_metric;
	private float floor_y_metric;

	public ViewFloorMap2DRenderer(Context context) {
		super(context, null, 0);
		paint = new Paint();
		mRoomPaint = new Paint();
		mRoomText=new Paint();
		mFloorBoundaryPaintRect = new Paint();
		mText=new Paint();
		mText.setColor(Color.BLACK);
		mRoomPaint.setColor(Color.BLACK);
		mRoomText.setColor(Color.BLACK);
		mRoomText.setStyle(Style.STROKE);
		mRoomText.setStrokeWidth(3);
		mFloorBoundaryPaintRect.setColor(Color.BLACK);
		mFloorBoundaryPaintRect.setStrokeWidth(5);
		mFloorBoundaryPaintRect.setStyle(Style.STROKE);
	}

	public ViewFloorMap2DRenderer(Context context, AttributeSet attrs) {
		super(context, attrs);

		paint = new Paint();
		mRoomPaint = new Paint();
		mRoomText=new Paint();
		mFloorBoundaryPaintRect = new Paint();
		mText=new Paint();
		mText.setColor(Color.BLACK);
		mRoomPaint.setColor(Color.BLACK);
		mRoomText.setColor(Color.BLACK);
		mRoomText.setStyle(Style.STROKE);
		mRoomText.setStrokeWidth(3);
		mFloorBoundaryPaintRect.setColor(Color.BLACK);
		mFloorBoundaryPaintRect.setStrokeWidth(5);
		mFloorBoundaryPaintRect.setStyle(Style.STROKE);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		c_height = canvas.getHeight();
		c_width = canvas.getWidth();
		paint.setColor(Color.WHITE);
		canvas.drawPaint(paint);
		canvas.save();

		for(int index=0;index<ViewFloorMap.floor_model.size();index++) {
			ArrayList<FloorModelPointsCordinate> singlemapModel = ViewFloorMap.floor_model.get(index);
			Path p = new Path();
			for (int i = 0; i <= singlemapModel.size(); i++) {
				if (i > 0) {
					p.setLastPoint(singlemapModel.get(i - 1).getXMark()*5, singlemapModel.get(i - 1).getYMark()*5);
					p.lineTo(singlemapModel.get(i - 1).getXMark()*5, singlemapModel.get(i - 1).getYMark()*5);
				}
			}
			canvas.drawPath(p, mRoomPaint);
		}
		
		if(ViewFloorMap.room_touched_flag) {
			ArrayList<FloorModelPointsCordinate> singlemapModel = ViewFloorMap.floor_model.get(ViewFloorMap.room_touched_index);
			mRoomPaint.setColor(Color.GREEN);
			Path p = new Path();
			for (int i = 0; i <= singlemapModel.size(); i++) {
				if (i > 0) {
					p.setLastPoint(singlemapModel.get(i - 1).getXMark()*5, singlemapModel.get(i - 1).getYMark()*5);
					p.lineTo(singlemapModel.get(i - 1).getXMark()*5, singlemapModel.get(i - 1).getYMark()*5);
				}
			}
			canvas.drawPath(p, mRoomPaint);
			mRoomPaint.setColor(Color.BLACK);
		}
		
		if(ViewFloorMap.floor_model.size()>0) {
		//drawing Floor Rectangle
		canvas.drawRect(ViewFloorMap.floor_x_min*5,ViewFloorMap.floor_y_min*5,ViewFloorMap.floor_x_max*5+20,ViewFloorMap.floor_y_max*5+20, mFloorBoundaryPaintRect);
        //Drawing Floor Metrics
		//Draw Floor Metrics
		floor_x_metric=(ViewFloorMap.floor_x_max - ViewFloorMap.floor_x_min);
		floor_y_metric=(ViewFloorMap.floor_y_max - ViewFloorMap.floor_y_min);
		mText.setTypeface(Typeface.DEFAULT_BOLD);
		canvas.drawText("Width of Floor : "+ floor_y_metric+" fts", (ViewFloorMap.floor_x_max*5)+30, (ViewFloorMap.floor_y_max*5)/2, mText);
		canvas.drawText("Length of Floor : "+ floor_x_metric+" fts", (ViewFloorMap.floor_x_max*5)/2, (ViewFloorMap.floor_y_max*5)+40, mText);
		}
	}

}
