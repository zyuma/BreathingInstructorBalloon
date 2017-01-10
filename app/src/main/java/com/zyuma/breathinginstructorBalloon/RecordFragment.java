package com.zyuma.breathinginstructorBalloon;

import com.zyuma.breathinginstructor3.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RecordFragment extends Fragment implements OnClickListener {

	public final static String DATA = "data";
	
	private BreathingData mRecord;
	private TextView mTitle;
	private TextView mTime;
	private TextView mBreathCount;
	private TextView mDuration;
	private TextView mBPM;
	private Button mBackButton;
	
	public static RecordFragment newInstance(BreathingData d) {
		RecordFragment f = new RecordFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(DATA, d);
		f.setArguments(bundle);
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_record, container, false);
		
		mRecord = (BreathingData) getArguments().getSerializable(DATA);
		
		mTitle = (TextView) view.findViewById(R.id.title);
		mTitle.setTypeface(MainActivity.lobster_font);
		
		mTime = (TextView) view.findViewById(R.id.time);
		mTime.setTypeface(MainActivity.blokletters_balpen_font);
		mTime.setText("Date and Time:\n" + mRecord.getTime());
		
		mBreathCount = (TextView) view.findViewById(R.id.count);
		mBreathCount.setTypeface(MainActivity.blokletters_balpen_font);
		mBreathCount.setText("Number of breaths took:\n" + mRecord.getBreathCount());
		
		mDuration = (TextView) view.findViewById(R.id.duration);
		mDuration.setTypeface(MainActivity.blokletters_balpen_font);
		mDuration.setText("Duration of exercise:\n" + mRecord.getDuration()/1000 + " seconds");
		
		mBPM = (TextView) view.findViewById(R.id.bpm);
		mBPM.setTypeface(MainActivity.blokletters_balpen_font);
		mBPM.setText("BPM:\n" + mRecord.getBPM());
		
		mBackButton = (Button) view.findViewById(R.id.backbutton);
		mBackButton.setTypeface(MainActivity.lobster_font);
		mBackButton.setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {
		FragmentTransaction fm = getFragmentManager().beginTransaction();

		switch (v.getId()) {
		case R.id.backbutton:

			fm.replace(R.id.container, HistoryFragment.newInstance()).commit();
			break;
		}
	}
}
