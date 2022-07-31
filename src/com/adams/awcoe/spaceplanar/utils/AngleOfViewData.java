package com.adams.awcoe.spaceplanar.utils;

public class AngleOfViewData {

	public static int[] angle = new int[360];

	public AngleOfViewData() {
		for (int i = 180; i <= 359; i++) {
			angle[i - 180] = i;
		}
		for (int i = 0; i <= 179; i++) {
			angle[i + 180] = i;
		}
	}

	public int find_position(int tilt) {
		int index = 0;
        for(index=0;index<359;index++)
        {
        	if(angle[index]==tilt)
        		break;
        }
		
		return index;
	}

}
