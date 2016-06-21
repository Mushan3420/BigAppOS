//
//  YZLeftViewCell.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/27.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZLeftViewCell.h"


@implementation YZLeftViewCell



- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

   
}

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated{
    [super setHighlighted:highlighted animated:animated];
     self.imageView.highlighted = highlighted;
}

-(void)layoutSubviews
{
    [super layoutSubviews];
    [self.imageView setFrame:CGRectMake(0, self.imageView.frame.origin.y,15, 15)];
    self.imageView.contentMode = UIViewContentModeScaleAspectFit;
    
}

@end
