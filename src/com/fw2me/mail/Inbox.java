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
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.fw2me.mail.GoogleAnalyticsApp.TrackerName;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class Inbox extends ActionBarActivity {

	static UserLocalStore	             uls;

	private static final String	       TAG_RESULT	   = "GetReceivedMailListResult";
	private static final String	       TAG_Id	       = "Id";
	private static final String	       TAG_FromName	 = "FromName";
	private static final String	       TAG_Subject	 = "Subject";
	private static final String	       TAG_CreatedDT	= "CreatedDT";
	ProgressDialog	                   PleaseWait;
	public static final String	       Cookies	     = "Cookies";
	ListView	                         lv	           = null;
	JSONArray	                         conns	       = null;
	GlobalTools	                       glb	         = new GlobalTools();
	ArrayList<HashMap<String, String>>	connArray;
	String FromMail = "";
	String ToMail = "";
	String mtype = "";
	AdView adView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inbox);
		Intent i = getIntent();
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		FromMail = i.getStringExtra("FromMail");
		ToMail = i.getStringExtra("ToMail");
		Context context = getApplicationContext();
		uls = new UserLocalStore(context);
		lv = ((ListView) findViewById(R.id.lvInbox));
		connArray = new ArrayList<HashMap<String, String>>();
		((RelativeLayout)findViewById(R.id.layout_InboxHeaderFrom)).setVisibility((FromMail.equals("") ? View.GONE : View.VISIBLE));
		((RelativeLayout)findViewById(R.id.layout_InboxHeaderTo)).setVisibility((ToMail.equals("") ? View.GONE : View.VISIBLE));
		((TextView) findViewById(R.id.tvInboxHeaderFrom)).setText(FromMail);
		((TextView) findViewById(R.id.tvInboxHeaderTo)).setText(ToMail);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      	new servisAT().execute("ReSendEMail?SendID=" + ((TextView)view.findViewById(R.id.tvInboxitemID)).getText().toString());
			}

		});
		
		adView = (AdView) findViewById(R.id.adViewInbox);
		if (MainActivity.showAds) {
			// AdRequest adRequest = new AdRequest.Builder().build();
			AdRequest adRequest = new AdRequest.Builder().addTestDevice(
			    "A0AC6DA8C6F5470C829C819123B7F8B2").build();
			adView.loadAd(adRequest);
		} else {
			adView.setVisibility(View.GONE);
		}

		Tracker t = ((GoogleAnalyticsApp) getApplication())
		    .getTracker(TrackerName.APP_TRACKER);
//		String pagetype = "?";
//		pagetype = ToMail.equals("") ? " 1" : (FromMail.equals("") ? " 2" : " 3");
//		pagetype = getResources().getString(R.string.scr_Inbox).concat(pagetype);
		//t.setScreenName(pagetype);
		t.enableAdvertisingIdCollection(true);
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(Inbox.this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(Inbox.this).reportActivityStop(this);
	}
	
	@Override
	protected void onPause() {
		if (MainActivity.showAds) adView.pause();
	    super.onPause();
	}

	@Override
	protected void onResume() {
		new servisAT().execute("GetReceivedMailList?FromMail=" + FromMail + "&ToMail=" + ToMail);
		super.onResume();
		if (MainActivity.showAds) adView.resume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.inbox, menu); // Þimdilik menu yok. Ama
		// ekleyeceksen dosyasý Inbox.xml
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

		@Override
    protected void onPreExecute() {
			super.onPreExecute();
			PleaseWait = ProgressDialog.show(Inbox.this, getResources()
			    .getString(R.string.PleaseWait),
			    getResources().getString(R.string.Loading));
		}

		@Override
		protected String doInBackground(Object... params) {
			String Response = "";
			String komut = (String) params[0];
			try {
				mtype = komut.substring(0, komut.indexOf("?"));
				Response = new GetResultFromWS().execute(komut);
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
					if (!jo.getString("Id").equals("0")) {
						err = true;
						GlobalTools.ShowTost(jo.getString("Msg"));
						if (jo.getString("Id").equals("12")) {
							startActivity(new Intent(Inbox.this, Login.class));
							finish();
						}
					}
				} catch (Exception e) {
					err = true;
					e.printStackTrace();
				}
				if (err)
				{
					if (mtype.equals("GetReceivedMailList"))
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
				}
				else
					try 
					{
						if ((new JSONObject(result).getJSONObject(mtype + "Result").getString("Id").equals("0")) && (mtype.equals("GetReceivedMailList")))
		          GlobalTools.WriteStringToCookie(mtype + "Result"+MainActivity.userNo(), result);
			    } catch (JSONException e1) {
			      e1.printStackTrace();
			    }
				if (err) return; 
				if (mtype.equals("GetReceivedMailList"))
					GetReceivedMailList(result);
				else if (mtype.equals("ReSendEMail"))
					GlobalTools.ShowTost(R.string.MailForwarded);
			} finally {
				if (PleaseWait != null)
					PleaseWait.dismiss();
			}
		}
	}

	private void GetReceivedMailList(String jsonStr) {
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
				String FromName = c.getString(TAG_FromName);
				String Subject = c.getString(TAG_Subject);
				String CreatedDT = c.getString(TAG_CreatedDT);

				HashMap<String, String> conn = new HashMap<String, String>();
				conn.put(TAG_Id, Id);
				conn.put(TAG_FromName, FromName);
				conn.put(TAG_Subject, Subject);
				conn.put(TAG_CreatedDT,
				    CreatedDT.substring(8, 10) + "." + CreatedDT.substring(5, 7) + "."
				        + CreatedDT.substring(0, 4) + " " + CreatedDT.substring(11, 19));

				connArray.add(conn);
			}
			((TextView) findViewById(R.id.tvInboxHeaderCount)).setText(Integer.toString(connArray.size()));
			ListAdapter adapter = new SimpleAdapter(getApplicationContext(),
			    connArray, R.layout.inbox_list_item, new String[] { TAG_Id,
				TAG_FromName, TAG_Subject, TAG_CreatedDT}, new int[] { R.id.tvInboxitemID,
			        R.id.tvInboxitemFrom, R.id.tvInboxitemSubject, R.id.tvInboxitemCreatedDT});

			lv.setAdapter(adapter);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
}
