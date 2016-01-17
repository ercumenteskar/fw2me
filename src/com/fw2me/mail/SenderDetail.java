package com.fw2me.mail;

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

import com.fw2me.mail.GoogleAnalyticsApp.TrackerName;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class SenderDetail extends ActionBarActivity {
  private static String  Id            = "0";
  private static String  Active        = "1";
  private static String  OrjSenderName  = "";
  //private static String  SenderName    = "";
  private static String  SenderEMail    = "";
  private static String  CreatedDT      = "";
  private static String  MyMail        = "";
  private String        mtype          = "";
  AdView adView;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.senderdetail);
    Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    Intent i = getIntent();
    Id = i.getStringExtra("Id");
    Active = i.getStringExtra("Active");
    OrjSenderName = i.getStringExtra("OrjSenderName");
    //SenderName = i.getStringExtra("SenderName");
    SenderEMail = i.getStringExtra("SenderEMail");
    CreatedDT = i.getStringExtra("CreatedDT");
    Active = i.getStringExtra("Active");
    MyMail = i.getStringExtra("MyMail");

    String fw2me_adr = SenderEMail.replace("@", "+") + "=" + MyMail;
    ((EditText) findViewById(R.id.etSenderName)).setText(OrjSenderName);
    ((EditText) findViewById(R.id.etSenderEMail)).setText(SenderEMail);
    ((EditText) findViewById(R.id.etFW2meEMail)).setText(fw2me_adr);
    ((EditText) findViewById(R.id.etSenderCreatedDT)).setText(CreatedDT);
    ((Button)findViewById(R.id.btFW2meEMailCopy)).getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);
    ((Button)findViewById(R.id.btActive)).getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);
    ((Button)findViewById(R.id.btInboxinSenderDetail)).getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);
    setTitle(OrjSenderName);
    ((Button) findViewById(R.id.btActive))
        .setText((Active.equals("1") ? R.string.btActive2 : R.string.btActive1));
    ((Button) findViewById(R.id.btActive))
        .setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
//            if (servis("SetMyMailSender?Id=" + Id + "&Active=" + (Active.equals("1") ? "0" : "1")) != "");
            new servisAT().execute("SetMyMailSender?Id=" + Id + "&Active=" + (Active.equals("1") ? "0" : "1"));
          }
        });
		((Button) findViewById(R.id.btFW2meEMailCopy))
		    .setOnClickListener(new OnClickListener() {
			    @Override
			    public void onClick(View v) {
				    int sdk = android.os.Build.VERSION.SDK_INT;
				    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
					    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					    clipboard.setText(((EditText) findViewById(R.id.etFW2meEMail))
					        .getText());
				    } else {
					    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					    android.content.ClipData clip = android.content.ClipData
					        .newPlainText("text label",
					            ((EditText) findViewById(R.id.etFW2meEMail)).getText());
					    clipboard.setPrimaryClip(clip);
				    }
				    Toast.makeText(MainActivity.context, R.string.emailaddresscopied,
				        Toast.LENGTH_SHORT).show();
				    // finish();
			    }
		    });
		((Button) findViewById(R.id.btInboxinSenderDetail))
    .setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
	  		Intent in = new Intent(getApplicationContext(), Inbox.class);
	  		in.putExtra("FromMail", SenderEMail);
	  		in.putExtra("ToMail", MyMail);
	  		startActivity(in);
		    //finish();
	    }
    });
		
    adView = (AdView) findViewById(R.id.adViewSenderDetail);
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
    t.setScreenName("Mail Gönderen Detay");
    t.enableAdvertisingIdCollection(true);
    t.send(new HitBuilders.AppViewBuilder().build());
  }

  @Override
  protected void onStart() {
    super.onStart();
    GoogleAnalytics.getInstance(SenderDetail.this).reportActivityStart(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    GoogleAnalytics.getInstance(SenderDetail.this).reportActivityStop(this);
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

  public class servisAT extends AsyncTask<Object, Void, String> {
    ProgressDialog  PleaseWait;

    protected void onPreExecute() {
      super.onPreExecute();
			//Log.i("hata bu mu", "baþlýyor");
			if (SenderDetail.this!=null)
			 PleaseWait = ProgressDialog.show(SenderDetail.this,
					 getResources().getString(R.string.PleaseWait), getResources().getString(R.string.PleaseWait));
			else Log.i("hata bu mu", "MyMailSenders.this is NULLLL");
			//Log.i("hata bu mu", "Baþarýlý");
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
             startActivity(new Intent(SenderDetail.this, Login.class));
             finish();
           }
        } else if (mtype.equals("SetMyMailSender"))
        {
        	if (Active.equals("1"))
        	 GlobalTools.ShowTost(getResources().getString(R.string.SenderPaused).replace("%s", SenderEMail));
        	else
         	 GlobalTools.ShowTost(getResources().getString(R.string.SenderResume).replace("%s", SenderEMail));
        	finish();
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
      // }
      if (PleaseWait != null)
        PleaseWait.dismiss();

    }
  }

}
