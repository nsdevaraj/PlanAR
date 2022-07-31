package com.adams.awcoe.spaceplanar.room;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.imageutils.CropImage;


public class NewRoomMapTextures extends Activity {
	// Android Variables
	ImageView room_newmap_setTexture_container;
	Button room_newmap_texture_cameraImage;
	Button room_newmap_texture_filemage;
	//Button room_newmap_texture_edit;
	Bitmap photo;
	String cam_request_temp_photo_fullpath, media_request_temp_photo_fullpath, card_temp_path;	

	// Member Variables
	private static final int CAMERA_REQUEST = 1889;
	protected static final int MEDIA_REQUEST = 1890;
	protected static final int CAM_CROP_REQUEST = 1891;
	private static final int MEDIA_CROP_REQUEST = 1892;
	private static final int EDIT_IMAGE_REQUEST = 1893;			
	public void startListeners() {
		room_newmap_texture_filemage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doTakeLocalFileAction();
			}
		});
		room_newmap_texture_cameraImage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doTakePhotoAction();
			}
		});
		/*room_newmap_texture_edit.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {				
				doEditTextureAction();
			}
		});*/
	}
	
	/*private void doEditTextureAction() {
		Intent editIntent = new Intent();
		editIntent.setAction(Intent.ACTION_EDIT);
		Uri imageToEditUri = selectedImageUri;  // Uri of existing photo
		editIntent.setDataAndType(imageToEditUri,"image/*");
		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(editIntent, 0);
		if(resInfo.size()>0) {
			startActivityForResult(Intent.createChooser(editIntent,"Edit Image"),EDIT_IMAGE_REQUEST);
		} else {
			Toast.makeText(NewRoomMapTextures.this,"No Image Editing App in your device!",3000).show();
		}
	}*/

	private void doTakeLocalFileAction() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);		
		intent.setType("image/*");		
		intent.putExtra("return-data", false);
		startActivityForResult(intent, MEDIA_REQUEST);
	}

	private void doTakePhotoAction() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cam_request_temp_photo_fullpath = card_temp_path + File.separator + "tempcam_" + java.lang.String.valueOf(System.currentTimeMillis()) + ".jpg";
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cam_request_temp_photo_fullpath)));
		intent.putExtra("return-data", false);
		startActivityForResult(intent, CAMERA_REQUEST);
	}
	
	/*@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Rect dialogBounds = new Rect();
		getWindow().getDecorView().getHitRect(dialogBounds);
		
		if(!dialogBounds.contains((int)ev.getX(), (int)ev.getY())){
			return false;
		} else {
			return super.dispatchTouchEvent(ev);
		}		
	}*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_newmap_generate2dview_textures);
		room_newmap_setTexture_container = (ImageView) findViewById(R.id.room_newmap_setTexture_container);
		room_newmap_texture_filemage = (Button) findViewById(R.id.room_newmap_texture_filemage);
		room_newmap_texture_cameraImage = (Button) findViewById(R.id.room_newmap_texture_cameraImage);
		//room_newmap_texture_edit=(Button)findViewById(R.id.room_newmap_texture_edit);
		room_newmap_setTexture_container.setImageBitmap(NewRoomMap2D.wall_textures_content.get(NewRoomMap2D.current_wall_id));
		card_temp_path = Environment.getExternalStorageDirectory() + File.separator + "SpacePlanARData" + File.separator + "TemporaryFiles";		
		startListeners();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == CAMERA_REQUEST) {
			// Reduce the resolution and size of bitmap to overcome OutOfMemoryError
			reduceBitmapSize(cam_request_temp_photo_fullpath, null);
			Intent intent = new Intent(this, CropImage.class);
			intent.putExtra("image-path", cam_request_temp_photo_fullpath);
			intent.putExtra("scale", true);
			startActivityForResult(intent, CAM_CROP_REQUEST);
		}
		if (requestCode == MEDIA_REQUEST) {
			// Reduce the resolution and size of bitmap to overcome OutOfMemoryError
			media_request_temp_photo_fullpath = card_temp_path + File.separator + "templocal_" + java.lang.String.valueOf(System.currentTimeMillis()) + ".jpg";
			reduceBitmapSize(media_request_temp_photo_fullpath, getPath(data.getData()));
			
			Intent intent = new Intent(this, CropImage.class);
			intent.putExtra("scale", true);
			intent.putExtra("image-path", media_request_temp_photo_fullpath);
			startActivityForResult(intent, MEDIA_CROP_REQUEST);
		}
		if (requestCode == CAM_CROP_REQUEST) {
			try {
				photo = BitmapFactory.decodeFile(cam_request_temp_photo_fullpath);
				room_newmap_setTexture_container.setImageBitmap(photo);
				NewRoomMap2D.wall_textures_content.set(NewRoomMap2D.current_wall_id, photo);
				NewRoomMap2D.wall_textures_content_BASE64.set(NewRoomMap2D.current_wall_id, BitMapToString(photo));
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError oome) {
				AlertDialog alert_dialog = new AlertDialog.Builder(NewRoomMapTextures.this).create();
				alert_dialog.setTitle("Memory Error");
				alert_dialog.setMessage("The system ran out of memory. Please select smaller resolution image for texture.");
				alert_dialog.setIcon(R.drawable.symbol_error);
				alert_dialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				alert_dialog.show();
				oome.printStackTrace();
			}
		}
		if (requestCode == MEDIA_CROP_REQUEST) {
			try {
				photo = MediaStore.Images.Media.getBitmap(
						NewRoomMapTextures.this.getContentResolver(),
						Uri.fromFile(new File(media_request_temp_photo_fullpath)));
				room_newmap_setTexture_container.setImageBitmap(photo);
				NewRoomMap2D.wall_textures_content.set(NewRoomMap2D.current_wall_id,photo);
				NewRoomMap2D.wall_textures_content_BASE64.set(NewRoomMap2D.current_wall_id,
				BitMapToString(photo));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError oome) {
				oome.printStackTrace();
			}

		}
	}
	
	private void reduceBitmapSize(String path_to_bitmap, String media_soft_path) {
		if(media_soft_path != null) {
			photo = BitmapFactory.decodeFile(media_soft_path);
		} else {
			photo = BitmapFactory.decodeFile(path_to_bitmap);
		}
		photo = Bitmap.createScaledBitmap(photo, 800, 600, false);
		FileOutputStream out = null;
		//photo = Bitmap.createScaledBitmap(photo, 600, 450, false);
		try {
			out = new FileOutputStream(new File(path_to_bitmap));
			photo.compress(Bitmap.CompressFormat.JPEG, 100, out);				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

	public String BitMapToString(Bitmap bmp) {		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		/**
		 * Commented out to set compress ratio and overcome OutOfMemoryError
		 */
		//bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);		
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		try {
			baos.flush();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();		
		System.gc();
	}

}
