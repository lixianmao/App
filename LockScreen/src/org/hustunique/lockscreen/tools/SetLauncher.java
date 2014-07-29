package org.hustunique.lockscreen.tools;

import org.hustunique.lockscreen.database.SpHelper;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class SetLauncher {

	private Context context;

	public SetLauncher(Context context) {
		this.context = context;
	}

	/**
	 * 获取与包名有关信息
	 * 
	 * @param pkgName
	 * @return
	 */
	public HomeInfo getHomeInfo(String pkgName) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(pkgName, 0);
			if (null == packageInfo) {
				return null;
			}
			HomeInfo homeInfo = new HomeInfo();
			homeInfo.className = packageInfo.applicationInfo.className;
			homeInfo.label = packageInfo.applicationInfo.loadLabel(
					context.getPackageManager()).toString();
			homeInfo.icon = packageInfo.applicationInfo.loadIcon(context
					.getPackageManager());
			homeInfo.packageName = packageInfo.packageName;
			return homeInfo;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(context, "找不到与包名有关信息", Toast.LENGTH_SHORT).show();
		}
		return null;
	}

	/**
	 * 获取当前默认launcher包名
	 * 
	 * @return
	 */
	public String getDefaultHome() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		ComponentName componentName = intent.resolveActivity(context
				.getPackageManager());
		if (componentName == null) {
			return null;
		}
		return componentName.getPackageName();
	}

	/**
	 * 清除默认启动器
	 * 
	 * @param hasDefault
	 */
	public void removeDefaultHome(boolean hasDefault) {
		String pkgName = getDefaultHome();
		if (pkgName == null || !hasDefault) {
			return;
		}
		Intent intent = new Intent();
		if (Build.VERSION.SDK_INT >= 9) {
			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + pkgName));
		} else {
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName("com.android.settings",
					"com.android.settings.InstalledAppDetails");
			intent.putExtra("com.android.settings.ApplicationPkgName", pkgName);
			intent.putExtra("pkg", pkgName);
			// activity.startActivityForResult(intent, 1);
		}
		context.startActivity(intent);
	}

	/**
	 * 判断本launcher是否为默认
	 * 
	 * @return
	 */
	public boolean isDefault() {
		return context.getPackageName().equals(getDefaultHome());
	}

	/**
	 * 判断是否存在默认launcher 未设置默认情况下，获取包名返回的是“android”
	 * 
	 * @return
	 */
	public boolean hasDefault() {
		String pkg = getDefaultHome();
		return !(null == pkg || pkg.equals("android"));
	}

	/**
	 * 对话框，引导清除默认设置
	 */
	public void guideDialog() {
		new AlertDialog.Builder(context).setTitle("清除默认设置")
				.setPositiveButton("下一步", guideListener)
				.setNegativeButton("取消", null).create().show();

	}

	/**
	 * 对话框，引导设置默认launcher
	 */
	public void setDialog() {
		new AlertDialog.Builder(context).setTitle("设置为默认")
				.setPositiveButton("下一步", setListener)
				.setNegativeButton("取消", null).create().show();

	}

	public void setDefault() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.putExtra("from", 0);
		context.startActivity(intent);
	}

	private OnClickListener guideListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			removeDefaultHome(hasDefault());
		}
	};
	private OnClickListener setListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			setDefault();
		}
	};

	public void saveHome() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		ComponentName componentName = intent.resolveActivity(context
				.getPackageManager());
		SpHelper spHelper = new SpHelper(context);
		if (!TextUtils.isEmpty(spHelper.getString("launcher_default_package"))) {
			return;
		}
		if (null == componentName||componentName.getPackageName().equals(context.getPackageName())) {
			spHelper.setString("launcher_default_package", "");
			spHelper.setString("launcher_default_activity", "");
			return;
		}
		spHelper.setString("launcher_default_package",
				componentName.getPackageName());
		spHelper.setString("launcher_default_activity",
				componentName.getClassName());
	}

	/**
	 * 切换到系统启动器
	 * 
	 * @param packageName
	 */
	public void switchHome() {
		SpHelper spHelper = new SpHelper(context);
		String pkg = spHelper.getString("launcher_default_package");
		String cls = spHelper.getString("launcher_default_activity");

		Log.e("pkg", pkg + "/" + cls);
		if (!TextUtils.isEmpty(pkg)) {
			ComponentName componentName = new ComponentName(pkg, cls);
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setComponent(componentName);
			context.startActivity(intent);
			return;
		}
		Toast.makeText(context, "无法切换到系统启动器", Toast.LENGTH_SHORT).show();

	}

}
