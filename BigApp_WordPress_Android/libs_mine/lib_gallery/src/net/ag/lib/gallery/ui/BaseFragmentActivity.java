package net.ag.lib.gallery.ui;

import net.ag.lib.gallery.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/***
 * Activity父类，主要用于统一控制返回键动画
 * 
 * @author AG
 * 
 */
public class BaseFragmentActivity extends FragmentActivity {
	public static final String ARG_FRAGMENT_TAG = "fragmentTag";

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

	/***
	 * 显示fragment + 动画效果
	 * 
	 * @param layoutId
	 * @param fragment
	 * @param extras
	 * @param back
	 */
	protected void showFragment(int layoutId, Fragment fragment, Bundle extras,
			String fragmentTag) {
		if (fragment != null) {
			if (extras != null) {
				fragment.setArguments(extras);
			}
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.setCustomAnimations(R.anim.v_right_in,
					R.anim.v_left_out, R.anim.v_left_in, R.anim.v_right_out);
			try {
				transaction.replace(layoutId, fragment, fragmentTag).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			transaction = null;
		}
	}

	/***
	 * 显示跳转界面, add
	 * 
	 * @param fragment
	 * @param extras
	 * @param back
	 */
	protected void showFragmentAdd(int layoutId, Fragment fragment,
			Bundle extras, boolean back, String fragmentTag) {
		if (fragment != null) {
			if (extras != null) {
				fragment.setArguments(extras);
			}
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			if (back) {
				transaction.setCustomAnimations(R.anim.v_roll_up,
						R.anim.v_roll_down);
			} else {
				// 定义进入和退出的动画
				transaction.setCustomAnimations(R.anim.v_roll_up,
						R.anim.v_roll_down, R.anim.v_roll_up,
						R.anim.v_roll_down);
			}
			transaction.add(layoutId, fragment, fragmentTag).commit();
			transaction.addToBackStack(fragmentTag);
			transaction = null;
		}
	}

	/***
	 * 显示fragment 不加动画效果
	 * 
	 * @param layoutId
	 * @param fragment
	 */
	protected void showDefaultFragment(int layoutId, Bundle extras,
			Fragment fragment, String fragmentTag) {
		if (fragment != null) {
			if (extras != null) {
				fragment.setArguments(extras);
			}
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			try {
				transaction.replace(layoutId, fragment, fragmentTag).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
			transaction = null;
		}
	}

	/***
	 * fragment跳转接口
	 * 
	 * @author nxp71465
	 * 
	 */
	public interface FragmentCallBack {

		/***
		 * 跳转指定的fragment
		 * 
		 * @param fragmentId
		 *            fragmentId
		 * @param extras
		 *            传递数据
		 * @param back
		 *            是否返回
		 */
		public void fragmentChanged(String fragmentTag, Bundle extras,
				boolean back);

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
