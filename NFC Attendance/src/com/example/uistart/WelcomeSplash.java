package com.example.uistart;

import com.example.uistart.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WelcomeSplash extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcomesplash);
		
		Thread timer = new Thread(){
			public void run(){
				try{
					sleep(2500);
				}catch(InterruptedException e){
					e.printStackTrace();
				}finally{
					Intent openMain = new Intent("com.example.uistart.MAINMENU");//parameter is the action name 
					startActivity(openMain);
				}
			}
		};
		timer.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	

}
