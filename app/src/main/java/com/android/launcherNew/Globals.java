package com.android.launcherNew;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class Globals {

	public static int _Port = 0;
	public static boolean BootComplete = false;
	
    public static void CreateFile(String body,String FileName)
    {
        FileOutputStream fos = null;
        try {
        	    final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RemoteCommands/" );
           	File Listfile[] = dir.listFiles();
           	try{
           	  for(int x=0;x<Listfile.length;x++){
           		Listfile[x].delete();
           	  }
           	}catch (Exception e){
           		Log.d("ZZ Glo WriteTofile","File not deleted");
           	}

        	    if (!dir.exists())
            {
                dir.mkdirs(); 
            }
            final File myFile = new File(dir,FileName);
            if (myFile.exists()) 
            {    
            	   myFile.delete();
            }
            myFile.createNewFile();
            fos = new FileOutputStream(myFile);
            fos.write(body.getBytes());
            Log.d("XX Glo writeToFile()",FileName+"created\n"+body);
            fos.close();
        } catch (IOException e) {
            Log.d("XX Glo writeToFile()","Writing to  ",e);
        }
    }


}
