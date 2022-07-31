package com.adams.awcoe.spaceplanar.room.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.opengles.GL10;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

public class FloorMesh {
	// Our vertex buffer.
	private FloatBuffer verticesBuffer = null;
	// Our index buffer.
	private ShortBuffer indicesBuffer = null;
	
	// The buffer holding the normals
	private FloatBuffer normalBuffer = null;
	// Normals
	@SuppressWarnings("unused")
	private float normals[] = {
			// Normals 						
			0.0f, 0.0f, 1.0f, 
			0.0f, 0.0f, 1.0f, 
			0.0f, 0.0f, 1.0f
							   };
	
	// Our texture buffer.
	private FloatBuffer mTextureBuffer;
	// Our texture id.
	private int mTextureId = -1;
	// Indicates if we need to load the texture.
	private boolean mShouldLoadTexture = true;
	// The bitmap we want to load as a texture for floor.
	private Bitmap mBitmap=null;
	// The number of indices.
	private int numOfIndices = -1;

	// Flat Color
	private float[] rgba = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	// Smooth Colors
	@SuppressWarnings("unused")
	private FloatBuffer colorBuffer = null;

	// Translate params.
	public float x = 0;
	public float y = 0;
	public float z = 0;

	// Rotate params.
	public float rx = 0;
	public float ry = 0;
	public float rz = 0;

	public void draw(GL10 gl) {		
		if(mShouldLoadTexture) 
		loadGLTexture(gl); 
		 
		if (mTextureId != -1 && mTextureBuffer != null) {
			// Counter-clockwise winding.
			gl.glFrontFace(GL10.GL_CW);
			// Enable face culling.
			//gl.glEnable(GL10.GL_CULL_FACE);
			// What faces to remove with the face culling.
			//gl.glCullFace(GL10.GL_BACK); 
			 

			// Enable the texture Buffer
			gl.glEnable(GL10.GL_TEXTURE_2D);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);

			// Enalbe Vertices Buffer
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);

			// Enable Normal Buffers
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);

			gl.glTranslatef(x, y, z);
			gl.glRotatef(rx, 1, 0, 0);
			gl.glRotatef(ry, 0, 1, 0);
			gl.glRotatef(rz, 0, 0, 1);

			// Draw
			gl.glDrawElements(GL10.GL_TRIANGLES, numOfIndices,
					GL10.GL_UNSIGNED_SHORT, indicesBuffer);
				
			// Disable the buffers.
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}
	}
	
	protected void setColor(float red, float green, float blue, float alpha) {
		
		rgba[0] = red;
		rgba[1] = green;
		rgba[2] = blue;
		rgba[3] = alpha;
	}

	protected void setVertices(float[] vertices) {

		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		verticesBuffer = vbb.asFloatBuffer();
		verticesBuffer.put(vertices);
		verticesBuffer.position(0);
	}

	protected void setTextureCoordinates(float[] textureCoords) {

		ByteBuffer byteBuf = ByteBuffer
				.allocateDirect(textureCoords.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		mTextureBuffer = byteBuf.asFloatBuffer();
		mTextureBuffer.put(textureCoords);
		mTextureBuffer.position(0);
	}
	
	protected void setTextureBitmap(Bitmap b)
	{
		mBitmap=b;
	}
	
	protected void setNormals(float[] floor_normals) {
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(floor_normals.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		normalBuffer = byteBuf.asFloatBuffer();
		normalBuffer.put(floor_normals);
		normalBuffer.position(0);
	}

	protected void setIndices(short[] indices) {
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indicesBuffer = ibb.asShortBuffer();
		indicesBuffer.put(indices);
		indicesBuffer.position(0);
		numOfIndices = indices.length;
	}

	/**
	 * Loads the texture.
	 * 
	 * @param gl
	 */
	private void loadGLTexture(GL10 gl) {
		// Generate one texture pointer...
		
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		mTextureId = textures[0];

		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);

		// Create Linear Filtered Texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		
		/**
		 * Added GL_CLAMP_TO_EDGE
		 * to loaded in Samsung Galaxy Tax with Android version 4.0.4		
		*/
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

		// Use the Android GLUtils to specify a two-dimensional texture image
		// from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
		
		mShouldLoadTexture=false;
	}

}
