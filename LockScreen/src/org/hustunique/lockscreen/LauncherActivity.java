package org.hustunique.lockscreen;

import org.hustunique.lockscreen.tools.SetLauncher;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class LauncherActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("launcher", "create");
		int i = getIntent().getIntExtra("from", 1);
		if (i == 0) {
			finish();
			return;
		}
		if (i == 1) { // 缺省值
			if (LockScreenActivity.onScreen == true) {
				finish();
				return;
			}
			new SetLauncher(this).switchHome();
			moveTaskToBack(true);
			finish();
		}

		setContentView(R.layout.activity_launcher);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e("launcher", "pause");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e("launcher", "stop");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("launcher", "destroy");
	}

}
