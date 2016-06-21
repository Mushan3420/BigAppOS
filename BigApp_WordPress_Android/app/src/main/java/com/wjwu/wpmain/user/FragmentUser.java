package com.wjwu.wpmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.wjwu.wpmain.cache.BaseResponse;
import com.wjwu.wpmain.cache.SdCacheTools;
import com.wjwu.wpmain.cache.SpTool;
import com.wjwu.wpmain.lib_base.BaseFragmentWithTitleBarSimple;
import com.wjwu.wpmain.net.FormFile;
import com.wjwu.wpmain.net.RequestTools;
import com.wjwu.wpmain.net.RequestUrl;
import com.wjwu.wpmain.util.CommonUtils;
import com.wjwu.wpmain.util.ImageLoaderOptions;
import com.wjwu.wpmain.util.ResponseListener;
import com.wjwu.wpmain.util.ZToastUtils;
import com.wjwu.wpmain.uzwp.R;
import com.wjwu.wpmain.widget.DialogPicChooser;
import com.wjwu.wpmain.widget.DialogSearching;

import net.ag.lib.gallery.ui.MediaPreviewCameraActivity;
import net.ag.lib.gallery.util.MediaConstants;
import net.ag.lib.gallery.util.MediaInfo;
import net.ag.lib.gallery.util.MediaStoreUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import event.LoginEvent;
import model.User;

/**
 * Created by wjwu on 2015/8/31.
 */
public class FragmentUser extends BaseFragmentWithTitleBarSimple {

    private String nickName, user_desc;
    private View item_nick_name, item_user_desc, item_pwd, item_avatar;
    private ImageView mIv_avatar;
    private ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options = ImageLoaderOptions.getOptionsCachedDisk(true);

    @Override
    public int initContentView() {
        return R.layout.v_fragment_user;
    }

    @Override
    public void findAndBindViews(View contentView) {
        setDefaultImageLeftVisible(true, R.attr.img_nav_back);
        setTitleText(R.string.v_left_menu_user);

        item_avatar = contentView.findViewById(R.id.item_avatar);
        item_nick_name = contentView.findViewById(R.id.item_nick_name);
        item_user_desc = contentView.findViewById(R.id.item_user_desc);
        item_pwd = contentView.findViewById(R.id.item_pwd);
        ((TextView) item_nick_name.findViewById(R.id.tv_title)).setText(R.string.v_user_nick_name);
        ((TextView) item_nick_name.findViewById(R.id.tv_title)).getPaint().setFakeBoldText(false);
        ((TextView) item_nick_name.findViewById(R.id.tv_title)).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        nickName = new SpTool(getActivity(), SpTool.SP_USER).getString("userName", "");
        user_desc = new SpTool(getActivity(), SpTool.SP_USER).getString("description", "");
        updateUserInfo();
        ((TextView) contentView.findViewById(R.id.tv_email)).setText(new SpTool(getActivity(), SpTool.SP_USER).getString("email", ""));
        ((TextView) contentView.findViewById(R.id.tv_pwd)).setText(R.string.v_user_edit_pwd);
        mIv_avatar = (ImageView) contentView.findViewById(R.id.iv_avatar);

        String avatar = new SpTool(getActivity(), SpTool.SP_USER).getString("avatar", "");
        if (!TextUtils.isEmpty(avatar)) {
            mImageLoader.displayImage(avatar, mIv_avatar, options);
        }

        item_avatar.setOnClickListener(this);
        item_nick_name.setOnClickListener(this);
        item_user_desc.setOnClickListener(this);
        item_pwd.setOnClickListener(this);
    }

    private void updateUserInfo() {
        ((TextView) item_nick_name.findViewById(R.id.tv_content)).setText(nickName);
        ((TextView) item_user_desc.findViewById(R.id.tv_user_desc)).setText(user_desc);
    }

    private DialogPicChooser mDialogPicChooser;

    private String mImagePath = "";

