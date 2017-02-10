package com.example.uistart;

import org.json.JSONArray;
import org.json.JSONException;

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

public class ShowAttendance extends Activity {
	private Spinner spShowAttendanceCourse, spShowAttendanceStudent;
	private Button bShowAttendanceOk, bShowAttendanceBack;
	private TextView tvShowAttendanceInfo;
	private JSONArray regTable, studentTable, courseTable, attTable;
	private String[] courses, uids = null;//, students = null;
	private ArrayAdapter<String> adapter1, adapter2;
//	private String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showattendance);

		spShowAttendanceCourse = (Spinner) findViewById(R.id.spShowAttendanceCourse);
		spShowAttendanceStudent = (Spinner) findViewById(R.id.spShowAttendanceStudent);
		bShowAttendanceOk = (Button) findViewById(R.id.bShowAttendanceOk);
		bShowAttendanceBack = (Button) findViewById(R.id.bShowAttendanceBack);
		tvShowAttendanceInfo = (TextView) findViewById(R.id.tvShowAttendanceInfo);

		new HttpRequestUtils();
		//check Internet connection retrieve data from database 
		new AsyncTask<String, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				regTable = JsonParser.JsonParse(JsonParser.JsonRead("Registration"));
				studentTable = JsonParser.JsonParse(JsonParser.JsonRead("Student"));
				courseTable = JsonParser.JsonParse(JsonParser.JsonRead("Course"));
				attTable = JsonParser.JsonParse(JsonParser.JsonRead("Attendance"));
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
		while (courses == null) {
		}
		adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, courses);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spShowAttendanceCourse.setAdapter(adapter1);
		spShowAttendanceCourse.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						//uids might be null if no student enrolls this course
						String[] students;
						if(spShowAttendanceCourse.getSelectedItem() != null){
							uids = JsonParser.returnRegedStudent(regTable,spShowAttendanceCourse.
								getSelectedItem().toString());
						}
						if(uids == null){
							uids = new String[0];
						}
//						System.out.println("uids is here!!!!!!");
//						for(int i = 0 ; i<uids.length;i++){
//							System.out.println("uids: "+uids[i]);
//						}
						
						// build students from uids
						students = new String[uids.length+1];
						try {
							students[0] = "All";
							String bufferSID;
							int length = 1;
							for(int i = 0; i<uids.length; i++){
								bufferSID=JsonParser.findStudentByUid(uids[i],studentTable).getString("sid");
								if(!JsonParser.isRecordExisted(bufferSID, students)){
									students[length] = JsonParser.findStudentByUid(uids[i], 
											studentTable).getString("sid");
									length++;
								}
							}
//							//printing
//							for(int i = 0 ; i<students.length;i++){
//								System.out.println("students: "+students[i]);
//							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						adapter2 = new ArrayAdapter<String>(adapter2.getContext(),
								android.R.layout.simple_spinner_item, students);
						spShowAttendanceStudent.setAdapter(adapter2);
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
//						uids = null;
//						students = null;
					}
				});
		spShowAttendanceCourse.setVisibility(View.VISIBLE);
		
		String[] buffer = {"Please choose course first"};
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
					cCourse = spShowAttendanceCourse.getSelectedItem().toString();
					//given: cCourse, studentTable, regTable, uids[]
					class Unit{
						String sid;
						int attendance;
//						public Unit(){
//								sid = null;
//								attendance = 0;
//						}
						public Unit(String sid, int attendance) {
							super();
							this.sid = sid;
							this.attendance = attendance;
						}
					}
					if(uids.length == 0){
						tvShowAttendanceInfo.setText("No Student Enrolls");
						return;
					}
					Unit[] record = new Unit[uids.length] ;
					String buff;
					for(int i = 0, num = 0 ; i < uids.length; i++){
						try {
							buff = JsonParser.findStudentByUid(uids[i], studentTable).getString("sid");
							//check duplication
							int index = -1;
							for(int j = 0 ; j<record.length && record[j] != null;j++){
								if(buff.equals(record[j].sid)){
									index = j;
									break;
								}
							}
							//build record
							if(index >= 0){//exist
								record[index].attendance= record[index].attendance + Integer.parseInt(
										JsonParser.findRecordInReg(uids[i], cCourse, regTable).getString("attendance"));
							}else{//not exist
								record[num] = new Unit(buff,Integer.parseInt(JsonParser.findRecordInReg(uids[i],cCourse,
										regTable).getString("attendance")));
//								System.out.println("new: "+record[num].sid+"+"+record[num].attendance);
								num++;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					//print record
					for(int i = 0 ; i<record.length && record[i]!=null;i++){
						content = content.append("Student ID: "+record[i].sid+"\nStudent Attendance: "
									+record[i].attendance+"\n\n");
					}
//=============================================================================================================================
//					records = new String[studentTable.length()];
//					
//					content = content.append("Course: "+ cCourse+"\nStudent: All enrolled students\n\n");
//					// uid and student not identical
//					try {
//						int counter = -1;
//						for(int i = 0; i<uids.length;i++){
//							//get student id and attendance first
//							String csid = JsonParser.findStudentByUid(uids[i], studentTable).getString("sid");
//							String cAttendance = JsonParser.findRecordInReg(uids[i], cCourse, regTable).getString("attendance");
//							//check if csid is already in the []records
//							if(JsonParser.isRecordExisted(csid, records)){
//								//not identical ;
//							}else{
//								counter++;
//								records[counter] = csid;
//								content = content.append("Student ID: "+csid+"\nStudent Attendance: "+cAttendance+"\n\n");
//							}
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
				}else{
					cCourse = spShowAttendanceCourse.getSelectedItem().toString();
					cStudent = spShowAttendanceStudent.getSelectedItem().toString();
					String[] cards;
					int attendance = 0;
					String content2 = "";
					content = content.append("Course: "+ cCourse+"\nStudent: "+cStudent+"\n");
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


