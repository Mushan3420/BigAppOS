//
//  UIImageView+Skin.m
//  NightMode
//
//  Created by chaoliangmei on 15/9/6.
//  Copyright (c) 2015年 chaoliangmei. All rights reserved.
//

#import "UIImageView+Skin.h"
#import <objc/runtime.h>
#import "ThemeManager.h"

@interface UIImageView ()

@property(strong,nonatomic)NSString *backgroudImageName;

@end


@implementation UIImageView (Skin)



#pragma mark - Hex

- (void)openThemeSkin
{
    [self configureViews];
    [self regitserAsObserver];
}

- (NSString *)backgroudImageName
{
    return objc_getAssociatedObject(self, @selector(backgroudImageName));
    
}

- (void)setThemeBackgroudImage:(NSString *)backgroudImage
{
    
    objc_setAssociatedObject(self, @selector(backgroudImageName), backgroudImage, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    
}


- (void)configureViews
{
    [self setImage:[[ThemeManager sharedInstance] imageWithImageName:self.backgroudImageName]];

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
