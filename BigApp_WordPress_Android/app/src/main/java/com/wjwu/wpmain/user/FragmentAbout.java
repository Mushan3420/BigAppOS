package com.wjwu.wpmain.user;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.cache.FileCache;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseFragmentWithTitleBarSimple;
import com.wjwu.wpmain.net.CustomConfigure;
import com.wjwu.wpmain.net.RequestTools;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ResponseListener;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.widget.DialogSearching;

import model.VersionUpdate;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentAbout extends BaseFragmentWithTitleBarSimple implements View.OnClickListener {

    private TextView tv_check_version;

    @Override
    public int initContentView() {
        return R.layout.v_fragment_about;
    }

    @Override
    public void findAndBindViews(View contentView) {
        setDefaultImageLeftVisible(true, R.attr.img_nav_back);
        setTitleText(R.string.v_setting_item_about);

        View item_version = contentView.findViewById(R.id.item_version);
        ((TextView) item_version.findViewById(R.id.tv_title)).setText(R.string.v_about_item_check_version);
        tv_check_version = (TextView) item_version.findViewById(R.id.tv_content);
        tv_check_version.setText(CommonUtils.getVersionName(mContext));
        ((TextView) contentView.findViewById(R.id.tv_version)).setText(CommonUtils.getVersionName(mContext));

        item_version.setOnClickListener(this);

        VersionUpdate.VersionInfo info = FileCache.getVersionInfo();
        if (info == null) {
            return;
        }
        String[] vname = info.latest_version.split("[.]");
        int v_code = Integer.parseInt(vname[0]) * 10000 + Integer.parseInt(vname[1]) * 100 + Integer.parseInt(vname[2]);
        if (v_code <= CommonUtils.getVersionCode(getActivity())) {
            FileCache.saveVersionInfo(null);
            return;
        }
        tv_check_version.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.z_shape_msg_red, 0);
    }

    @Override
    public void onViewClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.item_version:
                checkVersion();
                break;
        }
    }

    private void checkVersion() {
        //TODO 版本更新逻辑请自行添加
        ZToastUtils.toastMessage(getActivity().getApplicationContext(), R.string.z_version_toast_no_new_version);
    }
}
