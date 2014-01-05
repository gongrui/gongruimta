package com.gongrui.mta.common;

import net.tsz.afinal.FinalActivity;
import android.widget.Toast;

public class BaseActivity  extends FinalActivity { 

	/** 应用程序缺省显示Toast**/
	protected void showToast(String text) {
		Toast t= Toast.makeText(this, text, Toast.LENGTH_LONG);
		t.show();
	}

}
