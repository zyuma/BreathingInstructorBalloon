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

public class InstructionFragment extends Fragment implements OnClickListener {

	private TextView mTitle;
	private TextView mTip1;
	private TextView mTip2;
	private TextView mTip3;
	private TextView mTip4;
	private TextView mTip5;
	private Button mBackButton;
	
	public static InstructionFragment newInstance() {
		InstructionFragment f = new InstructionFragment();
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_instruction, container, false);
		
		mTitle = (TextView) view.findViewById(R.id.title);
		mTitle.setTypeface(MainActivity.lobster_font);
		
		mTip1 = (TextView) view.findViewById(R.id.tip1);
		mTip1.setTypeface(MainActivity.blokletters_balpen_font);
		mTip2 = (TextView) view.findViewById(R.id.tip2);
		mTip2.setTypeface(MainActivity.blokletters_balpen_font);
		mTip3 = (TextView) view.findViewById(R.id.tip3);
		mTip3.setTypeface(MainActivity.blokletters_balpen_font);
		mTip4 = (TextView) view.findViewById(R.id.tip4);
		mTip4.setTypeface(MainActivity.blokletters_balpen_font);
		mTip5 = (TextView) view.findViewById(R.id.tip5);
		mTip5.setTypeface(MainActivity.blokletters_balpen_font);
		
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

			fm.replace(R.id.container, StartFragment.newInstance()).commit();
			break;
		}
	}
}
