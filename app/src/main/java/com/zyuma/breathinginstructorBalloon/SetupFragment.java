package com.zyuma.breathinginstructorBalloon;

import java.util.ArrayList;

import com.zyuma.breathinginstructor3.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class SetupFragment extends Fragment {

	private final static String MONITOR_FRAGMENT = "MONITOR_FRAGMENT";
	private static final String DURATION = "duration";
	private static final String BPM = "bpm";
	private static final int FIVE_TWO_FIVE = 1;
	private static final int BPM_BASED = 2;
	
	private TextView mTitle;
	private TextView mDesc1;
	private TextView mLastBPMText;
	private TextView mDesc2;
	private TextView mDesc3;
	private TextView m525Desc;
	private Spinner mDurationSpinner;
	private Spinner mExerciseSpinner;
	private int mExerciseType;
	private Spinner mBPMSpinner;
	private double mLastBPM;
	
	private Button mNextButton;
	private int mDuration;
	private double mChosenBPM;
	
	private ArrayList<BreathingData> mDataList;
	private String[] mExerciseList;
	private String[] mBPMList;
	
	private double mRecommendedBPM;
	private int mPosition;

	public static SetupFragment newInstance() {
		SetupFragment f = new SetupFragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_setup, container, false);

		mTitle = (TextView) view.findViewById(R.id.title);
		mTitle.setTypeface(MainActivity.lobster_font);
		
		mDesc1 = (TextView) view.findViewById(R.id.duration);
		mDesc1.setTypeface(MainActivity.blokletters_balpen_font);
		
		mDesc2 = (TextView) view.findViewById(R.id.exercise);
		mDesc2.setTypeface(MainActivity.blokletters_balpen_font);
		
		m525Desc = (TextView) view.findViewById(R.id.desc525);
		m525Desc.setTypeface(MainActivity.blokletters_balpen_font);
		
		mDesc3 = (TextView) view.findViewById(R.id.bpm);
		mDesc3.setTypeface(MainActivity.blokletters_balpen_font);
		
		
		
		mLastBPMText = (TextView) view.findViewById(R.id.previous);
		mLastBPMText.setTypeface(MainActivity.blokletters_balpen_font);
		
		// Duration spinner
		mDurationSpinner = (Spinner) view.findViewById(R.id.duration_spinner);
		ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter
				.createFromResource(getActivity(), R.array.duration_array,
						android.R.layout.simple_spinner_item);
		durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mDurationSpinner.setAdapter(durationAdapter);
		mDurationSpinner.setSelection(2);
		mDurationSpinner
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						switch (pos) {
						case 0:
							mDuration = 15000;
							break;
						case 1:
							mDuration = 30000;
							break;
						case 2:
							mDuration = 60000;
							break;
						case 3:
							mDuration = 120000;
							break;
						case 4:
							mDuration = 180000;
							break;
						}
					}

					public void onNothingSelected(AdapterView<?> parent) {
						mDuration = 60000;
					}
				});

		// Exercise Spinner
		mExerciseType = BPM_BASED;
		mExerciseList = new String[] { "BPM-based exercise", "5-2-5 exercise" };
		mExerciseSpinner = (Spinner) view.findViewById(R.id.exercise_spinner);
		ArrayAdapter<String> exerciseAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, mExerciseList);
		mExerciseSpinner.setAdapter(exerciseAdapter);
		mExerciseSpinner.setSelection(0);
		mExerciseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				switch (pos) {
				case 0:
					mExerciseType = BPM_BASED;
					mBPMSpinner.setVisibility(View.VISIBLE);
					mLastBPMText.setVisibility(View.VISIBLE);
					mDesc3.setVisibility(View.VISIBLE);
					m525Desc.setVisibility(View.GONE);
					break;
				case 1:
					mExerciseType = FIVE_TWO_FIVE;
//					((Spinner) mBPMSpinner).getSelectedView().setEnabled(false);
//					mBPMSpinner.setEnabled(false);
					mBPMSpinner.setVisibility(View.GONE);
					mLastBPMText.setVisibility(View.GONE);
					mDesc3.setVisibility(View.GONE);
					m525Desc.setVisibility(View.VISIBLE);
					break;
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				mExerciseType = BPM_BASED;
				mBPMSpinner.setVisibility(View.VISIBLE);
				mLastBPMText.setVisibility(View.VISIBLE);
				mDesc3.setVisibility(View.VISIBLE);
			}
		});
		
		// BPM Spinner
		mDataList = MainActivity.mDataList;
		if (mDataList.size() != 0)
			mLastBPM = mDataList.get(mDataList.size() - 1).getBPM();
		else
			mLastBPM = 0;

		BPMRecommender(mLastBPM);

		mBPMSpinner = (Spinner) view.findViewById(R.id.BPM_spinner);

		ArrayAdapter<String> BPMAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, mBPMList);

		BPMAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mBPMSpinner.setAdapter(BPMAdapter);

		mBPMSpinner.setSelection(mPosition);
		mBPMSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				mChosenBPM = 10.0 - pos;
			}

			public void onNothingSelected(AdapterView<?> parent) {
				mChosenBPM = 10.0 - mPosition;
			}
		});

		if (mLastBPM != 0)
			mLastBPMText.setText("Last time: " + mLastBPM + " BPM");
		else
			mLastBPMText.setText("This is your first time!");

		mNextButton = (Button) view.findViewById(R.id.nextbutton);
		mNextButton.setTypeface(MainActivity.lobster_font);
		mNextButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				FragmentTransaction fm = getFragmentManager().beginTransaction();
				fm.replace(R.id.container, BreathingMonitorFragment.newInstance(mDuration, mExerciseType, mChosenBPM), MONITOR_FRAGMENT);
				fm.addToBackStack(null).commit();
			}
		});

		return view;
	}

	public void BPMRecommender(double lastBPM) {
		mBPMList = new String[] { "10 BPM", "9 BPM", "8 BPM", "7 BPM", "6 BPM",
				"5 BPM", "4 BPM", "3 BPM" };
		if (0 < lastBPM && lastBPM <= 4) {
			mRecommendedBPM = 3;
			mPosition = 7;
		} else if (4 < lastBPM && lastBPM <= 5) {
			mRecommendedBPM = 4;
			mPosition = 6;
		} else if (5 < lastBPM && lastBPM <= 6) {
			mRecommendedBPM = 5;
			mPosition = 5;
		} else if (6 < lastBPM && lastBPM <= 7) {
			mRecommendedBPM = 6;
			mPosition = 4;
		}

		else if (7 < lastBPM && lastBPM <= 8) {
			mRecommendedBPM = 7;
			mPosition = 3;
		} else {
			mRecommendedBPM = 8;
			mPosition = 2;
		}

		mBPMList[mPosition] += " (Recommended)";
	}
}
