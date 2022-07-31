package com.adams.awcoe.spaceplanar.room.mapdata;

public class AugmentedPointsData {
	
	double projection;
	float XAngle;
	float XLRange;
	float XRRange;
	public float getXLRange() {
		return XLRange;
	}

	public void setXLRange(float xLRange) {
		XLRange = xLRange;
	}

	public float getXRRange() {
		return XRRange;
	}

	public void setXRRange(float xRRange) {
		XRRange = xRRange;
	}
	float YAngle;
	String CordPosition;

	public String getCordPosition() {
		return CordPosition;
	}

	public void setCordPosition(String cordPosition) {
		CordPosition = cordPosition;
	}

	public float getYAngle() {
		return YAngle;
	}

	public void setYAngle(float yAngle) {
		YAngle = yAngle;
	}
	public static int index=-1;
	
	@SuppressWarnings("static-access")
	public AugmentedPointsData()
	{
		this.index=this.index+1;
	}
	
	public int getIndex() {
		return index;
	}

	public double getProjection() {
		return projection;
	}
	public void setProjection(double current_projection) {
		this.projection= current_projection;
	}
	
	public float getTilt() {
		return XAngle;
	}
	public void setTilt(float f) {
		this.XAngle = f;
	}
	
	

}
