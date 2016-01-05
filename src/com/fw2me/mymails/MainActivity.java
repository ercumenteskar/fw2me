package com.fw2me.mymails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.fw2me.mymails.GoogleAnalyticsApp.TrackerName;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

// implements MIActivity 
public class MainActivity extends ActionBarActivity {

	public static Context	             context;
	public static final String	       WSAdress	                        = "http://70.35.206.36:1001/MobileService.svc/";

	public static boolean	             WSBusy;
	static UserLocalStore	             uls;

	private static final String	       TAG_RESULT	                      = "GetMyMailListResult";
	private static final String	       TAG_ID	                          = "Id";
	private static final String	       TAG_EMail	                      = "EMail";
	private static final String	       TAG_Title	                      = "Title";
	private static final String	       TAG_Active	                      = "Active";
	private static final String	       TAG_CreatedDT	                  = "CreatedDT";
	private static final String	       const_maildomain	                = "fw2.me";	                                   // "benim.in";

	public static final String	       Cookies	                        = "Cookies";

	ListView	                         lv	                              = null;
	JSONArray	                         conns	                          = null;
	GlobalTools	                       glb	                            = new GlobalTools();
	ArrayList<HashMap<String, String>>	connArray;
	boolean	                           tazele	                          = true;
	private String	                   mtype	                          = "";

	private static final int	         PLAY_SERVICES_RESOLUTION_REQUEST	= 9000;
	public static final String	       PROPERTY_REG_ID	                = "AIzaSyAX8gYEmmFTI9sF-1xqqf8QAff7Au33JVM";
	public static final String	       PROJECT_ID	                      = "77689937197";
	private static final String	       PROPERTY_APP_VERSION	            = "appVersion";
	private static final String	       TAG	                            = "FW2ME GCM";
	GoogleCloudMessaging	             gcm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setIcon(R.drawable.ic_launcher);
		context = getApplicationContext();
		uls = new UserLocalStore(context);
		lv = ((ListView) findViewById(R.id.lvMyMails));
		connArray = new ArrayList<HashMap<String, String>>();
		setTitle(R.string.YourMails);
		// Pushy.listen(this);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
			    long id) {
				OpenMyMail(((TextView) view.findViewById(R.id.tvId)).getText()
				    .toString(), ((TextView) view.findViewById(R.id.tvTitle)).getText()
				    .toString(), ((TextView) view.findViewById(R.id.tvEMail)).getText()
				    .toString(), ((TextView) view.findViewById(R.id.tvActive))
				    .getText().toString());
			}

		});

		AdView mAdView = (AdView) findViewById(R.id.adViewMain);
		AdRequest adRequest = new AdRequest.Builder().build();
