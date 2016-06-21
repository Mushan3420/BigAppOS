package com.wjwu.wpmain.lib_base;

import android.widget.Toast;

/***
 * 主界面的父类，处理返回键点击两次退出
 * 
 * @author AG
 *
 */
public class BaseMainActivity extends BaseFragmentActivity {

	private long mExitAppTime = 0;

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - mExitAppTime < 2000) {
			finish();
		} else {
			mExitAppTime = System.currentTimeMillis();
			Toast.makeText(this, getString(R.string.z_toast_exit_app),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

}