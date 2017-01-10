package com.zyuma.breathinginstructorBalloon;

import java.util.ArrayList;

import com.zyuma.breathinginstructor3.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryFragment extends Fragment implements OnClickListener {

	private TextView mTitle;
	private Button mBackButton;
	private int size;
	private static ArrayAdapter<String> mListAdapter;

	public static HistoryFragment newInstance() {
		HistoryFragment f = new HistoryFragment();
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_history, container,
				false);
		
		mTitle = (TextView) view.findViewById(R.id.title);
		mTitle.setTypeface(MainActivity.lobster_font);
		
		mBackButton = (Button) view.findViewById(R.id.backbutton);
		mBackButton.setTypeface(MainActivity.lobster_font);
		mBackButton.setOnClickListener(this);

		ListView pastList = (ListView) view.findViewById(R.id.list);
		
		ArrayList<String> bpmList = new ArrayList<String>();
		size = MainActivity.mDataList.size();
		if(size != 0) {
			for (int i=0; i<size; i++) {
				bpmList.add(Double.toString(MainActivity.mDataList.get(size-1-i).getBPM()) + " BPM");
			}
		}
		
		ArrayList<String> durationList = new ArrayList<String>();
		if(size != 0) {
			int ms;
			String suffix;
			for (int i=0; i<size; i++) {
				ms = MainActivity.mDataList.get(size-1-i).getDuration();
				if(ms == 15000 || ms == 30000 || ms == 60000)
					suffix = " seconds";
				else
					suffix = " minutes";
				durationList.add(Integer.toString(ms/1000) + suffix);
			}
		}
		
		ArrayList<String> timeList = new ArrayList<String>();
		if(size != 0) {
			for (int i=0; i<size; i++) {
				timeList.add(MainActivity.mDataList.get(size-1-i).getTime());
			}
		}

//		ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(),
//				android.R.layout.simple_list_item_1, timeList);
//		pastList.setAdapter(a);
		
		mListAdapter = new HistoryArrayAdapter(
				getActivity(), 
				bpmList, 
				durationList,
				timeList
				);
		
		pastList.setAdapter(mListAdapter);
		
		pastList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//show appropriate persons picture and call
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				RecordFragment f = RecordFragment.newInstance(MainActivity.mDataList.get(size-1-position));
				ft.replace(R.id.container, f);
				ft.addToBackStack(null);
				ft.commit();
			}
		});

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
