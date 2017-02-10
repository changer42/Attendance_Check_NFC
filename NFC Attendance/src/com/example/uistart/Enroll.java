package com.example.uistart;

import org.json.JSONArray;

import com.example.lib.JsonParser;
import com.example.uistart.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**This class is to help a student to enroll a course.
 * 
 * @author Hilda
 *
 */

public class Enroll extends ParentActivity {

	private JSONArray regTable, studentTable, courseTable;
	private Button bEnrollOK, bEnrollBack;
	private EditText etEnrollUID,etEnrollAttendance;
	private Spinner spEnrollCourse;
	private ArrayAdapter<String> adapter;
	private String[] courses;

	// private String cCourse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enroll);
		bEnrollOK = (Button) findViewById(R.id.bEnrollOK);
		bEnrollBack = (Button) findViewById(R.id.bEnrollBack);
		etEnrollUID = (EditText) findViewById(R.id.etEnrollUID);
		etEnrollAttendance =(EditText) findViewById(R.id.etEnrollAttendance);
		spEnrollCourse = (Spinner) findViewById(R.id.spEnrollCourse);
		
		new AsyncTask<String, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				// String[] courses;
				regTable = JsonParser.JsonParse(JsonParser.JsonRead("Registration"));
				studentTable = JsonParser.JsonParse(JsonParser.JsonRead("Student"));
				courseTable = JsonParser.JsonParse(JsonParser.JsonRead("Course"));
//				System.out.println(regTable);
				if(regTable == null){
					courses = new String[1];
					return false;
				}else{
					courses = JsonParser.returnAllCourse(courseTable);
					return true;
				}
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if(result == false){
					Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
					finish();
				}
				super.onPostExecute(result);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		//wait for course to update
		while (courses == null) {
		}
		
		// 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, courses);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 将adapter 添加到spinner中
		spEnrollCourse.setAdapter(adapter);
		// 添加事件Spinner事件监听
		spEnrollCourse.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// cCourse = null;
			}
		});
		spEnrollCourse.setVisibility(View.VISIBLE);
		
		bEnrollBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		bEnrollOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String uid = etEnrollUID.getText().toString();
				final String attendance = etEnrollAttendance.getText().toString();
				final String course = spEnrollCourse.getSelectedItem().toString();
				
				if(uid.equals("") || course.equals(null)|| uid.equals(null)||
						attendance.equals("") || Integer.parseInt(attendance)<0){
					AlertDialog.Builder builder = new AlertDialog.Builder(Enroll.this);
					builder.setTitle("Enroll Course");
//					System.out.println("ok button check....");
					builder.setMessage("Please tap your card, choose a course and type in correct attendance!");
					builder.setNeutralButton("Back",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
					AlertDialog dialog = builder.create();
					dialog.show();
				}else{

				new AsyncTask<String, Void, Integer>() {
					@Override
					protected Integer doInBackground(String... params) {
						int flag;
						regTable = JsonParser.JsonParse(JsonParser.JsonRead("Registration"));
						studentTable = JsonParser.JsonParse(JsonParser.JsonRead("Student"));
						/*
						 * Case: 1. not a registered card 
						 * 2. already registered this course
						 */
						if(JsonParser.isCardExisted(uid, studentTable) == false){
							//not a registered card
							flag = 1;
						}else if(JsonParser.findRecordInReg(uid, course, regTable) != null){
							//2. already registered this course
							flag = 2;
						}else{
							//can insert new record
							flag = 0;
						}
						courseTable = JsonParser.JsonParse(JsonParser.JsonRead("Course"));
						return flag;
					}

					@Override
					protected void onPostExecute(Integer flag) {
						super.onPostExecute(flag);
						AlertDialog.Builder builder = new AlertDialog.Builder(Enroll.this);
						builder.setTitle("Enroll Course");
						if(flag == 1){
							//not a registered card
							builder.setMessage("Failed.\nThis card has not been registered " +
									"for any student yet.\nPlease register this card first");
							builder.setNegativeButton("Go to Register",new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,int which) {
											Intent openReg = new Intent("com.example.uistart.REGISTERCARD");//parameter is the action name 
											startActivity(openReg);
										}
									});
							builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
										}
									});
						}else if(flag == 2){
							//2. already registered this course
							builder.setMessage("Failed.\nThis card has already registered this course.");
							builder.setNeutralButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
										}
									});
						}else{
							//inserT new record
							new AsyncTask<String, Void, String>() {
								@Override
								protected String doInBackground(String... params) {
									regTable = JsonParser.JsonParse(JsonParser.JsonRead("Registration"));
									JsonParser.RegistrationCreate(uid, course, attendance);
									return null;
								}
							}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							builder.setMessage("Successed!");
							builder.setNeutralButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
										}
									});
							etEnrollUID.setText("");
							etEnrollAttendance.setText("");
						}
						AlertDialog dialog = builder.create();
						dialog.show();
					}
				}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
		});
	}

	@Override
	protected String dumpTagData(long id) {
		//data transfer
		String uid = String.valueOf(id);
		etEnrollUID.setText("" + uid);
		return uid;
	}

	@Override
	protected void buildTagViews(NdefMessage[] msgs) {
	}

}
