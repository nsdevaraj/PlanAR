package com.adams.awcoe.spaceplanar.room;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.floors.ViewFloorMap;
import com.adams.awcoe.spaceplanar.room.opengl.FloorTriangle;
import com.adams.awcoe.spaceplanar.room.opengl.GLText;
import com.adams.awcoe.spaceplanar.room.opengl.Group;
import com.adams.awcoe.spaceplanar.room.opengl.WallTriangle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;


public class ViewRoomMap3DRenderer  implements GLSurfaceView.Renderer {
	private Group root;
	private Context context;
	float stopX = 0, stopY = 0;
	double distance;
	int index;
	private GLText glText;
	float room_height;
	private float mScaleFactor = 1.0f;
	private float mPosX = 0.0f;
	private float mPosY = 0.0f;
	public float  mAngleX;
	public float  mAngleY;
	@SuppressWarnings("unused")
	private String senseOfRotation="X";

	float floor_textureCoordinates[] = {	 
			0.5f, 1.0f, 
			1.0f, 0.0f, 
			0.0f, 0.0f
			};	
	/*
	        0.0f, 0.0f, //bottom left
			1.0f, 0.0f, //bottom right
			1.0f, 1.0f, // top right
			0.0f, 1.0f  //top left
	 
	 */
	short[] wall_indices = { 0, 1, 2, 0, 2, 3 };
	float wall_textureCoordinates[] = {
		0.0f, 1.0f,
		1.0f, 1.0f,
		1.0f, 0.0f,
		0.0f, 0.0f
	};	
	float maxX = 0.0f;
	float minX = 0.0f;
	
	public ViewRoomMap3DRenderer(Context context, String height) {
		this.context = context;
		Group group = new Group();
		// get Max and Min of X
		for(int i = 0; i < ViewRoomMap.readAugmented3DPointsCordinates.size(); i++){			
			if(ViewRoomMap.readAugmented3DPointsCordinates.get(i).getXMark() > maxX){
				maxX = ViewRoomMap.readAugmented3DPointsCordinates.get(i).getXMark();
			} else if(ViewRoomMap.readAugmented3DPointsCordinates.get(i).getXMark() < minX){
				minX = ViewRoomMap.readAugmented3DPointsCordinates.get(i).getXMark();
			}
		}
		
		// draw floor
		for (int i = 2; i < ViewRoomMap.readAugmented3DPointsCordinates.size(); i++) {			
			float floorvertices[] = {
				-ViewRoomMap.readAugmented3DPointsCordinates.get(0).getXMark(),
				ViewRoomMap.readAugmented3DPointsCordinates.get(0).getYMark(),
				0.0f, // 0,
				-ViewRoomMap.readAugmented3DPointsCordinates.get(i - 1).getXMark(),
				ViewRoomMap.readAugmented3DPointsCordinates.get(i - 1).getYMark(),
				0.0f, // 1,
				-ViewRoomMap.readAugmented3DPointsCordinates.get(i).getXMark(),
				ViewRoomMap.readAugmented3DPointsCordinates.get(i).getYMark(), 0.0f };// 2
				
			float floor_normals[]={
				ViewRoomMap.readAugmented3DPointsCordinates.get(0).getXMark(),
				ViewRoomMap.readAugmented3DPointsCordinates.get(0).getYMark(),
				1.0f,
				ViewRoomMap.readAugmented3DPointsCordinates.get(i - 1).getXMark(),
				ViewRoomMap.readAugmented3DPointsCordinates.get(i - 1).getYMark(),
				1.0f,
				ViewRoomMap.readAugmented3DPointsCordinates.get(i).getXMark(),
				ViewRoomMap.readAugmented3DPointsCordinates.get(i).getYMark(),
				1.0f
			};
			FloorTriangle t = new FloorTriangle(floorvertices, 0.5f, 0.3f, 0.4f,
					1.0f, floor_textureCoordinates,floor_normals,ViewRoomMap2D.floor_bitmap);
			group.add(t);
		} // draw floor completed
		
		// draw wall
		room_height=Float.parseFloat(height);
		for (int i = 0; i < ViewRoomMap.readAugmented3DPointsCordinates.size(); i++) {			
			if (i < (ViewRoomMap.readAugmented3DPointsCordinates.size() - 1)) { // for walls except the last wall
				float wallvertices[] = {
					-ViewRoomMap.readAugmented3DPointsCordinates.get(i).getXMark(),
					ViewRoomMap.readAugmented3DPointsCordinates.get(i).getYMark(),
					0.0f, // 0,
					
					-ViewRoomMap.readAugmented3DPointsCordinates.get(i + 1).getXMark(),
					ViewRoomMap.readAugmented3DPointsCordinates.get(i + 1).getYMark(),
					0.0f, // 1,
					
					-ViewRoomMap.readAugmented3DPointsCordinates.get(i + 1).getXMark(),
					ViewRoomMap.readAugmented3DPointsCordinates.get(i + 1).getYMark(),
					room_height,// 2
					
					-ViewRoomMap.readAugmented3DPointsCordinates.get(i).getXMark(),
					ViewRoomMap.readAugmented3DPointsCordinates.get(i).getYMark(), 
					room_height// 3						
				};

				WallTriangle t = new WallTriangle(wallvertices, 0.5f, 0.5f, 0.5f,
						1.0f, wall_indices, wall_textureCoordinates, ViewRoomMap2D.wall_textures_content.get(i));
				group.add_wall(t);
			} else { // for last wall
				float vertices[] = {
					-ViewRoomMap.readAugmented3DPointsCordinates.get(i).getXMark(),
					ViewRoomMap.readAugmented3DPointsCordinates.get(i).getYMark(),
					0.0f, // 0,
					-ViewRoomMap.readAugmented3DPointsCordinates.get(0).getXMark(),
					ViewRoomMap.readAugmented3DPointsCordinates.get(0).getYMark(),
					0.0f, // 1,
					
					-ViewRoomMap.readAugmented3DPointsCordinates.get(0).getXMark(),
					ViewRoomMap.readAugmented3DPointsCordinates.get(0).getYMark(),
					room_height,// 2
					
					-ViewRoomMap.readAugmented3DPointsCordinates.get(i).getXMark(),
					ViewRoomMap.readAugmented3DPointsCordinates.get(i).getYMark(),
					room_height// 3						
				};                
				
				WallTriangle t = new WallTriangle(vertices, 0.5f, 0.5f, 0.5f,
						1.0f, wall_indices,wall_textureCoordinates,ViewRoomMap2D.wall_textures_content.get(i));
				group.add_wall(t);
			}
		}
		root = group;
	}

