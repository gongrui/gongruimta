package com.gongrui.mta;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class About extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }
  
    public void about_close(View v) {  
      	Intent intent = new Intent();
		intent.setClass(About.this,Main.class);
		startActivity(intent);
		this.finish();
      }  
   
}
