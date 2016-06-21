//
//  UILabel+Skin.m
//  NightMode
//
//  Created by chaoliangmei on 15/9/2.
//  Copyright (c) 2015年 chaoliangmei. All rights reserved.
//

#import "UILabel+Skin.h"
#import <objc/runtime.h>
#import "ThemeManager.h"


@interface UILabel ()

@property(strong,nonatomic)NSString *backgroudColorName;
@property(strong,nonatomic)NSString *textColorName;

@end

@implementation UILabel (Skin)



- (NSString *)backgroudColorName
{
    return objc_getAssociatedObject(self, @selector(backgroudColorName));
    
}

- (NSString *)textColorName
{
    return objc_getAssociatedObject(self, @selector(textColorName));
    
}

- (void)setThemeLabBackgroundColor:(NSString *)themBackgroundColor
{
    objc_setAssociatedObject(self, @selector(backgroudColorName), themBackgroundColor, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}
- (void)setThemeTextColor:(NSString *)themeColor
{
    objc_setAssociatedObject(self, @selector(textColorName), themeColor, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}



#pragma mark - Hex

- (void)openThemeSkin
{
    [self configureViews];
    [self regitserAsObserver];
}


- (void)configureViews
{
    [self setBackgroundColor:[[ThemeManager sharedInstance] colorWithColorName:self.backgroudColorName]];
    [self setTextColor:[[ThemeManager sharedInstance] colorWithColorName:self.textColorName]];
    
    
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
