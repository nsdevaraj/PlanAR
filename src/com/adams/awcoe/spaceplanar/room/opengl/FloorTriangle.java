package com.adams.awcoe.spaceplanar.room.opengl;

import android.graphics.Bitmap;

public class FloorTriangle extends FloorMesh {

	public FloorTriangle(float[] verts,float red, float green, float blue, float alpha, float[] textureCoordinates, float[] floor_normals,Bitmap b) {
		
		//float vertices[] = verts;
		short[] indices = { 0, 1, 2 };
		setIndices(indices);
		setVertices(verts);
		setTextureCoordinates(textureCoordinates);
		setNormals(floor_normals);
		setColor(red, green, blue, alpha);
		setTextureBitmap(b);
	}

}
