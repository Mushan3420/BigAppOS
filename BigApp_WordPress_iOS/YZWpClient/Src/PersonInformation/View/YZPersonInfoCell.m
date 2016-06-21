//
//  YZPersonInfoCell.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/15.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZPersonInfoCell.h"

@implementation YZPersonInfoCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

-(void)layoutSubviews
{
    [super layoutSubviews];
    [self.textLabel setFrame:CGRectMake(15, 0,self.frame.size.width, 40)];
    [self.detailTextLabel setFrame:CGRectMake(15, 30,self.frame.size.width-45, 40)];
    [self.detailTextLabel setFont:[UIFont systemFontOfSize:13]];
    self.detailTextLabel.textColor = [UIColor lightGrayColor];
    [self.detailTextLabel setNumberOfLines:0];
}

@end
