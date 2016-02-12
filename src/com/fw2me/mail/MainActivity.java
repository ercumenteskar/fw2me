package com.fw2me.mail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.fw2me.mail.GoogleAnalyticsApp.TrackerName;
import com.google.android.gms.ads.AdRequest;
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
	public static final String	       WSAdress	                        = "http://ws.fw2.me:1001/MobileService.svc/";
//	public static final String	       WSAdress	                        = "http://192.168.43.47:61163/MobileService.svc/";

	public static boolean	             WSBusy;
	static UserLocalStore	             uls;

	private static final String	       TAG_RESULT	                      = "GetMyMailListResult";
	private static final String	       TAG_ID	                          = "Id";
	private static final String	       TAG_EMail	                      = "EMail";
	private static final String	       TAG_Title	                      = "Title";
	private static final String	       TAG_Active	                      = "Active";
	private static final String	       TAG_CreatedDT	                  = "CreatedDT";
	private static final String	       const_maildomain	                = "fw2.me";	                                   // "benim.in";

	ListView	                         lv	                              = null;
	JSONArray	                         conns	                          = null;
	GlobalTools	                       glb	                            = new GlobalTools();
	ArrayList<HashMap<String, String>>	connArray;
	public static boolean	             tazele	                          = true;

	private static final int	         PLAY_SERVICES_RESOLUTION_REQUEST	= 9000;
	public static final String	       PROPERTY_REG_ID	                = "AIzaSyAX8gYEmmFTI9sF-1xqqf8QAff7Au33JVM";
	public static final String	       PROJECT_ID	                      = "77689937197";
	private static final String	       PROPERTY_APP_VERSION	            = "appVersion";
	private static final String	       TAG	                            = "FW2ME GCM";
	GoogleCloudMessaging	             gcm;
  public static boolean 						 showAds                          = true;
  AdView adView;
  
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
		if (userNo()!="")
			showAds = uls.getStoredUser().ShowAds;
		if (!uls.getUserLoggedIn())
		{
			startActivity(new Intent(MainActivity.this, Login.class));
			finish();
		}else{
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
			adView = (AdView) findViewById(R.id.adViewMain);
	    if (showAds)
	    {
		//		AdRequest adRequest = new AdRequest.Builder().build();
				AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				    "A0AC6DA8C6F5470C829C819123B7F8B2").build();
				adView.loadAd(adRequest);
	    }else{
	    	adView.setVisibility(View.GONE);
	    }
			Tracker t = ((GoogleAnalyticsApp) getApplication())
			    .getTracker(TrackerName.APP_TRACKER);
			//t.setScreenName("Ana Ekran"); // getResources().getString(R.string.scr_MainActivity)
			t.enableAdvertisingIdCollection(true);
			t.send(new HitBuilders.AppViewBuilder().build());
		}
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
	}

	@Override
	protected void onPause() {
		if (showAds) adView.pause();
	    super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (showAds) adView.resume();
		if (uls.getUserLoggedIn())
		  new GetPushTokenTask().execute();
//		if ((tazele)) // (GetPushTokenTask()!="") &&
		{
			new servisAT().execute("GetMyMailList?");
			tazele = false;
		}
	}

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
			((GoogleAnalyticsApp) getApplication())
	    .getTracker(TrackerName.APP_TRACKER).send(new HitBuilders.EventBuilder().setCategory("Button").setAction("Click").setLabel("action_logout").build());
			uls.setUserLoggedIn(false);
			startActivity(new Intent(this, Login.class));
			finish();
			// onResume();
			break;
		case R.id.action_add:
			((GoogleAnalyticsApp) getApplication())
	    .getTracker(TrackerName.APP_TRACKER).send(new HitBuilders.EventBuilder().setCategory("Button").setAction("Click").setLabel("action_add").build());
			new servisAT().execute("SetMyMail?Title=");
			// AddNewEMail();
			break;
		case R.id.action_Inbox:
			((GoogleAnalyticsApp) getApplication())
	    .getTracker(TrackerName.APP_TRACKER).send(new HitBuilders.EventBuilder().setCategory("Button").setAction("Click").setLabel("action_Inbox").build());
			Intent in = new Intent(getApplicationContext(), Inbox.class);
  		in.putExtra("FromMail", "");
  		in.putExtra("ToMail", "");
  		startActivity(in);
	    //finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void AddNewEMail(String jsonStr) {
		try {
			String jsonStrOrj = jsonStr;
			if (jsonStr != "") {
				String sid = new JSONObject(jsonStr).getJSONObject("SetMyMailResult")
				    .getString("Id");
				jsonStr = new JSONObject(jsonStr).getJSONObject("SetMyMailResult")
				    .getString("Obj");
				JSONObject c = new JSONObject(jsonStr);
				if (sid == "0")
					OpenMyMail(c.getString(TAG_ID), c.getString(TAG_Title),
					    c.getString(TAG_EMail) + "@" + const_maildomain, "1");
        try
        {
        if (new JSONObject(jsonStrOrj).getJSONObject("SetMyMailResult").getString("Id").equals("84"))
	        {
	        	new AlertDialog.Builder(MainActivity.this)
	          .setTitle(getResources().getString(R.string.eMailConfReqTitle))
	          .setMessage(getResources().getString(R.string.eMailConfReqDesc))
	          .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	              public void onClick(DialogInterface dialog, int which) { 
	                  new servisAT().execute("SendConfirmationCode?a=a");
	              }
	           })
	          .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	              public void onClick(DialogInterface dialog, int which) { 
	                  // do nothing
	              }
	           })
	          .setIcon(android.R.drawable.ic_dialog_email)
	           .show();
	        }
	      } catch (JSONException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	      }
			}
		} catch (Exception e) {
			GlobalTools.ShowTost(e.getMessage());
		}
	}

	private void GetMyMails(String jsonStr) {
		try {
			//String jsonStrOrj = jsonStr;
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
//			if (!userNo().equals(""))
//			  GlobalTools.WriteStringToCookie("LastMyMailList"+userNo(), jsonStrOrj);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public class servisAT extends AsyncTask<Object, Void, String> { // 8081 //
																																	// 61163 for
																																	// Debug
		ProgressDialog	PleaseWait;
    String mtype = "";
    boolean fromCache = false;
    String komut = "";
    
		protected void onPreExecute() {
			super.onPreExecute();
			PleaseWait = ProgressDialog.show(MainActivity.this,
				getResources().getString(R.string.PleaseWait), getResources().getString(R.string.Loading));
		}

		@Override
		protected String doInBackground(Object... params) {

			String Response = "";
			komut = (String) params[0];
			String cache = ((params.length>1) ? (String)params[1] : "");
			mtype = komut.substring(0, komut.indexOf("?"));
			fromCache = false;
			String jss = "";
			if (params.length>1) // boþ string gönderilmiþ olabilir o yüzden cache deðiþkenini kontrol etmiyorum.
			{
				if ((cache!="") && (PleaseWait != null)) PleaseWait.dismiss();
				try {
					Response = new GetResultFromWS().execute(komut);
					if (Response.equals(cache)) 
						Response = "";
					else
						try 
						{
							if ((new JSONObject(Response).getJSONObject(mtype + "Result").getString("Id").equals("0")) && (mtype.equals("GetMyMailList")))
			          GlobalTools.WriteStringToCookie(komut+"_cache_"+userNo(), Response);
				    } catch (JSONException e1) {
				      e1.printStackTrace();
				    }
				} catch (Exception e1) {
					GlobalTools.ShowTost(e1.getMessage());
				}
			}
			else
			{
				if ((mtype.equals("GetMyMailList")))
				  jss = GlobalTools.ReadStringFromCookie(komut+"_cache_"+userNo(), "");
			  
				if (!jss.equals(""))
		      try {
		        if (new JSONObject(jss).getJSONObject(mtype + "Result").getString("Id").equals("0"))
		          	Response = jss;
	        } catch (Exception e) {
		        GlobalTools.ShowTost(e.getMessage());
	        }
					fromCache = true;
			}

			return Response;
		}

		@Override
		protected void onPostExecute(String result) {
			try
			{
				super.onPostExecute(result);
				if (fromCache)
        {
					if (PleaseWait!=null) PleaseWait.dismiss();
					new servisAT().execute(komut, result);
				}
				if (result.equals(""))
					return;
				JSONObject jsonObj;
				boolean err = false;
				try {
					jsonObj = new JSONObject(result);
					JSONObject jo = jsonObj.getJSONObject(mtype + "Result");
					if (jo.getString("Id") != "0") {
						err = true;
						GlobalTools.ShowTost(jo.getString("Msg"));
						if (jo.getString("Id") == "12") {
							startActivity(new Intent(MainActivity.this, Login.class));
							finish();
						}
					}
				} catch (Exception e) {
					err = true;
					e.printStackTrace();
				}
				if (err)
				{
					try {
						if (!GlobalTools.isConnected())
						{
							GlobalTools.ShowTost(R.string.OfflineUsage);
						  String jss = GlobalTools.ReadStringFromCookie(mtype + "Result"+userNo(), "");
						  if (!jss.equals(""))
						  {
						  	if (new JSONObject(jss).getJSONObject(mtype + "Result").getString("Id").equals("0"))
				          {
				          	result = jss; 
				          	err = false;
				          }
						  }
						}else if ((new JSONObject(result).getJSONObject(mtype + "Result").getString("Id").equals("84")) && (mtype.equals("SetMyMail")))
							AddNewEMail(result);
	        } catch (JSONException e1) {
	          e1.printStackTrace();
	        }
				}
				else
					try 
					{
						if ((new JSONObject(result).getJSONObject(mtype + "Result").getString("Id").equals("0")) && (mtype.equals("GetMyMailList")))
		          GlobalTools.WriteStringToCookie(mtype + "Result"+userNo(), result);
			    } catch (JSONException e1) {
			      e1.printStackTrace();
			    }
				if (err) return; 
				if (mtype.equals("SetMyMail"))
					AddNewEMail(result);
				else if (mtype.equals("GetMyMailList"))
					GetMyMails(result);
				// else if (mtype.equals("SetUserInfo_Str"))
				// SaveToken(result);
		}
		finally
		{
			if (PleaseWait != null)
				PleaseWait.dismiss();
		}

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
			if (checkPlayServices()) {//GOOGLE PLAY SERVÝCE APK YÜKLÜMÜ
				if (gcm==null)
	        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());//GoogleCloudMessaging objesi oluþturduk
	      token = getRegistrationId(getApplicationContext()); //registration_id olup olmadýðýný kontrol ediyoruz
	      if(token.isEmpty())
		      token = gcm.register(PROJECT_ID);
		    else token="";
	    } else token = "";
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
				SharedPreferences.Editor editor = context.getSharedPreferences(GlobalTools.Cookies,
				    Context.MODE_PRIVATE).edit();
				editor.putString(PROPERTY_REG_ID+":"+userNo(), token);
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
		final SharedPreferences prefs = getSharedPreferences(GlobalTools.Cookies,
		    Context.MODE_PRIVATE);
		String registrationId = prefs.getString(PROPERTY_REG_ID+":"+userNo(), "");
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
