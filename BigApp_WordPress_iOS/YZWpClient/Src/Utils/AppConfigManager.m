//
//  AppConfigManager.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/28.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "AppConfigManager.h"
#import "MJExtension.h"
#import "RNOpenSSLCryptor.h"

@implementation AppConfigManager

#pragma mark -
#pragma mark Singleton Methods
+ (instancetype)sharedInstance
{
    static dispatch_once_t pred = 0;
    __strong static id _sharedObject = nil;
    dispatch_once(&pred, ^{
        _sharedObject = [[self alloc] init];
    });
    
    return _sharedObject;
}

- (instancetype)init{
    self = [super init];
    if (self)
    {
        //读取打包配置文件
        NSString *appConfigPath = [[NSBundle mainBundle] pathForResource:@"Config" ofType:@"plist"];
        _configPlist = [NSDictionary dictionaryWithContentsOfFile:appConfigPath];
    }
    
    return self;
}

-(NSString *)appBundleId{
    return _configPlist[@"appBundleId"];
}
- (NSString *)appHttpServer
{
    return _configPlist[@"appHttpServer"];
}
- (NSString *)appUpdateVersion
{
    return _configPlist[@"versionUpdate"];
}
- (NSString *)appDispalyName{
    return _configPlist[@"appDispalyName"];
}

- (NSString *)appNavigationbarColor{
    
    return [_configPlist[@"appNavigationbarColor"] stringByReplacingOccurrencesOfString:@"#" withString:@""];
}

- (BOOL )depth{
    
    BOOL isDepth;
    NSString *hexColor = _configPlist[@"appNavigationbarColor"];
    hexColor = [hexColor stringByReplacingOccurrencesOfString:@"#" withString:@""];
    
    NSScanner *scanner = [NSScanner scannerWithString:hexColor];
    unsigned hexNum;
    if (![scanner scanHexInt:&hexNum]) return NO;
    
    int r = (hexNum >> 16) & 0xFF;
    int g = (hexNum >> 8) & 0xFF;
    int b = (hexNum) & 0xFF;
    
    int grayLevel = r * 0.299 + g * 0.587 + b * 0.114;
    
    if (grayLevel >= 190) {
        //浅色
        isDepth = NO;
    }
    else{
        //深色
        isDepth = YES;
    }
    
    return isDepth;
}
#pragma mark -- sharesdks key

-(NSString *)wechatAppId{
    return _configPlist[@"wechatAppId"];
}

-(NSString *)wechatAppSecret{
    return _configPlist[@"wechatAppSecret"];
}

- (NSString *)weiboAppKey{
    return _configPlist[@"weiboAppKey"];
}

- (NSString *)weiboAppSecret{
    return _configPlist[@"weiboAppSecret"];
}

- (NSString *)weiboRedirectUri{
    return _configPlist[@"weiboRedirectUri"];
}

- (NSString *)QQAppId{
    return _configPlist[@"QQAppId"];
}

- (NSString *)QQAppSecret{
    return _configPlist[@"QQAppSecret"];
}


@end
