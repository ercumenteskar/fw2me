package com.fw2me.mymails;

import org.json.JSONObject;

import com.fw2me.mymails.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

		uls = new UserLocalStore(this);
		btLogin.setOnClickListener(this);
		btRegister.setOnClickListener(this);
		etEMail.setText(uls.getStoredUser().EMail);
		btLogin.getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);
	}
	
	@Override
	public void onClick(View v){
		switch (v.getId()) {

		case R.id.btLogin:

			String EMail = etEMail.getText().toString();
			String Password = etPassword.getText().toString();
			//String userinfoxml = "";
			try {
//				userinfoxml = 
				//String jsonStr = 
						//new GetResultFromWS().execute("Login?EMail="+ EMail+"&Password="+Password);//.get().toString();
						new servisAT().execute("Login?EMail="+ EMail+"&Password="+Password);
			} catch (Exception e) {
				GlobalTools.ShowTost(e.getMessage());
			}
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
      PleaseWait = ProgressDialog.show(Login.this, "Lütfen Bekleyiniz...", "Lütfen Bekleyiniz...");
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
			super.onPostExecute(result);
			JSONObject jsonObj;
	    try {
	      jsonObj = new JSONObject(result);
				JSONObject jo = jsonObj.getJSONObject(mtype+"Result");
				if (jo.getString("Id")!="0")
				{
//					Response = "";
					GlobalTools.ShowTost(jo.getString("Msg"));
//					if (jo.getString("Id")=="12")
//					{
//						startActivity(new Intent(Login.this, Login.class));
//						finish();
//					}
				}
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
//		}
		if (mtype.equals("Login"))
			LoginResult(result);
		if (PleaseWait!=null) 
				PleaseWait.dismiss();
		  
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
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}
}
