//
//  UIView+Skin.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/7.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "UIView+Skin.h"
#import <objc/runtime.h>
#import "ThemeManager.h"

@interface UIView ()

@property(strong,nonatomic)NSString *backgroudColorName;

@end

@implementation UIView (Skin)

- (NSString *)backgroudColorName
{
    return objc_getAssociatedObject(self, @selector(backgroudColorName));
}


- (void)setThemeViewBackgroudColor:(NSString *)backgroudColor;
{
    objc_setAssociatedObject(self, @selector(backgroudColorName), backgroudColor, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    
}


#pragma mark - Hex


- (void)openThemeSkin
{
    [self configureViews];
    [self regitserAsObserver];
}

- (void)closeThemeSkin
{
    [self unregisterAsObserver];
}


- (void)configureViews
{
    
    [self setBackgroundColor:[[ThemeManager sharedInstance] colorWithColorName:self.backgroudColorName]];

}

- (void)regitserAsObserver
{
    NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
    [center addObserver:self
               selector:@selector(configureViews)
                   name:ThemeDidChangeNotification
                 object:nil];
}

- (void)unregisterAsObserver
{
    NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
    [center removeObserver:self];
}


@end
