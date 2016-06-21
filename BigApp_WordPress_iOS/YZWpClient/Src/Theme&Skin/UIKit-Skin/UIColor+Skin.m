//
//  UIColor+Skin.m
//  NightMode
//
//  Created by chaoliangmei on 15/9/2.
//  Copyright (c) 2015年 chaoliangmei. All rights reserved.
//

#import "UIColor+Skin.h"

@implementation UIColor (Skin)

+ (UIColor *)colorWithHex:(int)hexValue alpha:(CGFloat)alpha
{
    return [UIColor colorWithRed:((float)((hexValue & 0xFF0000) >> 16))/255.0
                           green:((float)((hexValue & 0xFF00) >> 8))/255.0
                            blue:((float)(hexValue & 0xFF))/255.0
                           alpha:alpha];
}

+ (UIColor *)colorWithHex:(int )hexValue
{
    return [UIColor colorWithHex:hexValue alpha:1.0];
}

@end
