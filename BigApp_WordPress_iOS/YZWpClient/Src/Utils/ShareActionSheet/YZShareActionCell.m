//
//  ShareActionCell.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/26.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#define kShareCellWidth ([UIScreen mainScreen].bounds.size.width/3.0)


#import "YZShareActionCell.h"
#import "BuildConfig.h"
#import "UIViewExt.h"

@implementation YZShareActionCell

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        [self.contentView addSubview:self.imageView];
        [self.contentView addSubview:self.titleView];
        
        UIView *bottomLine = [[UIView alloc] initWithFrame:CGRectMake(0,
                                                                      self.contentView.height-0.5,
                                                                      self.contentView.width,
                                                                      0.5)];
        bottomLine.backgroundColor = RGBCOLOR(201, 201, 201);
        
        [self.contentView addSubview:bottomLine];
        
        UIView *rightLine = [[UIView alloc] initWithFrame:CGRectMake(self.contentView.width ,
                                                                     0,
                                                                     0.5,
                                                                     self.contentView.height)];
        rightLine.backgroundColor = RGBCOLOR(201, 201, 201);
        
        [self.contentView addSubview:rightLine];
        
    }
    return self;
}


- (UIImageView *)imageView{
    if (!_imageView) {
        _imageView =  [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"share_qzone"]];
        [_imageView setContentMode:UIViewContentModeScaleAspectFit];
        
        CGPoint center = self.contentView.center;
        center.y -= 6;
        _imageView.center = center;
    }
    return _imageView;
}


- (UILabel *)titleView{
    if (!_titleView) {
        _titleView = [[UILabel alloc] initWithFrame:CGRectZero];
        _titleView.frame = CGRectMake(0, _imageView.bottom, kShareCellWidth, 30);

        _titleView.textColor = RGBCOLOR(48, 48, 48);
        _titleView.backgroundColor = [UIColor clearColor];
        _titleView.textAlignment = NSTextAlignmentCenter;
        _titleView.font = [UIFont fontWithName:kChineseFontNameXi size:10];

        _titleView.text = @"朋友圈";
    }
    
    return _titleView;
}


-(void)layoutSubviews{


}

@end
