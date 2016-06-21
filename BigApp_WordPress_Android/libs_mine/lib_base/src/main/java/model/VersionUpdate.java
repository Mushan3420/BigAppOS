package model;

import java.io.Serializable;

/**
 * Created by wjwu on 2015/9/22.
 */
public class VersionUpdate implements Serializable {

    public VersionInfo version_info;

    public static class VersionInfo implements Serializable {
        /***
         * 0不升级，1建议更新，2强制更新
         */
        public int flag;
        /***
         * 最新版本号
         */
        public String latest_version;
        /***
         * 更新内容
         */
        public String updatemsg;
        /***
         * 安装包地址
         */
        public String pkgurl;
        public String md5;
    }
}
