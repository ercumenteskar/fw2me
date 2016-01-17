package com.fw2me.mail;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.DateFormat;

public class User {
	String UserNo, EMail, Pass, PushToken, SessionCode, Name, Surname, EmailConfirmationCode, Phone;
	int Lang_Id, DeviceType, Gender_Id, Profession_Id, Marital_Id, Country_Id, City_Id;
	String Lang_Name, Gender_Name, Profession_Name, Marital_Name, Country_Name, City_Name;
	boolean isEMailConfirmed, A_Request,
	A_NM_NameSurname, A_NM_Photo, A_NM_Gender, A_NM_PushNotification, A_NM_SMS, A_NM_Call, A_NM_EMail, ShowAds;
	String CreatedDT, SessionExpireDT, BirthDate, Anniversary;


	GlobalTools glb = new GlobalTools();

	public User() {
		
	}
	
	public User(String userinfoxml) {
		String uix = userinfoxml; // Kullaným Kolaylýðý için...
		User o = this;
		Class<?> c = o.getClass();
		Field f = null;
		String fName = "";
		String tmpStr = "";
		try {
			for (int i = 0; i < c.getDeclaredFields().length; i++) {
				f = c.getDeclaredFields()[i];
				f.setAccessible(true);
				fName = f.getName();
				String s = f.getType().toString();
				if (s.contains("String")){
					if (uix.contains("<a:"+fName+"/>"))
						f.set(o, "");
					else if (uix.contains("<a:"+fName+">")) {
						tmpStr = GlobalTools.OrtasiniGetir(uix, "<a:"+fName+">", "</a:"+fName+">");
						if ((fName.equals("BirthDate")) || (fName.equals("Anniversary")))	{	
							if (tmpStr.equals("0001-01-01T00:00:00"))
								tmpStr = "";
							else{
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  
								try {  
									Date date = format.parse(tmpStr);
//									java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(MainActivity.context);
									java.text.DateFormat df = DateFormat.getDateFormat(MainActivity.context);
									tmpStr = df.format(date);
								} catch (Exception e) {  
									GlobalTools.ShowTost(e.getMessage());
									tmpStr = "";
								}
								//f.set(o, tmpStr);
							}
						}
						f.set(o, tmpStr);
					}
				}
				else if (s.contains("boolean")){
					if (uix.contains("<a:"+fName+"/>"))
						f.set(o, false);
					else if (uix.contains("<a:"+fName+">")) {
						f.set(o, GlobalTools.OrtasiniGetir(uix, "<a:"+fName+">", "</a:"+fName+">").equals("true"));
					}
				}
				else if (s.contains("int")){
					if (uix.contains("<a:"+fName+"/>"))
						f.set(o, 0);
					else if (uix.contains("<a:"+fName+">")) {
						f.set(o, Integer.parseInt(GlobalTools.OrtasiniGetir(uix, "<a:"+fName+">", "</a:"+fName+">")));
					}
				}
				else if (s.contains("Date")){
					//if (uix.contains("<a:"+fName+"/>")) f.set(o, 0); else 
					if (uix.contains("<a:"+fName+">")) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						Date d = sdf.parse(GlobalTools.OrtasiniGetir(uix, "<a:"+fName+">", "</a:"+fName+">"));
						SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
						String str = sdf2.format(d);
						f.set(o, str);
					}
				}
			}
		} catch (Exception e) {
			GlobalTools.ShowTost(e.getMessage());
		}
	}

	public String getPropStr(String key) {
		Class<?> c = this.getClass();
		Field f = null;
		String val = "";
		try {
			f = c.getDeclaredField(key);
			if (f != null)
	      val = (String)f.get(this);
	    else return null;
    } catch (Exception e) {
      GlobalTools.ShowTost(e.getMessage());
    }
			return val;
	}

	public void setPropStr(String key, String value) {
		Class<?> c = this.getClass();
		Field f = null;
		try {
			f = c.getDeclaredField(key);
			if (f != null)
	      f.set(this, (String)value);
    } catch (Exception e) {
      GlobalTools.ShowTost(e.getMessage());
    }
	}
	
	public Integer getPropInt(String key) {
		Class<?> c = this.getClass();
		Field f = null;
		Integer val = 0;
		try {
			f = c.getDeclaredField(key);
			if (f != null)
	      val = (Integer)f.getInt(this);
	    else return null;
    } catch (Exception e) {
      GlobalTools.ShowTost(e.getMessage());
    }
			return val;
	}
	public void setPropInt(String key, int value) {
		Class<?> c = this.getClass();
		Field f = null;
		try {
			f = c.getDeclaredField(key);
			if (f != null)
	      f.setInt(this, value);
    } catch (Exception e) {
      GlobalTools.ShowTost(e.getMessage());
    }
	}
	
	public Boolean getPropBool(String key) {
		Class<?> c = this.getClass();
		Field f = null;
		boolean val = false;
		try {
			f = c.getDeclaredField(key);
			if (f != null)
	      val = f.getBoolean(this);
	    else return null;
    } catch (Exception e) {
      GlobalTools.ShowTost(e.getMessage());
    }
			return val;
	}

	public void setPropBool(String key, boolean value) {
		Class<?> c = this.getClass();
		Field f = null;
		try {
			f = c.getDeclaredField(key);
			if (f != null)
	      f.setBoolean(this, value);
    } catch (Exception e) {
      GlobalTools.ShowTost(e.getMessage());
    }
	}
	
	}
