//
//  BYSelectNewBar.m
//  BYDailyNews
//
//  Created by bassamyan on 15/1/18.
//  Copyright (c) 2015年 apple. All rights reserved.
//

#import "BYSelectNewBar.h"
#import "BuildConfig.h"

@interface BYSelectNewBar()
{
    UILabel *sublabel;
    UIButton *button;
}

@end

@implementation BYSelectNewBar

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self makeNewBar];
//        self.hidden= YES;
    }
    return self;
}
-(void)makeNewBar
{
    self.backgroundColor = Color_maingray;
    
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(20, 0, 60, 30)];
    label.font = [UIFont systemFontOfSize:14];
    label.textColor = RGBCOLOR(48, 48, 48);
    label.text = NSLocalizedString(@"MY_COLUMN", nil);
    [self addSubview:label];
    
    sublabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(label.frame)+10,10, 100, 11)];
    sublabel.font = [UIFont systemFontOfSize:11];
    sublabel.text = NSLocalizedString(@"DRAG_TO_SORT", nil);
    sublabel.textColor = sublabel_gray;
    sublabel.hidden = YES;
    [self addSubview:sublabel];
    
    button = [[UIButton alloc] initWithFrame:CGRectMake(BYScreenWidth-70, 5, 50, 22)];
    [button setTitle:NSLocalizedString(@"SORT_BTN", nil) forState:0];
    [button setTitleColor:RGBCOLOR(254, 48, 0) forState:0];
    button.titleLabel.font = [UIFont systemFontOfSize:13];
    button.layer.cornerRadius = button.frame.size.height/2;
    button.layer.borderWidth = 1;
    [button.layer setMasksToBounds:YES];
    button.layer.borderColor = [RGBCOLOR(254, 48, 0) CGColor];
    button.tag = 0;
    [button addTarget:self
               action:@selector(buttonclick)
     forControlEvents:1 << 6];
    [self addSubview:button];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(buttonclick)
                                                 name:@"press_longPressGesture"
                                               object:nil];
    
}


-(void)buttonclick
{
    if ([button.titleLabel.text isEqualToString:NSLocalizedString(@"FINISH_BTN", nil)]) {
        [button setTitle:NSLocalizedString(@"SORT_BTN", nil) forState:0];
        sublabel.hidden = YES;
    }
    else if([button.titleLabel.text isEqualToString:NSLocalizedString(@"SORT_BTN", nil)])
    {
        [button setTitle:NSLocalizedString(@"FINISH_BTN", nil) forState:0];
        sublabel.hidden = NO;
    }
    [[NSNotificationCenter defaultCenter] postNotificationName:@"srot_btn_click"
                                                        object:button
                                                      userInfo:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"srot_btn_click" object:nil];
}
@end

// 版权属于原作者
// http://code4app.com (cn) http://code4app.net (en)
// 发布代码于最专业的源码分享网站: Code4App.com 
