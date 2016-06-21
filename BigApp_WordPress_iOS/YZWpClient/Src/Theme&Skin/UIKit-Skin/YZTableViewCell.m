//
//  YZTableViewCell.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZTableViewCell.h"

@interface YZTableViewCell ()

@property(strong,nonatomic)NSString *backgroudColorName;
@property(strong,nonatomic)NSString *textColorName;
@property(strong,nonatomic)NSString *detailTextColorName;
@property(nonatomic)BOOL hadObserver;

@end

@implementation YZTableViewCell


- (void)setThemeCellBackgroudColor:(NSString *)backgroudColor
{
    self.backgroudColorName = backgroudColor;
}
- (void)setThemeCellTextColor:(NSString *)backgroudColor
{
    self.textColorName = backgroudColor;
}
- (void)setThemeCellDetailTextColor:(NSString *)backgroudColor
{
    self.detailTextColorName = backgroudColor;
}
#pragma mark - Hex

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

- (void)configureViews
{
    self.backgroundColor = [[ThemeManager sharedInstance] colorWithColorName:self.backgroudColorName];
    self.contentView.backgroundColor = [[ThemeManager sharedInstance] colorWithColorName:self.backgroudColorName];
    self.textLabel.textColor = [[ThemeManager sharedInstance] colorWithColorName:self.textColorName];
    self.detailTextLabel.textColor = [[ThemeManager sharedInstance] colorWithColorName:self.detailTextColorName];
    
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
