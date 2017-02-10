package com.example.uistart;
/**
 * This class displays card information to users 
 * 		 
 * @author Hilda
 */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.lib.JsonParser;
import com.example.uistart.R;

import android.nfc.NdefMessage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class CardInfo extends ParentActivity{
	private TextView tvCardInfoUID, tvCardInfoSID, tvCardInfoSName, tvCardInfoCourseCode,
	tvCardInfoCourseName, tvCardInfoCourseAttendance;
	private JSONArray studentTable, courseTable, regTable;
	private JSONObject cStudent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cardinfo);
		tvCardInfoUID = (TextView)findViewById(R.id.tvCardInfoUID);
		tvCardInfoSID = (TextView)findViewById(R.id.tvCardInfoSID);
		tvCardInfoSName = (TextView)findViewById(R.id.tvCardInfoSName);
		tvCardInfoCourseCode = (TextView)findViewById(R.id.tvCardInfoCourseCode);
		tvCardInfoCourseName = (TextView)findViewById(R.id.tvCardInfoCourseName);
		tvCardInfoCourseAttendance = (TextView)findViewById(R.id.tvCardInfoCourseAttendance);
		
		//check network connection
		new AsyncTask<String, Void, Boolean>() {
			
			@Override
			protected Boolean doInBackground(String... params) {
				courseTable = JsonParser.JsonParse(JsonParser.JsonRead("Course"));
				System.out.println(courseTable);
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
	}

	@Override
	protected String dumpTagData(long id) {
		final String uid = String.valueOf(id);
		
		new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				// get tables ready from database
				boolean flag;
				regTable = JsonParser.JsonParse(JsonParser.JsonRead("Registration"));
				studentTable = JsonParser.JsonParse(JsonParser.JsonRead("Student"));
				courseTable = JsonParser.JsonParse(JsonParser.JsonRead("Course"));
				
				//check card first
				if(JsonParser.isCardExisted(uid, studentTable)){
					cStudent = JsonParser.findStudentByUid(uid, studentTable);
					flag = true;
				}else{
					cStudent = null;
					flag = false;
				}
				return flag ;
			}

			@Override
			protected void onPostExecute(Boolean flag) {
				String sid = null, sName = null;
				String[] courses;
				JSONObject courseSet ;
				int q;
				if(flag){
					Toast.makeText(getApplicationContext(), "UID: "+uid, Toast.LENGTH_SHORT).show();
					try {
//						uid = cStudent.getString("uid");
						sid = cStudent.getString("sid");
						sName = cStudent.getString("name");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					tvCardInfoUID.setText("UID: "+ uid);
					tvCardInfoSID.setText("Student ID: "+ sid);
					tvCardInfoSName.setText("Student Name: "+sName);
					
					//get registration information
					courses = JsonParser.findCourseInReg(uid, regTable);
					q = Integer.parseInt(courses[0]);
					if(q == 0){
						tvCardInfoCourseCode.setText("This student has no course enrolled.");
						tvCardInfoCourseName.setText("");
						tvCardInfoCourseAttendance.setText("");
					}else if(q == 1){
						courseSet = JsonParser.findCourseByCode(courses[1], courseTable);
						try {
							tvCardInfoCourseCode.setText("Course Code: " + courses[1]);
							tvCardInfoCourseName.setText("Course Name: "+ courseSet.getString("name"));
							tvCardInfoCourseAttendance.setText("Course Attendance: "+ JsonParser.findRecordInReg(
									uid, courses[1], regTable).getString("attendance")) ;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						//TODO: q > 1
						StringBuffer content = new StringBuffer("");
						try{
							for(int i = 1; i<=q;i++){
								courseSet = JsonParser.findCourseByCode(courses[i], courseTable);
								content = content.append("Course Code: "+courses[i]+ "\n");
								content = content.append("Course Name: "+courseSet.getString("name") + "\n");
								content = content.append("Course Attendance: "+ JsonParser.findRecordInReg(
										uid, courses[i], regTable).getString("attendance")+"\n\n");
							}
							tvCardInfoCourseCode.setText(content);
							tvCardInfoCourseName.setText("");
							tvCardInfoCourseAttendance.setText("");
						}catch(Exception e){
							e.printStackTrace();
						}
//						LinearLayout layout = new LinearLayout(llCardInfoDisplay);
//						llCardInfoDisplay = (LinearLayout)findViewById(R.id.llCardInfoDisplay);
//						TextView tv = new TextView();
//						tv.setText("     我是动态添加的");
//						llCardInfoDisplay.addView(tv);
//						setContentView(R.layout.cardinfo);
					}
				}else{
					Toast.makeText(getApplicationContext(), "This card has not been " +
						"registered for any student yet.", Toast.LENGTH_SHORT).show();
					
					tvCardInfoUID.setText("UID: ");
					tvCardInfoSID.setText("Student ID: ");
					tvCardInfoSName.setText("Student Name: ");
					tvCardInfoCourseCode.setText("Course Code: ");
					tvCardInfoCourseName.setText("Course Name: ");
					tvCardInfoCourseAttendance.setText("Course Attendance: ");
				}
				super.onPostExecute(flag);
			}
		}.execute();
		
		//try return null, maybe not good
		return uid;
	}

	@Override
	protected void buildTagViews(NdefMessage[] msgs) {

	}
	

}
