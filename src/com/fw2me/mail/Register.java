package com.fw2me.mail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Register extends ActionBarActivity implements OnClickListener{

	EditText etEMail, etPassword, etPassword2;
	Button btRegister;
	TextView btLogin;
	GlobalTools glb = new GlobalTools();
	UserLocalStore uls;
	ProgressDialog PleaseWait;
  String mtype = "";
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		ImageView img = (ImageView) findViewById(R.id.iv_banner);
		img.getLayoutParams().height = size.x/2;
		img.getLayoutParams().width = size.x;
		img.requestLayout();
		uls = new UserLocalStore(this);

		etEMail = (EditText)findViewById(R.id.etEmail);
		etPassword = (EditText)findViewById(R.id.etPassword);
		etPassword2 = (EditText)findViewById(R.id.etPassword2);
		btRegister = (Button)findViewById(R.id.btRegister);
		btLogin = (TextView)findViewById(R.id.btLogin);

		btLogin.setOnClickListener(this);
		btRegister.setOnClickListener(this);

		btRegister.getBackground().setColorFilter(0xFF1A237E, PorterDuff.Mode.DARKEN);

	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btRegister:
							String EMail = etEMail.getText().toString();
							String Password = etPassword.getText().toString();
							String Password2 = etPassword2.getText().toString();
						  new servisAT().execute("Register?EMail="+ EMail+"&Password="+Password+"&Password2="+Password2);
			break;

		case R.id.btLogin:
			Intent lint = new Intent(this, Login.class);
			lint.putExtra("Direct", "*");
			startActivity(lint);
			finish();
			break;

		default:
			break;
		}
	}

	public class servisAT extends AsyncTask<Object, Void, String> { // 8081 //
		// 61163 for
		// Debug
		ProgressDialog	PleaseWait;
	
		protected void onPreExecute() {
			super.onPreExecute();
			PleaseWait = ProgressDialog.show(Register.this,
					getResources().getString(R.string.PleaseWait), getResources().getString(R.string.PleaseWait));
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
//			JSONObject jsonObj;
			try {
//				jsonObj = new JSONObject(result);
//				JSONObject jo = jsonObj.getJSONObject(mtype + "Result");
//				if (jo.getString("Id") != "0") {
//					// Response = "";
//					GlobalTools.ShowTost(jo.getString("Msg"));
////					 if (jo.getString("Id")=="12")
////					 {
////						 startActivity(new Intent(Register.this, Login.class));
////						 finish();
////					 }
//				}
//				else if (mtype.equals("Register"))
					RegisterResult(result);
	
			} catch (Exception e) {
				e.printStackTrace();
			}
			// }
			if (PleaseWait != null)
				PleaseWait.dismiss();
	
		}
	}

	public void RegisterResult(String jsonStr)
	{
		String rs = GlobalTools.OrtasiniGetir(jsonStr, "<a:ResultSet>", "</a:ResultSet>");
		String sonuc = GlobalTools.OrtasiniGetir(rs, "<a:Id>", "</a:Id>"); 
		if (!sonuc.equals("0")){
			GlobalTools.ShowTost(GlobalTools.OrtasiniGetir(rs, "<a:Msg>", "</a:Msg>"));
		} else {
			uls.storeUserData(new User(jsonStr));
			uls.setUserLoggedIn(true);
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

}
