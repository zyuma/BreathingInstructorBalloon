package com.zyuma.breathinginstructorBalloon;

import java.util.ArrayList;

import com.zyuma.breathinginstructor3.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	
	public static ArrayList<BreathingData> mDataList;
	public static Typeface lobster_font;
	public static Typeface blokletters_balpen_font;
	public static Typeface blokletters_potlood_font;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		lobster_font = Typeface.createFromAsset(getAssets(), "fonts/LobsterTwo-Italic.ttf");
		blokletters_balpen_font = Typeface.createFromAsset(getAssets(), "fonts/Blokletters-Balpen.ttf");
		blokletters_potlood_font = Typeface.createFromAsset(getAssets(), "fonts/Blokletters-Potlood.ttf");
		
		mDataList = new ArrayList<BreathingData>(); 
		new ReadFromDatabaseTask(this).execute();
		
		getFragmentManager().beginTransaction().add(R.id.container, StartFragment.newInstance()).commit();
	}
	
	private class ReadFromDatabaseTask extends AsyncTask<Void, Void, Void> {

		private AlertDialog dialog;
		private Context context;

		public ReadFromDatabaseTask(Context context) {
			super();
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Saving");
			builder.setView(new ProgressBar(context));
			builder.setCancelable(false);
			if (dialog == null) {
				dialog = builder.create();
			}
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			BPMDataHelper helper = new BPMDataHelper(context);
			mDataList = helper.getBPMFromDatabase();
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			dialog.dismiss();
		}

	}

	
}
