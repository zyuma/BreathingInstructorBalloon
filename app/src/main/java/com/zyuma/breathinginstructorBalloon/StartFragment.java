package com.zyuma.breathinginstructorBalloon;

import com.zyuma.breathinginstructor3.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StartFragment extends Fragment implements OnClickListener {

	private TextView mTitle;
	private Button mHistoryButton;
	private Button mStartButton;
	private Button mInstrButton;
	private Button mExitButton;
	
	public static StartFragment newInstance() {
		StartFragment f = new StartFragment();
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_start, container, false);
		
		mTitle = (TextView) view.findViewById(R.id.title);
		mTitle.setTypeface(MainActivity.lobster_font);
		mHistoryButton = (Button) view.findViewById(R.id.historybutton);
		mHistoryButton.setTypeface(MainActivity.lobster_font);
		mHistoryButton.setOnClickListener(this);
		mStartButton = (Button) view.findViewById(R.id.nextbutton);
		mStartButton.setTypeface(MainActivity.lobster_font);
		mStartButton.setOnClickListener(this);
		mInstrButton = (Button) view.findViewById(R.id.instructionbutton);
		mInstrButton.setTypeface(MainActivity.lobster_font);
		mInstrButton.setOnClickListener(this);
		mExitButton = (Button) view.findViewById(R.id.exitbutton);
		mExitButton.setTypeface(MainActivity.lobster_font);
		mExitButton.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		
		FragmentTransaction fm = getFragmentManager().beginTransaction();
		
		switch (v.getId()) {
		
		case R.id.historybutton:

		    fm.replace(R.id.container, HistoryFragment.newInstance());
		    fm.addToBackStack(null).commit();
			break;
			
		case R.id.instructionbutton:

		    fm.replace(R.id.container, InstructionFragment.newInstance());
		    fm.addToBackStack(null).commit();
			break;

		case R.id.nextbutton:

			Intent i = new Intent(getActivity(), ExerciseActivity.class);
			startActivity(i);
			break;
			
		case R.id.exitbutton:

			getActivity().finish();
			break;
		}
	}
}
