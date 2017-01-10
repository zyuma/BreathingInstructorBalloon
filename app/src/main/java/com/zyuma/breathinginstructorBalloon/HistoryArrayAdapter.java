package com.zyuma.breathinginstructorBalloon;

import java.util.ArrayList;

import com.zyuma.breathinginstructor3.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HistoryArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final ArrayList<String> BPMList;
	private final ArrayList<String> durationList;
	private final ArrayList<String> dateList;
//	private final ArrayList<String> timeList;

	public HistoryArrayAdapter(Context context, ArrayList<String> BPMList, ArrayList<String> durationList, ArrayList<String> dateList) {
		super(context, R.layout.history_row, dateList);
		this.context = context;
		this.BPMList = BPMList;
		this.durationList = durationList;
		this.dateList = dateList;
//		this.timeList = timeList;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.history_row, parent, false);
		
		TextView bpm = (TextView) rowView.findViewById(R.id.bpm);
		TextView dur = (TextView) rowView.findViewById(R.id.duration);
		TextView date = (TextView) rowView.findViewById(R.id.date);
//		TextView time = (TextView) rowView.findViewById(R.id.time);
		
		bpm.setText(BPMList.get(position));
		dur.setText(durationList.get(position));
		date.setText(dateList.get(position));
//		time.setText(timeList.get(position));
		
		return rowView;
	}
}