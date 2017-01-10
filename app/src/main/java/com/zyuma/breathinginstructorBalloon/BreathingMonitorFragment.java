package com.zyuma.breathinginstructorBalloon;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.zyuma.breathinginstructor3.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.NoiseSuppressor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BreathingMonitorFragment extends Fragment {

	private View mView;
	private static final String DURATION = "duration";
	private static final String EXERCISE = "exercise";
	private static final String BPM = "bpm";
	private static final int FIVE_TWO_FIVE = 1;
	private static final int BPM_BASED = 2;
	
	private TextView mTitle;
	
	private static final int sampleRate = 8000;
	private static final int INHALE = 5;
	private static final int HOLD = 7;
	private static final int EXHALE = 6;
	private AudioRecord audio;
	private int bufferSize;
	private double lastLevel = 0;
	private double lastLevelLPF = 0;
	private Thread thread;
	private static final long SAMPLE_DELAY = 1 / sampleRate;
	private TextView classificationText;
	private TextView timerText;
	private boolean isExhaling = false;
	private double[] lastTwoReadings;
	private double alpha;
	
	private int mStatus = INHALE;
	private int mStatusCounter;
	private String msg;
	
	public CountDownTimer mTimer;
	private double mExhaleLength;
	
	private ChartTask chartUpdate;
	private int mTarget;
	private int mThreshold = 150;
	
	private int mDuration;
	private int mExercise;
	private double mTargetBPM;
	private int mBreathNum;

	private XYSeries mSampleSeries;
	private XYSeries mLPFSeries;
	private XYSeries mTargetSeries;
	private XYMultipleSeriesDataset dataset;
	private XYSeriesRenderer mSampleRenderer;
	private XYSeriesRenderer mLPFRenderer;
	private XYSeriesRenderer mTargetRenderer;
	private XYMultipleSeriesRenderer multiRenderer;
	private GraphicalView mChart;
	
	
	private ImageView mBalloonImg;
	private ImageView mOutlineImg;
	private Bitmap mBalloonBmp;
	private Bitmap mOutlineBmp;
	
	private float smallestSize = 0.15f;
	private float initialSize = 0.15f;
	private float nextSize = 0.2f;
	private float biggestSize = 0.8f;

	public static BreathingMonitorFragment newInstance(int duration, int exercise, double bpm) {
		BreathingMonitorFragment f = new BreathingMonitorFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(DURATION, duration);
		bundle.putInt(EXERCISE, exercise);
		bundle.putDouble(BPM, bpm);
		f.setArguments(bundle);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_breathing_monitor,
				container, false);

		Bundle bundle = this.getArguments();
		mDuration = bundle.getInt(DURATION);
		mExercise = bundle.getInt(EXERCISE);
		mTargetBPM = bundle.getDouble(BPM);
		
		mTitle = (TextView) mView.findViewById(R.id.title);
		mTitle.setTypeface(MainActivity.lobster_font);
		classificationText = (TextView) mView.findViewById(R.id.classification);
		classificationText.setTypeface(MainActivity.blokletters_balpen_font);
		timerText = (TextView) mView.findViewById(R.id.timer);
		timerText.setTypeface(MainActivity.blokletters_balpen_font);

		mStatus = INHALE;
		mStatusCounter = 5;
		msg = "Inhale";
		mExhaleLength = 60/(mTargetBPM*2);
		
		
		try {
			bufferSize = AudioRecord
					.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
							AudioFormat.ENCODING_PCM_16BIT);
		} catch (Exception e) {
			android.util.Log.e("TrackingFlow", "Exception", e);
		}

		return mView;
	}

	public void onResume() {
		super.onResume();
		audio = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
				bufferSize);
		
		Options options = new BitmapFactory.Options();
	    options.inScaled = false;
	    mBalloonBmp = BitmapFactory.decodeResource(getResources(), R.drawable.balloon, options);
	    mOutlineBmp = BitmapFactory.decodeResource(getResources(), R.drawable.balloon_outline, options);
		
	    mBalloonImg = (ImageView) mView.findViewById(R.id.balloon);
		mBalloonImg.setImageBitmap(mBalloonBmp);
		
		mOutlineImg = (ImageView) mView.findViewById(R.id.outline);
		mOutlineImg.setImageBitmap(mOutlineBmp);
		
		NoiseSuppressor.create(audio.getAudioSessionId());
		audio.startRecording();
		startListening();
		if (mExercise == BPM_BASED)
			startBPMTimer();
		else if(mExercise == FIVE_TWO_FIVE)
			start525Timer();
		
