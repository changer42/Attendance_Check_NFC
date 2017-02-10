package com.example.uistart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.lib.HttpRequestUtils;
import com.example.lib.JsonParser;
import com.example.uistart.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

/**
 * This class shows attendance record 
 * 		 
 * @author Hilda
 *
 */

public class DShowAttendance extends Activity {
	private Spinner spShowAttendanceCourse, spShowAttendanceStudent;
	private Button bShowAttendanceOk, bShowAttendanceBack;
	private TextView tvShowAttendanceInfo;
	private JSONArray regTable, studentTable, /*courseTable,*/ attTable;
	private String[] /*courses,*/ codes = null, students = null;
	private ArrayAdapter<String> adapter1, adapter2;
//	private String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dshowattendance);

		spShowAttendanceCourse = (Spinner) findViewById(R.id.spDShowAttendanceCourse);
		spShowAttendanceStudent = (Spinner) findViewById(R.id.spDShowAttendanceStudent);
		bShowAttendanceOk = (Button) findViewById(R.id.bDShowAttendanceOk);
		bShowAttendanceBack = (Button) findViewById(R.id.bDShowAttendanceBack);
		tvShowAttendanceInfo = (TextView) findViewById(R.id.tvDShowAttendanceInfo);

		new HttpRequestUtils();
		//check Internet connection retrieve data from database 
		new AsyncTask<String, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				regTable = JsonParser.JsonParse(JsonParser.JsonRead("RegistrationD"));
				studentTable = JsonParser.JsonParse(JsonParser.JsonRead("StudentD"));
//				courseTable = JsonParser.JsonParse(JsonParser.JsonRead("CourseD"));
				attTable = JsonParser.JsonParse(JsonParser.JsonRead("AttendanceD"));
				if(regTable == null){
					students = new String[1];
					return false;
				}else{
					students = JsonParser.returnAllStudent(studentTable);
					return true;
				}
			}
			@Override
			protected void onPostExecute(Boolean result) {
				if(!result){
					Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
					finish();
				}
				super.onPostExecute(result);
			}
		}.execute();

		bShowAttendanceBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		// make a course spinner
		while (students == null) {
		}
		adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, students);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spShowAttendanceCourse.setAdapter(adapter1);
		spShowAttendanceCourse.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						//codes might be null if this student has not enrolled in any course
						String[] students;
						if(spShowAttendanceCourse.getSelectedItem() != null){
							String[] uids = JsonParser.findStudentUIDsBySid(spShowAttendanceCourse.
									getSelectedItem().toString(),studentTable);
							String uid = null;
							for(int i = uids.length - 1; i>=0; i--){
								if(uids[i] != null && uids[i] != ""){
									uid = uids[i];
									break;
								}
							}
							codes = JsonParser.findCourseInReg(uid, regTable);
//							uids = JsonParser.returnRegedStudent(regTable,spShowAttendanceCourse.
//								getSelectedItem().toString());
						}
						if(codes == null){
							codes = new String[1];
						}
