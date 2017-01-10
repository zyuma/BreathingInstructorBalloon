package com.zyuma.breathinginstructorBalloon;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.zyuma.breathinginstructor3.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class ResultsFragment extends Fragment implements
	SeekBar.OnSeekBarChangeListener{

	private static final String DURATION = "duration";
	private static final String EXERCISE = "exercise";
	private static final String BREATHNUM = "breathnum";
	private static final int FIVE_TWO_FIVE = 1;
	private static final int BPM_BASED = 2;
	
	private View mView;
	private TextView mTitle;
	private TextView mCongratz;
	private TextView mResultText;
	private TextView mDesc;
	private Button mNextButton;
	private int mExercise;
	private int mDuration;
	private int mBreathNum;
	private double mBPM;
	private int mRating;
	private SeekBar mBar;
	private TextView mRatingText;
	private boolean flag_savingTaskRunning = false;
	
	public static ResultsFragment newInstance(int exercise, int duration, int breathNum) {
		ResultsFragment f = new ResultsFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(EXERCISE, exercise);
		bundle.putInt(DURATION, duration);
		bundle.putInt(BREATHNUM, breathNum);
		f.setArguments(bundle);
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Bundle bundle = this.getArguments();
		mExercise = bundle.getInt(EXERCISE);
		mDuration = bundle.getInt(DURATION);
		mBreathNum = bundle.getInt(BREATHNUM);
		mBPM = mBreathNum/(mDuration/60000.0);
		
//		if (mExercise == FIVE_TWO_FIVE)
//			mView = inflater.inflate(R.layout.fragment_results_525, container, false);
//		else if(mExercise == BPM_BASED)
			mView = inflater.inflate(R.layout.fragment_results_bpm, container, false);
		
		mTitle = (TextView) mView.findViewById(R.id.title);
		mTitle.setTypeface(MainActivity.lobster_font);
		
		mCongratz = (TextView) mView.findViewById(R.id.congratulate);
		mCongratz.setTypeface(MainActivity.blokletters_balpen_font);
		
		mResultText = (TextView) mView.findViewById(R.id.result);
		mResultText = (TextView) mView.findViewById(R.id.result);
		mResultText.setTypeface(MainActivity.blokletters_balpen_font);
		mResultText.setText(mBPM + " BPM");
		mResultText.setVisibility(View.GONE);
		
		mDesc = (TextView) mView.findViewById(R.id.desc);
		mDesc.setTypeface(MainActivity.blokletters_balpen_font);
		mDesc.setVisibility(View.GONE);
		
		if(mExercise == BPM_BASED) {
			mResultText.setVisibility(View.VISIBLE);
			mDesc.setVisibility(View.VISIBLE);
		}
		
		mDesc = (TextView) mView.findViewById(R.id.desc);
		mDesc.setTypeface(MainActivity.blokletters_balpen_font);

		mRatingText = (TextView) mView.findViewById(R.id.rating);
		mRatingText.setTypeface(MainActivity.blokletters_balpen_font);
		
		mRating = 5;
		mBar = (SeekBar) mView.findViewById(R.id.bar);
		mBar.setMax(9);
		mBar.setProgress(5-1);
		mRatingText.setText("" + 5);
		mBar.setOnSeekBarChangeListener(this);
		
		
		mNextButton = (Button) mView.findViewById(R.id.nextbutton);
		mNextButton.setTypeface(MainActivity.lobster_font);
		mNextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	String dateTime = getDateTime(); 
            	MainActivity.mDataList.add(new BreathingData(mDuration, mBreathNum, mBPM, mRating, dateTime));
            	Log.i("DATE: ", dateTime);
            	if (!flag_savingTaskRunning) {
    				flag_savingTaskRunning = true;
    				new SaveToDatabaseTask((ExerciseActivity) getActivity()).execute();
    			}
            }
        });
		
		return mView;
	}

	private class SaveToDatabaseTask extends AsyncTask<Void, Void, Void> {

		private ExerciseActivity activity;
		private AlertDialog dialog;

		public SaveToDatabaseTask(ExerciseActivity activity) {
			super();
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("Saving");
			builder.setView(new ProgressBar(activity));
			builder.setCancelable(false);
			if(dialog == null) {
				dialog = builder.create();
			}
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			BPMDataHelper helper = new BPMDataHelper(activity);
			helper.setBPMToDatabase(MainActivity.mDataList);
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			dialog.dismiss();
			getActivity().finish();
		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		progress += 1;
		mRatingText.setText("" + progress);
		mRating = progress;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
	
	public String getDateTime() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
		return sdf.format(c.getTime());
	}
}
