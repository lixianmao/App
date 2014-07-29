package org.hustunique.lockscreen;

import org.hustunique.lockscreen.database.SpHelper;
import org.hustunique.lockscreen.function.DownActivity;
import org.hustunique.lockscreen.function.StoreActivity;
import org.hustunique.lockscreen.guide.GuideActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ReviewFragment extends Fragment implements OnClickListener {

	private ImageView menuView, alarmView, reviewView, rawView, masterView,
			storeView;
	private TextView selStoreView, sumView;
	private SpHelper spHelper;
	private Dialog alarmDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_review, null);
		initView(view);
		initData();
		return view;
	}

	private void initView(View view) {
		menuView = (ImageView) view.findViewById(R.id.review_menu);
		alarmView = (ImageView) view.findViewById(R.id.review_alarm);
		reviewView = (ImageView) view.findViewById(R.id.review_begin);
		rawView = (ImageView) view.findViewById(R.id.review_raw);
		masterView = (ImageView) view.findViewById(R.id.review_master);
		storeView = (ImageView) view.findViewById(R.id.review_store);
		selStoreView = (TextView) view.findViewById(R.id.review_sel_store);
		sumView = (TextView) view.findViewById(R.id.review_sum);

		menuView.setOnClickListener(this);
		alarmView.setOnClickListener(this);
		reviewView.setOnClickListener(this);
		rawView.setOnClickListener(this);
		masterView.setOnClickListener(this);
		storeView.setOnClickListener(this);
	}

	private void initData() {
		spHelper = new SpHelper(getActivity());
		String store = spHelper.getString(GuideActivity.PATH);
		int end = store.indexOf(".db");
		if (end != -1) {
			selStoreView.setText(store.substring(1, end));
		}
		sumView.setText(spHelper.getInt(GuideActivity.SUM) + "词");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if (id == R.id.review_menu) {
			SlidingActivity sa = (SlidingActivity) getActivity();
			sa.getSlidingMenu().showMenu();
		} else if (id == R.id.review_alarm) {
			alarmDialog = new Dialog(getActivity());
			alarmDialog.setContentView(R.layout.dialog_setting_review);
		} else if (id == R.id.review_begin) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getActivity().startActivity(intent);
		} else if (id == R.id.review_raw) {

		} else if (id == R.id.review_master) {
			Intent downIntent = new Intent(getActivity(), DownActivity.class);
			getActivity().startActivity(downIntent);
		} else if (id == R.id.review_store) {
			Intent storeIntent = new Intent(getActivity(), StoreActivity.class);
			getActivity().startActivity(storeIntent);
		}

	}

}
