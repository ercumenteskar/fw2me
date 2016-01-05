package com.fw2me.mymails;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLocalStore {
		GlobalTools glb = new GlobalTools();

		public static final String SP_Name = "userDetails";
		SharedPreferences userLocalDatabase;
		public UserLocalStore(Context context){
			userLocalDatabase = context.getSharedPreferences(SP_Name, 0);
			//userLocalDatabase = PreferenceManager.getDefaultSharedPreferences(context);
		}

		public void storeUserData(User user){
			SharedPreferences.Editor editor = userLocalDatabase.edit();
//			editor.putString("EMail", user.EMail);
//			editor.putString("Pass", user.Pass);
			Class<?> c = user.getClass();
			Field f = null;
			String fName = "";
      try {
      	for (int i = 0; i < c.getDeclaredFields().length; i++) {
  	      f = c.getDeclaredFields()[i];
	        fName = f.getName();
	        String s = f.getType().toString();
	        if (s.contains("String")){
	        	editor.putString(fName, (String)f.get(user));
	        }
	        else if (s.contains("boolean")){
	        	editor.putBoolean(fName, f.getBoolean(user));
	        }
	        else if (s.contains("int")){
	        	editor.putInt(fName, f.getInt(user));
	        }
	        else if (s.contains("Date")){
	        	editor.putString(fName, (String)f.get(user));
	        }
        }
      } catch (Exception e) {
	      GlobalTools.ShowTost(e.getMessage());
      }
			editor.commit();
		}
		
		public User getStoredUser(){
			User o = new User();
			Class<?> c = o.getClass();
			Field f = null;
			String fName = "";
      try {
      	for (int i = 0; i < c.getDeclaredFields().length; i++) {
  	      f = c.getDeclaredFields()[i];
	  			f.setAccessible(true);
	        fName = f.getName();
	        String s = f.getType().toString();
	        if (s.contains("String")){
	        	f.set(o, userLocalDatabase.getString(fName, ""));
	        }
	        else if (s.contains("boolean")){
	        	f.set(o, userLocalDatabase.getBoolean(fName, false));
	        }
	        else if (s.contains("int")){
	        	f.set(o, userLocalDatabase.getInt(fName, 0));
	        }
	        else if (s.contains("Date")){
	        	f.set(o, userLocalDatabase.getString(fName, ""));
	        }
        }
      } catch (Exception e) {
	      GlobalTools.ShowTost(e.getMessage());
      }
			return o;
		}
		
		public void setUserLoggedIn(boolean loggedIn){
			SharedPreferences.Editor editor = userLocalDatabase.edit();
			editor.putBoolean("loggedIn", loggedIn);
			editor.commit();
			if (!loggedIn)
				clearUserData();
		}

		public boolean getUserLoggedIn(){
			return userLocalDatabase.getBoolean("loggedIn", false);
		}

		private void clearUserData(){
			SharedPreferences.Editor editor = userLocalDatabase.edit();
			editor.clear();
			editor.commit();
		}
}
