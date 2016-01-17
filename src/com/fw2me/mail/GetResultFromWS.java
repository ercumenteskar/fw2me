package com.fw2me.mail;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;


public class GetResultFromWS {//extends AsyncTask<Object, Void, String> { //8081 // 61163 for Debug
	//private static final String WSAdress = "http://192.168.1.122:61163/MobileService.svc/";
	public String Response = "";
	HttpResponse hresponse;
	GlobalTools glb;
	Context ctx;
	ProgressDialog PleaseWait;
//	private MIActivity activity;
	
//	public GetResultFromWS() {
//    
//	}
//	
//	public GetResultFromWS(MIActivity activity) {
//    this.activity = activity;
//	}
	
//	public GetResultFromWS(Context cnt) {
//    this.ctx = cnt;
//	}
	
//	protected void onPreExecute(){
//		if (this.ctx!=null)
//			PleaseWait = ProgressDialog.show(this.ctx, "Lütfen Bekleyiniz...", "Lütfen Bekleyiniz...");
//	}
	
//	@Override
	protected String execute(Object... params) {
		String Rtn = "";
//		if (params.length>1){
//			ctx = (Context)params[1];
//  		//PleaseWait = ProgressDialog.show(ctx, "Kayýt iþlemi", "Lütfen bekleyiniz...", true);
//		}
		try
		{
//			//JSONObject RtnJson = new JSONObject();
//			try {
//	      Thread.sleep(5000);
//      } catch (InterruptedException e1) {
//	      // TODO Auto-generated catch block
//	      e1.printStackTrace();
//      }
			DefaultHttpClient client = new DefaultHttpClient(); // &IMEI="+MainActivity.IMEI+"
			String prms = (String)params[0];
			if (!prms.startsWith("http"))
				prms = MainActivity.WSAdress + prms + "&UserNo="+MainActivity.userNo()+"&SessionCode="+MainActivity.sessionCode()+"&LangId="+MainActivity.Lang_id();
//			prms = prms.replace(" &", "&").trim();
			prms = prms.replace(" ", "%20");//.trim();
			prms = prms.replace("?&", "?");//.trim();
			if (prms.indexOf("?")<1)
				prms = prms + "?";  //.trim();
			Log.i("WSKomut", prms);
			HttpGet request = new HttpGet(prms);
			HttpEntity entity;
			try {
				hresponse = client.execute(request);
				entity = hresponse.getEntity();
				if (entity.getContentLength() != 0) {
					BufferedReader r =new BufferedReader( new InputStreamReader(entity.getContent()));
					StringBuilder total = new StringBuilder();
					String line;
					while ((line = r.readLine()) != null) {
						total.append(line);
					}
					Rtn = total.toString();
				}
			} catch (Exception e) {
				MainActivity.WSBusy = false;
				Rtn = "-"+e.getMessage();
				//Toast.makeText(MainActivity.context, e.getMessage(), Toast.LENGTH_LONG).show();
			}
			Response = Rtn;
//      String strs = ((String)params[0]).substring(0, ((String)params[0]).indexOf("?"));
//			if ((strs!="Login") && (strs!="Register"))
//			{
//				JSONObject jsonObj;
//	      try {
//		      jsonObj = new JSONObject(Response);
//					JSONObject jo = jsonObj.getJSONObject(strs+"Result");
//					if (jo.getString("Id")!="0")
//					{
//						GlobalTools.ShowTost(jo.getString("Msg"));
//					}
//	      } catch (Exception e) {
//		      // TODO Auto-generated catch block
//		      e.printStackTrace();
//	      }
//			}
		}finally{
		}
		return Rtn;
	}
	
	//@Override
//	protected void onPostExecute(String result) {
//		if (this.activity!=null)
//			this.activity.processFinish(result);
//		if (PleaseWait!=null) 
//				PleaseWait.dismiss();
//	}

	
}