//						System.out.println("uids is here!!!!!!");
//						for(int i = 0 ; i<uids.length;i++){
//							System.out.println("uids: "+uids[i]);
//						}
						
						//build courses from code
						codes[0] = "All";
						adapter2 = new ArrayAdapter<String>(adapter2.getContext(),
								android.R.layout.simple_spinner_item, codes);
						spShowAttendanceStudent.setAdapter(adapter2);
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
//						uids = null;
//						students = null;
					}
				});
		spShowAttendanceCourse.setVisibility(View.VISIBLE);
		
		String[] buffer = {"Please choose a student first"};
		adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, buffer);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spShowAttendanceStudent.setAdapter(adapter2);
		spShowAttendanceStudent.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent,
					View view, int position, long id) {
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		bShowAttendanceOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StringBuffer content = new StringBuffer("");
//				String[] records;
				String cCourse, cStudent;
				if(spShowAttendanceCourse.getSelectedItem() == null || 
						spShowAttendanceStudent.getSelectedItem() == null){
					Toast.makeText(getApplicationContext(), "Please select valid student and course", Toast.LENGTH_SHORT).show();
				}else if(spShowAttendanceStudent.getSelectedItem().toString().equals("All")){
					//show all student records
					cStudent = spShowAttendanceCourse.getSelectedItem().toString();
					//given: cStudent, studentTable, regTable, uids[]
					class Unit{
						String code;
						String quota;
//						public Unit(){
//								sid = null;
//								attendance = 0;
//						}
						public Unit(String code, String quota) {
							super();
							this.code = code;
							this.quota = quota;
						}
					}
					if(codes.length == 0){
						tvShowAttendanceInfo.setText("This student has not enrolled any course yet.");
						return;
					}
					String[] uids = JsonParser.findStudentUIDsBySid(cStudent,studentTable);
					String uid = null;
					for(int i = uids.length - 1; i>=0; i--){
						if(uids[i] != null && uids[i] != ""){
							uid = uids[i];
							break;
						}
					}
					Unit[] record = new Unit[codes.length] ;
					for(int i = 1, num = 0 ; i < codes.length && codes[i] != null; i++,num++){
						try {
							JSONObject dummy = JsonParser.findRecordInReg(uid, codes[i], regTable);
							record[num] = new Unit(dummy.getString("cid"),dummy.getString("attendance"));
//							System.out.println("");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					//print record
					content = content.append("Student: "+cStudent+"\n\n");
					System.out.println("record[0]: "+record[0]);
					for(int i = 0 ; i<record.length && record[i]!=null;i++){
						System.out.println("record[i]: "+record[i]);
						content = content.append("Course Code: "+record[i].code+"\nCourse Quota: "
									+record[i].quota+"\n\n");
					}
					
				}else{
					cStudent = spShowAttendanceCourse.getSelectedItem().toString();
					cCourse = spShowAttendanceStudent.getSelectedItem().toString();
					String[] cards;
					int attendance = 0;
					String content2 = "", quota = "";
					try {
						String[] uids = JsonParser.findStudentUIDsBySid(spShowAttendanceCourse.
								getSelectedItem().toString(),studentTable);
						String uid = null;
						for(int i = uids.length - 1; i>=0; i--){
							if(uids[i] != null && uids[i] != ""){
								uid = uids[i];
								break;
							}
						}
						quota = JsonParser.findRecordInReg(uid, cCourse, regTable).getString("attendance");
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					content = content.append("Student: "+ cStudent+"\nCourse: "+cCourse+"\nQuota: "+ quota+ "\n");
					
					//given: cCourse(cid), cStudent(sid), attTable, studenTable
					cards = JsonParser.findStudentUIDsBySid(cStudent, studentTable);
					for(int i = 0 ; i<cards.length && cards[i] != null;i++){
						for(int j = 0; j<attTable.length();j++){
							String uid, cid, time, date;
							int hour, minute;
							try {
								uid = attTable.getJSONObject(j).getString("uid");
								cid = attTable.getJSONObject(j).getString("code");
								time = attTable.getJSONObject(j).getString("createdAt");
								date = time.substring(0, 10);
								hour = (Integer.parseInt(time.substring(11,13)) + 8)%24;
								minute = Integer.parseInt(time.substring(14,16));
								if(cCourse.equals(cid) && cards[i].equals(uid)){
									if(hour>=10 && minute>=10){
										content2 = content2 + "UID: "+ cards[i]+"\nNo.: "+ (attendance+1)+
												"\nDate: "+ date+"  Time: "+hour+":"+minute+" \n\n";
									}else{
										String hourS, minuteS;
										if(hour<10){
											hourS = "0"+hour;
										}else{
											hourS = hour+"";
										}
										if(minute<10){
											minuteS = "0"+minute;
										}else{
											minuteS = minute + "";
										}
										content2 = content2 + "UID: "+ cards[i]+"\nNo.: "+ (attendance+1)+
												"\nDate: "+ date+"  Time: "+hourS+":"+minuteS+" \n\n";
									}
									attendance++;
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
					content = content.append("Attendance: "+attendance+"\n\n").append(content2);
				}
				tvShowAttendanceInfo.setText(content);
//				uids = null;
//				students = null;
			}
		});
		
	}

}


