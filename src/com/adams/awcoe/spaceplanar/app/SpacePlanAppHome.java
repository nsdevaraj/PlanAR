package com.adams.awcoe.spaceplanar.app;

import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.adams.awcoe.spaceplanar.floors.NewFloorMap;
import com.adams.awcoe.spaceplanar.floors.ViewFloorMap;
import com.adams.awcoe.spaceplanar.help.Documentation;
import com.adams.awcoe.spaceplanar.room.NewRoomMap;
import com.adams.awcoe.spaceplanar.room.ViewRoomMap;
import com.adams.awcoe.spaceplanar.settings.AppSettings;
import com.adams.awcoe.spaceplanar.settings.DeviceHeightSettings;
import com.adams.awcoe.spaceplanar.settings.TiltCorrectionSettings;
import com.adams.awcoe.spaceplanar.utils.CopyResources;
import com.adams.awcoe.spaceplanar.utils.TitlePageIndicator;
import com.adams.awcoe.spaceplanar.utils.TitleProvider;


public class SpacePlanAppHome extends Activity {
	static int faq_doc_req_code = 101;
	CopyResources copy_resources_Obj;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spaceplanar_home_viewpager);
        
        copy_resources_Obj = new CopyResources(getResources().getAssets(), Environment.getExternalStorageDirectory() + File.separator + "SpacePlanARData" + File.separator + "TemporaryFiles");
        /**
         * 
         * Retrieving Preference Settings
         */        
        AppSettings obj=new AppSettings();
        if(obj.getCamHeight(SpacePlanAppHome.this)=="") {
        	obj.SetDefaultCameraSettings(SpacePlanAppHome.this);
        }
        
        
        if(obj.getTiltCorrection(SpacePlanAppHome.this)==0) {
        	obj.SetDefaultTiltSettings(SpacePlanAppHome.this);
        }
       
        
        MyPagerAdapter adapter = new MyPagerAdapter();
        ViewPager myPager = (ViewPager) findViewById(R.id.SpacePlanarHomeViewPager);
        myPager.setAdapter(adapter);
        myPager.setCurrentItem(0);
        
      //Bind the title indicator to the adapter
        TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);
        titleIndicator.setViewPager(myPager);
    }
    
    //Room Dashboard OnClicks
    public void newRoomMapButtonClick(View v) {
    	Intent i = new Intent(getApplicationContext(), NewRoomMap.class);
		startActivity(i);
    }
    public void viewRoomMapsButtonClick(View v) {
    	Intent i = new Intent(getApplicationContext(),ViewRoomMap.class);
		startActivity(i);
    }
    
    // Floor Dashboard OnClicks
    public void constructNewFloorButtonClick(View v) {
    	Intent i = new Intent(getApplicationContext(), NewFloorMap.class);
		startActivity(i);
    }
    
    public void viewFloorMapsButtonClick(View v) {
    	Intent i = new Intent(getApplicationContext(),ViewFloorMap.class);
		startActivity(i);
    }
    
    //Settings OnClick    
    public void deviceSettingsButtonClick(View v) {
    	Intent i = new Intent(getApplicationContext(), DeviceHeightSettings.class);
		startActivity(i);
    }
    
    public void tiltSettingsButtonClick(View v) {
    	Intent i = new Intent(getApplicationContext(), TiltCorrectionSettings.class);
		startActivity(i);
    }
    
    public void documentationButtonClick(View v) {
    	Intent i = new Intent(getApplicationContext(), Documentation.class);
		startActivity(i);
    }
    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == faq_doc_req_code) {
			copy_resources_Obj.deleteTempFile("faq.pdf");
		}
	}

	public void faqButtonClick(View v) {		
    	Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
	    pdfIntent.setDataAndType(Uri.fromFile(copy_resources_Obj.copyHelpFiles("faq.pdf")), "application/pdf");
	    try{
	    	startActivityForResult(pdfIntent, faq_doc_req_code);
	    } catch (ActivityNotFoundException anfe) {				    	
	    	Toast.makeText(SpacePlanAppHome.this, "No Application available to view pdf!", Toast.LENGTH_LONG).show();
		}		
    }    
    
    //Exit Application
    public void exitApplicationButtonClick(View v) {
    	finish();
    }
    
    private class MyPagerAdapter extends PagerAdapter implements TitleProvider{

        public int getCount() {
                return 4;
        }

        public Object instantiateItem(View collection, int position) {

                LayoutInflater inflater = (LayoutInflater) collection.getContext()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                int resId = 0;
                switch (position) {
                case 0:
                        resId = R.layout.room_fragment_layout;
                        break;
                case 1:
                        resId = R.layout.floor_fragment_layout;
                        break;
                case 2:
                        resId = R.layout.settings_fragment_layout;
                        break;
                case 3:
	                    resId = R.layout.help_fragment_layout;
	                    break;
                }

                View view = inflater.inflate(resId, null);

                ((ViewPager) collection).addView(view, 0);

                return view;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView((View) arg2);

        }

        @Override
        public void finishUpdate(View arg0) {
                

        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == ((View) arg1);

        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
                

        }

        @Override
        public Parcelable saveState() {
                
                return null;
        }

        @Override
        public void startUpdate(View arg0) {
               

        }

		public String getTitle(int position) {
			
			String title="";
			switch (position) {
            case 0:
                    title="Room";
                    break;
            case 1:
            	    title="Floors";
                    break;
            case 2:
            	    title="Settings";
                    break;
            case 3:
	        	    title="Help";
	                break;
            }
			return title;
		}


}
    
    
    

}