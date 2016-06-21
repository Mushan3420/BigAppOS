//
//  YZShareLoginView.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/22.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZShareLoginView.h"
#import "BuildConfig.h"
#import "UIViewExt.h"

#define viewSpace 30;
@implementation YZShareLoginView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        [self addSubview:self.weichatBtn];
        [self addSubview:self.qqBtn];

        [self addSubview:self.weiboBtn];
        
        [self setContentFrames];
        
    }
    return self;
}

-(void)setContentFrames{
    _weichatBtn.frame = CGRectMake(0, 0, 24, 24);
    _qqBtn.frame = CGRectMake(_weichatBtn.right + 30, 0, 24, 24);
    _weiboBtn.frame = CGRectMake(_qqBtn.right + 30, 0, 24, 24);
    
    self.width = _weiboBtn.right;
    self.height = 24;
}

- (UIButton *)qqBtn{
    if (!_qqBtn) {
        _qqBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        UIFont *font = [UIFont fontWithName:kChineseFontNameXi size:14.0];
        _qqBtn.titleLabel.font = font;
        
        [_qqBtn setImage:[UIImage imageNamed:@"login_qq"] forState:UIControlStateNormal];
        

        
    }
    
    return _qqBtn;
}


- (UIButton *)weiboBtn{
    if (!_weiboBtn) {
        _weiboBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        UIFont *font = [UIFont fontWithName:kChineseFontNameXi size:14.0];
        _weiboBtn.titleLabel.font = font;
        [_weiboBtn setImage:[UIImage imageNamed:@"login_weibo"] forState:UIControlStateNormal];
    }
    
    return _weiboBtn;
}


- (UIButton *)weichatBtn{
    if (!_weichatBtn) {
        _weichatBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        UIFont *font = [UIFont fontWithName:kChineseFontNameXi size:14.0];
        _weichatBtn.titleLabel.font = font;
        [_weichatBtn setImage:[UIImage imageNamed:@"login_weixin"] forState:UIControlStateNormal];

    }
    
    return _weichatBtn;
}
@end
