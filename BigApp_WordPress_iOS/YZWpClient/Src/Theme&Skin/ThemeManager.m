//
//  ThemeManager.m
//  NightMode
//
//  Created by chaoliangmei on 15/9/2.
//  Copyright (c) 2015年 chaoliangmei. All rights reserved.
//

#import "ThemeManager.h"
#import "AppConfigManager.h"
#import "Config.h"
NSString * const ThemeDidChangeNotification = @"uuzu.bigapp.theme.change";


@implementation ThemeManager

@synthesize theme=_theme;

+ (ThemeManager *)sharedInstance
{
    static dispatch_once_t once;
    static ThemeManager *instance = nil;
    dispatch_once(&once, ^{
        instance = [[ThemeManager alloc]init];
        instance.theme = [Config getThemeName];
    });
    return instance;
}

- (void)setTheme:(NSString *)theme
{
    _theme = [theme copy];
    
    ThemeStatus status = ThemeStatusDidChange;
    
    [[NSNotificationCenter defaultCenter]postNotificationName:ThemeDidChangeNotification object:[NSNumber numberWithInt:status]];
}
- (NSDictionary *)ColorDictionaryOfTheme
{
    NSString *directory = [NSString stringWithFormat:@"%@/%@",@"Resource",[self theme]];
    NSString *colorPlistPath = [[NSBundle mainBundle]pathForResource:@"colors" ofType:@"plist" inDirectory:directory];
    NSDictionary *dic = [NSDictionary dictionaryWithContentsOfFile:colorPlistPath];
    return dic;
}
- (UIColor *)colorWithColorName:(NSString *)colorHexName
{
    
    if ([colorHexName isEqualToString:kTmNavBarColor] && [[self theme] isEqualToString:@"Normal"]) {
        
        unsigned long textInt = strtoul([[AppConfigManager sharedInstance].appNavigationbarColor UTF8String], 0, 16);
        return [UIColor colorWithHex:(int)textInt alpha:1];
    }
    
    if ([colorHexName isEqualToString:kTmNavTitleColor] && [[self theme] isEqualToString:@"Normal"] && [AppConfigManager sharedInstance].depth) {
        
        return [UIColor whiteColor];
        
    }
    
    //top 分类菜单特殊处理
    if ([colorHexName isEqualToString:kTmMainTopSelectTextColor] && [[self theme] isEqualToString:@"Normal"]) {
        if ([AppConfigManager sharedInstance].depth) {
            
            unsigned long textInt = strtoul([[AppConfigManager sharedInstance].appNavigationbarColor UTF8String], 0, 16);

            return [UIColor colorWithHex:(int)textInt alpha:1];
        }
        else{
              return [UIColor blackColor];
        }
        
      
        
    }
    
    
    
    if ([colorHexName isEqualToString:kTmClearColor]) {
        return [UIColor clearColor];
    }
    NSDictionary *colorDic = [[ThemeManager sharedInstance] ColorDictionaryOfTheme];
    NSString *textColor = colorDic[colorHexName];
    if (textColor == nil) {
        return [UIColor lightGrayColor];
    }
    
    unsigned long textInt = strtoul([textColor UTF8String], 0, 16);
    return [UIColor colorWithHex:(int)textInt alpha:1];
}
- (UIImage *)imageWithImageName:(NSString *)imageName
{
    if (imageName==nil) {
        return nil;
    }

    if ([[self theme] isEqualToString:@"Normal"]&&[AppConfigManager sharedInstance].depth) {
        if (![imageName hasPrefix:@"corner_"]) {
            imageName = [self normalDepthImageName:imageName];
            NSString *directory = [NSString stringWithFormat:@"%@/%@",@"Resource",@"Normal"];
            NSString *imagePath = [[NSBundle mainBundle]pathForResource:imageName ofType:@"png" inDirectory:directory];
            return [UIImage imageWithContentsOfFile:imagePath];
        }

    }

    
    NSString *directory = [NSString stringWithFormat:@"%@/%@",@"Resource",[self theme]];
    NSString *imagePath = [[NSBundle mainBundle]pathForResource:imageName ofType:@"png" inDirectory:directory];
    return [UIImage imageWithContentsOfFile:imagePath];
}

- (NSString *)theme
{

    if (_theme == nil) {
        return @"Normal";
    }
    return _theme;
}


- (NSString *)normalDepthImageName:(NSString *)imageName{
    
    NSDictionary * imageNameDic = @{
                                    @"font_set_button@2x":@"detail_font_icon_white@2x",
                                    @"font_set_button@3x":@"detail_font_icon_white@3x",
                                    @"home_search_icon@2x":@"home_search_icon_white@2x",
                                    @"home_search_icon@3x":@"home_search_icon_white@3x",
                                    @"nav_back@2x":@"nav_back_white@2x",
                                    @"nav_back@3x":@"nav_back_white@3x",
                                    @"nav_tal_new@3x":@"nav_tal_white@3x",
                                    @"nav_tal_new@2x":@"nav_tal_white@2x"
                                    
                                    };
    
    return imageNameDic[imageName];
}

@end
