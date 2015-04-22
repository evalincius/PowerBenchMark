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
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ShapeDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
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

/**
 * Class PowerBenchmark extends the ActionBar Activity.
 * Its purpose is to create the user interface so the reasoners can be launched from
 * one main window. Moreover, this class implements the feature of selecting the dataset size
 * and the resoning task from the dropDown menu. 
 * It also has control test implemented which measures the power consumption.
 * 
 * @author  Edgaras Valincius
 * @version 1.0
 * @since   2015-02-19 
 */
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
	private String[] DatasetSize = { "1 Department","5 Departments","10 Departments","15 Departments" };
	private String[] ReasoningTask = { "Instance Retrieval","Inference & Instance Retrieval","Inference","Classification" };	
	private String[] DatasetSizeToSend = { "University00.owl","University05.owl","University010.owl","University015.owl" };
	Spinner spinnerState, spinnerState2;
	TextView tvState;
	TextView tvState2;
	
	/**
	 * onCreate is used to initialize activity. 
	 * In this method there are all main buttons and dropDown
	 * menus are initialized as well.
	 */ 
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
		
		/**
		 * This is a button that launches the PopUp Window,  which inspects the results.
		 */ 
		final Button btnOpenPopup = (Button)findViewById(R.id.openpopup);
        btnOpenPopup.setOnClickListener(new Button.OnClickListener(){       	
			   @SuppressLint("InflateParams")
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
							popuptext.setText(read("log"));
			             } catch (Exception e) {
							e.printStackTrace();
			             }
			             Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
			             Button btnClean = (Button)popupView.findViewById(R.id.clean);
			             
			             /**
			     		 * This is a button for the PopUp Window,  which closes 
			     		 * the popUp window.
			     		 */
			             btnDismiss.setOnClickListener(new Button.OnClickListener(){			
						     @Override
						     public void onClick(View v) {
							  popupOpened =false;
						      popupWindow.dismiss();
						     }
						 });
			             
			             /**
			              * This is a button for the PopUp Window,  which clears  
			              * the content of each file by overriding it by initial text. 
			              * To do that it calls the file Writer.
			              */
			             btnClean.setOnClickListener(new Button.OnClickListener(){			
						     @Override
						     public void onClick(View v) {
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

						     }
						  });			               		             
						  popupWindow.showAsDropDown(btnOpenPopup, 50, -30);
						  // Reactive to back Button,
						  // if pressed, window is closed.
						  popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
						        @Override
						        public void onDismiss() {
									popupOpened =false;
						            popupWindow.dismiss();
						        }
						   });
					}
			   }
        });	
		
		//Creates First DropDown menu 
	    spinnerState = (Spinner) findViewById(R.id.spinnerstate);
	    ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DatasetSize);
	    adapter_state.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
	    spinnerState.setAdapter(adapter_state);
	    
		//Creates Second DropDown menu 
	    spinnerState2 = (Spinner) findViewById(R.id.spinnerstate2);
	    ArrayAdapter<String> adapter_state2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ReasoningTask);
	    adapter_state2.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
	    spinnerState2.setAdapter(adapter_state2);
	    
	    // Remembers Second DropDown menu's selection
	    spinnerState2.setOnItemSelectedListener(new OnItemSelectedListener() {
	        @Override
	        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){	     	    
	     	    spinnerState2.setSelection(position);
	     	    SelectedQuery = spinnerState2.getSelectedItem().toString();	     	   
	        }
	        @Override
	        public void onNothingSelected(AdapterView<?> parentView) {
	        	//Does nothing
	        }
	    });


		// Remembers First DropDown menu's selection
	    spinnerState.setOnItemSelectedListener(new OnItemSelectedListener() {
	        @Override
	        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id){
	        	spinnerState.setSelection(position);
	        	int pos = spinnerState.getSelectedItemPosition();
	        	SelectedOntologyName = spinnerState.getSelectedItem().toString();
	        	SelectedOntologyFile = DatasetSizeToSend[pos];
	        }
	        @Override
	        public void onNothingSelected(AdapterView<?> parentView) {
	        	//does nothing
	        }
	    });
	    
	    /**
         * Button that creates the intent to other pre-installed application
         * in this case Hermit reasoner.
         * Adds extras into intent so reasoner launched will know the selections of 
         * dataset and reasoning task user selected. 
         */
		buttonHermit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("com.example.hermitowlapi", "com.example.hermitowlapi.MainActivity"));
		        intent.putExtra("ontologyName", SelectedOntologyName);
				intent.putExtra("ontologyFile", SelectedOntologyFile);
		        intent.putExtra("queryName", SelectedQuery);
		        startActivityForResult(intent,90);
			}
		});
		
		/**
         * Button that creates the intent to other pre-installed application
         * in this case Pellet reasoner.
         * Adds extras into intent so reasoner launched will know the selections of 
         * dataset and reasoning task user selected. 
         */
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
		
		/**
         * Button that creates the intent to other pre-installed application
         * in this case AndroJena reasoner.
         * Adds extras into intent so reasoner launched will know the selections of 
         * dataset and reasoning task user selected. 
         */
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
		
		/**
         * Button that launches the simulation method.
         */
		buttonNull.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				reasonerState=1;
				Simulate(createPermutation());
			}
		});	
		
		/**
         * Button that launches the control test.
         */
		buttonStartControl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startCountingTime = System.currentTimeMillis();
					getVoltage();
					start();
				}
		});
		
		/**
         * Button that stops the control test by calling stop method.
         */
		buttonStopControl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stop();
			}
		});	
	}
	
	/**
	 * Standard Android onPause method. 
	 */
	public void onPause() {
	    super.onPause();
	}
	
	/**
	 * Standard Android onStop method. 
	 */
	public void onStop() {
	    super.onStop();
	}
	
	/**
	 * Standard Android onResume method. 
	 * App is resumed after pause with no additional tasks.	 
	 */
	public void onResume(){
	    super.onResume();
	}
	
	/**
	 * Battery method bat() reads the battery 
	 * information and return the current flow of the battery.	             
	 * @return float draw that is current in mA flowing from the 
	 * battery at the moment.	 
	 */
	public  float bat(){		
		BatteryManager mBatteryManager =(BatteryManager)getSystemService(Context.BATTERY_SERVICE);
		Long energy =mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);					
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
	    	
	    	/**
	    	* Timer calls method run every second. 
	    	* Within run, power consumption is calculated.
	    	* Method invokes
	    	*/	
	        public void run() {	            
	        	final float curret =bat();
	        	
	        	/**
		    	* 3300s instead 3600s because after calculations there 
		    	* were some error rate determined and divided from 3300 covers the loss of data that
		    	* was missed to be recorded. Calculated by measuring amount of current drained per 1% and finding 
		    	* the constant that derives 31mah.
		    	*/	
	        	drained =drained +(curret/3300);
	        	
	        	/**
		    	* Watts drained were calculated by following formula W=I*V
		    	* (watt= current * voltage). Since voltage was measured in miliVolts, the equation had to
		    	* be divided from 1000 to get the SI units. In case below, it was also multiplied by time,
		    	* so was converted back to Watts instead of watt/hours.
		    	*/	
	        	watts = (float) ((drained*mvoltage/1000)*3.6);
	        	runOnUiThread(new Runnable() {	        		
	        	    @Override
	        	    public void run() {
	    				stopCountingTime = System.currentTimeMillis()-startCountingTime;	
	    				float timeElapsed = (float) (stopCountingTime/1000.0);
		        		((TextView)findViewById(R.id.textView)).setText("Capacity drained = " + drained + "mAh \n"+ 
		        		"Time elapsed : " +timeElapsed + "s\n"+"Power consumed: "+watts+"W");
	        	     }
	        	 });   	
	       }
	   }, 0, 1000 );
	}
	
	/**
	*Stops the previously launched Timer. Sets variable
	*drained to be 0 so the control test can be started again.
	*/
	public void stop() {
		if(timer!=null){
			unregisterReceiver(batteryInfoReceiver);
			drained=0;
			timer.cancel();
			timer = null;
			drained=0;
		}
	}

	/**
	* File Writer method writes the desired content into the file.
	* If there is no such a file, generates it.
	* @param  fname is a name for a file.
	* @param  fcontent is a content for a file. 
	*/
	public void write(String fname, String fcontent){
        String filename= "storage/emulated/0/Download/"+fname+".txt";
        String temp="";
        //check if file need to be empty or not by looking at the first line 
        if(!fcontent.equals("This is a Log File")){
        	temp = read(fname);
        }
        BufferedWriter writer = null;
        try {
            File logFile = new File(filename);
            System.out.println(logFile.getCanonicalPath());
            writer = new BufferedWriter(new FileWriter(logFile));            
            writer.write(temp + fcontent );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            }catch (Exception e) {
            }
        }
	}
	
	/**
	* File Reader method reads the content from the file.
	* @param  fname is a name for a file.
	* @return  response is a content that is read from the file.
	*/
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
	
		/**
		* createPermutation() method created the permutation of 3 reasoners,
		* and puts them to the stack.
		* @return Stack<String> stack.
		*/
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
	   
	/**
	* This method creates the simulation test for 3 reasoners.
	* It launches reasoners in respect to the permutation. 
	* @param Stack<String> s is a stack required for launching the simulation.
	*/
	public void Simulate(Stack<String> s){
	   final Stack<String> stack = s;		   
	   if(!stack.isEmpty()&&reasonerState!=-1){
		   // Checks if the reasoner is finished its task.
		   // 1 means it is FINISHED
		   // -1 means it was CANCELED by used, so simulations needs to be canceled as well.
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
		   
		   /**
			* Method invokes simulation every 1s to check if the previous task is
			* finished so every reasoner is launched in a row, one after another.
			*/
		   new Handler().postDelayed(new Runnable() {
		        @Override
		        public void run() {
		        	Simulate(stack);
		        }
		    }, 1000);
		   
	   }		
	}
	   
	/**
	 * Returns results from intent activity (reasoner that was called) to 
	 * get the information if the reasoner is finished its task or not.
	 */
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
	 /**
	  * Method records voltage. To do that it has to register the BroadcastReceiver,
	  * and every time the state of voltage changes, it records the resigns the mvoltage variable
	  * with the latest voltage measured in miliVolts.
	  */   
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
	  * Method reacts to the back button. If it is pressed,
	  * the popup window, if opened, is closed or the exit dialog is invoked. 
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
	 /**
	  * Method created the pop up dialog asking if user
	  * wants really quite an application.
	  */
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
			/**
			 * Button closes the app if pressed.
			 */		
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
	        		dialog.dismiss();             		
				}
			});
			/**
			 * Button closes just the dialog so the app remains opened.
			 */
			dialogButton2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();				
				}
			});
			dialog.show();
		   
	 }
   
}
