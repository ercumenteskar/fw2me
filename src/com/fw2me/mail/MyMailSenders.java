package com.fw2me.mail;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
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

public class MyMailSenders extends ActionBarActivity { // implements MIActivity

	public static Context	             context;
	// public static final String WSAdress =
	// "http://192.168.1.122:1001/MobileService.svc/";
	public static final String	       WSAdress	         = "http://192.168.43.47:1001/MobileService.svc/";
	public static boolean	             WSBusy;
	static UserLocalStore	             uls;

	private static final String	       TAG_RESULT	       = "GetMyMailSenderListResult";
	private static final String	       TAG_Id	           = "Id";
	private static final String	       TAG_SenderEMail	 = "SenderEMail";
	private static final String	       TAG_OrjSenderName	= "OrjSenderName";
	private static final String	       TAG_SenderName	   = "SenderName";
	private static final String	       TAG_CreatedDT	   = "CreatedDT";
	private static final String	       TAG_SenderActive	 = "Active";
	ProgressDialog	                   PleaseWait;
	public static final String	       Cookies	         = "Cookies";

	ListView	                         lv	               = null;
	JSONArray	                         conns	           = null;
	GlobalTools	                       glb	             = new GlobalTools();
	ArrayList<HashMap<String, String>>	connArray;
	private String	                   MyMail_Id	       = "0";
	private String	                   MyMail	           = "";
	private String	                   mtype	           = "";
	AdView adView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mymailsenders);
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Intent i = getIntent();
		MyMail_Id = i.getStringExtra("Id");
		MyMail = i.getStringExtra("MyMail");
		context = getApplicationContext();
		uls = new UserLocalStore(context);
		lv = ((ListView) findViewById(R.id.lvMyMailSenders));
		connArray = new ArrayList<HashMap<String, String>>();

		// GetMyMailSenders();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
			    long id) {
				Intent in = new Intent(getApplicationContext(), SenderDetail.class);
				in.putExtra(TAG_Id, ((TextView) view.findViewById(R.id.tvId)).getText()
				    .toString());
				in.putExtra(TAG_OrjSenderName, ((TextView) view
				    .findViewById(R.id.tvOrjSenderName)).getText().toString());
				in.putExtra(TAG_SenderName, ((TextView) view
				    .findViewById(R.id.tvSenderName)).getText().toString());
				in.putExtra(TAG_SenderEMail, ((TextView) view
				    .findViewById(R.id.tvSenderEMail)).getText().toString());
				in.putExtra(TAG_SenderActive, ((TextView) view
				    .findViewById(R.id.tvSenderActive)).getText().toString());
				in.putExtra(TAG_CreatedDT, ((TextView) view
				    .findViewById(R.id.tvCreatedDT)).getText().toString());
				in.putExtra("MyMail", MyMail);

				startActivity(in);
			}

		});

		adView = (AdView) findViewById(R.id.adViewSenderList);
    if (MainActivity.showAds)
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
		//t.setScreenName(getResources().getString(R.string.scr_MyMailSenders));
		t.enableAdvertisingIdCollection(true);
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(MyMailSenders.this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(MyMailSenders.this).reportActivityStop(this);
	}

	@Override
	protected void onResume() {
		new servisAT().execute("GetMyMailSenderList?MyMail_Id=" + MyMail_Id);
		super.onResume();
		if (MainActivity.showAds) adView.resume();
	}
	
	@Override
	protected void onPause() {
		if (MainActivity.showAds) adView.pause();
	    super.onPause();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			this.finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class servisAT extends AsyncTask<Object, Void, String> { // 8081 //
		// 61163 for
		// Debug
		ProgressDialog	PleaseWait;

		protected void onPreExecute() {
			super.onPreExecute();
			// Log.i("hata bu mu", "baþlýyor");
			// if (MyMailSenders.this!=null)
			PleaseWait = ProgressDialog.show(MyMailSenders.this, getResources()
			    .getString(R.string.PleaseWait),
			    getResources().getString(R.string.Loading));
			// else Log.i("hata bu mu", "MyMailSenders.this is NULLLL");
			// Log.i("hata bu mu", "Baþarýlý");
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
			try {
				super.onPostExecute(result);
				JSONObject jsonObj;
				boolean err = false;
				try {
					jsonObj = new JSONObject(result);
					JSONObject jo = jsonObj.getJSONObject(mtype + "Result");
					if (jo.getString("Id") != "0") {
						err = true;
						GlobalTools.ShowTost(jo.getString("Msg"));
						if (jo.getString("Id") == "12") {
							startActivity(new Intent(MyMailSenders.this, Login.class));
							finish();
						}
					}
				} catch (Exception e) {
					err = true;
					e.printStackTrace();
				}
				if (err)
				{
					if (!GlobalTools.isConnected())
						GlobalTools.ShowTost(R.string.OfflineUsage);
				  String jss = GlobalTools.ReadStringFromCookie(mtype + "Result"+MainActivity.userNo(), "");
				  if (!jss.equals(""))
						try {
		          if (new JSONObject(jss).getJSONObject(mtype + "Result").getString("Id").equals("0"))
		          {
		          	result = jss; 
		          	err = false;
		          }
		        } catch (JSONException e1) {
		          e1.printStackTrace();
		        }
				}
				else
					try 
					{
						if (new JSONObject(result).getJSONObject(mtype + "Result").getString("Id").equals("0"))
		          GlobalTools.WriteStringToCookie(mtype + "Result"+MainActivity.userNo(), result);
			    } catch (JSONException e1) {
			      e1.printStackTrace();
			    }
				if (err) return; 
				if (mtype.equals("GetMyMailSenderList"))
					GetMyMailSenders(result);
			} finally {
				if (PleaseWait != null)
					PleaseWait.dismiss();
			}
		}
	}

	private void GetMyMailSenders(String jsonStr) {
		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			JSONObject jo = jsonObj.getJSONObject(TAG_RESULT);
			jsonStr = jo.getString("Obj");
			conns = new JSONArray(jsonStr);
			connArray.clear();
			// looping through All Contacts
			for (int i = 0; i < conns.length(); i++) {
				JSONObject c = conns.getJSONObject(i);

				String Id = c.getString(TAG_Id);
				String SenderName = c.getString(TAG_SenderName);
				String SenderEMail = c.getString(TAG_SenderEMail);
				String CreatedDT = c.getString(TAG_CreatedDT);
				String SenderActive = c.getString(TAG_SenderActive);

				HashMap<String, String> conn = new HashMap<String, String>();
				conn.put(TAG_Id, Id);
				conn.put(TAG_OrjSenderName, SenderName);
				conn.put(TAG_SenderName, (SenderActive != "true" ? getResources()
				    .getString(R.string.lbActive) + " " : "")
				    + SenderName);
				conn.put(TAG_SenderEMail, SenderEMail);
				conn.put(TAG_SenderActive, (SenderActive == "true" ? "1" : "2"));
				conn.put(TAG_CreatedDT,
				    CreatedDT.substring(8, 10) + "." + CreatedDT.substring(5, 7) + "."
				        + CreatedDT.substring(0, 4) + " " + CreatedDT.substring(11, 19));

				connArray.add(conn);
			}
			if (connArray.isEmpty())
				((TextView) findViewById(R.id.tvNoSender)).setVisibility(View.VISIBLE);
			else
				((TextView) findViewById(R.id.tvNoSender)).setVisibility(View.GONE);
			ListAdapter adapter = new SimpleAdapter(getApplicationContext(),
			    connArray, R.layout.mymailsender_list_item, new String[] { TAG_Id,
			        TAG_SenderEMail, TAG_SenderName, TAG_OrjSenderName,
			        TAG_CreatedDT, TAG_SenderActive }, new int[] { R.id.tvId,
			        R.id.tvSenderEMail, R.id.tvSenderName, R.id.tvOrjSenderName,
			        R.id.tvCreatedDT, R.id.tvSenderActive });

			lv.setAdapter(adapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}

