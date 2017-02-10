package com.example.uistart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.uistart.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DNFCMenu extends ListActivity {
	String[] functions ={"DTakeAttendance", "DEnroll", "DRegisterCard", "DAddCourse",
			"DCardInfo", "DShowAttendance"}; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.nfcmenu,
                new String[]{"function","img"},
                new int[]{R.id.function,R.id.img});
        setListAdapter(adapter);

    }
    
    
 
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String selectedFunction = functions[position];
		try {
			Class function = Class.forName("com.example.uistart."+ selectedFunction);
			Intent myIntent = new Intent(DNFCMenu.this, function);
			startActivity(myIntent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}



	private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
 
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("function", "Take Attendance");
        map.put("img", R.drawable.numbery1);
        list.add(map);
 
        map = new HashMap<String, Object>();
        map.put("function", "Enroll");
        map.put("img", R.drawable.numbery2);
        list.add(map);
 
        map = new HashMap<String, Object>();
        map.put("function", "Register Card");
        map.put("img", R.drawable.numbery3);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("function", "Add Course");
        map.put("img", R.drawable.numbery4);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("function", "Card Information Retrieve");
        map.put("img", R.drawable.numbery5);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("function", "Show Attendance");
        map.put("img", R.drawable.numbery6);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("function", "");
        map.put("img", R.drawable.spare5);
        list.add(map);
         
        return list;
    }
}