    @Override
    public void onViewClick(View v) {
        int vId = v.getId();
        switch (vId) {
            case R.id.item_avatar:
                if (mDialogPicChooser == null) {
                    mDialogPicChooser = new DialogPicChooser(mContext, new DialogPicChooser.OnModeChangedLisener() {
                        @Override
                        public void onModeChanged(int mode) {
                            if (mode == 1) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                mImagePath = SdCacheTools.getTempCacheDir(mContext.getApplicationContext()) + "/camera_" + System.currentTimeMillis() + "jpg";
                                File imageFile = new File(mImagePath);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(imageFile));
                                startActivityForResult(intent, 1001);
                                return;
                            }
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");//相片类型
                            startActivityForResult(intent, 1002);
                        }
                    });
                }
                mDialogPicChooser.show();
                break;
            case R.id.item_nick_name:
                showEditDialog(R.string.v_user_edit_nick_name, nickName);
                break;
            case R.id.item_user_desc:
                ActivityUser.gotoFragmentModifyDescription(mContext);
                break;
            case R.id.item_pwd:
                ActivityUser.gotoFragmentModifyPwd(mContext);
                break;
        }
    }

    private EditText mInputEditView;
    private AlertDialog mAlertDialog;
    private String tmpContent = "";
    private int mTitleResId;

    private void showEditDialog(int titleResId, String content) {
        tmpContent = content;
        if (mInputEditView == null) {
            mInputEditView = new EditText(mContext);
            AlertDialog.Builder dialgBuilder = new AlertDialog.Builder(mContext);
            dialgBuilder.setView(mInputEditView).setNegativeButton(
                    getString(R.string.z_btn_cancel), null);
            dialgBuilder.setPositiveButton(getString(R.string.z_btn_confirm),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String inputStr = mInputEditView.getText().toString();
                            if (inputStr.equals(tmpContent)) {
                                return;
                            }
                            sendEdit(inputStr);
                        }
                    });
            mAlertDialog = dialgBuilder.create();
        }
        mTitleResId = titleResId;
        mAlertDialog.setTitle(titleResId);
        mInputEditView.setText(content);
        mInputEditView.setSelection(content.length());
        mAlertDialog.show();
        mInputEditView.setFocusable(true);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                CommonUtils.showSoftKeyBoard(mContext, mInputEditView);
            }
        });
    }

    private DialogSearching mDialogSearching;

    /***
     * @param content
     */
    private void sendEdit(final String content) {
        final HashMap<String, String> requestObject = new HashMap<>();
        switch (mTitleResId) {
            case R.string.v_user_edit_nick_name:
                requestObject.put("nickname", content);
                break;
            case R.string.v_user_edit_description:
                requestObject.put("description", content);
                break;
            case R.string.v_user_edit_pwd:
                requestObject.put("password", content);
                break;
        }
        if (mDialogSearching == null) {
            mDialogSearching = new DialogSearching(getActivity());
        }
        mDialogSearching.setContent(R.string.z_toast_modifying);
        mDialogSearching.show();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                new RequestTools(new ResponseListener(mContext.getApplicationContext()) {
                    @Override
                    public void onSuccess(Object obj) {
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                        try {
                            if (((BaseResponse<HashMap<String, Object>>) obj).error_code == 0) {
                                ZToastUtils.toastMessage(mContext, R.string.z_toast_modify_success);
                                switch (mTitleResId) {
                                    case R.string.v_user_edit_nick_name:
                                        nickName = content;
                                        new SpTool(getActivity(), SpTool.SP_USER).putString("userName", nickName);
                                        User user = new User();
                                        user.nice_name = nickName;
                                        EventBus.getDefault().post(new LoginEvent(user, 1));
                                        updateUserInfo();
                                        break;
                                    case R.string.v_user_edit_description:
                                        user_desc = content;
                                        new SpTool(getActivity(), SpTool.SP_USER).putString("description", user_desc);
                                        updateUserInfo();
                                        break;
                                }
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ZToastUtils.toastMessage(mContext, R.string.z_toast_modify_fail);
                    }

                    @Override
                    public void onSuccessError() {
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                    }

                    @Override
                    public void useCacheNotAndNoNetwork() {
                        if (mDialogSearching != null) {
                            mDialogSearching.cancel();
                        }
                    }

                    @Override
                    public void onCacheData(Object obj, boolean hasNetwork) {
                    }

                    @Override
                    public void onCacheDataError(boolean hasNetwork) {
                    }
                }).sendRequest(RequestUrl.edit_user_info, false, Request.Method.POST, requestObject, new TypeToken<BaseResponse<HashMap<String, Object>>>() {
                }, "edit_user_info");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        File imageFile = new File(mImagePath);
        Log.e("", "wenjun requestCode = " + requestCode + ", resultCode = " + requestCode + ", file = " + imageFile.exists());
        if (mContext == null) {
            return;
        }
        if (requestCode == 1001 && imageFile.exists()) {
            MediaPreviewCameraActivity.gotoPreviewCameraFroResult(this,
                    new MediaInfo(mImagePath));
        } else if (requestCode == MediaConstants.MEDIA_REQUEST_CAMERA_PIC) {
            if (resultCode != Activity.RESULT_OK) {
                ZToastUtils.toastMessage(mContext, R.string.z_toast_upload_cancel);
                return;
            }
            //预览界面返回
            uploadImg();
        } else if (requestCode == 1002) {
            Uri originalUri = data.getData();
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.MIME_TYPE};
                //好像是android多媒体数据库的封装接口，具体的看Android文档
                cursor = getActivity().managedQuery(originalUri, proj, null, null, null);
                //按我个人理解 这个是获得用户选择的图片的索引值
                int filepath_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
                int mimetype_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.MIME_TYPE);
                //将光标移至开头 ，这个很重要，不小心很容易引起越界
                cursor.moveToFirst();
                //最后根据索引值获取图片路径
                mImagePath = cursor.getString(filepath_index);
                String mimeType = cursor.getString(mimetype_index);
                Log.e("", "wenjun path = " + mImagePath + ", mimeType = " + mimeType);
                Bitmap bm = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), originalUri);        //显得到bitmap图片
                uploadImgByBitmap(bm, mimeType);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mDialogSearching.cancel();
                if (msg.obj == null) {
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_upload_fail);
                    return;
                }
                BaseResponse<String> base = (BaseResponse<String>) msg.obj;
                if (base.error_code == 0) {
                    mDialogSearching.cancel();
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), R.string.z_toast_upload_success);
                    new SpTool(getActivity(), SpTool.SP_USER).putString("avatar", base.data);
                    mImageLoader.displayImage(base.data, mIv_avatar, options);
                    User user = new User();
                    user.avatar = base.data;
                    EventBus.getDefault().post(new LoginEvent(user, 2));
                    return;
                }
                if (TextUtils.isEmpty(base.error_msg)) {
                    ZToastUtils.toastMessage(mContext.getApplicationContext(), base.error_msg);
                    return;
                }
            }
        }
    };

    private void uploadImg() {
        if (mDialogSearching == null) {
            mDialogSearching = new DialogSearching(getActivity());
        }
        mDialogSearching.setContent(R.string.z_toast_modifying);
        mDialogSearching.show();
        new Handler().post(new Runnable() {
                               @Override
                               public void run() {
                                   new Thread() {
                                       @Override
                                       public void run() {
                                           Message msg = new Message();
                                           msg.what = 1;
                                           msg.obj = null;
                                           try {
                                               ImageSize imageSize = BaseImageDecoder.getImageSize(mImagePath);
                                               BaseResponse<String> base = new Gson().fromJson((String) FormFile.postDatas(CommonUtils.getImageByteByPath(mImagePath, imageSize),
                                                       imageSize == null ? null : imageSize.getMimeType()), new TypeToken<BaseResponse<String>>() {
                                               }.getType());
                                               msg.obj = base;
                                               if (base.error_code == 0) {
                                                   String fileName = new HashCodeFileNameGenerator().generate(base.data);
                                                   MediaStoreUtils.copyFile(mImagePath, SdCacheTools.getOwnImageCacheDir(mContext) + "/" + fileName);
                                                   File file = new File(mImagePath);
                                                   if (!file.exists()) {
                                                       file.delete();
                                                   }
                                               }
                                           } catch (Throwable e) {
                                               e.printStackTrace();
                                               msg.obj = null;
                                           }
                                           mHanlder.sendMessage(msg);
                                       }
                                   }.start();
                               }
                           }

        );
    }

    private void uploadImgByBitmap(final Bitmap bitmap, final String mimeType) {
        if (mDialogSearching == null) {
            mDialogSearching = new DialogSearching(getActivity());
        }
        mDialogSearching.setContent(R.string.z_toast_modifying);
        mDialogSearching.show();
        new Handler().post(new Runnable() {
                               @Override
                               public void run() {
                                   new Thread() {
                                       @Override
                                       public void run() {
                                           Message msg = new Message();
                                           msg.what = 1;
                                           msg.obj = null;
                                           try {
                                               BaseResponse<String> base = new Gson().fromJson((String) FormFile.postDatas(CommonUtils.getImageByteByBitmap(bitmap),
                                                       mimeType), new TypeToken<BaseResponse<String>>() {
                                               }.getType());
                                               msg.obj = base;
                                               if (base.error_code == 0) {
                                                   String fileName = new HashCodeFileNameGenerator().generate(base.data);
                                                   MediaStoreUtils.copyFile(mImagePath, SdCacheTools.getOwnImageCacheDir(mContext) + "/" + fileName);
                                               }
                                           } catch (Throwable e) {
                                               e.printStackTrace();
                                               msg.obj = null;
                                           }
                                           mHanlder.sendMessage(msg);
                                       }
                                   }.start();
                               }
                           }

        );
    }
}
