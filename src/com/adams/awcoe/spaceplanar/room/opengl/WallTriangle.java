package com.adams.awcoe.spaceplanar.room.opengl;

import android.graphics.Bitmap;

public class WallTriangle extends WallMesh {
	public WallTriangle(float[] verts, float red, float green, float blue,
			float alpha, short[] indx, float[] textureCoordinates,Bitmap b) {
		
		setIndices(indx);
		setVertices(verts);
		setColor(red, green, blue, alpha);
		setTextureCoordinates(textureCoordinates);
		setTextureBitmap(b);		
	}
}
