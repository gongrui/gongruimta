package com.gongrui.mta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.tsz.afinal.FinalDb;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.gongrui.mta.adapter.ListViewAdapter;
import com.gongrui.mta.common.BaseActivity;
import com.gongrui.mta.common.DateUtil;
import com.gongrui.mta.entity.Temperature;

public class HistoryTopRightDialog extends BaseActivity {
	// private MyDialog dialog;
	private LinearLayout layoutdeleteall;
	private LinearLayout layoutexportall;
	private LinearLayout layoutexport100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_top_right_dialog);
		// dialog=new MyDialog(this);
		layoutexport100 = (LinearLayout) findViewById(R.id.export100);

		layoutexport100.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				export100();
			}
		});

		layoutexportall = (LinearLayout) findViewById(R.id.exportall);

		layoutexportall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				exportall();
			}
		});

		layoutdeleteall = (LinearLayout) findViewById(R.id.deleteall);

		layoutdeleteall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteall();
			}
		});

	}

	private void export100() {

		FinalDb db = FinalDb.create(this, "bluemta");
		List<Temperature> lts = db.findAllByWhere(Temperature.class, "1=1 Order by cwsj desc  limit 0,100");
		exportcsv(lts);

		finish();
	}

	private void exportall() {
		FinalDb db = FinalDb.create(this, "bluemta");
		List<Temperature> lts = db.findAll(Temperature.class);
		exportcsv(lts);
		finish();
	}

	private void deleteall() {

		
		Context context = HistoryTopRightDialog.this;
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle("提示信息");
		ad.setMessage("是否真的要删除所有数据？");

		ad.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				// TODO Auto-generated method stub
				FinalDb db = Main.instance.getDb();
				db.deleteAll(Temperature.class);

				ListViewAdapter la = (ListViewAdapter) Main.instance.getListView().getAdapter();
				la.items.clear();
				la.notifyDataSetChanged();
				finish();
				
			}
		});

		ad.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		ad.show();

	}

	private boolean confirm(String title, String msg, String b1, String b2) {

		

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();

		return true;
	}

	private void exportcsv(List<Temperature> lists) {

		StringBuilder sbBuilder = new StringBuilder();

		String filename = "allwendudata.csv";
		int rowCount = lists.size();
		FileWriter fw;
		BufferedWriter bfw;
		File sdCardDir = Environment.getExternalStorageDirectory();
		File saveFile = new File(sdCardDir, filename);

		byte b[] = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
		String bs = new String(b);

		try {

			fw = new FileWriter(saveFile);
			bfw = new BufferedWriter(fw);
			bfw.write(bs);
			bfw.write("id,测温时间,高程,坝段,仓位,温度1,温度2,温度3,经度,纬度,环境温度,环境湿度,位置");
			// 写好表头后换行
			bfw.newLine();
			Temperature t;
			// 写入数据
			for (int i = 0; i < rowCount; i++) {
				t = lists.get(i);
				bfw.write(t.getId() + ",");
				bfw.write(DateUtil.longtime2str(t.getCwsj(), "yyyy-MM-dd HH:mm") + ",");
				bfw.write(t.getGc() + ",");
				bfw.write(t.getBd() + ",");
				bfw.write(t.getCw() + ",");
				bfw.write(t.getWd1() + ",");
				bfw.write(t.getWd2() + ",");
				bfw.write(t.getWd3() + ",");

				bfw.write(t.getJd() + ",");
				bfw.write(t.getWd() + ",");

				bfw.write(t.getHjwd() + ",");
				bfw.write(t.getHjsd() + ",");
				bfw.write(t.getWz());
				bfw.newLine();
			}

			// 将缓存数据写入文件
			bfw.flush();
			// 释放缓存
			bfw.close();
			// Toast.makeText(mContext, "导出完毕！", Toast.LENGTH_SHORT).show();
			showToast("导出至 " + saveFile.getAbsolutePath() + " 完毕！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			showToast(e.getMessage());
		}
	}
	/*
	 * public void exitbutton1(View v) { this.finish(); } public void
	 * exitbutton0(View v) { this.finish();
	 * MainWeixin.instance.finish();//�ر�Main ���Activity }
	 */
}
