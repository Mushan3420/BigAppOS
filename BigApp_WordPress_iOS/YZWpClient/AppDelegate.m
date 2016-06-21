//
//  AppDelegate.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "AppDelegate.h"
#import "YZMainController.h"
#import "YZBaseNavgationController.h"
#import "Dao.h"
#import "YZLeftViewController.h"
#import "YZLauchViewController.h"
#import "Config.h"
#import "BuildConfig.h"

#import <ShareSDK/ShareSDK.h>
#import <ShareSDKConnector/ShareSDKConnector.h>

#import <TencentOpenAPI/TencentOAuth.h>
#import <TencentOpenAPI/QQApiInterface.h>
#import "WXApi.h"
#import "WeiboSDK.h"
#import "AFNetworkActivityIndicatorManager.h"
#import "AppConfigManager.h"
#import <Fabric/Fabric.h>
#import <Crashlytics/Crashlytics.h>


@interface AppDelegate ()

@end

@implementation AppDelegate

- (void)loadMainView{
    
    YZMainController  *homeView = [[YZMainController alloc] init];
    
    YZBaseNavgationController *navHome = [[YZBaseNavgationController alloc] initWithRootViewController:homeView];
    
    YZLeftViewController *colorsVC = [[YZLeftViewController alloc] init];
    
    ICSDrawerController *drawer = [[ICSDrawerController alloc] initWithLeftViewController:colorsVC
                                                                     centerViewController:navHome];
    self.window.rootViewController = drawer;
}


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    [Config loadCookies];
//    [DAO createTablesNeeded];
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    self.window.backgroundColor = [UIColor whiteColor];
    [self.window makeKeyAndVisible];
    
    // 初始化启动页
    YZLauchViewController *launchVC = [[YZLauchViewController alloc] init];
    [self.window setRootViewController:launchVC];
    
    [self registerShareSdkForApp];
    
    [AFNetworkActivityIndicatorManager sharedManager].enabled = YES;
    
    [Fabric with:@[[Crashlytics class]]];

    [Crashlytics startWithAPIKey:[AppConfigManager sharedInstance].appBundleId];
    
    return YES;
}

- (void)registerShareSdkForApp{
    
    /**
     *  设置ShareSDK的appKey，如果尚未在ShareSDK官网注册过App，请移步到http://mob.com/login 登录后台进行应用注册，
     *  在将生成的AppKey传入到此方法中。
     *  方法中的第二个参数用于指定要使用哪些社交平台，以数组形式传入。第三个参数为需要连接社交平台SDK时触发，
     *  在此事件中写入连接代码。第四个参数则为配置本地社交平台时触发，根据返回的平台类型来配置平台信息。
     *  如果您使用的时服务端托管平台信息时，第二、四项参数可以传入nil，第三项参数则根据服务端托管平台来决定要连接的社交SDK。
     */
    [ShareSDK registerApp:@"9e5b81d0a27d"
          activePlatforms:@[
                            @(SSDKPlatformTypeSinaWeibo),
                            @(SSDKPlatformTypeWechat),
                            @(SSDKPlatformTypeQQ)]
                 onImport:^(SSDKPlatformType platformType) {
                     
                     switch (platformType)
                     {
                         case SSDKPlatformTypeWechat:
                             [ShareSDKConnector connectWeChat:[WXApi class]];
                             break;
                         case SSDKPlatformTypeQQ:
                             [ShareSDKConnector connectQQ:[QQApiInterface class] tencentOAuthClass:[TencentOAuth class]];
                             break;
                         default:
                             break;
                     }
                     
                 }
          onConfiguration:^(SSDKPlatformType platformType, NSMutableDictionary *appInfo) {
              
              switch (platformType)
              {
                  case SSDKPlatformTypeSinaWeibo:
                      //设置新浪微博应用信息,其中authType设置为使用SSO＋Web形式授权
                      [appInfo SSDKSetupSinaWeiboByAppKey:[AppConfigManager sharedInstance].weiboAppKey
                                                appSecret:[AppConfigManager sharedInstance].weiboAppSecret
                                              redirectUri:[AppConfigManager sharedInstance].weiboRedirectUri
                                                 authType:SSDKAuthTypeBoth];
                      break;
                  case SSDKPlatformTypeWechat:
                      [appInfo SSDKSetupWeChatByAppId:[AppConfigManager sharedInstance].wechatAppId
                                            appSecret:[AppConfigManager sharedInstance].wechatAppSecret];
                      break;
                  case SSDKPlatformTypeQQ:
                      [appInfo SSDKSetupQQByAppId:[AppConfigManager sharedInstance].QQAppId
                                           appKey:[AppConfigManager sharedInstance].QQAppSecret
                                         authType:SSDKAuthTypeBoth];
                      break;

                  default:
                      break;
              }
              
          }];

}

- (void)applicationWillResignActive:(UIApplication *)application {

}

- (void)applicationDidEnterBackground:(UIApplication *)application {
 
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
  
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    [self getServerConfig];
}

- (void)applicationWillTerminate:(UIApplication *)application {
   
}

//获得端上配置信息／版本信息
- (void)getServerConfig
{
    [YZConfService getBaseServerConfigWith:nil success:^(YZServerConfig *config){
        [Config saveUsersCanRegister:config.users_can_register];
        
    } failure:^(NSString *statusCode, NSString *error){
    
    }];
}
    

@end
