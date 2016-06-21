//
//  UINavigationBar+Skin.h
//  NightMode
//
//  Created by chaoliangmei on 15/9/6.
//  Copyright (c) 2015年 chaoliangmei. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UINavigationBar (Skin)

- (void)openThemeSkin;
- (void)setThemeBackgroudImage:(NSString *)imageName;
- (void)setThemeTintColor:(NSString *)colorName;
- (void)setThemeBarTintColor:(NSString *)colorName;
- (void)setThemeTitleColor:(NSString *)colorName;

@end
