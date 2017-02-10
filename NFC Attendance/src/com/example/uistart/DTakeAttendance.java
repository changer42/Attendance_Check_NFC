package com.example.uistart;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.nfc.NdefMessage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lib.JsonParser;
import com.example.lib.NdefMessageParser;
import com.example.lib.ParsedNdefRecord;
import com.example.uistart.R;

/**This class is to provide basic function for this application. 
 * It helps user to take attendance.
 * 
 * @author Hilda
 */

public class DTakeAttendance extends ParentActivity {

	private static final DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();
	private LinearLayout CardContent;
	private TextView tvCounter;
	private JSONArray regTable, studentTable;
	private String courseCode, studentID;
	private int counter;
	private ArrayList<String> checkList;
	private NdefMessage[] msgs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dattendanceviewer);
		CardContent = (LinearLayout) findViewById(R.id.dattendanceList);
		tvCounter = (TextView) findViewById(R.id.tvDCounter);
		counter = 0;
		//retrieve data from web
		new AsyncTask<String, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				// new HttpRequestUtils();
				regTable = JsonParser.JsonParse(JsonParser
						.JsonRead("RegistrationD"));
				studentTable = JsonParser.JsonParse(JsonParser
						.JsonRead("StudentD"));
				if (regTable == null) {
					return false;
				} else {
					return true;
				}
			}
			//connection check
			@Override
			protected void onPostExecute(Boolean result) {
				if (result == false) {
					Toast.makeText(getApplicationContext(),
							"No Internet Connection", Toast.LENGTH_SHORT).show();
					finish();
				}
				super.onPostExecute(result);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		//build document for duplication check
		checkList = new ArrayList<String>();
	}

	@Override
	protected String dumpTagData(final long id) {
		StringBuilder sb = new StringBuilder();
		sb.append("UID: ").append(id);
		//
		new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				String uid = String.valueOf(id);
				String decrease =null;
				// refresh table --->>get instant data to avoid shutting down
				regTable = JsonParser.JsonParse(JsonParser
						.JsonRead("RegistrationD"));
				String[] courseReged = JsonParser
						.findCourseInReg(uid, regTable);
				final int quantity = Integer.parseInt(courseReged[0]);
				
				// insert new record in attendance
				try {
					studentID = JsonParser.findStudentByUid(uid, studentTable)
							.getString("sid");
					// System.out.println("StudentID: "+ studentID);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Can't find this student in student table");
				}

				if (courseReged[0].equals("1")) {
					//check duplication
					if (checkList.contains(studentID + "" + courseReged[1])) {
						return null;
					}
					// update registration table & insert new record in
					// attendance table
					JsonParser.AttendanceDCreate(uid, courseReged[1]);
					//check out of quota
					if(JsonParser.decreaseAttValue(uid, courseReged[1], regTable).equals("fail")){
						return "fail";
					}
					courseCode = courseReged[1];
					checkList.add(studentID + "" + courseReged[1]);
					counter++;
				}

				// generate return String
				String returnValue = courseReged[0] + " ";
				int i;
				for (i = 1; i < quantity; i++) {
					returnValue = returnValue + courseReged[i] + " ";
				}
				returnValue = returnValue + courseReged[i];
				return returnValue;// uid+"+"+course;
			}

			@Override
			protected void onPostExecute(String result) {
				 System.out.println("result = " + result);
				if (result == null) {
					courseCode = null;
					studentID = null;
					Toast.makeText(getApplicationContext(), "Duplication",
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(result.equals("fail")){
					courseCode = null;
					studentID = null;
					AlertDialog.Builder builder = new AlertDialog.Builder(
							DTakeAttendance.this);
					builder.setTitle("Take Attendance");
					builder.setMessage("Failed. \nNo more quota!  "
							+ "Please recharge.\nNo attendance changed.");
					builder.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
					AlertDialog dialog = builder.create();
					dialog.show();
					return;
				}
				
				// Split result
				String[] courses = result.split(" ");
				final int quantity = Integer.parseInt(courses[0]);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						DTakeAttendance.this);
				if (quantity == 0) {
					builder.setTitle("Take Attendance");
					builder.setMessage("Failed. \nThis card have not been registered for "
							+ "any course yet.\n No attendance changed.");
					builder.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
				} else if (quantity == 1) {
					// counter++;
					builder.setTitle("Take Attendance");
					builder.setMessage("Successed!");
					builder.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
					updateView();
				} else if (quantity >= 2) {
					builder.setTitle("Pick a Course");
					final String[] choiceS = new String[quantity];
					for (int i = 0; i < quantity; i++) {
						// System.out.println("course"+courses[i+1]);
						choiceS[i] = courses[i + 1];
					}
					builder.setItems(choiceS,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									courseCode = choiceS[item];
									if (checkList.contains(studentID + ""
											+ choiceS[item])) {
										courseCode = null;
										studentID = null;
										Toast.makeText(getApplicationContext(),
												"Duplication",
												Toast.LENGTH_SHORT).show();
										return;
									}
									courseCode = choiceS[item];
									Toast.makeText(getApplicationContext(),
											choiceS[item], Toast.LENGTH_SHORT)
											.show();
									updateView();
									new AsyncTask<String, Void, String>() {
										@Override
										protected String doInBackground(
												String... params) {
											// update registration table &
											// insert new record in attendance table
											String uid = String.valueOf(id);
											JsonParser.AttendanceDCreate(uid,
													courseCode);
											//TODO: check fails
											if(JsonParser.decreaseAttValue(uid,
													courseCode, regTable).equals("fail")){
												return "fail";
											}
											counter++;
											checkList.add(studentID + ""
													+ courseCode);
											return null;
										}

										@Override
										protected void onPostExecute(
												String result) {
											if(result.equals("fail")){
												Toast.makeText(getApplicationContext(), "No More Quota. Please Recharge.",
														Toast.LENGTH_SHORT).show();
											}else{
												tvCounter.setText("Attendance Counter: "+ counter);
											}
											super.onPostExecute(result);
										}
									}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
									// counter++;
								}
							});
				}
				AlertDialog dialog = builder.create();
				dialog.show();
				tvCounter.setText("Attendance Counter: " + counter);
				super.onPostExecute(result);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		//sb.append("student ID: " + studentID).append("\n");
		//sb.append("course code: " + courseCode).append("\n");
		// print list
