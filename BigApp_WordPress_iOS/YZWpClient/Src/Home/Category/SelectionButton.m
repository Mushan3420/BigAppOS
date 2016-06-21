//
//  SelectionButton.m
//  BYDailyNews
//
//  Created by bassamyan on 15/1/18.
//  Copyright (c) 2015年 apple. All rights reserved.
//

#import "SelectionButton.h"
#import "BYSelectNewBar.h"
#import "BYSelectionDetails.h"


@implementation SelectionButton


/******************************
 
 初始化ArrowButton
 
 ******************************/
- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setImage:[UIImage imageNamed:@"Arrow.png"] forState:0];
        [self setImage:[UIImage imageNamed:@"Arrow.png"] forState:1<<0];
        self.backgroundColor = Color_maingray;
        [self addTarget:self
                 action:@selector(ArrowClick)
       forControlEvents:1 << 6];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(ArrowClick)
                                                     name:@"arrow_change"
                                                   object:nil];
    }
    return self;
}

-(void)ArrowClick{
//    self.Newbar.hidden = (self.Detail.frame.origin.y<0)?NO:YES;
//    [UIView animateWithDuration:arrow_animation_time animations:^{
//        CGAffineTransform rotation = self.imageView.transform;
//        self.imageView.transform = CGAffineTransformRotate(rotation,M_PI);
//        self.Detail.transform = (self.Detail.frame.origin.y<0)?CGAffineTransformMakeTranslation(0, BYScreenHeight):CGAffineTransformMakeTranslation(0, -BYScreenHeight);
//    }];
}
- (CGRect)imageRectForContentRect:(CGRect)contentRect{
    CGFloat image_width = 18;
    return CGRectMake((contentRect.size.width-image_width)/2, (conditionScrollH-image_width)/2, image_width, image_width);
}


@end

// 版权属于原作者
// http://code4app.com (cn) http://code4app.net (en)
// 发布代码于最专业的源码分享网站: Code4App.com 
