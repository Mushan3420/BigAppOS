//
//  YZWebView.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZWebView.h"

@interface YZWebView ()

@property (strong,nonatomic)NSString *webBackgroudColorName;
@property (strong,nonatomic)NSString *webTextColorName;
@property(nonatomic)BOOL hadObserver;


@end

@implementation YZWebView



- (void)setThemeWebBackgroundColor:(NSString *)themBackgroundColor
{
    self.webBackgroudColorName = themBackgroundColor;
}
- (void)setThemeWebTextColor:(NSString *)themeColor
{
    self.webTextColorName = themeColor;
}



- (void)openThemeSkin
{
    [self configureViews];
    [self regitserAsObserver];
    self.hadObserver = YES;
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

- (void)dealloc
{
    if (self.hadObserver) {
        [self unregisterAsObserver];
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