//		for (String att : checkList) {
//			System.out.println("ckeckList contains: " + att);
//		}
		return sb.toString();
	}

	@Override
	protected void buildTagViews(NdefMessage[] msgs) {
		this.msgs = msgs;
		/*
		 * if (msgs == null || msgs.length == 0) { return; } LayoutInflater
		 * inflater = LayoutInflater.from(this); LinearLayout content =
		 * CardContent;
		 * 
		 * // Parse the first message in the list // Build views for all of the
		 * sub records Date now = new Date(); List<ParsedNdefRecord> records =
		 * NdefMessageParser.parse(msgs[0]); final int size = records.size();
		 * for (int i = 0; i < size; i++) { TextView timeView = new
		 * TextView(this); timeView.setText(TIME_FORMAT.format(now));
		 * content.addView(timeView, 0); ParsedNdefRecord record =
		 * records.get(i); content.addView(record.getView(this, inflater,
		 * content, i), 1 + i); content.addView(
		 * inflater.inflate(R.layout.carddivider, content, false), 2 + i); }
		 */

	}

	private void updateView() {
		if (msgs == null || msgs.length == 0) {
			return;
		}
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout content = CardContent;

		// Parse the first message in the list
		// Build views for all of the sub records
		Date now = new Date();
		List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
		final int size = records.size();
		for (int i = 0; i < size; i++) {
			TextView timeView = new TextView(this);
			timeView.setText(TIME_FORMAT.format(now));
			content.addView(timeView, 0);
			ParsedNdefRecord record = records.get(i);
			content.addView(record.getView(this, inflater, content, i), 1 + i);
			content.addView(
					inflater.inflate(R.layout.carddivider, content, false),
					2 + i);
		}
	}
}
