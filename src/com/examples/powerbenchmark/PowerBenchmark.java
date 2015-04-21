package com.examples.powerbenchmark;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.drawable.ColorDrawable;

public class PowerBenchmark extends ActionBarActivity {
	private TextView textView;
	private Button buttonHermit;
	private Button buttonNull;
	private Button buttonPellet;
	private Button buttonAndroJena;
	private Button buttonStartControl;
	private Button buttonStopControl;
	private long startCountingTime;
	private long stopCountingTime;


	
	private Timer timer;
	private float draw;
	private float drained;
	private String SelectedOntologyFile, SelectedOntologyName, SelectedQuery;
	private int reasonerState;
	
	private BroadcastReceiver batteryInfoReceiver;
	private int mvoltage;
	private float watts;
	private PopupWindow popupWindow = null;
	private boolean popupOpened = false;

	static final int PICK_CONTACT_REQUEST = 1;  // The request code
	private static final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
	
	private String[] DatasetSize = { "1 Department","5 Departments","10 Departments","15 Departments" };
	private String[] ReasoningTask = { "Instance Retrieval","Inference & Instance Retrieval","Inference","Classification" };
	
	private String[] DatasetSizeToSend = { "University00.owl","University05.owl","University010.owl","University015.owl" };
	//private String[] ReasoningTaskToSend = { "Query1","Query2","Query3","Query4" };



