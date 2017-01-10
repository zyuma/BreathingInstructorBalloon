package com.zyuma.breathinginstructorBalloon;

import java.util.ArrayList;

import com.zyuma.breathinginstructor3.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class ExerciseActivity extends Activity {
	
	private final static String MONITOR_FRAGMENT = "MONITOR_FRAGMENT";
	
	public static ArrayList<BreathingData> mDataList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		getFragmentManager().beginTransaction().add(R.id.container, SetupFragment.newInstance()).commit();
	}
	
	@Override
	public void onBackPressed() {
	    if (getFragmentManager().getBackStackEntryCount() == 2 || getFragmentManager().getBackStackEntryCount() == 0) {
	        this.finish();
	    } 
	    else if (getFragmentManager().getBackStackEntryCount() == 1) {
	    	BreathingMonitorFragment f = (BreathingMonitorFragment) getFragmentManager().findFragmentByTag(MONITOR_FRAGMENT);
	    	f.onBackPressed();
	    	super.onBackPressed();
	    }
	    else {
	        getFragmentManager().popBackStack();
	    }
	}
	

	
}
