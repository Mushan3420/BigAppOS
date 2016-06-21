//
//  YZImageView.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZImageView.h"

@interface YZImageView ()

@property(strong,nonatomic)NSString *backgroudImageName;
@property(nonatomic)BOOL hadObserver;

@end

@implementation YZImageView

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

- (void)setThemeBackgroudImage:(NSString *)backgroudImage
{
    
    self.backgroudImageName = backgroudImage;
    
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