	Spinner spinnerState, spinnerState2;
	TextView tvState;
	TextView tvState2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_power_benchmark);
		textView = (TextView) findViewById(R.id.textView);
		buttonHermit = (Button) findViewById(R.id.buttonHermit);
		buttonNull = (Button) findViewById(R.id.buttonNull);
		buttonPellet = (Button) findViewById(R.id.buttonPellet);
		buttonAndroJena = (Button) findViewById(R.id.buttonAndroJena);
		buttonStartControl = (Button) findViewById(R.id.buttonStartControl);
		buttonStopControl = (Button) findViewById(R.id.buttonStopControl);


		reasonerState = 1;
		
		
			//creates button for popup window
		final Button btnOpenPopup = (Button)findViewById(R.id.openpopup);
        btnOpenPopup.setOnClickListener(new Button.OnClickListener(){
        	
        	
			    //creates popup window
			   @Override
			   public void onClick(View arg0) {
				   if(!popupOpened){
				   popupOpened =true;
				   LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
				   View popupView = layoutInflater.inflate(R.layout.popup, null);  
			       popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
			             popupWindow.showAtLocation(textView, Gravity.CENTER, 0, 0);
			             TextView popuptext = (TextView)popupView.findViewById(R.id.popuptext);
			             popupWindow.setBackgroundDrawable(new ShapeDrawable());
			             
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
							  popupOpened =false;
						      popupWindow.dismiss();
						     }});
			             
			             
			             btnClean.setOnClickListener(new Button.OnClickListener(){
			
						     @Override
						     public void onClick(View v) {
						      // TODO Auto-generated method stub
						     	write("log", "This is a Log File");
						     	write("justdata", "This is a Log File");
								write("ontLoader", "This is a Log File");
					    		write("Results", "This is a Log File");
					    		write("ReasonerTime", "This is a Log File");
					    		write("LoaderTime", "This is a Log File");
					    		write("PowerReasoner", "This is a Log File");
					    		write("PowerLoader", "This is a Log File");
							    popupOpened =false;
						        popupWindow.dismiss();

						     }});
			               		             
						             popupWindow.showAsDropDown(btnOpenPopup, 50, -30);
						         
						             popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			
						            	    @Override
						            	    public void onDismiss() {
											    popupOpened =false;
						            	    	popupWindow.dismiss();
						            	    	
						            	        // end may TODO anything else                   
						            	    }
						            	});
						             
						             
						   }
			   }
   });	
		
		//Creates DropDown menu 

	    spinnerState = (Spinner) findViewById(R.id.spinnerstate);

	    ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
	            android.R.layout.simple_spinner_item, DatasetSize);
	    adapter_state
	            .setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
	    spinnerState.setAdapter(adapter_state);
	    
		//Creates second DropDown menu 


	    spinnerState2 = (Spinner) findViewById(R.id.spinnerstate2);

	    ArrayAdapter<String> adapter_state2 = new ArrayAdapter<String>(this,
	            android.R.layout.simple_spinner_item, ReasoningTask);
	    adapter_state2
	            .setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
	    spinnerState2.setAdapter(adapter_state2);
	    spinnerState2.setOnItemSelectedListener(new OnItemSelectedListener() 
	    {
	        @Override
	        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
	        {
	        	 
	     	    
	     	    spinnerState2.setSelection(position);
	     	    SelectedQuery = spinnerState2.getSelectedItem().toString();
	     	   
	        }

	        @Override
	        public void onNothingSelected(AdapterView<?> parentView) 
	        {
	        }
	    });



	    spinnerState.setOnItemSelectedListener(new OnItemSelectedListener() 
	    {
	        @Override
	        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
	        {
	        	spinnerState.setSelection(position);
	        	int pos = spinnerState.getSelectedItemPosition();
	        	SelectedOntologyName = spinnerState.getSelectedItem().toString();
	        	SelectedOntologyFile = DatasetSizeToSend[pos];
	        }

	        @Override
	        public void onNothingSelected(AdapterView<?> parentView) 
	        {
	        }
	    });


	

	
		//Button that creates the intent to other preinstalled application
	    //in this case Hermit reasoner.
		
		buttonHermit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("com.example.hermitowlapi", "com.example.hermitowlapi.MainActivity"));
		        intent.putExtra("ontologyName", SelectedOntologyName);
				intent.putExtra("ontologyFile", SelectedOntologyFile);
		        intent.putExtra("queryName", SelectedQuery);
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
				intent.putExtra("ontologyName", SelectedOntologyName);
				intent.putExtra("ontologyFile", SelectedOntologyFile);
				intent.putExtra("queryName", SelectedQuery);
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
				intent.putExtra("ontologyName", SelectedOntologyName);
				intent.putExtra("ontologyFile", SelectedOntologyFile);
				intent.putExtra("queryName", SelectedQuery);
		        startActivityForResult(intent,90);
			}
		});
		//Button that sets the drain variable to 0.
		buttonNull.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				reasonerState=1;
				Simulate(createPermutation());
			}
		});	
		buttonStartControl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startCountingTime = System.currentTimeMillis();
					getVoltage();
					start();
				}
		});	
		buttonStopControl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stop();
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
	
	/**
	 * Battery method that reads the battery 
	 * information and does some records and calculations 	  
	 *            
	 * @return float draw that is curent in mA flowing from the 
	 * battery at the moment.
	 
	 */
	public  float bat(){		
				BatteryManager mBatteryManager =
						(BatteryManager)getSystemService(Context.BATTERY_SERVICE);
						Long energy =
						mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);					
				float currentdraw = energy;
				draw = currentdraw;

		return draw;
	}
	
	
	/**
	 * Starts timer that registers current flow in mA of battery
	 * and transforms it to mAh.
	 */	
	public void start() {
	    if(timer != null) {
	        return;
	    }
	    timer = new Timer();	   
	    timer.schedule(new TimerTask() {
	        public void run() {	            
	        	final float curret =bat();
	        	drained =drained +(curret/3300);//3300s instead 3600s because after calculations there 
	        	//were some error rate determined and diviation from 3300 covers the loss of data that
	        	//was missed to be recorded. Calculated by measuring amount of current drained per 1% and finding 
	        	//the constant that derives 31mah
	        	watts = (float) ((drained*mvoltage/1000)*3.6);

	        	runOnUiThread(new Runnable() {

	        		
	        	    @Override
	        	    public void run() {
	    				stopCountingTime = System.currentTimeMillis()-startCountingTime;	
	    				float timeElapsed = (float) (stopCountingTime/1000.0);
		        		((TextView)findViewById(R.id.textView)).setText("Capacity drained = " + drained + "mAh \n"+ 
		        			"Time elapsed : " +timeElapsed + "s\n"+"Power consumed: "+watts+"W"
		        					);
	        	            }
	        	    });	        	
	       }
	   }, 0, 1000 );
	}

	public void stop() {
		if(timer!=null){
			unregisterReceiver(batteryInfoReceiver);
			drained=0;
			timer.cancel();
			timer = null;
			drained=0;
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
	
	
	

	//File writter
	public void write(String fname, String fcontent){
	        String filename= "storage/emulated/0/Download/"+fname+".txt";
	        String temp="";
	        //check if file need to bempty or not by looking at the first line 
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
	   @SuppressWarnings("resource")
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
		   
		   stack.push("pellet");
		   stack.push("androjena");
		   stack.push("hermit");
		   
		   stack.push("pellet");
		   stack.push("hermit");
		   stack.push("androjena");

		   stack.push("hermit");
		   stack.push("androjena");
		   stack.push("pellet");
		   
		   stack.push("hermit");
		   stack.push("pellet");
		   stack.push("androjena");
		   return stack;
	   }
	   
	   public void Simulate(Stack<String> s){
		    final Stack<String> stack = s;
		   
		   
		   if(!stack.isEmpty()&&reasonerState!=-1){
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
	   
   public void getVoltage(){
       batteryInfoReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {			
				mvoltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);				
			}
		};
		registerReceiver(this.batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}
   /**
    * Method created the pop up dialog asking if user
    * wants really quite and application.
    */
   @Override
   public void onBackPressed() {
	   if(popupWindow!=null){
   	  	
		   if(popupWindow.isShowing()){
			   popupOpened =false;
			   popupWindow.dismiss();	 
		   }else{
			   callExitDialog(); 
		   }
	   }else{
		   callExitDialog();
	   }
	}
   public void callExitDialog(){
 		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.customexit);			
		dialog.setTitle("PowerBenchMark");
		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.text);
		text.setText("Are you sure you want");
		TextView text2 = (TextView) dialog.findViewById(R.id.text2);
		text2.setText("to EXIT?");
		ImageView image = (ImageView) dialog.findViewById(R.id.image);
		image.setImageResource(R.drawable.exit); 
		Button dialogButton = (Button) dialog.findViewById(R.id.btnok);
		Button dialogButton2 = (Button) dialog.findViewById(R.id.btncancel);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
        		dialog.dismiss();             		
			}
		});
		
		dialogButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});

		dialog.show();
	   
   }
   
}
