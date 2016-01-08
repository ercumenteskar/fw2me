package com.fw2me.mymails;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fw2me.mymails.GoogleAnalyticsApp.TrackerName;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MailAddedActivity extends ActionBarActivity {
	private static String	Id	   = "0";
	private static String	Active	= "1";
	private String	      mtype	 = "";

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
    ((Button)findViewById(R.id.btCopy)).getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);
    ((Button)findViewById(R.id.btOk)).getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);
    ((Button)findViewById(R.id.btActive)).getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);
    ((Button)findViewById(R.id.btSenders)).getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);

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
	    @Override
	    public void onClick(View v) {
		    // TODO Auto-generated method stub
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
		AdView mAdView = (AdView) findViewById(R.id.adViewMailAdded);
//		AdRequest adRequest = new AdRequest.Builder().build();
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
		    "A0AC6DA8C6F5470C829C819123B7F8B2").build();
		mAdView.loadAd(adRequest);
		Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Mail Detay");
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
			Log.i("hata bu mu", "ba�l�yor");
			if (MailAddedActivity.this!=null)
			 PleaseWait = ProgressDialog.show(MailAddedActivity.this,
			     "L�tfen Bekleyiniz...", "L�tfen Bekleyiniz...");
			else Log.i("hata bu mu", "MailAddedActivity.this is NULLLL");
			Log.i("hata bu mu", "Ba�ar�l�");
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
					 if (jo.getString("Id")=="12")
					 {
					 startActivity(new Intent(MailAddedActivity.this, Login.class));
					 finish();
					 }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// }
			if (mtype.equals("SetMyMail"))
				finish();
			if (PleaseWait != null)
				PleaseWait.dismiss();

		}
	}

}
