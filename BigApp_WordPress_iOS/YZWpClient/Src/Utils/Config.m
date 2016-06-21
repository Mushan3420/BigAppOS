//
//  Config.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/16.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "Config.h"

NSString * const kUserId = @"yz_userId";
NSString * const kAccount = @"yz_account";
NSString * const kIsLogin = @"yz_account_isLogin";

NSString * const kEmail = @"yz_email";
NSString * const kDescription = @"yz_description";
NSString * const kAvatar = @"yz_avatar";

NSString * const kSearchHistoryKey = @"yz_search_history";

NSString * const kWpSessionCookies = @"wp_session_cookies";

NSString * const kWpFontSize = @"wp_font_size";

NSString * const kWpThemeName = @"wp_theme_name";

NSString * const kWpUsersCanRegister = @"wp_users_can_register";

//图片模式
NSString * const kWPPictureQualitySheet = @"wp_picture_quality_sheet";

@implementation Config

+ (void)saveLoginUser:(YZLoginModel *)loginModel
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:@(loginModel.id) forKey:kUserId];
    [userDefaults setObject:loginModel.niceName forKey:kAccount];
    [userDefaults setObject:loginModel.email forKey:kEmail];
    if (loginModel.udescription == nil || [loginModel.udescription isEqual:[NSNull null]]) {
        [userDefaults setObject:nil forKey:kDescription];
    }
    else
    {
        [userDefaults setObject:loginModel.udescription forKey:kDescription];
    }
    if (loginModel.avatar == nil || [loginModel.avatar isEqual:[NSNull null]]) {
        [userDefaults setObject:nil forKey:kAvatar];
    }
    else
    {
        [userDefaults setObject:loginModel.avatar forKey:kAvatar];

    }
    
    
    [userDefaults synchronize];
}

+ (YZLoginModel *)loadLoginUser
{
    YZLoginModel *log = [[YZLoginModel alloc]init];
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *logid = [userDefaults objectForKey:kUserId];
    log.id = logid.integerValue;
    log.niceName = [userDefaults objectForKey:kAccount];
    log.email = [userDefaults objectForKey:kEmail];
    log.udescription = [userDefaults objectForKey:kDescription];
    log.avatar = [userDefaults objectForKey:kAvatar];
    
    return log;
}

+ (void)saveUserAccount:(NSString *)account password:(NSString *)password{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:account forKey:kAccount];
    
    [self saveCookies];
    
    [userDefaults synchronize];
}

+ (void)loadCookies
{
    NSArray *cookies = [NSKeyedUnarchiver unarchiveObjectWithData: [[NSUserDefaults standardUserDefaults] objectForKey: kWpSessionCookies]];
    NSHTTPCookieStorage *cookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    
    for (NSHTTPCookie *cookie in cookies){
        [cookieStorage setCookie: cookie];
    }
    
}

// 有时退出应用后，cookie不保存，手动保存cookie ，暂时存入NSUserDefaults，考虑安全因素以后放进Keychain
+ (void)saveCookies
{
    NSData *cookiesData = [NSKeyedArchiver archivedDataWithRootObject: [[NSHTTPCookieStorage sharedHTTPCookieStorage] cookies]];
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject: cookiesData forKey:kWpSessionCookies];
    [defaults synchronize];
}


+ (void)clearCookie{
    NSHTTPCookieStorage *cookieJar = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    NSArray *cookies = [NSArray arrayWithArray:[cookieJar cookies]];
    for (NSHTTPCookie *cookie in cookies) {
        [cookieJar deleteCookie:cookie];
    }
    
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults removeObjectForKey:kWpSessionCookies];
    [userDefaults synchronize];

}

+ (int64_t)getUserID{
    
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *logid = [userDefaults objectForKey:kUserId];
    return logid.integerValue;
}

+ (NSString *)getNickName{
    
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *account = [userDefaults objectForKey:kAccount];
    
    
    return account;
}

+ (BOOL)isLogin{
    
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    id isLogin = [userDefaults objectForKey:kWpSessionCookies];
    if (isLogin) {
        return YES;
    }
    return NO;
}

+ (BOOL)usersCanRegister{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *usersCanRegister = [userDefaults objectForKey:kWpUsersCanRegister];
    if ([usersCanRegister isEqualToString:@"1"]) {
        return YES;
    }
    return NO;
}

+ (void)saveUsersCanRegister:(NSString *)usersCanRegister{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject: usersCanRegister forKey:kWpUsersCanRegister];
    [defaults synchronize];

}
#pragma makr-- 搜索历史
+ (NSArray *)getSearchHistoryList{
     NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    
    return  [userDefaults objectForKey:kSearchHistoryKey];
}

+ (void)saveSearchHistoryWithString:(NSString *)history{
    
    NSArray *historyArr = [self getSearchHistoryList];
    
    if ([historyArr isKindOfClass:[NSArray class]] && historyArr) {
        NSMutableArray *mutableHistory = [historyArr mutableCopy];
        [mutableHistory insertObject:history atIndex:0];
        
        NSArray * array = [NSArray arrayWithArray:mutableHistory];

        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        [userDefaults setObject:array forKey:kSearchHistoryKey];
        [userDefaults synchronize];
    }
    else{
        NSMutableArray *mutableArray = [NSMutableArray arrayWithObjects:history, nil];
        NSArray * array = [NSArray arrayWithArray:mutableArray];
        
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        [userDefaults setObject:array forKey:kSearchHistoryKey];
        [userDefaults synchronize];
    }

}

+ (void)deleteSearchHistory{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:nil forKey:kSearchHistoryKey];
    [userDefaults synchronize];
}

#pragma mark-- 图片模式
+ (void)savePictureQualityMod:(NSInteger)index{
    
    NSArray *imgMods = @[@"3",@"2",@"1"];//   imgMod = 3 高质量图 ,imgMod = 2 低质量图，imgMod = 1 无图  

    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject: imgMods[index] forKey:kWPPictureQualitySheet];
    [defaults synchronize];
}

+ (NSString *)getPictureQualityMod{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    
    NSString *imgMod = [userDefaults objectForKey:kWPPictureQualitySheet];
    
    return  imgMod ? imgMod : @"3";
}

#pragma mark--
+ (NSString *)getFontSize{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    
    return  [userDefaults objectForKey:kWpFontSize]?[userDefaults objectForKey:kWpFontSize] : @"100";
}

+ (void)saveFontSize:(NSInteger )sizeIndex{
    NSArray *fontSizes = @[@"90",@"100",@"110",@"120"];
    if (sizeIndex < fontSizes.count) {
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        [userDefaults setObject:fontSizes[sizeIndex] forKey:kWpFontSize];

        [userDefaults synchronize];
    }
}
#pragma mark-- 设置夜间模式
+ (NSString *)getThemeName
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    return  [userDefaults objectForKey:kWpThemeName]?[userDefaults objectForKey:kWpThemeName] : @"Normal";
}
+ (void)saveThemeModel:(NSString *)themeType
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:themeType forKey:kWpThemeName];
    [userDefaults synchronize];
}
@end
