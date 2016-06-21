//
//  YZHistoryCell.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/12.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZHistoryCell.h"

#import "BuildConfig.h"
#import "UIViewExt.h"
#import "UIImage+SNAdditions.h"

@implementation YZHistoryCell

- (void)awakeFromNib {
    // Initialization code
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.backgroundColor = [UIColor whiteColor];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        self.textLabel.backgroundColor = [UIColor clearColor];
        [self setThemeCellBackgroudColor:kTmMainCellBackgroudColor];
        [self openThemeSkin];
        
        YZView *line = [[YZView alloc] initWithFrame:CGRectMake(0, kSearchHistoryCellRow-1, kSCREEN_WIDTH, 1)];
        [line setThemeViewBackgroudColor:kTmDetailCellSeperColor];
        [line openThemeSkin];
        
        [self addSubview:line];
        
        self.textLabel.font = [UIFont systemFontOfSize:14];
        
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
