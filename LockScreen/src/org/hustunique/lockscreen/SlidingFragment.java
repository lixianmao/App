package org.hustunique.lockscreen;

import org.hustunique.lockscreen.database.SpHelper;
import org.hustunique.lockscreen.guide.GuideActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class SlidingFragment extends Fragment implements OnClickListener {

	private TextView reviewView;
	private TextView studyView;
	private TextView unlockView;
	private CheckBox switchBox;
	private Fragment content;
	private SpHelper spHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_sliding, null);
		spHelper = new SpHelper(getActivity());
		initView(view);
		return view;
	}

	private void initView(View view) {
		reviewView = (TextView) view.findViewById(R.id.sf_review);
		studyView = (TextView) view.findViewById(R.id.sf_study);
		unlockView = (TextView) view.findViewById(R.id.sf_unlock);
		switchBox = (CheckBox) view.findViewById(R.id.sf_switch);

		reviewView.setOnClickListener(this);
		reviewView.setBackgroundResource(R.drawable.sf_bg_sel);
		studyView.setOnClickListener(this);
		unlockView.setOnClickListener(this);
		switchBox.setChecked(spHelper.getBoolean(GuideActivity.DO_LOCK));
		switchBox.setOnCheckedChangeListener(new mOnCheckedChangeListener());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		setBackgroundDefault();
		int id = v.getId();
		if (id == R.id.sf_review) {
			content = new ReviewFragment();
			reviewView.setBackgroundResource(R.drawable.sf_bg_sel);
		} else if (id == R.id.sf_study) {
			studyView.setBackgroundResource(R.drawable.sf_bg_sel);
		} else if (id == R.id.sf_unlock) {
			content = new SettingFragment();
			unlockView.setBackgroundResource(R.drawable.sf_bg_sel);
		} else if (id == R.id.sf_switch) {
			
		}
		
		switchContent(content);
	}

	class mOnCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (!isChecked) {
				Intent service = new Intent(getActivity(),
						LockScreenService.class);
				getActivity().stopService(service);
				spHelper.setBoolean(GuideActivity.DO_LOCK, false);

			} else {
				Intent service = new Intent();
				service.setClass(getActivity(), LockScreenService.class);
				getActivity().startService(service);
				spHelper.setBoolean(GuideActivity.DO_LOCK, true);
			}
		}

	}

	/**
	 * 点击侧边栏菜单，切换到选中content
	 * 
	 * @param fragment
	 */
	private void switchContent(Fragment fragment) {
		if (null == getActivity()) {
			return;
		}
		SlidingActivity sa = (SlidingActivity) getActivity();
		sa.showContent(fragment);
	}

	/**
	 * 默认所有未选中项背景
	 */
	private void setBackgroundDefault() {
		reviewView.setBackgroundColor(getResources().getColor(
				R.color.sf_bg_unsel));
		studyView.setBackgroundColor(getResources().getColor(
				R.color.sf_bg_unsel));
		unlockView.setBackgroundColor(getResources().getColor(
				R.color.sf_bg_unsel));
	}
}
