//
//  ThemeManager.h
//  NightMode
//
//  Created by chaoliangmei on 15/9/2.
//  Copyright (c) 2015年 chaoliangmei. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "UIColor+Skin.h"

#import "YZButton.h"
#import "YZImageView.h"
#import "YZLabel.h"
#import "YZNavigationBar.h"
#import "YZView.h"
#import "YZWebView.h"
#import "YZTableViewCell.h"
#import "YZCollectionView.h"


#ifndef _SYSTEMCONFIGURATION_H
#error  You should include the `SystemConfiguration` framework
#endif

#ifdef _SYSTEMCONFIGURATION_H
extern NSString * const ThemeDidChangeNotification;
#endif

#define kTmClearColor                   @"clearColor"
#define kTmNavBarColor                  @"navBarColor"
#define kTmToolBarColor                 @"toolBarColor"
#define kTmNavTintColor                 @"navTintColor"
#define kTmNavTitleColor                @"navTitleColor"
#define kTmViewBackgroudColor           @"viewBackgroudColor"
#define kTmDetailCellBackgroudColor     @"detailCellBackgroudColor"
#define kTmDetailCellTextColor          @"detailCellTextColor"
#define kTmDetailCellDetailTextColor    @"detailCellDetailTextColor"
#define kTmDetailCellTimeTextColor      @"detailCellTimeTextColor"
#define kTmDetailWebViewBackgroudColor  @"detailWebViewBackgroudColor"
#define kTmDetailWebViewTextColor       @"detailWebViewTextColor"
#define kTmDetailSectionBackgroudColor  @"detailCellSectionBackgroudColor"
#define kTmDetailSectionTextColor       @"detailCellSectionTextColor"
#define kTmDetailCellSeperColor         @"detailCellSeperColor"
#define kTmDetailSegmentColor           @"detailSegmentTintColor"

//main
#define kTmMainCellBackgroudColor       @"mainCellBackgroudColor"
#define kTmMainCellSelectBackgroudColor       @"mainCellSelectBackgroudColor"
#define kTmMainCellTitleColor           @"mainCellTitleColor"
#define kTmMainCellDetailTitleColor     @"mainCellDetailTitleColor"
#define kTmMainCellTimeColor            @"mainCellTimeColor"
#define kTmMainCellSeperatorColor       @"mainCellSeperatorColor"
#define kTmMainTopBackgroudColor        @"mainTopBackgroudColor"
#define kTmMainTopTextColor             @"mainTopTextColor"
#define kTmMainTopSelectTextColor       @"mainToptextSelectColor"



#define IMAGE(imagePath) [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:(imagePath) ofType:@"png"]]

#define ThemeImage(imageName) [[ThemeManager sharedInstance] imageWithImageName:(imageName)]

typedef enum {
    ThemeStatusWillChange = 0, // todo
    ThemeStatusDidChange,
} ThemeStatus;

@interface ThemeManager : NSObject

@property (strong, nonatomic) NSString *theme;

+ (ThemeManager *)sharedInstance;
- (NSDictionary *)ColorDictionaryOfTheme;
- (UIColor *)colorWithColorName:(NSString *)colorHexName;
- (UIImage *)imageWithImageName:(NSString *)imageName;

@end
