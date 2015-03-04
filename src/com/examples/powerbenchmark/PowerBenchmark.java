package com.examples.powerbenchmark;


import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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



	private static final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
	private String[] NumberOfUniversities = { "a.owl","c.owl","pizza.owl","University0.owl","50 Universities" };


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
			
		System.out.println(NumberOfUniversities.length);
	    tvState = (TextView) findViewById(R.id.mystate);

	    spinnerState = (Spinner) findViewById(R.id.spinnerstate);

	    ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
	            android.R.layout.simple_spinner_item, NumberOfUniversities);
	    adapter_state
	            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinnerState.setAdapter(adapter_state);
	    spinnerState.setOnItemSelectedListener(this);
	

	

	
		
		
		buttonHermit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				start();

				Intent i;
				PackageManager manager = getPackageManager();
				try {
				    i = manager.getLaunchIntentForPackage("com.example.hermitowlapi");
				    if (i == null){
					    System.out.println("apk not found");
				        throw new PackageManager.NameNotFoundException();
				    }
				    i.putExtra("ontologyName", SelectedOntology);
				    i.addCategory(Intent.CATEGORY_LAUNCHER);
				    //System.out.println("notsda");
				    startActivity(i);
				} catch (PackageManager.NameNotFoundException e) {

				}

			}
		});
		
		buttonPellet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				start();

				Intent i;
				PackageManager manager = getPackageManager();
				try {
				    i = manager.getLaunchIntentForPackage("es.deusto.deustotech.adaptui");
				    if (i == null){
					    System.out.println("apk not found");
				        throw new PackageManager.NameNotFoundException();
				    }
				    i.putExtra("ontologyName", SelectedOntology);
				    i.addCategory(Intent.CATEGORY_LAUNCHER);
				    //System.out.println("notsda");
				    startActivity(i);
				} catch (PackageManager.NameNotFoundException e) {

				}

			}
		});
		
		buttonAndroJena.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				start();

				Intent i;
				PackageManager manager = getPackageManager();
				try {
				    i = manager.getLaunchIntentForPackage("es.deusto.deustotech.androjena");
				    if (i == null){
					    System.out.println("apk not found");
				        throw new PackageManager.NameNotFoundException();
				    }
				    i.putExtra("ontologyName", SelectedOntology);
				    i.addCategory(Intent.CATEGORY_LAUNCHER);
				    //System.out.println("notsda");
				    startActivity(i);
				} catch (PackageManager.NameNotFoundException e) {

				}

			}
		});
		
		buttonNull.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//stop();
				drained = 0;
			}
		});	
	}
	
	public void onPause() {
	    super.onPause();}
	
	public void onStop() {
	    super.onStop();}
	
	public void onResume(){
	    super.onResume();
	    start();
	}
	
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
	
	


	public void start() {
	    if(timer != null) {
	        return;
	    }
	    timer = new Timer();	   
	    timer.schedule(new TimerTask() {
	        public void run() {	            
	        	float curret =bat(); 
	        	drained =drained +(curret/7200);
	            		
	       }
	   }, 0, 500 );
	}

	public void stop() {
	    timer.cancel();
	    timer = null;
	}
	 
	public void getBatteryCapacity() {
	    Object mPowerProfile_ = null;

	    final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

	    try {
	        mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
	                .getConstructor(Context.class).newInstance(this);
	    } catch (Exception e) {
	        e.printStackTrace();
	    } 

	    try {
	        double batteryCapacity = (Double) Class
	                .forName(POWER_PROFILE_CLASS)
	                .getMethod("getAveragePower", java.lang.String.class)
	                .invoke(mPowerProfile_, "battery.capacity");
	        Toast.makeText(PowerBenchmark.this, batteryCapacity + " mah",
	                Toast.LENGTH_LONG).show();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } 
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
	
	
	
}
