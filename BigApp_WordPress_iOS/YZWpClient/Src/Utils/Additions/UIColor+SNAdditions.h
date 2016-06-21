//
//  UIColor+SNAdditions.h
//  SNFramework
//
//  Created by  liukun on 13-1-14.
//  Copyright (c) Suning. All rights reserved.
//

#import <UIKit/UIKit.h>



#undef	HEX_RGB
#define HEX_RGB(V)		[UIColor colorWithRGBHex:V]

@interface UIColor (SNAdditions)

+ (UIColor *)colorWithRGBHex:(UInt32)hex;
+ (UIColor *)colorWithHexString:(NSString *)stringToConvert;

+ (UIColor *)colorWithCssName:(NSString *)cssColorName;

@end
