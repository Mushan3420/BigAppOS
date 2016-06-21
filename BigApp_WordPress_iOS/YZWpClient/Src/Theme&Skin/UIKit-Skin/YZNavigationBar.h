//
//  YZNavigationBar.h
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface YZNavigationBar : UINavigationBar

- (void)openThemeSkin;
- (void)setThemeBackgroudImage:(NSString *)imageName;
- (void)setThemeTintColor:(NSString *)colorName;
- (void)setThemeBarTintColor:(NSString *)colorName;
- (void)setThemeTitleColor:(NSString *)colorName;

@end
