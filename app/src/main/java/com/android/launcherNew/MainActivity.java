package com.android.launcherNew;
import java.io.BufferedReader;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

public class MainActivity extends Activity {

	Dialog alertdialog,netdialog;
	String user_language="German";
	private static int app_type = 1;//1 dfern / 2 join
	private static int app_version = 6;// J R G
	private static String launcher_update_link="";
	private static int launcher_size = 2608000;//2522112;2673664
	private static boolean ActivityOn = true;
	SettingManager settingobj;
	private static final int REQ_ACTIVATE_DEVICE_ADMIN = 10;
    private Policy mPolicy;
    TextView wifi_,threedot_,screen_,astril_,remote_,reset_;
	LinearLayout wifi_btn,threedot_btn,screen_btn,astril_btn,remote_btn,reset_btn;
    Boolean isInternetPresent = false;
    InternetChecking cd;
	Handler handler = new Handler();
    Runnable Refresh;
    String ControllerURL = "https://l.deutsches-fernsehen.net/launcher/launcher.php";
    private String LOG_TAG = "Main";
    private String LOG = "LLL";
    private int UpdateAvailable=0;
    public Dialog CodeBox;
    int pos=1;
    String[] code_arr = new String[6];
    TextView del,clear,done,head,error,loading,logo_lay;
    public boolean LogFileAvailable=false;
    ProgressDialog	Abar;
    public int ActivationAttempt=1;
    LinearLayout lay_top,lay_one,lay_two;
    public  int BootingDone=0;
    public boolean debug = false;
    
