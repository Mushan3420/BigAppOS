//
//  UIImage+SNAdditions.h
//  SNFramework
//
//  Created by  liukun on 13-1-25.
//  Copyright (c) Suning. All rights reserved.
//

#import <UIKit/UIKit.h>

#undef	__IMAGE
#define __IMAGE( __name )	[UIImage imageNamed:__name]


@interface UIImage (SNAdditions)


+ (UIImage *)streImageNamed:(NSString *)imageName;
+ (UIImage *)streImageNamed:(NSString *)imageName capX:(CGFloat)x capY:(CGFloat)y;
+ (UIImage *)imageWithColor:(UIColor *)color size:(CGSize)size;

+ (UIImage *) scaleImage: (UIImage *)image scaleFactor:(float)scaleBy;
@end
