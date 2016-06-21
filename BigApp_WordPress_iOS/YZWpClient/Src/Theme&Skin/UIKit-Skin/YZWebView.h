//
//  YZWebView.h
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface YZWebView : UIWebView

- (void)openThemeSkin;

- (void)setThemeWebBackgroundColor:(NSString *)themBackgroundColor;
- (void)setThemeWebTextColor:(NSString *)themeColor;
- (void)configureViews;

@end
