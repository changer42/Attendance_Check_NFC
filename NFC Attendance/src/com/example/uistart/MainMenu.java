package com.example.uistart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenu extends Activity{
	private Button bMainMenuLeft, bMainMenuRight;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);
		
		bMainMenuLeft = (Button) findViewById(R.id.bMainMenuLeft);
		bMainMenuRight = (Button) findViewById(R.id.bMainMenuRight);
		
		bMainMenuRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent openMain = new Intent("com.example.uistart.NFCMENU");//parameter is the action name 
				startActivity(openMain);
			}
		});
		bMainMenuLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent openMain = new Intent("com.example.uistart.DNFCMENU");//parameter is the action name 
				startActivity(openMain);
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}
