package com.android.launcherNew;



 
import android.content.Context;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
 
public class SettingManager {
    // Shared Preferences
    SharedPreferences pref;
     
    // Editor for Shared preferences
    Editor editor;
     
    // Context
    Context _context;
     
    // Shared pref mode
    int PRIVATE_MODE = 0;
     
    // Constructor
    public SettingManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences("LPref", PRIVATE_MODE);
        editor = pref.edit();
    }
     
       
      
    public boolean GetStatus(String key)
    {
      	return pref.getBoolean("showAdd"+key,true);
    }
    public void SetStatus(String key,boolean value)
    {
      	 editor.putBoolean("showAdd"+key,value);
         editor.commit();
    }
    public void DeleteAll()
    {
    	   editor.clear();  
    	   editor.commit();
    }
    
    
}