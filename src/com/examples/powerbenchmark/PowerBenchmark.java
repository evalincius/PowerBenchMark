package com.examples.powerbenchmark;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PowerBenchmark extends ActionBarActivity implements OnItemSelectedListener {
	private TextView textView;
	private Button buttonHermit;
	private Button buttonNull;
	private Button buttonPellet;
	private Button buttonAndroJena;

	private TextView batteryInfo;
	private ImageView imageBatteryState;
	private BroadcastReceiver batteryInfoReceiver;
	private Timer timer;
	private float draw;
	private float drained;
	private String SelectedOntology;
	private int reasonerState;


	static final int PICK_CONTACT_REQUEST = 1;  // The request code
	private static final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
	private String[] NumberOfUniversities = { "University00.owl","University05.owl","University010.owl","University015.owl" };


	Spinner spinnerState, spinnerCapital;
	TextView tvState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_power_benchmark);
		textView = (TextView) findViewById(R.id.textView);
		buttonHermit = (Button) findViewById(R.id.buttonHermit);
		buttonNull = (Button) findViewById(R.id.buttonNull);
		buttonPellet = (Button) findViewById(R.id.buttonPellet);
		buttonAndroJena = (Button) findViewById(R.id.buttonAndroJena);
		reasonerState = 1;
		
		

			//creates button for popup window
		final Button btnOpenPopup = (Button)findViewById(R.id.openpopup);
        btnOpenPopup.setOnClickListener(new Button.OnClickListener(){
        	
        	
        	//creates popup window
   @Override
   public void onClick(View arg0) {
    LayoutInflater layoutInflater 
     = (LayoutInflater)getBaseContext()
      .getSystemService(LAYOUT_INFLATER_SERVICE);  
    View popupView = layoutInflater.inflate(R.layout.popup, null);  
             final PopupWindow popupWindow = new PopupWindow(
               popupView, 
               LayoutParams.WRAP_CONTENT,  
                     LayoutParams.WRAP_CONTENT);  
             popupWindow.showAtLocation(textView, Gravity.CENTER, 0, 0);
             TextView popuptext = (TextView)popupView.findViewById(R.id.popuptext);
             try {
            	//write("log", "The Log file is currently empty");
				popuptext.setText(read("log"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
             Button btnClean = (Button)popupView.findViewById(R.id.clean);

             btnDismiss.setOnClickListener(new Button.OnClickListener(){

			     @Override
			     public void onClick(View v) {
			      // TODO Auto-generated method stub
			      popupWindow.dismiss();
			     }});
             
             
             btnClean.setOnClickListener(new Button.OnClickListener(){

			     @Override
			     public void onClick(View v) {
			      // TODO Auto-generated method stub
			     	write("log", "This is a Log File");
			        popupWindow.dismiss();
			     }});
               
          
             
			               
			             popupWindow.showAsDropDown(btnOpenPopup, 50, -30);
			         
			   }});
        
        

		
		
		
		
		
		//Creates DropDown menu 
		System.out.println(NumberOfUniversities.length);
	    tvState = (TextView) findViewById(R.id.mystate);

	    spinnerState = (Spinner) findViewById(R.id.spinnerstate);

	    ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
	            android.R.layout.simple_spinner_item, NumberOfUniversities);
	    adapter_state
	            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinnerState.setAdapter(adapter_state);
	    spinnerState.setOnItemSelectedListener(this);
	

	

	
		//Button that creates the intent to other preinstalled application
	    //in this case Hermit reasoner.
		
		buttonHermit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("com.example.hermitowlapi", "com.example.hermitowlapi.MainActivity"));
		        intent.putExtra("ontologyName", SelectedOntology);
		        startActivityForResult(intent,90);

			}
		});
		//Button that creates the intent to other preinstalled application
	    //in this case Pellet reasoner.
		buttonPellet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("es.deusto.deustotech.adaptui", "es.deusto.deustotech.adaptui.ActivityExample"));
		        intent.putExtra("ontologyName", SelectedOntology);
		        startActivityForResult(intent,90);
			}
		});
		//Button that creates the intent to other preinstalled application
	    //in this case AndroJena reasoner.
		buttonAndroJena.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("es.deusto.deustotech.androjena", "es.deusto.deustotech.androjena.MainActivity"));
		        intent.putExtra("ontologyName", SelectedOntology);
		        startActivityForResult(intent,90);
			}
		});
		//Button that sets the drain variable to 0.
		buttonNull.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Simulate(createPermutation());
			}
		});	
	}
	
	public void onPause() {
	    super.onPause();
	}
	
	public void onStop() {
	    super.onStop();}
	
	public void onResume(){
	    super.onResume();
	    //start();
	}
	//Battery method that reads the battery information and does some records and calculations 
	public  float bat(){		
        registerReceiver(this.batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        batteryInfoReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {			
				int  plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
				String  technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
				int  temperature= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
				int  voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);				
				
				BatteryManager mBatteryManager =
						(BatteryManager)getSystemService(Context.BATTERY_SERVICE);
						Long energy =
						mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);					
				float currentdraw = energy;
				draw = currentdraw;		
				batteryInfo.setText("Plugged: "+plugged+"\n"+
						"Technology: "+technology+"\n"+
						"Temperature: "+temperature+"\n"+
						"Voltage: "+voltage+"\n"+
						"Current mA = " + energy + "mA"+ "\n"+
						"Capacity Drained = " + drained + "mAh"+ "\n"
						);

			}
		};
		batteryInfo=(TextView)findViewById(R.id.textView);
		return draw;
	}
	
	

	//Starts timer that registers current flow in mA of battery 
	//and transforms it to mAh
	public void start() {
	    if(timer != null) {
	        return;
	    }
	    timer = new Timer();	   
	    timer.schedule(new TimerTask() {
	        public void run() {	            
	        	float curret =bat(); 
	        	drained =drained +(curret/64000);
	            		
	       }
	   }, 0, 50 );
	}

	public void stop() {
	    timer.cancel();
	    timer = null;
	}
	 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.power_benchmark, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	
	
	
	
	
	public void onItemSelected(AdapterView<?> parent, View view, int position,
	        long id) {

	    spinnerState.setSelection(position);
	    SelectedOntology = spinnerState.getSelectedItem().toString();
	    //tvState.setText("Currently selected  " + SelectedOntology);
	    
	}

	public void onNothingSelected(AdapterView<?> parent) {}
	

	//File writter
	public void write(String fname, String fcontent){
	        String filename= "storage/emulated/0/Download/"+fname+".txt";
	        String temp="";
	        if(!fcontent.equals("This is a Log File")){
	        	temp = read(fname);
	        }
	        BufferedWriter writer = null;
	        try {
	            //create a temporary file
	            File logFile = new File(filename);

	            // This will output the full path where the file will be written to...
	            System.out.println(logFile.getCanonicalPath());

	            writer = new BufferedWriter(new FileWriter(logFile));
	            
	            writer.write(temp + fcontent );
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                // Close the writer regardless of what happens...
	                writer.close();
	            } catch (Exception e) {
	            }
	        }
	   }
	
	//File reader
	   public String read(String fname){
	     BufferedReader br = null;
	     String response = null;
	      try {
	        StringBuffer output = new StringBuffer();
	        String fpath = "storage/emulated/0/Download/"+fname+".txt";
	        br = new BufferedReader(new FileReader(fpath));
	        String line = "";
	        while ((line = br.readLine()) != null) {
	          output.append(line +"\n");
	        }
	        response = output.toString();
	      } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	      }
	      return response;
	   }
	
	   //simulates the permutations of all reasoners.
	   public Stack<String> createPermutation(){
		   Stack<String> stack = new Stack<String>();
		   
			
		   stack.push("androjena");
		   stack.push("pellet");
		   stack.push("hermit");
		   
		   stack.push("androjena");
		   stack.push("hermit");
		   stack.push("pellet");
		   return stack;
	   }
	   
	   public void Simulate(Stack<String> s){
		    final Stack<String> stack = s;
		   
		   
		   if(!stack.isEmpty()){
			   if(reasonerState==1){
				   String a = stack.pop();
				   if(a.equals("androjena")){
					   System.out.println("before "+reasonerState);
					   reasonerState=0;
					   buttonAndroJena.performClick();
				   }
				   if(a.equals("pellet")){
					   buttonPellet.performClick();
					   reasonerState=0;
	
				   }
				   if(a.equals("hermit")){
					   buttonHermit.performClick();
					   reasonerState=0;
				   }				  				   
			   }
			   new Handler().postDelayed(new Runnable() {

			        @Override
			        public void run() {
			        	Simulate(stack);
			        }
			    }, 1000);
			   
		   }
	   }
	   
	   //returns results from external activity(any reasoner called) to 
	   //get the information if the reasoner is finished its task or not.
	   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	         switch(requestCode) {
	                case 90:
	                 if (resultCode == RESULT_OK) {
	                     Bundle res = data.getExtras();
	                     int result = res.getInt("results");
	                     reasonerState = result;
	                     System.out.println(result);
	                     
	                     }
	                break;
	         }
	    }
}
