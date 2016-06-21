//
//  UIWebView+Skin.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/7.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "UIWebView+Skin.h"
#import <objc/runtime.h>
#import "ThemeManager.h"

@interface UIWebView ()

@property (strong,nonatomic)NSString *webBackgroudColorName;
@property (strong,nonatomic)NSString *webTextColorName;

@end

@implementation UIWebView (Skin)

- (NSString *)webBackgroudColorName
{
    return objc_getAssociatedObject(self, @selector(webBackgroudColorName));
}
- (NSString *)webTextColorName
{
    return objc_getAssociatedObject(self, @selector(webTextColorName));
}

- (void)setThemeWebBackgroundColor:(NSString *)themBackgroundColor
{
    objc_setAssociatedObject(self, @selector(webBackgroudColorName), themBackgroundColor, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}
- (void)setThemeWebTextColor:(NSString *)themeColor
{
    objc_setAssociatedObject(self, @selector(webTextColorName), themeColor, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}



- (void)openThemeSkin
{
    [self configureViews];
    [self regitserAsObserver];
}


- (void)configureViews
{
    NSString *textColor = [[ThemeManager sharedInstance] ColorDictionaryOfTheme][self.webTextColorName];
    //字体颜色
    [self stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"document.getElementsByTagName('body')[0].style.webkitTextFillColor= '#%@'",textColor]];
    
    //页面背景色
    NSString *backgroudColor = [[ThemeManager sharedInstance] ColorDictionaryOfTheme][self.webBackgroudColorName];
    [self stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"document.getElementsByTagName('body')[0].style.background='#%@'",backgroudColor]];
    

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
