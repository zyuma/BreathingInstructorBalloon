package com.zyuma.breathinginstructorBalloon;

import java.io.Serializable;

public class BreathingData implements Serializable {
	
	private static final String DURATION = "duration";
	private static final String BREATHCOUNT = "breathcount";
	private int mDuration;
	private int mBreathCount;
	private double mBPM;
	private int mRating;
	private String mTime;
	
	public BreathingData() {
		mDuration = 0;
		mBreathCount = 0;
		mBPM = 0;
		mRating = 5;
		mTime = "Unknown";
	}
	
	public BreathingData(int duration, int breathNum, double bpm, int improvement, String dateTime) {
		mDuration = duration;
		mBreathCount = breathNum;
		mBPM = bpm;
		mRating = improvement;
		mTime = dateTime;
	}
	
	public int getDuration() {
		return mDuration;
	}
	
	public void setDuration(int duration) {
		mDuration = duration;
	}
	
	public int getBreathCount() {
		return mBreathCount;
	}
	
	public void setBreathCount(int breathcount) {
		mBreathCount = breathcount;
	}
	
	public double getBPM() {
		return mBPM;
	}
	
	public void setBPM(double bpm) {
		mBPM = bpm;
	}
	
	public int getRating() {
		return mRating;
	}
	
	public void setRating(int rating) {
		mRating = rating;
	}
	
	public String getTime() {
		return mTime;
	}
	
	public void setTime(String t) {
		mTime = t;
	}
}
