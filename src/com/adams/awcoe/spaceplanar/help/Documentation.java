package com.adams.awcoe.spaceplanar.help;

import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.adams.awcoe.spaceplanar.app.R;
import com.adams.awcoe.spaceplanar.utils.CopyResources;

public class Documentation extends Activity {	
	int mainNavSelectedIndex;
	int subNavSelectedIndex;
	private boolean loadPdf = false;	
	
	Spinner mainnavspinner;
	Spinner subnavspinner;
	SpinnerAdapter mainNavAdapter;
	SpinnerAdapter subNavAdapter;
	Button opendoc;
	File targetFile;	
	
	static int documentation_doc_req_code = 102;
	static String targetFile_name = "";
	CopyResources copy_resources_Obj;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spaceplanar_help_documentation);
		
		mainnavspinner = (Spinner)findViewById(R.id.mainnavspinner);
		subnavspinner = (Spinner)findViewById(R.id.subnavspinner);
		opendoc = (Button)findViewById(R.id.opendoc);
		copy_resources_Obj = new CopyResources(getResources().getAssets(), Environment.getExternalStorageDirectory() + File.separator + "SpacePlanARData" + File.separator + "TemporaryFiles");
		
		mainNavAdapter = ArrayAdapter.createFromResource(Documentation.this, R.array.documentation_mainnav_list,
		          R.layout.help_documentation_item);
		mainnavspinner.setAdapter(mainNavAdapter);
		
		mainnavspinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mainNavSelectedIndex = mainnavspinner.getSelectedItemPosition();				
				
				switch (mainNavSelectedIndex) {
				case 0:
					subNavAdapter = null;
					break;
				case 1:
					subNavAdapter = ArrayAdapter.createFromResource(Documentation.this, R.array.documentation_subnav_room_list,
					          R.layout.help_documentation_item);
					break;
				case 2:
					subNavAdapter = ArrayAdapter.createFromResource(Documentation.this, R.array.documentation_subnav_floor_list,
					          R.layout.help_documentation_item);
					break;
				case 3:
					subNavAdapter = ArrayAdapter.createFromResource(Documentation.this, R.array.documentation_subnav_settings_list,
					          R.layout.help_documentation_item);
					break;				
				}
				subnavspinner.setAdapter(subNavAdapter);
			}

			public void onNothingSelected(AdapterView<?> arg0) {				
			}			
		});
		
		opendoc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				subNavSelectedIndex = subnavspinner.getSelectedItemPosition();				
				switch (mainNavSelectedIndex) {
				case 0:
					Toast.makeText(Documentation.this, "Please Select a Document Type", Toast.LENGTH_LONG).show();					
					break;
				case 1:
					loadPdf = true;
					switch (subNavSelectedIndex) {
					case 0:						
						targetFile = copy_resources_Obj.copyHelpFiles("markRoomMap.pdf");
						targetFile_name = "markRoomMap.pdf";
						break;
					case 1:
						targetFile = copy_resources_Obj.copyHelpFiles("setTextures.pdf");
						targetFile_name = "setTextures.pdf";
						break;
					case 2:
						targetFile = copy_resources_Obj.copyHelpFiles("regularization.pdf");
						targetFile_name = "regularization.pdf";
						break;
					case 3:
						targetFile = copy_resources_Obj.copyHelpFiles("saveRoomMap.pdf");
						targetFile_name = "saveRoomMap.pdf";
						break;
					case 4:
						targetFile = copy_resources_Obj.copyHelpFiles("viewRoomMap.pdf");
						targetFile_name = "viewRoomMap.pdf";
						break;
					}
					break;
				case 2:
					loadPdf = true;
					switch (subNavSelectedIndex) {
					case 0:
						targetFile = copy_resources_Obj.copyHelpFiles("contructFloor.pdf");
						targetFile_name = "contructFloor.pdf";
						break;
					case 1:
						targetFile = copy_resources_Obj.copyHelpFiles("exportAndSaveFloor.pdf");
						targetFile_name = "exportAndSaveFloor.pdf";
						break;
					case 2:
						targetFile = copy_resources_Obj.copyHelpFiles("viewFloorMap.pdf");
						targetFile_name = "viewFloorMap.pdf";
						break;
					}
					break;
				case 3:
					loadPdf = true;
					switch (subNavSelectedIndex) {
					case 0:
						targetFile = copy_resources_Obj.copyHelpFiles("adjustViewHeight.pdf");
						targetFile_name = "adjustViewHeight.pdf";
						break;
					case 1:
						targetFile = copy_resources_Obj.copyHelpFiles("calibrateDevice.pdf");
						targetFile_name = "calibrateDevice.pdf";
						break;
					}
					break;
				}
				
				if(loadPdf == true){
					Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
				    pdfIntent.setDataAndType(Uri.fromFile(targetFile), "application/pdf");
				    try{
				    	startActivityForResult(pdfIntent, documentation_doc_req_code);
				    } catch (ActivityNotFoundException anfe) {				    	
				    	Toast.makeText(Documentation.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
					}
				}
			}
		});		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == documentation_doc_req_code){
			copy_resources_Obj.deleteTempFile(targetFile_name);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		System.gc();
	}
}
