package org.hustunique.lockscreen;

import org.hustunique.lockscreen.database.SpHelper;
import org.hustunique.lockscreen.guide.GuideActivity;
import org.hustunique.lockscreen.tools.AlarmService;
import org.hustunique.lockscreen.tools.SetLauncher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingFragment extends Fragment implements OnClickListener {

	private LinearLayout unlockLayout;
	private TextView unlockView;
	private int unlockNum;
	private LinearLayout reviewLayout;
	private TextView reView;
	private String reviewTime;
	private CheckBox reviewBox;
	private EditText hourText;
	private EditText minText;
	private SpHelper spHelper;
	private Dialog reviewDialog;
	private CheckBox lockBox;

	private TextView updateView;
	private CheckBox homeBox;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_setting, null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		spHelper = new SpHelper(getActivity());
		unlockLayout = (LinearLayout) view.findViewById(R.id.setting_unlock);
		unlockLayout.setOnClickListener(this);
		unlockView = (TextView) view.findViewById(R.id.setting_unlock_tv);
		unlockNum = spHelper.getInt(GuideActivity.COUNT);
		unlockView.setText("看" + unlockNum + "个词解锁");

		reviewLayout = (LinearLayout) view.findViewById(R.id.setting_review);
		reviewLayout.setOnClickListener(this);
		reView = (TextView) view.findViewById(R.id.setting_review_tv);
		reviewTime = spHelper.getString(GuideActivity.REVIEW_TIME);
		reView.setText("每天" + reviewTime);

		lockBox = (CheckBox) view.findViewById(R.id.setting_swicth);
		lockBox.setChecked(new SpHelper(getActivity())
				.getBoolean(GuideActivity.DO_LOCK));
		lockBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					Intent service = new Intent(getActivity(),
							LockScreenService.class);
					getActivity().stopService(service);
					new SpHelper(getActivity()).setBoolean(
							GuideActivity.DO_LOCK, false);

				} else {
					Intent service = new Intent();
					service.setClass(getActivity(), LockScreenService.class);
					getActivity().startService(service);
					new SpHelper(getActivity()).setBoolean(
							GuideActivity.DO_LOCK, true);
				}

			}
		});

		updateView = (TextView) view.findViewById(R.id.setting_update_tv);
		updateView.setText("每天" + spHelper.getInt(GuideActivity.UPDATE_TIME)
				+ "点");

		homeBox = (CheckBox) view.findViewById(R.id.setting_home_cb);
		homeBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SetLauncher setLauncher = new SetLauncher(getActivity());
				if (!homeBox.isChecked()) {
					setLauncher.removeDefaultHome(true);
				} else {
					if (setLauncher.hasDefault()) {
						setLauncher.guideDialog();
					} else {
						setLauncher.setDialog();
					}
				}
				homeBox.setChecked(setLauncher.isDefault());
			}
		});

		new SetLauncher(getActivity()).saveHome();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if (id == R.id.setting_unlock) {
			int item = which(new SpHelper(getActivity())
					.getInt(GuideActivity.COUNT));
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(getActivity().getText(R.string.unlock_style));
			builder.setSingleChoiceItems(R.array.unlock_style, item,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							new SpHelper(getActivity()).setInt(
									GuideActivity.COUNT, count(which));
							unlockView.setText("看" + count(which) + "个词解锁");
							dialog.cancel();
						}
					});
			builder.setPositiveButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});
			builder.create().show();
		} else if (id == R.id.setting_review) {
			reviewDialog = new Dialog(getActivity());
			reviewDialog.setContentView(R.layout.dialog_setting_review);
			hourText = (EditText) reviewDialog
					.findViewById(R.id.dialog_setting_et1);
			hourText.setText(new SpHelper(getActivity()).getString(
					GuideActivity.REVIEW_TIME).substring(0, 2));
			minText = (EditText) reviewDialog
					.findViewById(R.id.dialog_setting_et2);
			minText.setText(new SpHelper(getActivity()).getString(
					GuideActivity.REVIEW_TIME).substring(3));
			reviewBox = (CheckBox) reviewDialog
					.findViewById(R.id.dialog_setting_cb);
			reviewBox.setChecked(new SpHelper(getActivity())
					.getBoolean(GuideActivity.DO_REMIND));
			Button btnOk = (Button) reviewDialog
					.findViewById(R.id.dialog_setting_ok);
			Button btnCancel = (Button) reviewDialog
					.findViewById(R.id.dialog_setting_cancel);
			reviewDialog.setTitle(R.string.review_remind);
			OnClickListener remindClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int dialogId = v.getId();
					if (dialogId == R.id.dialog_setting_cancel) {
						reviewDialog.cancel();
					} else {
						if (reviewBox.isChecked()) {
							String hour = hourText.getText().toString();
							String min = minText.getText().toString();
							if (hour.length() != 2 || min.length() != 2
									|| Integer.parseInt(hour) > 23
									|| Integer.parseInt(min) > 60) {
								Toast.makeText(getActivity(), "时间格式不正确",
										Toast.LENGTH_SHORT).show();
							} else {
								new SpHelper(getActivity()).setString(
										GuideActivity.REVIEW_TIME, hour + ":"
												+ min);
								new SpHelper(getActivity()).setBoolean(
										GuideActivity.DO_REMIND, true);
								reviewDialog.cancel();
								reView.setText(hour + ":" + min);
								// 重置闹钟事件，先取消再设置
								AlarmService alarm = new AlarmService(
										getActivity());
								alarm.cancelAlarmReview();
								alarm.setAlarmReview(hour + ":" + min);
							}

						} else {
							new SpHelper(getActivity()).setBoolean(
									GuideActivity.DO_REMIND, false);
							reviewDialog.cancel();
						}
					}

					reView.setText(new SpHelper(getActivity())
							.getString(GuideActivity.REVIEW_TIME));

				}
			};
			btnOk.setOnClickListener(remindClickListener);
			btnCancel.setOnClickListener(remindClickListener);
			reviewDialog.show();
		} else if (id == R.id.setting_update) {

		}

	}

	private int count(int which) {
		int count = 0;
		switch (which) {
		case 0:
			count = 1;
			break;
		case 1:
			count = 3;
			break;
		case 2:
			count = 5;
			break;
		case 3:
			count = 10;
		default:
			break;
		}
		return count;
	}

	private int which(int count) {
		int which = 0;
		switch (count) {
		case 1:
			which = 0;
			break;
		case 3:
			which = 1;
			break;
		case 5:
			which = 2;
			break;
		case 10:
			which = 3;
		default:
			break;
		}
		return which;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SetLauncher setLauncher = new SetLauncher(getActivity());
		homeBox.setChecked(setLauncher.isDefault());
		if (!setLauncher.hasDefault()) {
			setLauncher.setDialog();
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e("setting", "pause");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e("setting", "stop");
	}
}
