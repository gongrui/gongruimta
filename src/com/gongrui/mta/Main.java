package com.gongrui.mta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gongrui.mta.adapter.ListViewAdapter;
import com.gongrui.mta.common.BaseActivity;
import com.gongrui.mta.common.DateUtil;
import com.gongrui.mta.common.Pager;
import com.gongrui.mta.entity.Temperature;
import com.gongrui.mta.service.PreferencesService;

public class Main extends BaseActivity implements View.OnTouchListener{

	public static Main instance = null;

	private ViewPager mTabPager;
	private ImageView mTabImg;// 动画图片
	private ImageView mTab1, mTab2, mTab3;
	private View mView1, mView2, mView3;
	
	private LinearLayout mLayer1,mLayer2,mLayer3;
	

	private int zero = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int one;// 单个水平动画位移
	private int two;
	
	private LinearLayout mClose;
	private LinearLayout mCloseBtn;
	private View layout;
	private boolean menu_display = false;
	private PopupWindow menuWindow;
	private LayoutInflater inflater;

	private EditText etcwsj;
	private EditText etwd1;
	private EditText etwd2;
	private EditText etwd3;
	private EditText etgc;
	private EditText etbd;
	private EditText etcw;
	private EditText ethjwd;
	private EditText ethjsd;
	private EditText etjd;
	private EditText etwd;
	private EditText etwz;

	private EditText etserverip;
	private EditText etserverport;
	private EditText etserverurl;

	private FinalDb db;
	private Socket socket = null;
	private PreferencesService service;

	private ListView listView;
	private ListViewAdapter adapter;
	private Pager pager = new Pager(0, 20);
	
	
	
	
	private BufferedReader in = null;
	private PrintWriter out = null;

	private String content = "";
	private Handler mHandler;

	// private Button mRightBtn;
	
	
	protected static final int CONTEXTMENU_SENDITEM = 0;
	protected static final int CONTEXTMENU_DELETEITEM = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		db = FinalDb.create(this, "bluemta");
		service = new PreferencesService(this);// 放到oncreate,只需要实例化一次这个对象就可以了
		mHandler = new Handler();

		

		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		instance = this;