    public boolean isConnected(Context con) {
        ConnectivityManager
                cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	    requestWindowFeature(Window.FEATURE_NO_TITLE);
    	    Abar = new ProgressDialog(MainActivity.this);
    	    ActivationAttempt=1;
      	setContentView(R.layout.main_new);
		View rootView = getWindow().getDecorView();
		rootView.setSystemUiVisibility(8);
	    netdialog = new Dialog(MainActivity.this);
	    CodeBox = new Dialog(MainActivity.this);
	    CodeBox.setContentView(R.layout.code_box);
	    CodeBox.setTitle("Aktivierungs-Code");
	    
	    try{
	    
	    	  File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TVAppData/" );
          if (!dir.exists()){ dir.mkdirs();}
	    
	    }catch(Exception e){}
	    
	    if(debug){
	      ControllerURL = "https://l.deutsches-fernsehen.net/launcher/launcher.php?debug=1";
	      launcher_update_link ="https://l.deutsches-fernsehen.net/launcher/beta/Launcher.apk";

	    }else{
	      launcher_update_link ="https://l.deutsches-fernsehen.net/launcher/Launcher.apk";
		  ControllerURL = "https://l.deutsches-fernsehen.net/launcher/launcher.php?v="+app_version;
	    }
	    loading = (TextView) findViewById(R.id.loading_text);
	    lay_top  = (LinearLayout) findViewById(R.id.logo_layout);
	    lay_one  = (LinearLayout) findViewById(R.id.top);
	    lay_two  = (LinearLayout) findViewById(R.id.right_col);

	    del   = (TextView) CodeBox.findViewById(R.id.delete);
		clear = (TextView) CodeBox.findViewById(R.id.clear);
		done  = (TextView) CodeBox.findViewById(R.id.donee);
	    head = (TextView) CodeBox.findViewById(R.id.head);
	    error = (TextView) CodeBox.findViewById(R.id.error);

        mPolicy = new Policy(this);
	    settingobj = new SettingManager(getApplicationContext());
        String AppName ="DTV -";
        LogFileAvailable=false;
        Toast.makeText(getApplicationContext(), AppName+app_version, Toast.LENGTH_LONG).show();
   	   
     	wifi_      = (TextView) findViewById(R.id.wifi_);
     	threedot_  = (TextView) findViewById(R.id.threedot_);
     	screen_    = (TextView) findViewById(R.id.screen_);
     	astril_    = (TextView) findViewById(R.id.astril_);
     	remote_    = (TextView) findViewById(R.id.remote_);
     	reset_     = (TextView) findViewById(R.id.reset_);

    	    
     	wifi_btn =(LinearLayout) findViewById(R.id.wifi_btn);
     	threedot_btn = (LinearLayout) findViewById(R.id.threedot_btn);
     	screen_btn = (LinearLayout) findViewById(R.id.screen_btn);
     	astril_btn = (LinearLayout) findViewById(R.id.astril_btn);
     	remote_btn = (LinearLayout) findViewById(R.id.remote_btn);
     	reset_btn = (LinearLayout) findViewById(R.id.reset_btn);
     	
     	wifi_btn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				 if(hasFocus){
					 ShowHide(1,wifi_btn);
				 }else {
					 wifi_btn.setBackgroundResource(0);
				 }
			}
		});
     	wifi_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowHide(1,wifi_btn);
				try{
				  ActivityOn=false;	
				  startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
				} catch(Exception e){
					
					Toast.makeText(getApplicationContext(),"Unable to open Wifi Settings",Toast.LENGTH_SHORT).show();
					
			   }
			}
		});
     	
     	threedot_btn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				 if(hasFocus){
				   ShowHide(1,threedot_btn);
				 }else {
					 threedot_btn.setBackgroundResource(0);
				 }
			}
		});
     	threedot_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowHide(1,threedot_btn);
				try{
					ActivityOn=false; 
					startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS));
					} catch(Exception e){
			Toast.makeText(getApplicationContext(),"Unable to open Wifi Settings",Toast.LENGTH_SHORT).show();
				    }
			}
		});
     	screen_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowHide(1,screen_btn);
				ActivityOn=false;
				startActivity(new Intent(Settings.ACTION_SETTINGS));
			}
		});
     	screen_btn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				 if(hasFocus){
				   ShowHide(1,screen_btn);
				 }else {
					 screen_btn.setBackgroundResource(0);
				 }
			}
		});
     	astril_btn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				 if(hasFocus){
				   ShowHide(1,astril_btn);
				 }else {
					 astril_btn.setBackgroundResource(0);
				 }
			}
		});
     	remote_btn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				 if(hasFocus){
				   ShowHide(1,remote_btn);
				 }else {
					 remote_btn.setBackgroundResource(0);
				 }
			}
		});
     	remote_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowHide(1,remote_btn);
				  try {
					 ExitAlertBox();
					} catch(Exception e){
					  Log.d("MA","DialogBox",e);	
					}
			}
		});
     	reset_btn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				 if(hasFocus){
				   ShowHide(1,reset_btn);
				 }else {
					 reset_btn.setBackgroundResource(0);
				 }
			}
		});
     	
     	
        LinearLayout jointv = (LinearLayout) findViewById(R.id.jointv);
        LinearLayout browser = (LinearLayout) findViewById(R.id.browser);
        LinearLayout settings = (LinearLayout) findViewById(R.id.setting);
        LinearLayout ass = (LinearLayout) findViewById(R.id.ass);
        LinearLayout remote = (LinearLayout) findViewById(R.id.remote);
        LinearLayout radio = (LinearLayout) findViewById(R.id.radio);
        LinearLayout reset = (LinearLayout) findViewById(R.id.reset);
        remote.setVisibility(View.GONE);
        
        jointv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
				ShowHide(0,wifi_btn);
			}
	     });
        browser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
				ShowHide(0,wifi_btn);
			}
	     });
        reset_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowHide(1,reset_btn);
				if(mPolicy.isAdminActive()){
			     	   ResetData(1);
			     }else {
			     	   ResetData(0);
			     }
			}
	    }); 	

        reset.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mPolicy.isAdminActive()){
			     	Log.d("Admin","Active");
			     	   ResetData(1);
			     }else {
			     	   Log.d("Admin","Not Active");
			     	   ResetData(0);
			     }

			}
	    }); 	
        radio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
					ShowHide(0,wifi_btn);
			}
	     });
        radio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try{	
				   LogFileAvailable = CheckDatFile();
				}catch(Exception e){}
			 if(!LogFileAvailable){
					ShowCodeBox();
			 }else{		
				
				Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("tunein.player");
				if(LaunchIntent!=null){ 
					   if(settingobj.GetStatus("radio")){
					      ActivityOn=true;
					    	  ShowAdvertisement(0);	
					    }else {
					    	  ActivityOn=false;
					    	  startActivity(LaunchIntent);
					    }
				}else {
			        	ActivityOn=true;  
			       new DownloadNewVersionRadio().execute(); 
			        
			    }
			  }//
			}
	    }); 	
        
       remote.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
				 ExitAlertBox();
				} catch(Exception e){
				  Log.d("MA","DialogBox",e);	
				}
			}
	   });
        ass.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.vakoms.astrillvpn");
				     if(LaunchIntent!=null){ 
					    if(settingobj.GetStatus("ass")){
					      ActivityOn=true;
					    	  ShowAdvertisement(1);	
					    }else {
					    	  ActivityOn=false;
					    	  startActivity(LaunchIntent);
					    }
				    	       
			         }else {
			        	     ActivityOn=true;  
			        	     new DownloadNewVersionAstrill().execute();
			        	 }				
			      }/////
	   });
        
      astril_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShowHide(1,astril_btn);
				     
				
				     Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.vakoms.astrillvpn");
				     if(LaunchIntent!=null){ 
					    if(settingobj.GetStatus("ass")){
					      ActivityOn=true;
					    	  ShowAdvertisement(1);	
					    }else {
					    	  ActivityOn=false;
					    	  startActivity(LaunchIntent);
					    }
			         }else {
			        	     ActivityOn=true;  
			        	     new DownloadNewVersionAstrill().execute();
			        	 }				
			      }/////
	   });
 
        jointv.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ShowHide(0,wifi_btn);
				  try{	
				  LogFileAvailable = CheckDatFile();
				  }catch(Exception e){}
				  if(!LogFileAvailable){
					   ShowCodeBox();
				  }else{     
					
					 String tvPkgName="com.smkamran.germantv";
					 Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(tvPkgName);
					     if(LaunchIntent!=null){ 
						   ActivityOn=false;
						   startActivity(LaunchIntent);
				         }else {
				           ActivityOn=true;
				           new DownloadNewVersion().execute();   
				         }				
					  }
				   }
		     });	
        	browser.setOnClickListener(new View.OnClickListener() {
				
					@Override
					public void onClick(View v) {
						ShowHide(0,wifi_btn);
						
						Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.android.browser");
						if(LaunchIntent!=null){
						   ActivityOn=false;
						   startActivity(LaunchIntent);
					     }else {
					       ActivityOn=true;
Toast.makeText(getApplicationContext(), "Browser App is not installed on this device", Toast.LENGTH_LONG).show();
					     }
					 }
				
			});
        	settings.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus)
					ShowHide(0,wifi_btn);
				}
		 });
        	
        	settings.setOnClickListener(new View.OnClickListener() {
	
				@Override
				public void onClick(View v) {
					ShowHide(0,wifi_btn);
					Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.rockchip.settings");
					if(LaunchIntent!=null){
			        	  ActivityOn=false;
					  startActivity(LaunchIntent);
			        }else {
			        	  ActivityOn=true;
			        	   if(app_type==6){
Toast.makeText(getApplicationContext(), "Настройка приложение не установлено на этом устройстве", Toast.LENGTH_LONG).show();	        
			        	   }else {
Toast.makeText(getApplicationContext(), "Setting App is not installed on this device", Toast.LENGTH_LONG).show();	        
			           }
			        	 }
					
			      }
			});
    }
	private void ShowPerScreen(){
		
		Intent activateDeviceAdminIntent =
             new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
             mPolicy.getPolicyAdmin());
        activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
             getResources().getString(R.string.device_admin_activation_message));
        startActivityForResult(activateDeviceAdminIntent, REQ_ACTIVATE_DEVICE_ADMIN);

	}
	public void ShowAdvertisement(final int kk)
	{
       
		alertdialog = new Dialog(MainActivity.this);
		alertdialog.setContentView(R.layout.ad);
		
		alertdialog.setTitle("Empfehlung");
		if(app_type==6){
		  alertdialog.setTitle("рекомендация");
		}
		Button btn_no = (Button) alertdialog.findViewById(R.id.btn_no);
		Button btn_yes = (Button) alertdialog.findViewById(R.id.btn_yes);

		TextView cont_lable1 = (TextView) alertdialog.findViewById(R.id.cont_lable1);
		String txt = "<html>Um diese Applikation nutzen zu können, benötigen Sie eine externe Tastatur," +
				" wir empfehlen Ihnen eine <a href=\"http://www.logitech.com/de-de/product/wireless-touch-keyboard-k400r?crid=26\">Logitech K400</a>. Jede andere Tastatur oder Maus ist per USB aber auch nutzbar!";
		
		cont_lable1.setText(Html.fromHtml(txt));
		cont_lable1.setMovementMethod(LinkMovementMethod.getInstance());
		alertdialog.show();	
		btn_no.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				alertdialog.dismiss();
			Intent LaunchIntent=null;
			if(kk==0){
				settingobj.SetStatus("radio",false);

				LaunchIntent = getPackageManager().getLaunchIntentForPackage("tunein.player");
			}else{
				settingobj.SetStatus("ass",false);
				LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.vakoms.astrillvpn");
			}
				if(LaunchIntent!=null){ 
				    	  ActivityOn=false;
				    	  startActivity(LaunchIntent);
		         }else {
		        	     ActivityOn=true;  
		        	        if(kk==0){	
			        	     new DownloadNewVersionRadio().execute();
		 	   			}else{
			        	     new DownloadNewVersionAstrill().execute();
		 	   			}		        	 }				

				
			}
		});
        btn_yes.setOnClickListener(new View.OnClickListener() {
	
	         @Override
 	         public void onClick(View v) {
			   //settingobj.SetStatus(true);
	        	   alertdialog.dismiss();
	        	   Intent LaunchIntent=null;
	   			if(kk==0){	
					settingobj.SetStatus("radio",true);

	   				LaunchIntent = getPackageManager().getLaunchIntentForPackage("tunein.player");
	   			}else{
					settingobj.SetStatus("ass",true);
	   				LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.vakoms.astrillvpn");
	   			}	if(LaunchIntent!=null){ 
					    	  ActivityOn=false;
					    	  startActivity(LaunchIntent);
			         }else {
			        	     ActivityOn=true;  
			        	        if(kk==0){	
				        	     new DownloadNewVersionRadio().execute();
			 	   			}else{
				        	     new DownloadNewVersionAstrill().execute();
			 	   			}
			        	 }
	        }
        });
		
	}
	public void ResetData(final int flag)
	{
    	    alertdialog = new Dialog(MainActivity.this);
		alertdialog.setContentView(R.layout.reset_box);
		Button btn_no = (Button) alertdialog.findViewById(R.id.btn_no);
		Button btn_yes = (Button) alertdialog.findViewById(R.id.btn_yes);
		TextView cont_lable2 = (TextView) alertdialog.findViewById(R.id.cont_lable2);
		cont_lable2.setTextColor(Color.parseColor("#FFFFFF"));
		alertdialog.setTitle("Factory Reset");
		
		
		if(flag==0){
	cont_lable2.setText("Wenn Sie mit der TV-Box ein technisches Problem haben, klicken Sie auf “Weiter” und " +
			"und best\u00e4tigen Sie den Vorgang mit “Aktivieren” um die TV-Box Funktionen wiederherzustellen.\n\nhre Vertragsdaten bleiben dabei gespeichert!\n\nSollte Ihre TV-Box per Wi-Fi verbunden sein, müssen Sie das Wlan-Passwort nach dem Vorgang neu eingeben um sich mit dem Netzwerk zu verbinden.");	
			btn_yes.setText("Weiter");
			
		
		}else {
			//direct reset
			
			cont_lable2.setText("Wenn Sie mit der TV-Box ein technisches Problem haben, klicken Sie auf “Weiter” und " +
			"und best\u00e4tigen Sie den Vorgang mit “Aktivieren” um die TV-Box Funktionen wiederherzustellen.\n\nhre Vertragsdaten bleiben dabei gespeichert!\n\nSollte Ihre TV-Box per Wi-Fi verbunden sein, müssen Sie das Wlan-Passwort nach dem Vorgang neu eingeben um sich mit dem Netzwerk zu verbinden.");	
			btn_yes.setText("Weiter");
			

		}
		
		alertdialog.show();
		btn_no.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertdialog.dismiss();
			}
        });
		btn_yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertdialog.dismiss();
				if(flag==0){
					 ShowPerScreen();
				}else {
				  new WipeSDCard().execute();
				}
			}
        });

        
	}
    public void ExitAlertBox()
	{
    	    alertdialog = new Dialog(MainActivity.this);
		alertdialog.setContentView(R.layout.alert_box);
		alertdialog.setTitle("Remote Support - Ver: "+app_version);
		
		if(ActivityOn==true){
		  alertdialog.show();
		}
		Button btn_no = (Button) alertdialog.findViewById(R.id.btn_no);
		Button btn_yes = (Button) alertdialog.findViewById(R.id.btn_yes);
		TextView cont_lable = (TextView) alertdialog.findViewById(R.id.cont_lable);
		final TextView cont_lable1 = (TextView) alertdialog.findViewById(R.id.cont_lable1);
		TextView cont_lable2 = (TextView) alertdialog.findViewById(R.id.cont_lable2);

		cont_lable2.setText("Sie ben\u00f6tigen Hilfe f\u00fcr Ihre TV-Box?");
cont_lable.setText("2.\r\rDr\u00fccken Sie \"weiter\", um die Support-Software zu \u00f6ffnen.\n" +
	   		"3.\r\rGeben Sie die angezeigte ID an den Techniker im Chat durch.\n" +
	   "4.\r\rDr\u00fccken Sie \"zulassen\", um dem Techniker den Zugriff zu erm\u00f6glichen.\n" +
"5.\r\rDr\u00fccken Sie noch einmal \"zulassen\", um dem Techniker die Steuerung zu erm\u00f6glichen." +
	 "\n\nWarten Sie bitte, w\u00e4hrend der Techniker das Problem untersucht.");
//http://help.tvsupport.net/chat.php		
cont_lable1.setText("1.\r\r\u00d6ffnen Sie ein Chat-Fenster mit unserem Support unter https://support.deutsches-fernsehen.net/live/chat.php");
	btn_no.setText("abbrechen");
    btn_yes.setText("weiter");
        

		
		cont_lable1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				cont_lable1.setTextColor(Color.parseColor("#FFFFFF"));
				alertdialog.dismiss();
				Intent viewIntent =  new Intent("android.intent.action.VIEW",
				Uri.parse("https://support.deutsches-fernsehen.net/live/chat.php"));
				startActivity(viewIntent);
				ActivityOn=false;
			}
		});
		
		cont_lable1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(arg1){
					cont_lable1.setTextColor(Color.parseColor("#FFFFFF"));
				}else {
					cont_lable1.setTextColor(Color.parseColor("#ADD6F6"));
				}
			}
		});
		
		btn_yes.setOnClickListener(new View.OnClickListener() {
			
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//alertdialog.dismiss();
						//ApplicationInfo addon = getPackageManager().getApplicationLabel("com.teamviewer.quicksupport.addon.aosp14");//.getLaunchIntentForPackage("com.teamviewer.quicksupport.addon.aosp14");
						Intent teamapp = getPackageManager().getLaunchIntentForPackage("com.teamviewer.quicksupport.market");
	
					    boolean isInstalled = false; 
                         try{
     					   PackageManager pm = MainActivity.this.getPackageManager();
                        	   isInstalled = isPackageInstalled("com.teamviewer.quicksupport.addon.aosp14", pm);
                         }catch(Exception e){ }
						
						if(!isInstalled){
						   alertdialog.dismiss();	
			///Toast.makeText(getApplicationContext(), "addon",Toast.LENGTH_SHORT).show();			   
						 new DownloadAddOn().execute();
						}else if(teamapp==null){
						   alertdialog.dismiss();  
			///Toast.makeText(getApplicationContext(), "app",Toast.LENGTH_SHORT).show();			   
					new DownloadNewVersionTeamViewer().execute();
						}else if(teamapp!=null && isInstalled){
							ActivityOn=false;
							startActivity(teamapp);
						}else{
							alertdialog.dismiss();
	Toast.makeText(getApplicationContext(), "Teamviewer couldn't open", Toast.LENGTH_LONG).show();	        
						}
						
					}
				});
		btn_no.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						alertdialog.dismiss();
					}
		});
			
	}
    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
    //Download Radio app
	class DownloadNewVersionRadio extends AsyncTask<String,Integer,Boolean>{

	ProgressDialog	bar = new ProgressDialog(MainActivity.this);

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		bar = new ProgressDialog(MainActivity.this);
		bar.setCancelable(false); 

        bar.setMessage("Herunterladen neuer Version...");
        if(app_type==6){
           bar.setMessage("Скачать новую версию...");
        }
        bar.setIndeterminate(true);
		bar.setCanceledOnTouchOutside(false);
		bar.show();
		
	}

	protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        bar.setIndeterminate(false);
        bar.setMax(100);
        bar.setProgress(progress[0]);
        String msg = "";
        if(progress[0]>99){
          	
	         	 msg="Bitte warten Sie ... ";
	         	 if(app_type==6){
	         		msg="Пожалуйста, подождите ... ";
	         	 }
        
        }else {
         
	       msg="Herunterladen neuer Version... "+progress[0]+"%";
	        if(app_type==6){
		   msg="Скачать новую версию... "+progress[0]+"%";
	        }
	        	
        	
        }
	    bar.setMessage(msg);

    }
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result==true)
		{
		   bar.dismiss();
		   
Toast.makeText(getApplicationContext(), "Radio app is downloaded successfully",Toast.LENGTH_LONG).show();	
		   
		}
		else
		{
		   bar.dismiss();
		   
Toast.makeText(getApplicationContext(), "Unable to connect to server. Try again",Toast.LENGTH_LONG).show();	
		   
		}
	}

	

	@Override
	protected Boolean doInBackground(String... arg0) {
		Boolean flag = false;
		
		// File DbFile=new File("mnt/sdcard/HelloAndroid.apk");
	    //if(!(DbFile.exists()))
		
		try {
			URL url = new URL("https://l.deutsches-fernsehen.net/launcher/apk/tunein.player.apk");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
    		    String PATH = Environment.getExternalStorageDirectory()+"/Download/";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file,"update.apk");
            Log.d("Update",url.toString());
            if(outputFile.exists()){
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();
            int total_size = 9421824;//apk_size;//
            
            Log.d("apk size i download Radio",total_size+" S");
            byte[] buffer = new byte[1024];
            int len1 = 0;
            int per = 0;
            int downloaded=0;
            while ((len1 = is.read(buffer)) != -1) {
            	     fos.write(buffer, 0, len1);
            	     downloaded +=len1;
            	     per = (int) (downloaded * 100 / total_size);
            	     publishProgress(per);
            }
            fos.close();
            is.close();
            Log.d("After Size",total_size+" S "+downloaded);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(PATH+"update.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
            startActivity(intent);
           
            flag = true;
         } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! TeamViewver " + e.getMessage());
            flag = false;
         }
         return flag;		
    
	
	}
	
}


    //Download new version
	class DownloadNewVersion extends AsyncTask<String,Integer,Boolean>{

	ProgressDialog	bar = new ProgressDialog(MainActivity.this);

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		bar = new ProgressDialog(MainActivity.this);
		bar.setCancelable(false); 

        bar.setMessage("Herunterladen neuer Version...");
        
        bar.setIndeterminate(true);
		bar.setCanceledOnTouchOutside(false);
		bar.show();
		
	}

	protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        bar.setIndeterminate(false);
        bar.setMax(100);
        bar.setProgress(progress[0]);
        String msg = "";
        if(progress[0]>99){
          	
	        msg="Bitte warten Sie ... ";
	        
        
        }else {
         
	      msg="Herunterladen neuer Version... "+progress[0]+"%";
	        
        	
        }
	    bar.setMessage(msg);

    }
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result==true)
		{
		   bar.dismiss();
		   
		   Toast.makeText(getApplicationContext(), "TV app is downloaded ..",Toast.LENGTH_LONG).show();	
		   
		}
		else
		{
		   bar.dismiss();
		   
		   Toast.makeText(getApplicationContext(), "Unable to connect to server. Try again",Toast.LENGTH_LONG).show();	
		   
		}
	}

	

	@Override
	protected Boolean doInBackground(String... arg0) {
		Boolean flag = false;
		
		// File DbFile=new File("mnt/sdcard/HelloAndroid.apk");
	    //if(!(DbFile.exists()))
		
		try {
			URL url = new URL("https://l.deutsches-fernsehen.net/box/DTV.apk");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
    		    String PATH = Environment.getExternalStorageDirectory()+"/Download/";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file,"update.apk");
            Log.d("Update",url.toString());
            if(outputFile.exists()){
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();
            int total_size = 3553000;
            
            Log.d("apk size i download",total_size+" S");
            byte[] buffer = new byte[1024];
            int len1 = 0;
            int per = 0;
            int downloaded=0;
            while ((len1 = is.read(buffer)) != -1) {
            	     fos.write(buffer, 0, len1);
            	     
            	     downloaded +=len1;
            	     per = (int) (downloaded * 100 / total_size);
            	     publishProgress(per);
            	     
            }
            fos.close();
            is.close();
            Log.d("After Size",total_size+" S "+downloaded);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(PATH+"update.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
            startActivity(intent);
           
            flag = true;
         } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! ",e);
            flag = false;
         }
         return flag;		
    
	
	}
	
}
	
	//download teamviewer addon
	class DownloadNewVersionTeamViewer extends AsyncTask<String,Integer,Boolean>{

	ProgressDialog	bar = new ProgressDialog(MainActivity.this);

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		bar = new ProgressDialog(MainActivity.this);
		bar.setCancelable(false); 

        if(app_type==6){
        	  bar.setMessage("Скачать новую версию...");
        }else {
	    	  bar.setMessage("Herunterladen neuer Version...(2/2)");
        }
        bar.setIndeterminate(true);
		bar.setCanceledOnTouchOutside(false);
		bar.show();
		
	}

	protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        bar.setIndeterminate(false);
        bar.setMax(100);
        bar.setProgress(progress[0]);
        String msg = "";
        if(progress[0]>99){
          	
	       if(app_type==6){
	        	  msg="Пожалуйста, подождите ... ";
	       }else {
	          msg="Bitte warten Sie ...(2/2)";
	       }
        }else {
         
	        msg="Herunterladen neuer Version... "+progress[0]+"% (2/2)";
	        if(app_type==6){
		        msg="Скачать новую версию... "+progress[0]+"%";
	        }
	         	 
	        	
        	
        }
	    bar.setMessage(msg);

    }
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result==true)
		{
		   bar.dismiss();
		   if(app_type==6){
			  Toast.makeText(getApplicationContext(), "TeamViewer приложение успешно загружены..",Toast.LENGTH_LONG).show();	
		   }else {
			  Toast.makeText(getApplicationContext(), "TeamViewer app is downloaded successfully..",Toast.LENGTH_LONG).show();	
		   }
		}
		else
		{
		   bar.dismiss();
		   if(app_type==6){
	Toast.makeText(getApplicationContext(), "Не удалось подключиться к серверу. Попробуйте еще раз",Toast.LENGTH_LONG).show();	
		   }else {
		    Toast.makeText(getApplicationContext(), "Unable to connect to server. Try again",Toast.LENGTH_LONG).show();	
		   }
		}
	}

	

	@Override
	protected Boolean doInBackground(String... arg0) {
		Boolean flag = false;
		
		
		try {
			URL url = new URL("https://l.deutsches-fernsehen.net/launcher/apk/com.teamviewer.quicksupport.market_new.apk");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
    		    String PATH = Environment.getExternalStorageDirectory()+"/Download/";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file,"update.apk");
            Log.d("Update",url.toString());
            if(outputFile.exists()){
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();
            int total_size = 11441000;//apk_size;//
            
            Log.d("apk size i download TeamViewver",total_size+" S");
            byte[] buffer = new byte[1024];
            int len1 = 0;
            int per = 0;
            int downloaded=0;
            while ((len1 = is.read(buffer)) != -1) {
            	     fos.write(buffer, 0, len1);
            	     downloaded +=len1;
            	     per = (int) (downloaded * 100 / total_size);
            	     publishProgress(per);
            }
            fos.close();
            is.close();
            Log.d("After Size",total_size+" S "+downloaded);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(PATH+"update.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
            startActivity(intent);
           
            flag = true;
         } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! TeamViewver " + e.getMessage());
            flag = false;
         }
         return flag;		
    
	
	}
	
}

    //Download TeamViewver
	class DownloadAddOn extends AsyncTask<String,Integer,Boolean>{

	ProgressDialog	bar = new ProgressDialog(MainActivity.this);

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		bar = new ProgressDialog(MainActivity.this);
		bar.setCancelable(false); 

        if(app_type==6){
        	  bar.setMessage("Скачать новую версию...");
        }else {
	    	  bar.setMessage("Herunterladen neuer Version...(1/2)");
        }
        bar.setIndeterminate(true);
		bar.setCanceledOnTouchOutside(false);
		bar.show();
		
	}

	protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        bar.setIndeterminate(false);
        bar.setMax(100);
        bar.setProgress(progress[0]);
        String msg = "";
        if(progress[0]>99){
          	
	       if(app_type==6){
	        	  msg="Пожалуйста, подождите ... ";
	       }else {
	          msg="Bitte warten Sie ...(1/2)";
	       }
        }else {
         
	        msg="Herunterladen neuer Version... "+progress[0]+"% (1/2)";
	        if(app_type==6){
		        msg="Скачать новую версию... "+progress[0]+"%";
	        }
	         	 
	        	
        	
        }
	    bar.setMessage(msg);

    }
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result==true)
		{
		   bar.dismiss();
		   if(app_type==6){
			  Toast.makeText(getApplicationContext(), "TeamViewer приложение успешно загружены..",Toast.LENGTH_LONG).show();	
		   }else {
			  Toast.makeText(getApplicationContext(), "TeamViewer app is downloaded successfully..",Toast.LENGTH_LONG).show();	
		   }
		}
		else
		{
		   bar.dismiss();
		   if(app_type==6){
	Toast.makeText(getApplicationContext(), "Не удалось подключиться к серверу. Попробуйте еще раз",Toast.LENGTH_LONG).show();	
		   }else {
		    Toast.makeText(getApplicationContext(), "Unable to connect to server. Try again",Toast.LENGTH_LONG).show();	
		   }
		}
	}

	

	@Override
	protected Boolean doInBackground(String... arg0) {
		Boolean flag = false;
		
		
		try {
			URL url = new URL("https://l.deutsches-fernsehen.net/launcher/apk/TeamViewerQSAddon14.apk");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
    		    String PATH = Environment.getExternalStorageDirectory()+"/Download/";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file,"update.apk");
            Log.d("Update",url.toString());
            if(outputFile.exists()){
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();
            int total_size = 1228000;//apk_size;//
            
            Log.d("apk size i download TeamViewver",total_size+" S");
            byte[] buffer = new byte[1024];
            int len1 = 0;
            int per = 0;
            int downloaded=0;
            while ((len1 = is.read(buffer)) != -1) {
            	     fos.write(buffer, 0, len1);
            	     downloaded +=len1;
            	     per = (int) (downloaded * 100 / total_size);
            	     publishProgress(per);
            }
            fos.close();
            is.close();
            Log.d("After Size",total_size+" S "+downloaded);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(PATH+"update.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
            startActivity(intent);
           
            flag = true;
         } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! TeamViewver " + e.getMessage());
            flag = false;
         }
         return flag;		
    
	
	}
	
}

	//Download Astrill
	class DownloadNewVersionAstrill extends AsyncTask<String,Integer,Boolean>{

	ProgressDialog	bar = new ProgressDialog(MainActivity.this);

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		bar = new ProgressDialog(MainActivity.this);
		bar.setCancelable(false); 

             if(app_type==6){
               bar.setMessage("Подключение к серверу...");
             }else {
	    	       bar.setMessage("Herunterladen neuer Version...");
             } 
        bar.setIndeterminate(true);
		bar.setCanceledOnTouchOutside(false);
		bar.show();
		
	}

	protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        bar.setIndeterminate(false);
        bar.setMax(100);
        bar.setProgress(progress[0]);
        String msg = "";
        if(progress[0]>99){
          	
	            if(app_type==6){
	             	msg="Пожалуйста, подождите... ";
	            }else {
	        		    msg="Bitte warten Sie ... ";
	            }
        
        }else {
         
	        	   if(app_type==6){
	        		  msg="Скачать новую версию ..."+progress[0]+"%";
	        	   }else {
	         	  msg="Herunterladen neuer Version... "+progress[0]+"%";
	        	   }
        }
	    bar.setMessage(msg);

    }
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(result==true)
		{
		   bar.dismiss();
		   if(app_type==6){
Toast.makeText(getApplicationContext(), "Astrill приложение успешно загружены..",Toast.LENGTH_LONG).show();	
		   }else {
Toast.makeText(getApplicationContext(), "Astrill app is downloaded successfully..",Toast.LENGTH_LONG).show();	
		   }
		}
		else
		{
		   bar.dismiss();
		   if(app_type==6){
	Toast.makeText(getApplicationContext(), "Невозможно подключиться к серверу. Попробуйте еще раз",Toast.LENGTH_LONG).show();	
		   }else {
		   Toast.makeText(getApplicationContext(), "Unable to connect to server. Try again",Toast.LENGTH_LONG).show();	
		   }
		}
	}

	

	@Override
	protected Boolean doInBackground(String... arg0) {
		Boolean flag = false;
		
		try {
			URL url = new URL("https://l.deutsches-fernsehen.net/launcher/apk/AstrillVPN.apk");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
    		    String PATH = Environment.getExternalStorageDirectory()+"/Download/";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file,"update.apk");
            Log.d("Update",url.toString());
            if(outputFile.exists()){
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();
            int total_size = 7903232;//apk_size;//
            
            Log.d("apk size i download astrill",total_size+" S");
            byte[] buffer = new byte[1024];
            int len1 = 0;
            int per = 0;
            int downloaded=0;
            while ((len1 = is.read(buffer)) != -1) {
            	     fos.write(buffer, 0, len1);
            	     downloaded +=len1;
            	     per = (int) (downloaded * 100 / total_size);
            	     publishProgress(per);
            }
            fos.close();
            is.close();
            Log.d("After Size",total_size+" S "+downloaded);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(PATH+"update.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
            startActivity(intent);
           
            flag = true;
         } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! " + e.getMessage());
            flag = false;
         }
         return flag;		
    
	
	}
	
}


	class DownloadLauncher extends AsyncTask<String,Integer,Boolean>{

	ProgressDialog	bar = new ProgressDialog(MainActivity.this);

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		bar = new ProgressDialog(MainActivity.this);
		bar.setCancelable(false); 

        bar.setMessage("Herunterladen neuer Version...");
        if(app_type==6){
        	bar.setMessage("Скачать новую версию...");
        }
        bar.setIndeterminate(true);
		bar.setCanceledOnTouchOutside(false);
		bar.show();
		
	}

	protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        bar.setIndeterminate(false);
        bar.setMax(100);
        bar.setProgress(progress[0]);
        String msg = "";
        if(progress[0]>99){
          	
	         	 msg="Bitte warten Sie ... ";
	         	 if(app_type==6){
	         		msg="Пожалуйста, подождите ... ";
	         	 }
        
        }else {
         
	        msg="Herunterladen neuer Version... "+progress[0]+"%";
	        if(app_type==6){
		  msg="Скачать новую версию... "+progress[0]+"%";
	        }
        	
        }
	    bar.setMessage(msg);

    }
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		UpdateAvailable=0;
		if(result==true)
		{
		   bar.dismiss();
		   
		   Toast.makeText(getApplicationContext(), "Launcher app is downloaded successfully..",Toast.LENGTH_LONG).show();	
		   
		}
		else
		{
		   bar.dismiss();
		   
		   Toast.makeText(getApplicationContext(), "Unable to connect to server. Please try again",Toast.LENGTH_LONG).show();	
		   
		}
	}

	

	@Override
	protected Boolean doInBackground(String... arg0) {
		Boolean flag = false;
		
		try {
			URL url = new URL(launcher_update_link);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
    		    String PATH = Environment.getExternalStorageDirectory()+"/Download/";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file,"update.apk");
            Log.d("Update",url.toString());
            if(outputFile.exists()){
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();
            int total_size = launcher_size;//8592081;//apk_size;//
            
            Log.d("apk size i download",total_size+" S");
            byte[] buffer = new byte[1024];
            int len1 = 0;
            int per = 0;
            int downloaded=0;
            while ((len1 = is.read(buffer)) != -1) {
            	     fos.write(buffer, 0, len1);
            	     downloaded +=len1;
            	     per = (int) (downloaded * 100 / total_size);
            	     publishProgress(per);
            }
            fos.close();
            is.close();
            Log.d("After Size",total_size+" S "+downloaded);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(PATH+"update.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
            ActivityOn=true;
            startActivity(intent);
           
            flag = true;
         } catch (Exception e) {
            Log.e("UpdateAPP", "Update error! " + e.getMessage());
            flag = false;
         }
         return flag;		
	}
}

	class CheckNetwork extends AsyncTask<String,Integer,String>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		    
			loading.setText("Please wait....");

		
		}
		protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);

	        if(progress[0]==2){
	        	  loading.setText("Connecting...");
	        }else if(progress[0]==3){
	        	  loading.setText("Connected...");
	        }else{
	          loading.setText("Please wait..");
	        }
		}
        
		@Override
		protected String doInBackground(String... params) {
			Log.d(LOG, "doInBackground");
			String res="";
			try{
			    int boot_count=1;
			    while(!isConnected() && BootingDone==0 && boot_count<=2){
			    	    boot_count++;
			    	    Log.d(LOG, "BooisConnected():"+boot_count);
			    	    publishProgress(1); 
			    	    Thread.sleep(3000);
			    	    
			    }
			    publishProgress(2);
			    
				for(int v=1;v<=4;v++){ 
					Log.d(LOG, "Internet:"+v);
			    	    res = CallServer(ControllerURL);
			    	    if(BootingDone==2){
			    	    	    break;
			    	    }
			    	    if(res.equals("")){
			    	    	   Thread.sleep(5000);
			    	    }else {
			    	      publishProgress(3); 
			    	      Log.d(LOG, "Internet Connected:"+v);
			    	    	  break;
			    	    }
			    }
				 String b ="";
				 if(BootingDone!=2 && res.equals("")){
					 b = CallServer("https://de-guide-01.epg.to/ping");
					 if(!b.equals("")){
				         return "ok";
				     }
				 }
				 
				 if(!res.equals("")){
					   JSONObject jo = new JSONObject(res);
					   int UpdateCode = jo.getInt("UpdateCode");
					   UpdateAvailable=0;
					   Log.d(LOG, "Update: Code:"+UpdateCode);
					   Log.d(LOG, "Update: AppCode:"+app_version);
					   if(UpdateCode>app_version){
						  UpdateAvailable=1;
					   }else{
						  UpdateAvailable=0;
					   }
					   Log.d("Update", "UpdateAvailable:"+UpdateAvailable);
				    }
			  
			  
			}catch(Exception e){
			   res="";
			   Log.d(LOG_TAG,"Error doInBackground",e);	
			}
			return res;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.d(LOG, "UpdateAvailable:"+UpdateAvailable);
      
			if(BootingDone==0){
		        if(result.equals("")){
		          BootingDone=1;	
		        	  startActivity(new Intent(Settings.ACTION_SETTINGS));
		        	  loading.setText("Keine Verbindung zum Server");
Toast.makeText(getApplication(), "Keine Verbindung zum Server",Toast.LENGTH_LONG).show();	

		        }else{
		          
		           BootingDone=2;	
		           Log.d(LOG, "Done: BootingDone:"+BootingDone);
		           lay_top.setVisibility(View.GONE);
		        	   lay_one.setVisibility(View.VISIBLE);
		        	   lay_two.setVisibility(View.VISIBLE);
		        	   loading.setText("Pleas wait.....");
		        	   Proceed();
		        }
			}else if(BootingDone==1){
				
		       if(result.equals("")){
				 
		    	     BootingDone=1;	
	        	     startActivity(new Intent(Settings.ACTION_SETTINGS));
	        	     loading.setText("Keine Verbindung zum Server");
Toast.makeText(getApplication(), "Keine Verbindung zum Server",Toast.LENGTH_LONG).show();	
		       
		       }else{
		    	   
		    	       BootingDone=2;	
		           lay_top.setVisibility(View.GONE);
		        	   lay_one.setVisibility(View.VISIBLE);
		        	   lay_two.setVisibility(View.VISIBLE);
		        	   loading.setText("Please wait......");
		        	   Proceed();
		    	   
		       }
				
			}else{
				
				if(result.equals("")){
	Toast.makeText(getApplication(), "Keine Verbindung zum Server",Toast.LENGTH_LONG).show();	
				}else{
					Proceed();
				}

			}
			Log.d(LOG, "BootingDone:"+BootingDone);

		}
		 public String CallServer(String server_url) throws IOException {
			 
			 
		        String Response = "";
		        InputStream inStream = null;
		 
		        try {
		 
		            //Define URL to call by using HttpURLConnection
		            URL url = new URL(server_url);
		 
		            //making call to server
		            HttpURLConnection connection = (HttpURLConnection) 
		                       url.openConnection();
		            connection.setReadTimeout(5000);
		            connection.setConnectTimeout(5000);
		            connection.setRequestMethod("GET");
		            connection.setDoInput(true);
		            connection.connect();
		 
		            int response = connection.getResponseCode();
		            
		            Log.d(LOG,"Code:"+response);
		            Log.d(LOG,"URL:"+server_url);
		            if(response==200) {
		                inStream = connection.getInputStream();
		                Response = ReadStream(inStream);
		            }
		        }catch (IOException e) {
		            Log.e(LOG,"Error: "+server_url,e);
		        }catch (Exception e) {
		            Log.e(LOG,"Error: "+server_url,e);
		        }finally {
		            if (inStream != null) {
		                inStream.close();
		            }
		        }
		        return Response;
		  }
		 
		 
		    //Method to convert stream into string
		    public String ReadStream(InputStream stream) {
		 
		        InputStreamReader isr = new InputStreamReader(stream);
		        BufferedReader reader = new BufferedReader(isr);
		        StringBuilder JsonString = new StringBuilder();
		        String line = null;
		        try {
		            while ((line = reader.readLine()) != null) {
		                JsonString.append(line);
		            }
		 
		        } catch (IOException e) {
		            Log.e(LOG_TAG, "Error in ReadStream", e);
		        }catch (Exception e) {
		            Log.e(LOG_TAG, "Error in ReadStream", e);
		        }finally {
		            try {
		                stream.close();
		            } catch (IOException e) {
		                Log.e(LOG_TAG, "Error in ReadStream",e);
		            }catch (Exception e) {
		                Log.e(LOG_TAG, "Error in ReadStream",e);
		            }
		        }
		        return JsonString.toString();
		 
		    }
	}

    public void Proceed(){
    	  
    	 if(UpdateAvailable==1){
		 new DownloadLauncher().execute();
	  }else if(!LogFileAvailable){
		 if(ActivityOn){
		    if(CodeBox!=null){
			    if(!CodeBox.isShowing()){
		    	      ShowCodeBox();
			    }
		    }
		 }
	  }
    }
	public void Clear(){
		error.setText("");
		code_arr[0]=" _ ";
		code_arr[1]=" _ ";
		code_arr[2]=" _ ";
		code_arr[3]=" _ ";
		code_arr[4]=" _ ";
		code_arr[5]=" _ ";
		pos=1;
		head = (TextView) CodeBox.findViewById(R.id.head);
		String head_str ="";
		for(int h=0;h<code_arr.length;h++){
			
			if(h==3){ head_str = head_str+"  -  ";}
			head_str = head_str + code_arr[h];
		}
		head.setText(head_str);
    }
    public void Delete(String c){
    	    error.setText("");
    	   Log.d("Pos","Pos"+pos);
       if(pos==1){
	    	   
	    }else if(pos==2){
	       code_arr[0]=c;
	       pos=1;
        }else if(pos==3){
 	      code_arr[1]=c;
 	     pos=2;
        }else if(pos==4){
 	      code_arr[2]=c;
 	      pos=3;
       }else if(pos==5){
 	      code_arr[3]=c;
 	      pos=4;
       }else if(pos==6){
 	      code_arr[4]=c;
 	      pos=5;
       }else if(pos==7){
  	      code_arr[5]=c;
  	      pos=6;
        }
       head = (TextView) CodeBox.findViewById(R.id.head);
		String head_str ="";
		for(int h=0;h<code_arr.length;h++){
			if(h==3){ head_str = head_str+"  -  ";}
			head_str = head_str + code_arr[h];
		}
		head.setText(head_str);
    }
    public void Merge(String c){
    	
    	   error.setText("");
    	    String a = "";
    	    c = " "+c+" ";
    	    if(pos==1){
    	    	   code_arr[0]=c;
    	    	   code_arr[1]=" _ ";
    	    	   code_arr[2]=" _ ";
    	    	   code_arr[3]=" _ ";
    	    	   code_arr[4]=" _ ";
    	    	   code_arr[5]=" _ ";
    	    	   pos=2;
    	    }else if(pos==2){
    	       code_arr[1]=c;
 	    	   code_arr[2]=" _ ";
 	    	   code_arr[3]=" _ ";
 	    	   code_arr[4]=" _ ";
 	    	   code_arr[5]=" _ ";
 	    	   pos=3;
 	    }else if(pos==3){
	    	   code_arr[2]=c;
	    	   code_arr[3]=" _ ";
	    	   code_arr[4]=" _ ";
	    	   code_arr[5]=" _ ";
	    	   pos=4;
	    }else if(pos==4){
	    	   code_arr[3]=c;
	    	   code_arr[4]=" _ ";
	    	   code_arr[5]=" _ ";
	    	   pos=5;
	    }else if(pos==5){
	    	   code_arr[4]=c;
	    	   code_arr[5]=" _ ";
	    	   pos=6;
	    }else if(pos==6 || pos==7){
	    	   code_arr[5]=c;
	    	   pos=7;
	    	   done.requestFocus();
	    }
		head = (TextView) CodeBox.findViewById(R.id.head);
		String head_str ="";
		for(int h=0;h<code_arr.length;h++){
			
			if(h==3){ head_str = head_str+"  -  ";}
			head_str = head_str + code_arr[h];
		}
		head.setText(head_str);
    	    //return a;
    }
    void ShowCodeBox(){
    	    
    	    code_arr[0]="_ ";
    	    code_arr[1]="_ ";
    	    code_arr[2]="_ ";
    	    code_arr[3]="_ ";
    	    code_arr[4]="_ ";
    	    code_arr[5]="_ ";
    	    pos=1;
    	    head = (TextView) CodeBox.findViewById(R.id.head);
    		String head_str ="";
    		for(int h=0;h<code_arr.length;h++){
    			if(h==3){ head_str = head_str+"  -  ";}
    			head_str = head_str + code_arr[h];
    		}
    		head.setText(head_str);
    		error.setText("");
		TextView b1 = (TextView) CodeBox.findViewById(R.id.one);
		TextView b2 = (TextView) CodeBox.findViewById(R.id.two);
		TextView b3 = (TextView) CodeBox.findViewById(R.id.three);
		TextView b4 = (TextView) CodeBox.findViewById(R.id.four);
		TextView b5 = (TextView) CodeBox.findViewById(R.id.five);
		TextView b6 = (TextView) CodeBox.findViewById(R.id.six);
		TextView b7 = (TextView) CodeBox.findViewById(R.id.seven);
		TextView b8 = (TextView) CodeBox.findViewById(R.id.eight);
		TextView b9 = (TextView) CodeBox.findViewById(R.id.nine);
		TextView b0 = (TextView) CodeBox.findViewById(R.id.zero);
		
		if(ActivityOn==true){ 
			
			CodeBox.show(); 
			b1.requestFocus();
		}
		
		  //done = (TextView) CodeBox.findViewById(R.id.donee);  
		  
		  del.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				
					Delete(" _ ");  
				
				 }
		  });
		  clear.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {

					Clear();  
				 }
		  });
		  done.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				
					String f = head.getText().toString().trim();
					if(f.contains("_")){
						error.setText("Geben Sie den vollst\u00e4ndigen Code ein");
					}else{
						f = f.replace(" ","").replace("-","").trim();
						////error.setText(f);
						new ActivateCode().execute(f);
						

					}
					
				 }
		  });
		  b1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Merge("1");  
			 }
	        });
			b2.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Merge("2");
						}
				    });
			b3.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Merge("3");
				}
			});
			b4.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Merge("4");
				}
			});
			b5.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Merge("5");
				}
			});
			b6.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Merge("6");
				}
			});
			b7.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Merge("7");
				}
			});
			b8.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Merge("8");
				}
			});
			b9.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Merge("9");
				}
			});
			b0.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Merge("0");
				}
			});
		
    	
    	
    }

 	@Override
	protected void onDestroy() {
		if(alertdialog !=null) {
     	   alertdialog.dismiss();
   	     }
		super.onDestroy();
	}
 	
 	public  boolean isConnected() {
        
 		boolean isConnected = false;
 		ConnectivityManager cm =
 		        (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

 		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
 		if(activeNetwork!=null){
 			isConnected = activeNetwork.isConnected();
 			if(isConnected){ 
 		        if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
 		        	  Log.d(LOG,"Wifi");
 		        }else if(activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET){
 		        	  Log.d(LOG,"Cable");
 		        }else {
 		        	  Log.d(LOG,"Other");
 		        }
 	 		}
 		}
 		
 		Log.d(LOG,"isConnected() "+isConnected);
 		return isConnected;
 		
    }
	class ActivateCode extends AsyncTask<String,Integer,String>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Abar = new ProgressDialog(MainActivity.this);
			Abar.setCancelable(false); 
			Abar.setMessage("Bitte warten... (1/5)");
			Abar.setIndeterminate(true);
			Abar.setCanceledOnTouchOutside(false);
			Abar.show();
		}
		protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);

	        if(progress[0]==2 || progress[0]==4){
	          Abar.setMessage("Bitte warten _ _ ("+progress[0]+"/5)");
	        }else{
	        	  Abar.setMessage("Bitte warten... ("+progress[0]+"/5)");
	        }
		}
        
		@Override
		protected String doInBackground(String... params) {
			
			String res="";
			
			String code = params[0].substring(0,3)+"-"+params[0].substring(3,6);
			try{
				String URL = "https://deutsches-fernsehen.net/api/box/activate/"+code;
				Log.d("ActivateCode", "URL:"+URL);
				
				for(int y=2;y<=5;y++){ 
				   res = CallServer(URL);
				   ///res="";
				   if(!res.equals("")){
					   break;
				   }else{
					  Thread.sleep(5000);
					  publishProgress(y); 
				   }
				}
				if(res==""){
					return res;
				}
				
				JSONObject obj = new JSONObject(res);
				int rc = obj.getInt("rc");
				if(rc==1){
					String un = obj.getString("licUserid");
					String pw = obj.getString("licPassword");
		            writeToFile(un+"`"+pw+"`German`ON`ON");

					return "OK";
				}else if(rc==-100){
				   return "INVALID";
				}else if(rc==-102){
				   return "USED";
				}else if(rc==-101){
				   return "WRONG";
				}
				Log.d("ActivateCode", "RC:"+rc);
			}
			catch(Exception e){
			   res="";
			   Log.d(LOG_TAG,"Error ActivateCode",e);	
			}
			return res;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(Abar!=null){
				Abar.dismiss();
			}
			
			if(result.equals("INVALID")){
				error.setText("Code ung\u00fcltig");
			}else if(result.equals("USED")){
				error.setText("Code wurde bereits verwendet");
			}else if(result.equals("WRONG")){
				error.setText("Falscher Code");
			}else if(result.equals("OK")){
				error.setText("Aktivierung erfolgreich");
				if(CodeBox!=null){  CodeBox.dismiss(); }
				
				
			}else{
		startActivity(new Intent(Settings.ACTION_SETTINGS));
		error.setText("Keine Verbindung zum Server");
		Toast.makeText(getApplicationContext(), "Keine Verbindung zum Server", Toast.LENGTH_LONG).show();		
			}
		}
		 public String CallServer(String server_url) throws IOException {
			 
			 
		        String Response = "";
		        InputStream inStream = null;
		 
		        try {
		 
		            //Define URL to call by using HttpURLConnection
		            URL url = new URL(server_url);
		 
		            //making call to server
		            HttpURLConnection connection = (HttpURLConnection) 
		                       url.openConnection();
		            connection.setReadTimeout(5000);
		            connection.setConnectTimeout(5000);
		            connection.setRequestMethod("GET");
		            connection.setDoInput(true);
		            connection.connect();
		 
		            int response = connection.getResponseCode();
		            if(response==200) {
		                inStream = connection.getInputStream();
		                Response = ReadStream(inStream);
		            }
		        }catch (IOException e) {
		            Log.e(LOG_TAG,"Error in CallServer",e);
		        }catch (Exception e) {
		            Log.e(LOG_TAG,"Error in CallServer",e);
		        }finally {
		            if (inStream != null) {
		                inStream.close();
		            }
		        }
		        return Response;
		  }
		 
		 
		    //Method to convert stream into string
		    public String ReadStream(InputStream stream) {
		 
		        InputStreamReader isr = new InputStreamReader(stream);
		        BufferedReader reader = new BufferedReader(isr);
		        StringBuilder JsonString = new StringBuilder();
		        String line = null;
		        try {
		            while ((line = reader.readLine()) != null) {
		                JsonString.append(line);
		            }
		 
		        } catch (IOException e) {
		            Log.e(LOG_TAG, "Error in ReadStream", e);
		        }catch (Exception e) {
		            Log.e(LOG_TAG, "Error in ReadStream", e);
		        }finally {
		            try {
		                stream.close();
		            } catch (IOException e) {
		                Log.e(LOG_TAG, "Error in ReadStream",e);
		            }catch (Exception e) {
		                Log.e(LOG_TAG, "Error in ReadStream",e);
		            }
		        }
		        return JsonString.toString();
		 
		    }
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_ACTIVATE_DEVICE_ADMIN && resultCode == RESULT_OK) {
            // User just activated the application as a device administrator.
             Log.d("Admin","Activate Press");
			 new WipeSDCard().execute();

        	   // setScreenContent(mCurrentScreenId);
        } else {
        	Intent activateDeviceAdminIntent =
	                  new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
	              activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
	                      mPolicy.getPolicyAdmin());
	              activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
	                      getResources().getString(R.string.device_admin_activation_message));
	              startActivityForResult(activateDeviceAdminIntent, REQ_ACTIVATE_DEVICE_ADMIN);
	              ActivityOn=false;
        	//fff
        	   Log.d("Admin","Not Activate Press");
        	    //super.onActivityResult(requestCode, resultCode, data);
        }
    }
	private boolean CheckDatFile(){
		
		File file = new File(Environment.getExternalStorageDirectory(),"TVAppData/TVAppData.dat");
        if(file.exists()){
        	  return true;
        }else{
        	  return false;
        }
		
	}
	@Override
	protected void onResume() {
		super.onResume();

		ActivityOn=true;
	    LogFileAvailable = CheckDatFile();
	    new CheckNetwork().execute();
		

	}
	
	class WipeSDCard extends AsyncTask<String,Integer,Boolean>{

	ProgressDialog	bar = new ProgressDialog(MainActivity.this);

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		bar = new ProgressDialog(MainActivity.this);
		bar.setCancelable(false); 
        bar.setMessage("Loeschen von Daten...");
        bar.setIndeterminate(true);
		bar.setCanceledOnTouchOutside(false);
		bar.show();
		
	}

	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(bar.isShowing()){
			bar.dismiss();
		}
	  	if(result==true){
	  		
Toast.makeText(getApplicationContext(),"Data deleted...",Toast.LENGTH_LONG).show();
	 
	    }else {
	    	    
Toast.makeText(getApplicationContext(),"Data not deleted",Toast.LENGTH_LONG).show();
	    	    
	  	}
	  	mPolicy.ClearData();
	
	}

	

	@Override
	protected Boolean doInBackground(String... arg0) {
		Boolean flag = false;
		
		
		try {
		   wipingSdcard();
		   flag = true;
         } catch (Exception e) {
            Log.e("MainActivity", "Wiping data" + e.getMessage());
            flag = false;
         }
         return flag;		
	}
	
}

 
	public void wipingSdcard() {
        File deleteMatchingFile = new File(Environment
                .getExternalStorageDirectory().toString());
        try {
            File[] filenames = deleteMatchingFile.listFiles();
            if (filenames != null && filenames.length > 0) {
                for (File tempFile : filenames) {
                    if (tempFile.isDirectory()) {
                    	   Log.d("File",tempFile.toString()+"-");
                      if(!tempFile.toString().contains("TVAppData")){   
                    	    wipeDirectory(tempFile.toString());
                        tempFile.delete();
                      }else {
                   	   Log.d("File Excape",tempFile.toString()+"-");

                      }
                    } else {
                        tempFile.delete();
                    }
                }
            } else {
                deleteMatchingFile.delete();
            }
        } catch (Exception e) {
           // e.printStackTrace();
        	   Log.d("File","File delting",e);
        }
    }

    private void wipeDirectory(String name) {
        File directoryFile = new File(name);
        File[] filenames = directoryFile.listFiles();
        if (filenames != null && filenames.length > 0) {
            for (File tempFile : filenames) {
                if (tempFile.isDirectory()) {
                    wipeDirectory(tempFile.toString());
                    tempFile.delete();
                } else {
                    tempFile.delete();
                }
            }
        } else {
            directoryFile.delete();
        }
    }
	@Override
	protected void onPause() {
		if(alertdialog!=null){
			alertdialog.dismiss();
		}
		if(netdialog!=null){
		   netdialog.dismiss();
		}
		ActivityOn=false;
  		handler .removeCallbacksAndMessages(null); 
		super.onPause();
	}
	 
	private void ShowHide(int i,LinearLayout l){
		
		
		LayoutParams params = lay_two.getLayoutParams();
		if(i==0){
		params.width = 70;
		wifi_btn.setBackgroundResource(0);
     	threedot_btn.setBackgroundResource(0);
     	screen_btn.setBackgroundResource(0);
     	astril_btn.setBackgroundResource(0);
     	remote_btn.setBackgroundResource(0);
     	reset_btn.setBackgroundResource(0);

			wifi_.setVisibility(View.GONE);
	     	threedot_.setVisibility(View.GONE);
	     	screen_.setVisibility(View.GONE);
	     	astril_.setVisibility(View.GONE);
	     	remote_.setVisibility(View.GONE);
	     	reset_.setVisibility(View.GONE);
		}else {
		    params.width = 400;
		    l.setBackgroundResource(R.drawable.icon_hover);
			wifi_.setVisibility(View.VISIBLE);
	     	threedot_.setVisibility(View.VISIBLE);
	     	screen_.setVisibility(View.VISIBLE);
	     	astril_.setVisibility(View.VISIBLE);
	     	remote_.setVisibility(View.VISIBLE);
	     	reset_.setVisibility(View.VISIBLE);
		}
		
	}
	
     public  void writeToFile(String body){
        FileOutputStream fos = null;
        try {
        	    final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TVAppData/" );
            if (!dir.exists())
            {
                dir.mkdirs(); 
            }
            final File myFile = new File(dir,"TVAppData.dat");
            if (myFile.exists()) 
            {    
            	   myFile.delete();
            }
            myFile.createNewFile();
            fos = new FileOutputStream(myFile);
            fos.write(body.getBytes());
            Log.d(LOG_TAG,"TVAppData.dat created\n"+body);
            fos.close();
        } catch (IOException e) {
            //e.printStackTrace();
            Log.d(LOG_TAG,"Writing to  ",e);
        }
    }

}
