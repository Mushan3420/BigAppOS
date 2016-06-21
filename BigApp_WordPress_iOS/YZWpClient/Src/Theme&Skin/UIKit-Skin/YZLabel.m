//
//  YZLabel.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZLabel.h"

@interface YZLabel ()

@property(strong,nonatomic)NSString *backgroudColorName;
@property(strong,nonatomic)NSString *textColorName;
@property(nonatomic)BOOL hadObserver;


@end

@implementation YZLabel

- (void)setThemeLabBackgroundColor:(NSString *)themBackgroundColor
{
    self.backgroudColorName = themBackgroundColor;
}
- (void)setThemeTextColor:(NSString *)themeColor
{
    self.textColorName = themeColor;
}


#pragma mark - Hex

- (void)dealloc
{
    if (self.hadObserver) {
        [self unregisterAsObserver];
    }
}

- (void)openThemeSkin
{
    [self configureViews];
    [self regitserAsObserver];
    self.hadObserver = YES;
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