	double roundDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("###.###");
		return Double.valueOf(twoDForm.format(d));
	}

	public void setRotation(float angleX, float angleY) {
		mAngleX = angleX;
		mAngleY = angleY;
	}

	public void setScaleFactor(float scalef) {
		mScaleFactor = scalef;
	}

	public void setTranslate(float x, float y) {
		mPosX = x;
		mPosY = y;
	}
	
	
	/*public void setSenseRotation(String senseRotation) {
		senseOfRotation=senseRotation;
	}*/
	
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);		
		//gl.glClearColor(0,0,0,0);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		//gl.glTranslatef(mPosX,mPosY,-30);
		gl.glTranslatef(mPosX,mPosY,-5);
		//gl.glTranslatef(-(maxX+minX)/2,mPosY,-5);
		//gl.glScalef(mScaleFactor, mScaleFactor, 0);
		gl.glRotatef(mAngleX, 0,1,0);
		gl.glRotatef(-90, 1, 0, 0);
		
		/*//gl.glClearColor(mRed, mGreen, mBlue, 1.0f);
		// Clears the screen and depth buffer.
		//else if(mAngleX>180&&mAngleX<=359)
		//gl.glRotatef(-60, 1, 0, 0);
		//gl.glRotatef(-90,1, 0, 0); */
		
		// Draw our scene.
		try {
			/*gl.glPushMatrix();
			gl.glTranslatef(mPosX,mPosY,-5);
			gl.glRotatef(mAngleX, 0,1,0);
			gl.glRotatef(-90, 1, 0, 0);
			gl.glTranslatef(-mPosX,-mPosY,5);
			root.draw(gl);
			gl.glPopMatrix();*/
			
			root.draw(gl);
			
			/**
			 * Commented out as enable texture is done individually in WallMesh.java draw() method 
			 */
			
			/*gl.glEnable(GL10.GL_TEXTURE_2D); // Enable Texture Mapping		
			gl.glEnable(GL10.GL_BLEND); // Enable Alpha Blend
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // Set Color to Use */
			
			
			//Put Marker Symbols
			/*glText.begin(0.0f, 0.0f, 1.0f, 1.0f);
			for(int i=0;i<ViewRoomMap.readAugmented3DPointsCordinates.size();i++)
			glText.draw((i+1)+"", ViewRoomMap.readAugmented3DPointsCordinates.get(i).getXMark(),
					ViewRoomMap.readAugmented3DPointsCordinates.get(i).getYMark());
			glText.end();
			
			//Put Distances on Map
			glText.begin(1.0f, 0.0f, 0.0f, 1.0f);
			//Put Marker Symbols
			for(index=1;index<ViewRoomMap.readAugmented3DPointsCordinates.size();index++) {
				 distance =
					  Math.sqrt(Math.pow((ViewRoomMap.readAugmented3DPointsCordinates.get(index).getXMark() -
					  ViewRoomMap.readAugmented3DPointsCordinates.get(index-1).getXMark()), 2) +
					  Math.pow((ViewRoomMap.readAugmented3DPointsCordinates.get(index).getYMark() -
					  ViewRoomMap.readAugmented3DPointsCordinates.get(index-1).getYMark()), 2));
				 
				 glText.draw(roundDecimals(distance)+" fts",(ViewRoomMap.readAugmented3DPointsCordinates.get(index).getXMark() +
						 ViewRoomMap.readAugmented3DPointsCordinates.get(index-1).getXMark())/2,
						 (ViewRoomMap.readAugmented3DPointsCordinates.get(index).getYMark() +
								 ViewRoomMap.readAugmented3DPointsCordinates.get(index-1).getYMark())/2);
			}
			index=index-1;
			 distance =
					  Math.sqrt(Math.pow((ViewRoomMap.readAugmented3DPointsCordinates.get(index).getXMark() -
					  ViewRoomMap.readAugmented3DPointsCordinates.get(0).getXMark()), 2) +
					  Math.pow((ViewRoomMap.readAugmented3DPointsCordinates.get(index).getYMark() -
					  ViewRoomMap.readAugmented3DPointsCordinates.get(0).getYMark()), 2));
			
			 glText.draw(roundDecimals(distance)+" fts",(ViewRoomMap.readAugmented3DPointsCordinates.get(index).getXMark() +
					  ViewRoomMap.readAugmented3DPointsCordinates.get(0).getXMark())/2,
					  (ViewRoomMap.readAugmented3DPointsCordinates.get(index).getYMark() +
							  ViewRoomMap.readAugmented3DPointsCordinates.get(0).getYMark())/2);
			
			glText.end();
			
			glText.begin(1.0f, 0.0f, 1.0f, 0.0f);
			glText.draw("Centre",5,5);
			glText.end();*/
	
			// disable texture + alpha
			gl.glDisable(GL10.GL_BLEND); // Disable Alpha Blend
			gl.glDisable(GL10.GL_TEXTURE_2D);
		} catch (Exception e){
			e.printStackTrace();			
		}
	}

	
public void onSurfaceChanged(GL10 gl, int width, int height) {		
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);
		@SuppressWarnings("unused")
		float r= (float) width / (float) height;
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity();
		
		//gl.glFrustumf(-r,r,-1,1,-1,1);
		// Calculate the aspect ratio of the window
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity();
	}

	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0,0,0,0);
		gl.glShadeModel(GL10.GL_SMOOTH);
		/*gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);*/
		// Depth buffer setup.
		gl.glClearDepthf(1.0f);
		// Enables depth testing.
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// The type of depth testing to do.
		gl.glDepthFunc(GL10.GL_LEQUAL);
		// Really nice perspective calculations.
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		// Create the GLText
		// Create the GLText
		glText = new GLText(gl, context.getAssets());
		// Load the font from file (set size + padding), creates the texture
		// NOTE: after a successful call to this the font is ready for
		// rendering!
		glText.load("Roboto-Regular.ttf",14, 0, 0); // Create Font (Height: 14
														// Pixels / X+Y Padding
														// 2 Pixels)
	}
}
