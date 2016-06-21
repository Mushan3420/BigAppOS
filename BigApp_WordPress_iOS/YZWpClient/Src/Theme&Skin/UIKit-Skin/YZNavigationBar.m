//
//  YZNavigationBar.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZNavigationBar.h"

@interface YZNavigationBar ()

@property(strong,nonatomic)NSString *backgroudImageName;
@property(strong,nonatomic)NSString *tintColorName;
@property(strong,nonatomic)NSString *barTintColorName;
@property(strong,nonatomic)NSString *titleColorName;
@property(nonatomic)BOOL hadObserver;



@end

@implementation YZNavigationBar


- (void)setThemeBackgroudImage:(NSString *)imageName
{
    self.backgroudImageName = imageName;
}
- (void)setThemeTintColor:(NSString *)colorName
{
    self.tintColorName = colorName;
}
- (void)setThemeBarTintColor:(NSString *)colorName
{
    self.barTintColorName = colorName;
}
- (void)setThemeTitleColor:(NSString *)colorName
{
    self.titleColorName = colorName;
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
    
    [self setBackgroundImage:[[ThemeManager sharedInstance] imageWithImageName:self.backgroudImageName] forBarMetrics:UIBarMetricsDefault];
    if (self.tintColorName) {
        self.tintColor = [[ThemeManager sharedInstance] colorWithColorName:self.tintColorName];
    }
    if (self.barTintColorName) {
        self.barTintColor = [[ThemeManager sharedInstance] colorWithColorName:self.barTintColorName];
    }
    if (self.titleColorName) {
        NSDictionary *attributes=[NSDictionary dictionaryWithObjectsAndKeys:[[ThemeManager sharedInstance] colorWithColorName:self.titleColorName],NSForegroundColorAttributeName,nil];
        [self setTitleTextAttributes:attributes];
    }
    
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
