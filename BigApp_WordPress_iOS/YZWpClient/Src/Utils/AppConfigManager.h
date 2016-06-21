//
//  AppConfigManager.h
//  YZWpClient
//
//  Created by zhoutl on 15/8/28.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//
//  打包配置读取类
#import <Foundation/Foundation.h>

@interface AppConfigManager : NSObject

//默认配置plist文件
@property (nonatomic, strong) NSDictionary *configPlist;

@property (nonatomic, strong) NSString     *appDispalyName;

@property (nonatomic, strong) NSString     *appBundleId;

@property (nonatomic, strong) NSString     *appVersion;

@property (nonatomic, strong) NSString     *appNavigationbarColor;

@property (nonatomic, strong) NSString     *appHttpServer;

@property (nonatomic, strong) NSString     *appUpdateVersion;

@property (nonatomic, assign) BOOL          depth;//（判断导航栏颜色是浅色（NO） 、还是深色（YES））

#pragma mark-- share AppKey and AppSecret

//微信
@property (nonatomic, strong) NSString     *wechatAppId;
@property (nonatomic, strong) NSString     *wechatAppSecret;

//微博
@property (nonatomic, strong) NSString     *weiboAppKey;
@property (nonatomic, strong) NSString     *weiboAppSecret;
@property (nonatomic, strong) NSString     *weiboRedirectUri;

//QQ
@property (nonatomic, strong) NSString     *QQAppId;
@property (nonatomic, strong) NSString     *QQAppSecret;

+ (instancetype)sharedInstance;

@end