//		if(mChart == null) { 
//			setupChart();
//		}
//		else {
//			mChart.repaint();
//		}
//		
//		chartUpdate = new ChartTask();
//		chartUpdate.execute();
	}

	private void readAudioBuffer() {

		try {
			short[] buffer = new short[bufferSize];

			int bufferReadResult = 1;

			if (audio != null) {

				// Sense the voice...
				bufferReadResult = audio.read(buffer, 0, bufferSize);
				double sumLevel = 0;
				for (int i = 0; i < bufferReadResult; i++) {
					sumLevel += buffer[i];
				}
				lastLevel = Math.pow(sumLevel / bufferReadResult, 2);
				alpha = 0.08;
				lastLevelLPF = lowPassFilter(lastLevel);
				lastLevel = Math.sqrt(lastLevel);
				lastLevelLPF = Math.sqrt(lastLevelLPF);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double lowPassFilter(double input) {
		if (lastTwoReadings == null) {
			lastTwoReadings = new double[2];
			lastTwoReadings[0] = input;
			return lastTwoReadings[0];
		}
		lastTwoReadings[1] = lastTwoReadings[0] + alpha
				* (input - lastTwoReadings[0]);
		lastTwoReadings[0] = lastTwoReadings[1];
		return lastTwoReadings[1];
	}

	@Override
	public void onPause() {
		super.onPause();
		thread.interrupt();
		thread = null;
//		chartUpdate.cancel(true);
		try {
			if (audio != null) {
				audio.stop();
				audio.release();
				audio = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start525Timer() {
		Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				mTimer = new CountDownTimer(mDuration, 1000) {
					
					public void onTick(long millisUntilFinished) {
						
						int timeLeft = (int) millisUntilFinished/1000;
						timerText.setText(timeLeft + " sec left");
						
						mStatusCounter -= 1;
						
						if(mStatusCounter == 0) {
							if(mStatus == INHALE) {
								mStatus = HOLD;
								mStatusCounter = 2;
								//scaleAnimation(smallestSize, biggestSize, 2000);
								msg = "Hold";
								
							}
							else if(mStatus == HOLD) {
								
								mStatus = EXHALE;
								mStatusCounter = 5;
								scaleAnimation(smallestSize, biggestSize, 2000);
							    
								msg = "Exhale";
								
							}
							else if(mStatus == EXHALE) {
								
								mStatus = INHALE;
								mStatusCounter = 5;
								scaleAnimation(biggestSize, smallestSize, 2000);
								msg = "Inhale";
								
							}

						}
						
						classificationText.setText(msg + " for " + mStatusCounter + " more seconds");
					}

					public void onFinish() {
						
						FragmentTransaction fm = getFragmentManager().beginTransaction();
					    fm.replace(R.id.container, ResultsFragment.newInstance(mExercise, mDuration, mBreathNum));
					    fm.addToBackStack(null).commit();
						
					}
				}.start();
				classificationText.setText(msg + " for " + mDuration + " more seconds");
				scaleAnimation(biggestSize, smallestSize, 10);
			}
		};
		handler.post(runnable);
	}
	
	public void startBPMTimer() {
		Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				mTimer = new CountDownTimer(mDuration, 1000) {
					
					public void onTick(long millisUntilFinished) {
						
						int timeLeft = (int) millisUntilFinished/1000;
						
						timerText.setText(timeLeft + " sec left");
						
						Log.i("time left", ""+timeLeft);
						Log.i("exhale length", ""+mExhaleLength);
						Log.i("now", ""+timeLeft%mExhaleLength);
						if(Math.floor(timeLeft%mExhaleLength) == 0.0) {
							if(mStatus == EXHALE) {
								
								mStatus = INHALE;
							    scaleAnimation(biggestSize, smallestSize, 2000);
								msg = "Inhale";
								
							}
							else if(mStatus == INHALE) {
								
								mStatus = EXHALE;
								scaleAnimation(smallestSize, biggestSize, 2000);
								msg = "Exhale";
								
							}

						}
						int timeLeft2 = (int) (timeLeft%mExhaleLength);
						if(timeLeft2 == 0)
							timeLeft2 += (int) mExhaleLength;
						classificationText.setText(msg + " for " + timeLeft2 + " more sec");
					}

					public void onFinish() {
						
						FragmentTransaction fm = getFragmentManager().beginTransaction();
					    fm.replace(R.id.container, ResultsFragment.newInstance(mExercise, mDuration, mBreathNum));
					    fm.addToBackStack(null).commit();
						
					}
				}.start();
				classificationText.setText(msg + " for " + mDuration + " more seconds");
				scaleAnimation(biggestSize, smallestSize, 10);
			}
		};
		handler.post(runnable);
	}

	public void scaleAnimation(float initialSize, float finalSize, int duration) {
		AnimationSet animSet = new AnimationSet(true);
	    animSet.setFillAfter(true);
//	    animSet.setInterpolator(new BounceInterpolator());
	    animSet.setInterpolator(new AccelerateDecelerateInterpolator());
	    animSet.setDuration(duration);
	    ScaleAnimation scale = new ScaleAnimation(initialSize, finalSize, initialSize, finalSize, ScaleAnimation.RELATIVE_TO_PARENT, 0.5f, ScaleAnimation.RELATIVE_TO_PARENT, 1f);
	    scale.initialize((int) (mOutlineImg.getWidth()*initialSize), (int) (mOutlineImg.getHeight()*initialSize), mOutlineImg.getWidth(), mOutlineImg.getHeight());
	    animSet.addAnimation(scale);
	    mOutlineImg.startAnimation(animSet);
	}
	
	public void startListening() {
		thread = new Thread(new Runnable() {
			public void run() {
				mBreathNum = 0;
				while (thread != null && !thread.isInterrupted()) {
					try {
						Thread.sleep(SAMPLE_DELAY);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
					readAudioBuffer(); // get lastLevel
					mBalloonImg.post(new Runnable() {
				        public void run() {
							float nextSize = (float) (lastLevelLPF/200);
							if(nextSize > 0.8f)
								nextSize = 0.8f;
							else if(nextSize < 0.15f)
								nextSize = 0.15f;
							AnimationSet animSet = new AnimationSet(true);
						    animSet.setFillAfter(true);
						    animSet.setDuration(100);
						    ScaleAnimation scale = new ScaleAnimation(initialSize, nextSize, initialSize, nextSize, ScaleAnimation.RELATIVE_TO_PARENT, 0.5f, ScaleAnimation.RELATIVE_TO_PARENT, 1f);
						    animSet.addAnimation(scale);
						    mBalloonImg.startAnimation(animSet);
						    initialSize = nextSize;
				        }
					});
					
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							
							if (lastLevelLPF > 0 && lastLevelLPF <= 150) {
								isExhaling = false;
								
							} else {
								if(!isExhaling)
									mBreathNum += 1;
								isExhaling = true;
							}

						}
					});
				}
			}
		});
		thread.start();
	}

	private void setupChart() {

		// series for sample and target values
		mSampleSeries = new XYSeries("sample");
		mLPFSeries = new XYSeries("lpf");
		mTargetSeries = new XYSeries("target");
		dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(mSampleSeries);
		dataset.addSeries(mLPFSeries);
		dataset.addSeries(mTargetSeries);

		// renderer for sample values
		mSampleRenderer = new XYSeriesRenderer();
		mSampleRenderer.setColor(Color.GREEN);
		mSampleRenderer.setLineWidth(2);

		// renderer for sample values
		mLPFRenderer = new XYSeriesRenderer();
		mLPFRenderer.setColor(Color.BLUE);
		mLPFRenderer.setLineWidth(2);

		// renderer for target values
		mTargetRenderer = new XYSeriesRenderer();
		mTargetRenderer.setColor(Color.RED);
		mTargetRenderer.setLineWidth(2);

		multiRenderer = new XYMultipleSeriesRenderer();
		multiRenderer.setChartTitle("Sound Monitor");
		multiRenderer.setChartTitleTextSize(24);
		multiRenderer.setXTitle("Time");
		multiRenderer.setYTitle("Sound level");
		multiRenderer.setAxisTitleTextSize(20);
		multiRenderer.setXLabelsPadding(10);
		multiRenderer.setYLabelsPadding(10);
		multiRenderer.setXAxisMin(0);
		multiRenderer.setXAxisMax(15);
		multiRenderer.setYAxisMin(-100);
		multiRenderer.setYAxisMax(500);
		multiRenderer.setAxesColor(Color.BLACK);
		multiRenderer.setShowGrid(true);
		multiRenderer.setGridColor(Color.GRAY);
		multiRenderer.setBarSpacing(2);
		multiRenderer.setApplyBackgroundColor(true);
		multiRenderer.setBackgroundColor(Color.WHITE);
		multiRenderer.setMarginsColor(Color.WHITE);
		multiRenderer.setPanEnabled(false, false);

		multiRenderer.addSeriesRenderer(mSampleRenderer);
		multiRenderer.addSeriesRenderer(mLPFRenderer);
		multiRenderer.addSeriesRenderer(mTargetRenderer);

		// Getting a reference to LinearLayout of the MainActivity Layout
//		LinearLayout chartContainer = (LinearLayout) mView
//				.findViewById(R.id.chart_container);

		mChart = (GraphicalView) ChartFactory.getLineChartView(getActivity()
				.getBaseContext(), dataset, multiRenderer);

		// Adding the Line Chart to the LinearLayout
//		chartContainer.addView(mChart);
	}

	private class ChartTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			int i = 0;
			try {
				do {
					if(isCancelled()) break;
					
					String[] values = new String[4];
					int sample = (int) lastLevel;
					int LPF = (int) lastLevelLPF;

					values[0] = Integer.toString(i);
					values[1] = Integer.toString(sample);
					values[2] = Integer.toString(LPF);
					values[3] = Integer.toString(mTarget);

					publishProgress(values);
					Thread.sleep(50);
					i++;

					multiRenderer.setXAxisMax(i);
					multiRenderer.setXAxisMin(i - 100);
				} while (true);
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			mSampleSeries.add(Integer.parseInt(values[0]),
					Integer.parseInt(values[1]));
			mLPFSeries.add(Integer.parseInt(values[0]),
					Integer.parseInt(values[2]));
			mTargetSeries.add(Integer.parseInt(values[0]),
					Integer.parseInt(values[3]));
			mChart.repaint();
		}
		
		
	}
	
	public void onBackPressed() {
        mTimer.cancel();
    }
}