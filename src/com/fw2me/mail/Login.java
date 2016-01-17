package com.fw2me.mail;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Login extends ActionBarActivity implements View.OnClickListener {
	EditText	     etEMail, etPassword;
	Button	       btLogin;
	TextView	     btRegister;
	UserLocalStore	uls;
	GlobalTools	   glb	= new GlobalTools();
	String mtype = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// this.setTitle("Login");
		etEMail = (EditText) findViewById(R.id.etEmail);
		etPassword = (EditText) findViewById(R.id.etPassword);
		btLogin = (Button) findViewById(R.id.btLogin);
		btRegister = (TextView) findViewById(R.id.btRegister);
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		ImageView img = (ImageView) findViewById(R.id.iv_banner);
		img.getLayoutParams().height = size.x/2;
		img.getLayoutParams().width = size.x;
		img.requestLayout();
		uls = new UserLocalStore(this);
		btLogin.setOnClickListener(this);
		btRegister.setOnClickListener(this);
		((TextView)findViewById(R.id.btForgotPassword)).setOnClickListener(this);
		String lastEMail = GlobalTools.ReadStringFromCookie("LastEmail", "");
		etEMail.setText(lastEMail);
		btLogin.getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);
		if ((lastEMail.equals("")) && (getIntent().getStringExtra("Direct")==null)) {
			startActivity(new Intent(this, Register.class));
			finish();
		}
		if (!lastEMail.equals(""))
			etPassword.requestFocus();
	}
	
	@Override
	public void onClick(View v){
		switch (v.getId()) {

		case R.id.btLogin:
 			new servisAT().execute("Login?EMail="+ etEMail.getText().toString() + "&Password=" + etPassword.getText().toString());
			break;

		case R.id.btForgotPassword:
 			new servisAT().execute("ResetPassword?EMail="+ etEMail.getText().toString());
			break;

		case R.id.btRegister:
			startActivity(new Intent(this, Register.class));
			finish();
			break;

		default:
			break;
		}
	}

	public class servisAT extends AsyncTask<Object, Void, String> { //8081 // 61163 for Debug
		ProgressDialog PleaseWait;
		
		protected void onPreExecute(){
			super.onPreExecute();
      PleaseWait = ProgressDialog.show(Login.this, getResources().getString(R.string.PleaseWait), getResources().getString(R.string.PleaseWait));
		}
		
		@Override
		protected String doInBackground(Object... params) {
			String Response = "";
    	String komut = (String)params[0];
	    try {
		  	mtype = komut.substring(0, komut.indexOf("?"));
		    //Response = 
		  	Response = new GetResultFromWS().execute(komut);
    		//Response = doInBackground2(komut);//.get();
	    } catch (Exception e1) {
		    e1.printStackTrace();
	    }
			return Response;
		}
				
		@Override
		protected void onPostExecute(String result) {
			try
			{
				super.onPostExecute(result);
				JSONObject jsonObj;
		    boolean err = false;
		    try {
		      jsonObj = new JSONObject(result);
					JSONObject jo = jsonObj.getJSONObject(mtype+"Result");
					if (jo.getString("Id")!="0")
					{
						err = true;
						GlobalTools.ShowTost(jo.getString("Msg"));
					}
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		    if (err) return;
				if (mtype.equals("Login"))
					LoginResult(result);
				else if (mtype.equals("ResetPassword"))
					GlobalTools.ShowTost(R.string.ResetPasswordLink);
					
			}
			finally
			{
				if (PleaseWait!=null) 
					PleaseWait.dismiss();
			}
		  
		}
	}
	
	
	private void LoginResult(String jsonStr)
	{
		String rs = "";
		String sonuc = "";
		rs = GlobalTools.OrtasiniGetir(jsonStr, "<a:ResultSet>", "</a:ResultSet>");
		sonuc = GlobalTools.OrtasiniGetir(rs, "<a:Id>", "</a:Id>"); 
		if (!sonuc.equals("0")){
			GlobalTools.ShowTost(GlobalTools.OrtasiniGetir(rs, "<a:Msg>", "</a:Msg>"));
			return;
		}
		if (sonuc.equals("0")){
			uls.storeUserData(new User(jsonStr));
			uls.setUserLoggedIn(true);
			GlobalTools.WriteStringToCookie("LastEmail", uls.getStoredUser().EMail);
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}
}
