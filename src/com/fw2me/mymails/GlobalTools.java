package com.fw2me.mymails;

import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class GlobalTools {
	public static void ShowTost(String Msg){
		Toast.makeText(MainActivity.context, Msg, Toast.LENGTH_LONG).show();
	}
	public boolean isConnectingToInternet(){
		ConnectivityManager connectivity = (ConnectivityManager) MainActivity.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
		}
		return false;
	}
	public String getUserCountry(Context context) {
		String rtn = "";
		try {
			TelephonyManager tm = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)); 
			rtn = tm.getSimCountryIso();
			if (isNull(rtn).length() == 2) // SIM country code is available
				return rtn;

			rtn = tm.getNetworkCountryIso();
			if (isNull(rtn).length() == 2) // network country code is available
				return rtn;

			rtn = Locale.getDefault().getCountry(); 
			if (isNull(rtn).length() == 2) //Locale available
				return rtn;

			rtn = isNull(rtn);
		}
		catch (Exception e) { rtn = ""; }
		return rtn;
	}

	public static String isNull(String _value, String _default){
		return _value==null ? _default : _value;
	}
	public static String isNull(String _value){
		return isNull(_value, "");
	}
	public static String OrtasiniGetir(String Gelen, String Basi, String Sonu)
	{
		String Rtn = "";
		if (Gelen.indexOf(Basi)>-1 && Gelen.indexOf(Basi)+Basi.length()<Gelen.indexOf(Sonu))
			Rtn = Gelen.substring(Gelen.indexOf(Basi)+Basi.length(), Gelen.indexOf(Sonu));
		else Rtn = "";
		return Rtn;
	}


}