//		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
//		    "A0AC6DA8C6F5470C829C819123B7F8B2").build();
		mAdView.loadAd(adRequest);
		Tracker t = ((GoogleAnalyticsApp) getApplication())
		    .getTracker(TrackerName.APP_TRACKER);
		t.setScreenName(this.getTitle().toString());
		t.enableAdvertisingIdCollection(true);
		t.send(new HitBuilders.AppViewBuilder().build());

	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(MainActivity.this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(MainActivity.this).reportActivityStop(this);
	}

	private void OpenMyMail(String Id, String Title, String EMail, String Active) {
		Intent in = new Intent(getApplicationContext(), MailAddedActivity.class);
		in.putExtra(TAG_ID, Id);
		in.putExtra(TAG_Title, Title);
		in.putExtra(TAG_EMail, EMail);
		in.putExtra(TAG_Active, Active);
		startActivity(in);
		tazele = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		new GetPushTokenTask().execute();
		if ((tazele)) // (GetPushTokenTask()!="") &&
		{
			new servisAT().execute("GetMyMailList?");
			tazele = false;
		}
	}

	// public String GetPushTokenTask()
	// {
	// String token = "";
	// try {
	// SharedPreferences sp = context.getSharedPreferences(Cookies, 0);
	// if (checkPlayServices()) {//GOOGLE PLAY SERVÝCE APK YÜKLÜMÜ
	// gcm =
	// GoogleCloudMessaging.getInstance(getApplicationContext());//GoogleCloudMessaging
	// objesi oluþturduk
	// token = getRegistrationId(getApplicationContext()); //registration_id olup
	// olmadýðýný kontrol ediyoruz
	// if(token.isEmpty())
	// token = gcm.register(PROJECT_ID);
	// } else token = "No Google Play Services";
	// if ((!token.startsWith("-")) && (!token.equals("")) && (token != null))
	// new servisAT().execute("SetUserInfo_Str?Field=PushToken&_value=" + token);
	// token = "";
	// } catch (Exception e2) {
	// token = e2.toString();
	// }
	// return token;
	// }

	public static String sessionCode() {
		return uls.getStoredUser().SessionCode;
	}

	public static int Lang_id() {
		if (Locale.getDefault().getLanguage().equals("tr"))
			return 2;
		else
			return 1;
	}

	public static String userNo() {
		return uls.getStoredUser().UserNo;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {

		// case R.id.action_settings:
		// startActivity(new Intent(MainActivity.this, PrefsActivity.class));
		// break;
		case R.id.action_logout:
			uls.setUserLoggedIn(false);
			startActivity(new Intent(this, Login.class));
			finish();
			// onResume();
			break;
		case R.id.action_add:
			new servisAT().execute("SetMyMail?Title=");
			// AddNewEMail();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void AddNewEMail(String jsonStr) {
		try {
			if (jsonStr != "") {
				String sid = new JSONObject(jsonStr).getJSONObject("SetMyMailResult")
				    .getString("Id");
				jsonStr = new JSONObject(jsonStr).getJSONObject("SetMyMailResult")
				    .getString("Obj");
				JSONObject c = new JSONObject(jsonStr);
				if (sid == "0")
					OpenMyMail(c.getString(TAG_ID), c.getString(TAG_Title),
					    c.getString(TAG_EMail) + "@" + const_maildomain, "1");
			}
		} catch (Exception e) {
			GlobalTools.ShowTost(e.getMessage());
		}
	}

	private void GetMyMails(String jsonStr) {
		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			JSONObject jo = jsonObj.getJSONObject(TAG_RESULT);
			jsonStr = jo.getString("Obj");
			conns = new JSONArray(jsonStr);
			connArray.clear();
			// looping through All Contacts
			for (int i = 0; i < conns.length(); i++) {
				JSONObject c = conns.getJSONObject(i);

				String id = c.getString(TAG_ID);
				String email = c.getString(TAG_EMail);
				String createddt = c.getString(TAG_CreatedDT);
				String title = c.getString(TAG_Title);
				String active = c.getString(TAG_Active);

				HashMap<String, String> conn = new HashMap<String, String>();
				conn.put(TAG_ID, id);
				conn.put(TAG_Title,
				    ((title.startsWith("#") && title.endsWith("#")) ? "" : title));
				title = ((title.startsWith("#") && title.endsWith("#")) ? title
				    .substring(1, title.length() - 1) : title);
				// conn.put(TAG_ListTitle, title);
				conn.put(TAG_EMail, email + "@" + const_maildomain);
				conn.put(TAG_Active, (active.equals("true") ? "1" : "0"));
				conn.put("Title2", (active.equals("true") ? title : getResources()
				    .getString(R.string.lbActive) + " " + title));
				conn.put(TAG_CreatedDT,
				    createddt.substring(8, 10) + "." + createddt.substring(5, 7) + "."
				        + createddt.substring(0, 4) + " " + createddt.substring(11, 19));

				connArray.add(conn);
			}
			if (connArray.isEmpty())
				((TextView) findViewById(R.id.tvNoMyMail)).setVisibility(View.VISIBLE);
			else
				((TextView) findViewById(R.id.tvNoMyMail)).setVisibility(View.GONE);
			ListAdapter adapter = new SimpleAdapter(getApplicationContext(),
			    connArray, R.layout.mymail_list_item, new String[] { TAG_ID,
			        TAG_Active, TAG_EMail, TAG_Title, // TAG_ListTitle,
			        TAG_CreatedDT, "Title2" }, new int[] { R.id.tvId, R.id.tvActive,
			        R.id.tvEMail, R.id.tvTitle, // R.id.tvListTitle,
			        R.id.tvCreatedDT, R.id.tvOrjTitle });

			lv.setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public class servisAT extends AsyncTask<Object, Void, String> { // 8081 //
																																	// 61163 for
																																	// Debug
		ProgressDialog	PleaseWait;

		protected void onPreExecute() {
			super.onPreExecute();
			Log.i("hata bu mu", "baþlýyor");
			if (MainActivity.this!=null)
			 PleaseWait = ProgressDialog.show(MainActivity.this,
			     "Lütfen Bekleyiniz...", "Lütfen Bekleyiniz...");
			else Log.i("hata bu mu", "MainActivity.this is NULLLL");
			Log.i("hata bu mu", "Baþarýlý");
		}

		@Override
		protected String doInBackground(Object... params) {
			String Response = "";
			String komut = (String) params[0];
			try {
				mtype = komut.substring(0, komut.indexOf("?"));
				// Response =
				Response = new GetResultFromWS().execute(komut);
				// Response = doInBackground2(komut);//.get();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return Response;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			JSONObject jsonObj;
			try {
				jsonObj = new JSONObject(result);
				JSONObject jo = jsonObj.getJSONObject(mtype + "Result");
				if (jo.getString("Id") != "0") {
					// Response = "";
					GlobalTools.ShowTost(jo.getString("Msg"));
					if (jo.getString("Id") == "12") {
						startActivity(new Intent(MainActivity.this, Login.class));
						finish();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// }
			if (mtype.equals("SetMyMail"))
				AddNewEMail(result);
			else if (mtype.equals("GetMyMailList"))
				GetMyMails(result);
			// else if (mtype.equals("SetUserInfo_Str"))
			// SaveToken(result);
			if (PleaseWait != null)
				PleaseWait.dismiss();

		}
	}

	public class GetPushTokenTask extends AsyncTask<Object, Void, String> { // 8081
																																					// //
																																					// 61163
																																					// for
																																					// Debug

		@Override
	protected String doInBackground(Object... params) {
		String token = "";
		try {
			SharedPreferences sp = context.getSharedPreferences(Cookies, 0);
			if (checkPlayServices()) {//GOOGLE PLAY SERVÝCE APK YÜKLÜMÜ
				if (gcm==null)
	        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());//GoogleCloudMessaging objesi oluþturduk
	      token = getRegistrationId(getApplicationContext()); //registration_id olup olmadýðýný kontrol ediyoruz
	      if(token.isEmpty())
		      token = gcm.register(PROJECT_ID);
		    else token="";
	    } else token = "No Google Play Services";
		} catch (Exception e2) {
			token = e2.toString();
		}
		return token;
	}

		@Override
		protected void onPostExecute(String token) {
			if ((!token.startsWith("-")) && (!token.equals("")) && (token != null)) {
				new servisAT().execute("SetUserInfo_Str?Field=PushToken&_value="
				    + token);
				SharedPreferences.Editor editor = context.getSharedPreferences(Cookies,
				    Context.MODE_PRIVATE).edit();
				editor.putString(PROPERTY_REG_ID, token);
				editor.putInt(PROPERTY_APP_VERSION,
				    getAppVersion(getApplicationContext()));
				editor.commit();
			}
			token = "";
		}
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
				    PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "Google Play Servis Yükleyin.");
				finish();
			}
			return false;
		}
		return true;
	}

	private String getRegistrationId(Context context) { // registration_id geri
																											// döner
		final SharedPreferences prefs = getSharedPreferences(Cookies,
		    Context.MODE_PRIVATE);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration id bulunamadý.");
			return "";
		}

		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
		    Integer.MIN_VALUE);
		int currentVersion = getAppVersion(getApplicationContext());
		if (registeredVersion != currentVersion) {// versionlar uyuþmuyorsa
																							// güncelleme olmuþ demektir. Yani
																							// tekrardan registration
																							// iþlemleri yapýlcak
			Log.i(TAG, "App version deðiþmiþ.");
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
			    context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Paket versiyonu bulunamadý: " + e);
		}
	}
}
