//
//  UINavigationBar+Skin.m
//  NightMode
//
//  Created by chaoliangmei on 15/9/6.
//  Copyright (c) 2015年 chaoliangmei. All rights reserved.
//

#import "UINavigationBar+Skin.h"
#import <objc/runtime.h>
#import "ThemeManager.h"


@interface UINavigationBar ()

@property(strong,nonatomic)NSString *backgroudImageName;
@property(strong,nonatomic)NSString *tintColorName;
@property(strong,nonatomic)NSString *barTintColorName;
@property(strong,nonatomic)NSString *titleColorName;

@end

@implementation UINavigationBar (Skin)

- (NSString *)backgroudImageName
{
    return objc_getAssociatedObject(self, @selector(backgroudImageName));
}
- (NSString *)tintColorName
{
    return objc_getAssociatedObject(self, @selector(tintColorName));
}
- (NSString *)barTintColorName
{
    return objc_getAssociatedObject(self, @selector(barTintColorName));
}
- (NSString *)titleColorName
{
    return objc_getAssociatedObject(self, @selector(titleColorName));
}
- (void)setThemeBackgroudImage:(NSString *)imageName
{
    objc_setAssociatedObject(self, @selector(backgroudImageName), imageName, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}
- (void)setThemeTintColor:(NSString *)colorName
{
    objc_setAssociatedObject(self, @selector(tintColorName), colorName, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}
- (void)setThemeBarTintColor:(NSString *)colorName
{
    objc_setAssociatedObject(self, @selector(barTintColorName), colorName, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}
- (void)setThemeTitleColor:(NSString *)colorName
{
    objc_setAssociatedObject(self, @selector(titleColorName), colorName, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}



#pragma mark - Hex

- (void)openThemeSkin
{
    [self configureViews];
    [self regitserAsObserver];
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
