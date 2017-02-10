package com.example.uistart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.lib.HttpRequestUtils;
import com.example.lib.JsonParser;
import com.example.uistart.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * DONE!!!!!!!
 * 
 * ***************Test create multiple course before back to main page
 * 
 * @author Hilda
 *
 */


public class AddCourse extends Activity {
	private Button bCourseAddConfirm, bCourseAddCancel;
	private EditText etCode, etName, etRemark;
	private JSONArray courseTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcourse);
		new HttpRequestUtils();

		bCourseAddConfirm = (Button) findViewById(R.id.bCourseAddConfirm);
		bCourseAddCancel = (Button) findViewById(R.id.bCourseAddCancel);
		etCode = (EditText) findViewById(R.id.etCourseCode);
		etName = (EditText) findViewById(R.id.etCourseName);
		etRemark = (EditText) findViewById(R.id.etCourseRemark);

		bCourseAddCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		//check network connection
		new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				courseTable = JsonParser.JsonParse(JsonParser.JsonRead("Course"));
//				System.out.println(courseTable);
				if(courseTable == null){
					return false;
				}else{
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
		}.execute();
		

		bCourseAddConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String courseCode = etCode.getText().toString();
				final String courseName = etName.getText().toString();
				final String remark = etRemark.getText().toString();
				
				if(courseCode.equals("")||courseName.equals("")){
					AlertDialog.Builder builder = new AlertDialog.Builder(AddCourse.this);
					builder.setTitle("Add Course");
					builder.setMessage("Please type in both course code and name.");
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
				
				new AsyncTask<String, Void, Boolean>() {
					//check duplicate course
					@Override
					protected Boolean doInBackground(String... params) {
						boolean flag;
						courseTable = JsonParser.JsonParse(JsonParser
								.JsonRead("Course"));
//						System.out.println("return value: "+courseTable);
						JSONObject course = JsonParser.findCourseByCode(
								courseCode, courseTable);
						if(courseCode.equals("")){
							flag = true;
						}else if (course != null) {
							// exist courseCode
							try {
								System.out.println("exist course code:"+ course.getString("code"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
							flag = true;
						} else {
							flag = false;
							//create this course
							JsonParser.CourseCreate(courseCode, courseName, remark);
						}
						return flag;
					}

					@Override
					protected void onPostExecute(Boolean flag) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								AddCourse.this);
						builder.setTitle("Add Course");
						//flag:true -->> duplicate
						if (flag == true) {
							System.out.println("flag is true");
							// can not create this duplicated course
							builder.setMessage("Failed. This course exists already!");
							builder.setNeutralButton("Back",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
										}
									});
						} else {
							System.out.println("flag is false");
							builder.setMessage("Successed!\nCourse Created!\n\nCourse Code: " + courseCode
									+ "\nCourse Name: " + courseName + "\nRemark: "
									+ remark);
							
							// positive on left
							builder.setNeutralButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
										}
									});
							etCode.setText("");
							etName.setText("");
							etRemark.setText("");
						}
						AlertDialog dialog = builder.create();
						dialog.show();
						super.onPostExecute(flag);
					}
				}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
		});
		
		//no more thing to do in onCreate()
	}
}
