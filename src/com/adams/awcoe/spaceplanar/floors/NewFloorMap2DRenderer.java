package com.adams.awcoe.spaceplanar.floors;
import java.util.ArrayList;
import java.util.Stack;

import com.adams.awcoe.spaceplanar.floors.modeldata.FloorModelPointsCordinate;

import com.adams.awcoe.spaceplanar.app.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class NewFloorMap2DRenderer extends View {
	int c_width;
	int c_height;
	Context c;
	float floor_x_max;
	float floor_x_min;
	float floor_y_max;
	float floor_y_min;
	float floor_x_metric;
	float floor_y_metric;
	float map_y_max;
	float map_y_min;
	float map_x_max;
	float map_x_min;
    
	public static class Point {
		public float x;
		public float y;
		public int pid = -1;
		public boolean isOk = false;
	}
	
	public static class PointMaxMin {
		float x_max;
		float x_min;
		float y_max;
		float y_min;
		String RoomName;
	}

	Paint mPaintDragPointer;
	Paint mPointPaintMap;
	Paint mFloorBoundaryPaintRect;
	Paint mRoomText;
	Drawable room_drawable;
	private Paint paint;
	public static ArrayList<Point> mapfloorPoints;
	static public Stack<Point> mPoints;
	static public ArrayList<PointMaxMin> mapsMaxMinPoints;
	
	public NewFloorMap2DRenderer(Context context) {
		super(context, null, 0);
		c = context;
		mapfloorPoints = new ArrayList<Point>();
		mPoints = new Stack<Point>();
		mapsMaxMinPoints=new ArrayList<PointMaxMin>();
		mFloorBoundaryPaintRect = new Paint();
		mPointPaintMap = new Paint();
		mPaintDragPointer = new Paint();
		mRoomText=new Paint();
		room_drawable = getResources().getDrawable(R.drawable.floor_background);
		floor_x_max = 0;
		floor_x_min = 0;
		floor_y_max = 0;
		floor_y_min = 0;
		map_x_max = 0;
		map_x_min = 0;
		map_y_max = 0;
		map_y_min = 0;
		paint = new Paint();
		
		mPointPaintMap.setColor(Color.BLUE);
		mPointPaintMap.setStyle(Style.STROKE);
		mPointPaintMap.setStyle(Paint.Style.STROKE);
		mPointPaintMap.setStrokeJoin(Paint.Join.ROUND);
		mPointPaintMap.setStrokeCap(Paint.Cap.ROUND);
		mPointPaintMap.setStrokeWidth(3);
		mPaintDragPointer.setColor(Color.BLUE);
		mFloorBoundaryPaintRect.setColor(Color.BLACK);
		mFloorBoundaryPaintRect.setStrokeWidth(5);
		mFloorBoundaryPaintRect.setStyle(Style.STROKE);
		mRoomText.setColor(Color.WHITE);
		mRoomText.setStrokeWidth(1);
	}

	public NewFloorMap2DRenderer(Context context, AttributeSet attrs) {
		super(context, attrs);
      
		c = context;
		mapfloorPoints = new ArrayList<Point>();
		mPoints = new Stack<Point>();
		mapsMaxMinPoints=new ArrayList<PointMaxMin>();
		mFloorBoundaryPaintRect = new Paint();
		mPointPaintMap = new Paint();
		mPaintDragPointer = new Paint();
		mRoomText=new Paint();
		room_drawable = getResources().getDrawable(R.drawable.floor_background);
		floor_x_max = 0;
		floor_x_min = 0;
		floor_y_max = 0;
		floor_y_min = 0;
		map_x_max = 0;
		map_x_min = 0;
		map_y_max = 0;
		map_y_min = 0;
		paint = new Paint();
		mPointPaintMap.setColor(Color.BLACK);
		mPointPaintMap.setStyle(Style.STROKE);
		mPointPaintMap.setStyle(Paint.Style.STROKE);
		mPointPaintMap.setStrokeJoin(Paint.Join.ROUND);
		mPointPaintMap.setStrokeCap(Paint.Cap.ROUND);
		mPointPaintMap.setStrokeWidth(3);
		mPaintDragPointer.setColor(Color.BLUE);
		mFloorBoundaryPaintRect.setColor(Color.BLACK);
		mFloorBoundaryPaintRect.setStrokeWidth(5);
		mFloorBoundaryPaintRect.setStyle(Style.STROKE);
		mRoomText.setColor(Color.WHITE);
		mRoomText.setStrokeWidth(1);

	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		c_height = canvas.getHeight();
		c_width = canvas.getWidth();
		paint.setColor(Color.WHITE);
		canvas.drawPaint(paint);
		canvas.save();
		mapsMaxMinPoints.clear();
		
		if(NewFloorMap.map_clicked) {
			for (Point point : mPoints) {
				if (point.isOk) {
					ArrayList<FloorModelPointsCordinate> singlemapModel = NewFloorMap.floor_model.get(NewFloorMap.floor_map_index);
					Path p = new Path();
					for (int i = 0; i <= singlemapModel.size(); i++) {
						if (i > 0) {
							p.setLastPoint(point.x
									+ singlemapModel.get(i - 1).getXMark() * 5,
									point.y + singlemapModel.get(i - 1).getYMark()
											* 5);
							p.lineTo(point.x + singlemapModel.get(i - 1).getXMark()
									* 5, point.y
									+ singlemapModel.get(i - 1).getYMark() * 5);
						}

					}
					canvas.drawPath(p, mPaintDragPointer);
				}
			}
		} else if(NewFloorMap.map_reposition_clicked) {
			for (Point point : mPoints) {
				if (point.isOk) {
					ArrayList<FloorModelPointsCordinate> singlemapModel = NewFloorMap.floor_model
							.get(NewFloorMap.map_index_reposition_clicked);
					Path p = new Path();
					for (int i = 0; i <= singlemapModel.size(); i++) {
						if (i > 0) {
							p.setLastPoint(point.x
									+ singlemapModel.get(i - 1).getXMark() * 5,
									point.y + singlemapModel.get(i - 1).getYMark()
											* 5);
							p.lineTo(point.x + singlemapModel.get(i - 1).getXMark()
									* 5, point.y
									+ singlemapModel.get(i - 1).getYMark() * 5);
						}

					}
					mPaintDragPointer.setColor(Color.GREEN);
					canvas.drawPath(p, mPaintDragPointer);
					mPaintDragPointer.setColor(Color.BLUE);
				}
			}
		}
		

		//Draw Room on Floor at Dragged position
		for (int j = 0; j < mapfloorPoints.size(); j++) {
			Path p = new Path();
			ArrayList<FloorModelPointsCordinate> singlemapModel = NewFloorMap.floor_model
					.get(j);
			p.moveTo(mapfloorPoints.get(j).x + singlemapModel.get(0).getXMark()
					* 5, mapfloorPoints.get(j).y
					+ singlemapModel.get(0).getYMark() * 5);

			map_x_max = mapfloorPoints.get(j).x
					+ singlemapModel.get(0).getXMark() * 5;
			map_x_min = mapfloorPoints.get(j).x
					+ singlemapModel.get(0).getXMark() * 5;
			map_y_max = mapfloorPoints.get(j).y
					+ singlemapModel.get(0).getYMark() * 5;
			map_y_min = mapfloorPoints.get(j).y
					+ singlemapModel.get(0).getYMark() * 5;

			for (int i = 0; i <= singlemapModel.size(); i++) {
				if (i != singlemapModel.size()) {
					p.lineTo(mapfloorPoints.get(j).x
							+ singlemapModel.get(i).getXMark() * 5,
							mapfloorPoints.get(j).y
									+ singlemapModel.get(i).getYMark() * 5);

					if (floor_x_max < mapfloorPoints.get(j).x
							+ singlemapModel.get(i).getXMark() * 5)
						floor_x_max = mapfloorPoints.get(j).x
								+ singlemapModel.get(i).getXMark() * 5;
					if (floor_x_min > mapfloorPoints.get(j).x
							+ singlemapModel.get(i).getXMark() * 5)
						floor_x_min = mapfloorPoints.get(j).x
								+ singlemapModel.get(i).getXMark() * 5;
					if (floor_y_max < mapfloorPoints.get(j).y
							+ singlemapModel.get(i).getYMark() * 5)
						floor_y_max = mapfloorPoints.get(j).y
								+ singlemapModel.get(i).getYMark() * 5;
					if (floor_y_min > mapfloorPoints.get(j).y
							+ singlemapModel.get(i).getYMark() * 5)
						floor_y_min = mapfloorPoints.get(j).y
								+ singlemapModel.get(i).getYMark() * 5;

					if (map_x_max < mapfloorPoints.get(j).x
							+ singlemapModel.get(i).getXMark() * 5)
						map_x_max = mapfloorPoints.get(j).x
								+ singlemapModel.get(i).getXMark() * 5;
					if (map_x_min > mapfloorPoints.get(j).x
							+ singlemapModel.get(i).getXMark() * 5)
						map_x_min = mapfloorPoints.get(j).x
								+ singlemapModel.get(i).getXMark() * 5;
					if (map_y_max < mapfloorPoints.get(j).y
							+ singlemapModel.get(i).getYMark() * 5)
						map_y_max = mapfloorPoints.get(j).y
								+ singlemapModel.get(i).getYMark() * 5;
					if (map_y_min > mapfloorPoints.get(j).y
							+ singlemapModel.get(i).getYMark() * 5)
						map_y_min = mapfloorPoints.get(j).y
								+ singlemapModel.get(i).getYMark() * 5;

				} else {
					p.lineTo(mapfloorPoints.get(j).x
							+ singlemapModel.get(0).getXMark() * 5,
							mapfloorPoints.get(j).y
									+ singlemapModel.get(0).getYMark() * 5);
				}
			}
			
			// Draw Room Map on Floor by Rectangular Region
			//mPointPaintMap.setColor(Color.BLUE);
			//mPointPaintMap.setStrokeWidth(6);
			//canvas.drawRect(map_x_min, map_y_min, map_x_max,map_y_max,mPointPaintMap);
			Rect myRoom=new Rect((int)map_x_min, (int)map_y_min, (int)map_x_max,(int)map_y_max);
			room_drawable.setBounds(myRoom);
			room_drawable.draw(canvas);
			PointMaxMin maxmin=new PointMaxMin();
			maxmin.x_max=map_x_max;
			maxmin.x_min=map_x_min;
			maxmin.y_min=map_y_min;
			maxmin.y_max=map_y_max;
			mapsMaxMinPoints.add(maxmin);
			//Draw Actual Room Path
			mPointPaintMap.setStrokeWidth(1);
			mPointPaintMap.setColor(Color.WHITE);
			canvas.drawPath(p, mPointPaintMap);
			mPointPaintMap.setColor(Color.BLACK);
		}
		
		// Drawing Room Name
		mRoomText.setTextSize(15);
		mRoomText.setTypeface(Typeface.DEFAULT_BOLD);
		mRoomText.setColor(Color.BLUE);		
		for(int i=0;i<mapsMaxMinPoints.size();i++) {
			String roomName=NewFloorMap.floor_names_list.get(i);
			canvas.drawText(roomName,
		    ((mapsMaxMinPoints.get(i).x_max + mapsMaxMinPoints.get(i).x_min) / 2)-roomName.length(), ((mapsMaxMinPoints.get(i).y_max + mapsMaxMinPoints.get(i).y_min) / 2),
			 mRoomText);
		}
		mRoomText.setColor(Color.BLACK);
		if(mapfloorPoints.size()>=1) {
	    // Dynamic Floor Boundary
		canvas.drawRect(floor_x_min, floor_y_min, floor_x_max+20, floor_y_max+20,
				mFloorBoundaryPaintRect);
		//Draw Floor Metrics
		floor_x_metric=(floor_x_max/5 - floor_x_min/5);
		floor_y_metric=(floor_y_max/5 - floor_y_min/5);
		canvas.drawText("Width of Floor : "+ floor_y_metric+" fts", floor_x_max+30, floor_y_max/2, mRoomText);
		canvas.drawText("Length of Floor : "+ floor_x_metric+" fts", floor_x_max/2, floor_y_max+40, mRoomText);
	   }
		mRoomText.setColor(Color.WHITE);

		canvas.restore();
	}

}
