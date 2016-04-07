package com.fw2me.mail;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fw2me.mail.GoogleAnalyticsApp.TrackerName;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MailAddedActivity extends ActionBarActivity {
	private static String	Id	   = "0";
	private static String	Active	= "1";
	private String	      mtype	 = "";
	AdView adView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mailadded);
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);
		Intent i = getIntent();
		Id = i.getStringExtra("Id");
		Active = i.getStringExtra("Active");
		String Title = i.getStringExtra("Title");
		((EditText) findViewById(R.id.etTitle)).setText(Title);
		((EditText) findViewById(R.id.etEMail)).setText(i.getStringExtra("EMail"));
		Title = (Title.equals("") ? "("+getResources().getString(R.string.undefined)+")": Title);
		setTitle(Title);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    ((Button)findViewById(R.id.btCopy)).getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);
//    ((Button)findViewById(R.id.btOk)).getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);
//    ((Button)findViewById(R.id.btActive)).getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);
//    ((Button)findViewById(R.id.btSenders)).getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);
//    ((Button)findViewById(R.id.btInboxinMailAdded)).getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);

    if(Build.VERSION.SDK_INT <= 0){//Build.VERSION_CODES.KITKAT) {

	    Resources r = getApplicationContext().getResources();
	    int mrgn = (int) TypedValue.applyDimension(
	            TypedValue.COMPLEX_UNIT_DIP,
	            5, 
	            r.getDisplayMetrics()
	    );
	    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,      
	        android.view.ViewGroup.LayoutParams.WRAP_CONTENT
			);
			params.setMargins(mrgn, mrgn, mrgn, mrgn);
	    ((Button)findViewById(R.id.btCopy)).setLayoutParams(params);
	    ((Button)findViewById(R.id.btOk)).setLayoutParams(params);
	    ((Button)findViewById(R.id.btActive)).setLayoutParams(params);
	    ((Button)findViewById(R.id.btSenders)).setLayoutParams(params);
	    ((Button)findViewById(R.id.btInboxinMailAdded)).setLayoutParams(params);
		}
    ((EditText) findViewById(R.id.etTitle)).setSelection(((EditText) findViewById(R.id.etTitle)).getText().length());
		((Button) findViewById(R.id.btOk))
    .setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
	    	new servisAT().execute("SetMyMail?Id=" + Id + "&Title=" + ((EditText) findViewById(R.id.etTitle)).getText());
	    	//if (servis("SetMyMail?Id=" + Id + "&Title=" + ((EditText) findViewById(R.id.etTitle)).getText())!="")
		      //finish();
	    }
    });
		((Button) findViewById(R.id.btActive)).setText((Active.equals("1") ? R.string.btActive2 : R.string.btActive1));
		((Button) findViewById(R.id.btActive))
    .setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
	    	new servisAT().execute("SetMyMail?Id=" + Id + "&Active="+(Active.equals("1") ? "0" : "1"));//!="");
//	    	if (servis("SetMyMail?Id=" + Id + "&Active="+(Active.equals("1") ? "0" : "1"))!="");
		      //finish();
	    }
    });
		((Button) findViewById(R.id.btCopy))
    .setOnClickListener(new OnClickListener() {
	    @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
	    public void onClick(View v) {
		    int sdk = android.os.Build.VERSION.SDK_INT;
		    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
			    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			    clipboard.setText(((EditText) findViewById(R.id.etEMail)).getText());
		    } else {
			    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			    android.content.ClipData clip = android.content.ClipData.newPlainText("text label", ((EditText) findViewById(R.id.etEMail)).getText());
			    clipboard.setPrimaryClip(clip);
		    }
		    Toast.makeText(MainActivity.context, R.string.emailaddresscopied,
		        Toast.LENGTH_SHORT).show();
		    //finish();
	    }
    });
		((Button) findViewById(R.id.btSenders))
    .setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
	  		Intent in = new Intent(getApplicationContext(), MyMailSenders.class);
	  		in.putExtra("Id", Id);
	  		in.putExtra("MyMail", ((EditText) findViewById(R.id.etEMail)).getText().toString());
	  		startActivity(in);
		    //finish();
	    }
    });
		((Button) findViewById(R.id.btInboxinMailAdded))
    .setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
	  		Intent in = new Intent(getApplicationContext(), Inbox.class);
	  		in.putExtra("FromMail", "");
	  		in.putExtra("ToMail", ((EditText) findViewById(R.id.etEMail)).getText().toString());
	  		startActivity(in);
		    //finish();
	    }
    });
		adView = (AdView) findViewById(R.id.adViewMailAdded);
    if (MainActivity.showAds)
    {
	//		AdRequest adRequest = new AdRequest.Builder().build();
			AdRequest adRequest = new AdRequest.Builder().addTestDevice(
			    "A0AC6DA8C6F5470C829C819123B7F8B2").build();
			adView.loadAd(adRequest);
    }else{
    	adView.setVisibility(View.GONE);
    }

		Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(TrackerName.APP_TRACKER);
		//t.setScreenName(getResources().getString(R.string.scr_MailAddedActivity));
		t.enableAdvertisingIdCollection(true);
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(MailAddedActivity.this).reportActivityStart(
		    this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(MailAddedActivity.this)
		    .reportActivityStop(this);

	}
	
	@Override
	protected void onPause() {
		if (MainActivity.showAds) adView.pause();
	    super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (MainActivity.showAds) adView.resume();
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
			 PleaseWait = ProgressDialog.show(MailAddedActivity.this,
					 getResources().getString(R.string.PleaseWait), getResources().getString(R.string.Loading));
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
				if (!jo.getString("Id").equals("0")) {
					// Response = "";
					GlobalTools.ShowTost(jo.getString("Msg"));
					 if (jo.getString("Id").equals("12"))
					 {
					 startActivity(new Intent(MailAddedActivity.this, Login.class));
					 finish();
					 }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// }
			if (mtype.equals("SetMyMail")) {
				MainActivity.tazele = true;
				finish();
			}
		  if (PleaseWait != null)
				PleaseWait.dismiss();

		}
	}

}
