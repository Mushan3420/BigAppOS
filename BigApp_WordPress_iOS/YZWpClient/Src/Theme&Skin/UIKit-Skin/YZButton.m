//
//  YZButton.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZButton.h"

@interface YZButton ()

@property(strong,nonatomic)NSString *backgroudColorName;
@property(strong,nonatomic)NSString *backgroudImageName;
@property(strong,nonatomic)NSString *textColorName;
@property(nonatomic)BOOL hadObserver;


@end

@implementation YZButton

- (void)dealloc
{
    if (self.hadObserver) {
        [self unregisterAsObserver];
    }
}

- (void)setThemeButtonBackgroudColor:(NSString *)backgroudColor
{
    self.backgroudColorName = backgroudColor;
}

- (void)setThemeButtonImage:(NSString *)backgroudImage
{
    self.backgroudImageName = backgroudImage;
}

- (void)setthemeTextColor:(NSString *)themeColor
{
    self.textColorName = themeColor;
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
    [self setImage:[[ThemeManager sharedInstance] imageWithImageName:self.backgroudImageName] forState:UIControlStateNormal];
    [self setTitleColor:[[ThemeManager sharedInstance] colorWithColorName:self.textColorName] forState:UIControlStateNormal];
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
