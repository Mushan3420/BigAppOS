//
//  Config.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/16.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "YZLoginModel.h"

@interface Config : NSObject


+ (void)saveUserAccount:(NSString *)account password:(NSString *)password;
+ (void)loadCookies;
+ (void)saveCookies;
+ (void)clearCookie;

+ (void)saveLoginUser:(YZLoginModel *)loginModel;
+ (YZLoginModel *)loadLoginUser;

+ (int64_t)getUserID;
+ (NSString *)getNickName;

+ (BOOL)isLogin;
+ (BOOL)usersCanRegister;
+ (void)saveUsersCanRegister:(NSString *)usersCanRegister;

#pragma mark-- 搜索历史
+ (NSArray *)getSearchHistoryList;
+ (void)saveSearchHistoryWithString:(NSString *)history;
+ (void)deleteSearchHistory;

#pragma mark-- 图片模式
+ (void)savePictureQualityMod:(NSInteger)index;
+ (NSString *)getPictureQualityMod;

#pragma mark-- 字体大小
+ (NSString *)getFontSize;
+ (void)saveFontSize:(NSInteger )sizeIndex;

#pragma mark-- 设置夜间模式
+ (NSString *)getThemeName;
+ (void)saveThemeModel:(NSString *)themeType;
@end