		mTabPager = (ViewPager) findViewById(R.id.tabpager);
		mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());

		mTab1 = (ImageView) findViewById(R.id.img_index);
		mTab2 = (ImageView) findViewById(R.id.img_history);
		mTab3 = (ImageView) findViewById(R.id.img_settings);

		mTabImg = (ImageView) findViewById(R.id.img_tab_now);
		
		mLayer1 = (LinearLayout)findViewById(R.id.layertabindex);
		mLayer2 = (LinearLayout)findViewById(R.id.layertabhistory);
		mLayer3 = (LinearLayout)findViewById(R.id.layertabsettings);
		
		
		

		mTab1.setOnClickListener(new MyOnClickListener(0));
		mTab2.setOnClickListener(new MyOnClickListener(1));
		mTab3.setOnClickListener(new MyOnClickListener(2));

		Display currDisplay = getWindowManager().getDefaultDisplay();// 获取屏幕当前分辨率
		int displayWidth = currDisplay.getWidth();
		int displayHeight = currDisplay.getHeight();
		one = displayWidth / 3; // 设置水平动画平移大小
		two = one * 2;
		
		// Log.i("info", "获取的屏幕分辨率为" + one + two + three + "X" + displayHeight);

		// 将要分页显示的View装入数组中
		LayoutInflater mLi = LayoutInflater.from(this);
		mView1 = mLi.inflate(R.layout.main_tab_index, null);
		mView2 = mLi.inflate(R.layout.main_tab_history, null);
		mView3 = mLi.inflate(R.layout.main_tab_settings, null);

		// 每个页面的view数据
		final ArrayList<View> views = new ArrayList<View>();
		views.add(mView1);
		views.add(mView2);
		views.add(mView3);

		// 初始化所有元素 ,findviewbyid
		init();
		
		
		//加入日期触摸选择
		etcwsj.setOnTouchListener(this);
		
		
		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(views.get(position));
			}

			// @Override
			// public CharSequence getPageTitle(int position) {
			// return titles.get(position);
			// }

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(views.get(position));
				return views.get(position);
			}
		};
		
		mTabPager.setAdapter(mPagerAdapter);
		
		//设置当前时间到 etcwsj
		setCurrentTime();
		
		//设置本地历史记录适配器
		setListView();
		
		//增加listview上下文菜单
		listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			public void onCreateContextMenu(ContextMenu conMenu, View view, ContextMenuInfo info) {
				conMenu.setHeaderTitle("操作数据");
				conMenu.add(0, 0, 0, "发送本条数据");
				conMenu.add(0, 1, 0, "删除本条数据");
				/* Add as many context-menu-options as you want to. */
			}
		});
		
		//读取配置信息
		setinitconfig();

	}
	
	
	private void setinitconfig(){
		Map<String, String> params = service.getPreference();
		etserverip.setText(params.get("ip"));
		etserverport.setText(params.get("port"));
		etserverurl.setText(params.get("serverurl"));
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * 页卡切换监听(原作者:D.Winter)
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_index_pressed));
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_history_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
				}
				break;
			case 1:
				mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_history_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, one, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_index_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
				}
				break;
			case 2:
				mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, two, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_index_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_history_normal));
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index);
			
		}
	};

	private void setListView() {

		listView = (ListView) mView2.findViewById(R.id.lvtemp);
		initializeAdapter();
		listView.setAdapter(adapter);
		
	};

	private void initializeAdapter() {

		List<Temperature> lts = db.findAllByWhere(Temperature.class, "1=1 Order by cwsj desc  limit " + pager.getIndex() + ", " + pager.getView_Count());
		adapter = new ListViewAdapter(this, lts);

	}
	
	//listview的上下文菜单
	@Override
	public boolean onContextItemSelected(MenuItem aItem) {
		ContextMenuInfo menuInfo = (ContextMenuInfo) aItem.getMenuInfo();

		AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterContextMenuInfo) menuInfo;

		// 获取选中行位置
		int position = contextMenuInfo.position;

		/* Switch on the ID of the item, to get what the user selected. */
		switch (aItem.getItemId()) {
		case CONTEXTMENU_SENDITEM:
			/* Get the selected item out of the Adapter by its position. */
			sendselecttoserver(position);
			return true; /* true means: "we handled the event". */

		case CONTEXTMENU_DELETEITEM:
			/* Get the selected item out of the Adapter by its position. */
			Temperature t = (Temperature) listView.getAdapter().getItem(position);

			db.delete(t);

			ListViewAdapter la = (ListViewAdapter) listView.getAdapter();
			la.items.remove(position);
			la.notifyDataSetChanged();
			return true; /* true means: "we handled the event". */
		}
		return false;
	}

	public void init() {

		this.etcwsj = (EditText) mView1.findViewById(R.id.etcwsj);
		this.etwd1 = (EditText) mView1.findViewById(R.id.ett1);
		this.etwd2 = (EditText) mView1.findViewById(R.id.ett2);
		this.etwd3 = (EditText) mView1.findViewById(R.id.ett3);

		this.etgc = (EditText) mView1.findViewById(R.id.etgc);
		this.etbd = (EditText) mView1.findViewById(R.id.etbd);
		this.etcw = (EditText) mView1.findViewById(R.id.etcw);
		this.ethjwd = (EditText) mView1.findViewById(R.id.ethjwd);
		this.ethjsd = (EditText) mView1.findViewById(R.id.ethjsd);
		this.etjd = (EditText) mView1.findViewById(R.id.etjd);
		this.etwd = (EditText) mView1.findViewById(R.id.etwd);

		this.etwz = (EditText) mView1.findViewById(R.id.etwz);

		this.etserverip = (EditText) mView3.findViewById(R.id.et_serverip);
		this.etserverport = (EditText) mView3.findViewById(R.id.et_serverport);
		this.etserverurl = (EditText) mView3.findViewById(R.id.et_serverurl);
	}

	@SuppressLint("SimpleDateFormat")
	private void setCurrentTime() {
		final Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		etcwsj.setText(format.format(mCalendar.getTime()));
	}

	/**
	 * 获取位置
	 * 
	 * @param v
	 */
	public void btngetwz(View v) {
		addLocation();

	}

	/**
	 * 获取温度
	 * 
	 * @param v
	 */
	public void btngetwd(View v) {
		new Thread(runnable).start();

	}

	/**
	 * 保存到本地
	 * 
	 * @param v
	 */
	public void btnsavelocal(View v) throws ParseException {
		Temperature t = new Temperature();

		// 注意format的格式要与日期String的格式相匹配

		t.setCwsj(DateUtil.str2longtime(etcwsj.getText().toString(), "yyyy-MM-dd HH:mm"));
		t.setBd(etbd.getText().toString());
		t.setCw(etcw.getText().toString());
		t.setGc(etgc.getText().toString());
		t.setWd1(etwd1.getText().toString());
		t.setWd2(etwd2.getText().toString());
		t.setWd3(etwd3.getText().toString());
		t.setWz(etwz.getText().toString());
		t.setHjwd(ethjwd.getText().toString());
		t.setHjsd(ethjsd.getText().toString());

		t.setJd(etjd.getText().toString());
		t.setWd(etwd.getText().toString());

		db.save(t);

		setListView();
		this.showToast("保存数据成功！");
	}

	/**
	 * 保存到本地
	 * 
	 * @param v
	 */
	public void btnselectwifi(View v) {
		if (android.os.Build.VERSION.SDK_INT > 10) {
			// 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
			startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
		} else {
			startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		}
	}

	/**
	 * 提交到服务器
	 * 
	 * @param v
	 */
	public void btncommitserver(View v) {
		

		Map<String, String> settingparams = service.getPreference();
		String serverurl = settingparams.get("serverurl");
		AjaxParams params = new AjaxParams();
		  params.put("cwsj", this.etcwsj.getText().toString());
		  params.put("t1", this.etwd1.getText().toString());
		  params.put("t2", this.etwd2.getText().toString());
		  params.put("t3", this.etwd3.getText().toString());
		  params.put("wz", this.etwz.getText().toString());
		  params.put("bd", this.etbd.getText().toString());
		  params.put("cw", this.etcw.getText().toString());
		  params.put("gc", this.etgc.getText().toString());
		  
		  params.put("jd", this.etjd.getText().toString());
		  params.put("wd", this.etwd.getText().toString());
		
		  params.put("hjwd", this.ethjwd.getText().toString());
		  params.put("hjsd", this.ethjsd.getText().toString());


		  
		  FinalHttp fh = new FinalHttp();
		  fh.post(serverurl, params, new AjaxCallBack<Object>(){
		        @Override
		        public void onLoading(long count, long current) {
//		                textView.setText(current+"/"+count);
		        }
		        @Override
		        public void onSuccess(Object t){
		        	if(t.toString().equals("success")){

			        	showToast("温度数据已经成功保存至服务器！");
		        	}else{
			        	showToast("提交至服务器失败！");
		        	}
		        };
		        
		        @Override
		    	public void onFailure(Throwable t,int errorNo ,String strMsg){
		        	showToast("提交数据失败！");
		        };		        
		        
		  });
	}
	
	
	/**
	 * 从选中列表内容提交到服务器
	 * 
	 * @param v
	 */
	private void sendselecttoserver(int position) {

		Temperature t = (Temperature) listView.getAdapter().getItem(position);

		Map<String, String> settingparams = service.getPreference();
		String serverurl = settingparams.get("serverurl");
		AjaxParams params = new AjaxParams();

		params.put("cwsj", DateUtil.longtime2str(t.getCwsj(), "yyyy-MM-dd HH:mm"));
		params.put("t1", t.getWd1());
		params.put("t2", t.getWd2());
		params.put("t3", t.getWd3());
		params.put("wz", t.getWz());
		params.put("bd", t.getBd());
		params.put("cw", t.getCw());
		params.put("gc", t.getGc());
		
		
		params.put("jd", t.getJd());
		params.put("wd", t.getWd());
		
		params.put("hjwd", t.getHjwd());
		params.put("hjsd", t.getHjsd());

		FinalHttp fh = new FinalHttp();
		fh.post(serverurl, params, new AjaxCallBack<Object>() {
			@Override
			public void onLoading(long count, long current) {
				// textView.setText(current+"/"+count);
			}

			@Override
			public void onSuccess(Object t) {
				if (t.toString().equals("success")) {

					showToast("温度数据已经成功保存至服务器！");
				} else {
					showToast("提交至服务器失败！");

				}
			};

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {

				showToast("提交数据失败！");

			};

		});
	}
	

	/**
	 * 缺省配置
	 * 
	 * @param v
	 */
	public void btnsettingdefault(View v) {
		etserverip.setText("192.168.1.10");
		etserverport.setText("5678");
		etserverurl.setText("http://192.168.1.10/savewendu.php");
	}

	/**
	 * 保存配置
	 * 
	 * @param v
	 */
	public void btnsettingsave(View v) {
		service.save(etserverip.getText().toString(), Integer.parseInt(etserverport.getText().toString()), etserverurl.getText().toString());
		this.showToast("保存配置成功！");
		mTabPager.setCurrentItem(0);

	}
	
	public void startAbout(View v){
		Intent intent = new Intent (Main.this,About.class);			
		startActivity(intent);	
		
	}

	/**
	 * 本地记录标题右侧按钮
	 * 
	 * @param v
	 */
	public void btnhistoryright(View v) {
		Intent intent = new Intent(Main.this, HistoryTopRightDialog.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //
			if (menu_display) { //
				menuWindow.dismiss();
				menu_display = false;
			} else {
				Intent intent = new Intent();
				intent.setClass(Main.this, Exit.class);
				startActivity(intent);

			}
		}
		return menu_display;
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {

		case R.id.etcwsj:

			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				View view = View.inflate(this, R.layout.date_time_dialog, null);
				final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
				final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker);
				builder.setView(view);

				Calendar cal = Calendar.getInstance();

				cal.setTimeInMillis(System.currentTimeMillis());

				datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

				timePicker.setIs24HourView(true);

				timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));

				timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));

				final int inType = etcwsj.getInputType();

				etcwsj.setInputType(InputType.TYPE_NULL);

				etcwsj.onTouchEvent(event);

				etcwsj.setInputType(inType);

				etcwsj.setSelection(etcwsj.getText().length());

				builder.setTitle("选取起始时间");
				builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						StringBuffer sb = new StringBuffer();
						sb.append(String.format("%d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth()));

						sb.append("  ");


						sb.append(String.format("%02d:%02d", timePicker.getCurrentHour(), timePicker.getCurrentMinute()));

						etcwsj.setText(sb);
						etcwsj.requestFocus();
						dialog.cancel();
					}

				});

				Dialog dialog = builder.create();
				dialog.show();

			}
			return true;

		}

		return true;
	}
	
	
	
	
	
	
	/**
	 * 获取位置信息
	 */
	private void addLocation() {
		LocationManager locationManager = (LocationManager) Main.this.getSystemService(Context.LOCATION_SERVICE);

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Toast.makeText(Main.this, "请打开gps", Toast.LENGTH_SHORT).show();
			return;
		}

		Toast.makeText(Main.this, "定位中", Toast.LENGTH_SHORT).show();

		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);

		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private void updateWithNewLocation(Location result) {
		
//		Toast.makeText(MainActivity.this, "lat:" + result.getLatitude() + "  lon:" + result.getLongitude(), Toast.LENGTH_SHORT).show();
		
		etjd.setText(String.valueOf(result.getLongitude()));
		etwd.setText(String.valueOf(result.getLatitude()));

		
		Geocoder geocoder = new Geocoder(Main.this, Locale.getDefault());

		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(result.getLatitude(), result.getLongitude(), 5);
			// Toast.makeText(MainActivity.this, "lat:"+ addresses.size(),
			// Toast.LENGTH_SHORT).show();

			String placename = ((Address) addresses.get(0)).getAddressLine(0);
			if (((Address) addresses.get(0)).getAddressLine(1) != null) {
				placename += ((Address) addresses.get(0)).getAddressLine(1);
			}

			if (((Address) addresses.get(0)).getAddressLine(2) != null) {
				placename += ((Address) addresses.get(0)).getAddressLine(2);
			}

			if (placename != null) {

				etwz.setText(placename);
			}

		} catch (IOException e) {

		}

		((LocationManager) Main.this.getSystemService(Context.LOCATION_SERVICE)).removeUpdates(locationListener);

	}
	
	
	/**
	 * 读取socket信息
	 */

	Runnable runnable = new Runnable() {

		String[] temp = new String[2];

		@Override
		public void run() {

			Map<String, String> params = service.getPreference();

			String HOST = params.get("ip");
			int PORT = Integer.parseInt(params.get("port"));

			try {
				socket = new Socket(HOST, PORT);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
				try {
					while (true) {
						if (socket.isConnected()) {

							if (!socket.isInputShutdown()) {
								if ((content = in.readLine()) != null) {
									temp = content.split("=");

									if (temp[0].equalsIgnoreCase("t1")) {
										mHandler.post(new Runnable() {
											@Override
											public void run() {
												etwd1.setText(temp[1]);
											}
										});

									} else if (temp[0].equalsIgnoreCase("t2")) {
										mHandler.post(new Runnable() {
											@Override
											public void run() {
												etwd2.setText(temp[1]);
											}
										});
									} else if (temp[0].equalsIgnoreCase("t3")) {
										mHandler.post(new Runnable() {
											@Override
											public void run() {
												etwd3.setText(temp[1]);
											}
										});
									}
								} else {

								}

							}
						}
					}
				} catch (Exception e) {
					try {
						socket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						temp[0] =e.getMessage();
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								showToast(temp[0]);
							}
						});
					}
					temp[0] =e.getMessage();
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							showToast(temp[0]);
						}
					});
				}

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				// ShowDialog(e.getMessage().toString());
				temp[0] =e.getMessage();
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						showToast(temp[0]);
					}
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				temp[0] =e.getMessage();
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						showToast(temp[0]);
					}
				});
			}
		}
	};
	
	
	

	public ListView getListView() {
		return listView;
	}


	public void setListView(ListView listView) {
		this.listView = listView;
	}


	public FinalDb getDb() {
		return db;
	}


	public void setDb(FinalDb db) {
		this.db = db;
	}
	
	
}
