//
//  YZCollectionView.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZCollectionView.h"

@interface YZCollectionView ()

@property(strong,nonatomic)NSString *backgroudColorName;
@property(nonatomic)BOOL hadObserver;

@end

@implementation YZCollectionView


#pragma mark - Hex

- (void)dealloc
{
    if (self.hadObserver) {
        [self unregisterAsObserver];
    }
}

- (void)setThemeCollectionViewBackgroudColor:(NSString *)backgroudColor;
{
    self.backgroudColorName = backgroudColor;
    
}

- (void)openThemeSkin
{
    [self configureViews];
    [self regitserAsObserver];
    self.hadObserver = YES;
}

- (void)configureViews
{
    
    [self setBackgroundColor:[[ThemeManager sharedInstance] colorWithColorName:self.backgroudColorName]];
    
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
