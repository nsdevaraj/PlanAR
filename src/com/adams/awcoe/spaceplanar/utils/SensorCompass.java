package com.adams.awcoe.spaceplanar.utils;

import com.adams.awcoe.spaceplanar.app.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SensorCompass extends ImageView {
	Paint paint;
	int direction = 0;

	public SensorCompass(Context context) {
		super(context);

		paint = new Paint();
		paint.setColor(Color.TRANSPARENT);
		paint.setStrokeWidth(2);
		paint.setStyle(Style.STROKE);
		this.setImageResource(R.drawable.room_newmap_sesnorcompass);
	}

	public SensorCompass(Context context, AttributeSet set) {
		super(context, set);
		paint = new Paint();
		paint.setColor(Color.TRANSPARENT);
		paint.setStrokeWidth(2);
		paint.setStyle(Style.STROKE);
		this.setImageResource(R.drawable.room_newmap_sesnorcompass);
	}

	@Override
	public void onDraw(Canvas canvas) {
		int height = this.getHeight();
		int width = this.getWidth();
		canvas.rotate(-direction, width / 2, height / 2);
		super.onDraw(canvas);
	}

	public void setDirection(int direction) {
		this.direction = direction;
		this.invalidate();
	}

}
