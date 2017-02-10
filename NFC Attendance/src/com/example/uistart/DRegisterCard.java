package com.example.uistart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.lib.JsonParser;
import com.example.uistart.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.nfc.NdefMessage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class register a new card for a student.
 * 
 * 
 * @author Hilda
 */

public class DRegisterCard extends ParentActivity{
	
	private JSONArray studentTable;
	private EditText etRegCardSName, etRegCardSID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dregistercard);
		etRegCardSName = (EditText)findViewById(R.id.etDRegCardSName);
		etRegCardSID = (EditText)findViewById(R.id.etDRegCardSID);
		
		//check network connection
		new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				studentTable = JsonParser.JsonParse(JsonParser.JsonRead("StudentD"));
//				System.out.println(studentTable);
				if(studentTable == null){
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
		// data transmission
		
		new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				boolean flag;
				studentTable = JsonParser.JsonParse(JsonParser.JsonRead("StudentD"));
				if(JsonParser.isCardExisted(uid, studentTable)){
					flag = true;
				}else{
					flag = false;
				}
				return flag;
			}
			
			@Override
			protected void onPostExecute(Boolean flag) {
				AlertDialog.Builder builder = new AlertDialog.Builder(DRegisterCard.this);
				JSONObject cStudent;
				String sid = null, sName = null;
				if(flag){
					builder.setTitle("Card Exists");
					cStudent = JsonParser.findStudentByUid(uid, studentTable);
					try {
						sid = cStudent.getString("sid");
						sName = cStudent.getString("name");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					builder.setMessage("UID: "+uid+"\nStudent ID: "+sid+"\nStudent Name: "+sName);
					builder.setNeutralButton("Back", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
				}else{
					final Editable nSid = etRegCardSID.getText();
					final Editable nSName = etRegCardSName.getText();
					if((nSid+"").equalsIgnoreCase("") || (nSName+"").equalsIgnoreCase("")){
						builder.setTitle("New Card Detected ");
						builder.setMessage("Please type in both student name and ID then tap your card again.");
						builder.setNeutralButton("Back", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							//back to previous page
							}
						});
					}else{
						builder.setTitle("New Card Detected ");
						builder.setMessage("UID: "+uid+"\nStudent ID: "+etRegCardSID.getText()+
								"\nStudent Name: "+etRegCardSName.getText()+"\n");
						//positive on right
						builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							//back to previous page
							}
						});
						//negative on left
						builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								new AsyncTask<String, Void, String>() {
									@Override
									protected String doInBackground(String... params) {
										JsonParser.StudentDCreate(uid, nSid+"", nSName+"");
										return null;
									}
									@Override
									protected void onPostExecute(String result) {
										etRegCardSID.setText("");
										etRegCardSName.setText("");
										super.onPostExecute(result);
									}
								}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							}
						});
					}
				}
				AlertDialog dialog = builder.create();
				dialog.show();
				super.onPostExecute(flag);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		return uid;
	}

	@Override
	protected void buildTagViews(NdefMessage[] msgs) {
	}

}
