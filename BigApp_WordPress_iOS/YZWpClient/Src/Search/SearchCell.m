//
//  SearchCell.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/8.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "SearchCell.h"
#import "BuildConfig.h"
#import "UIViewExt.h"
#import "UIImage+SNAdditions.h"

@implementation SearchCell

- (void)awakeFromNib {
    // Initialization code
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.backgroundColor = [UIColor whiteColor];
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        [self setThemeCellBackgroudColor:kTmMainCellBackgroudColor];
        [self openThemeSkin];
        
        UIImageView *line = [[UIImageView alloc] initWithImage:[UIImage streImageNamed:@"separatory_line"]];
        line.top = kSearchHistoryCellRow - 0.5;
        line.width = kSCREEN_WIDTH;
        [self.contentView addSubview:line];
        
        UIButton *arrowBtn = [[UIButton alloc]initWithFrame:CGRectMake(kSCREEN_WIDTH-40, 0, 36, 44)];
        arrowBtn.backgroundColor = [UIColor clearColor];
        [arrowBtn setImage:[UIImage imageNamed:@"search_history_arrow"] forState:UIControlStateNormal];
        arrowBtn.imageView.contentMode = UIViewContentModeScaleToFill;
        [self.contentView addSubview:arrowBtn];
        [arrowBtn addTarget:self action:@selector(titleClick:) forControlEvents:UIControlEventTouchUpInside];
        
    }
    return self;
}

- (void)titleClick:(id)sender
{
    _searchTitleBlock(self.textLabel.text);
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
