//
//  YZDetailCommentCell.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/28.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//
//  文章详情评论

#import "YZDetailCommentCell.h"
#import "UIImage+SNAdditions.h"
#import "UIViewExt.h"
#import "BuildConfig.h"
#import "UIColor+SNAdditions.h"
#import "YZDetailCommentViewModel.h"



@implementation YZDetailCommentCell
{
    UIImageView *_line;
    YZImageView *_cornerCircle;
}
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        _line = [[UIImageView alloc] initWithFrame:CGRectMake(25, 0, kSCREEN_WIDTH-50, 0.5)];
//        _line.left =25;
//        _line.width = kSCREEN_WIDTH - 50;
        _line.backgroundColor = [[ThemeManager sharedInstance]colorWithColorName:kTmDetailCellSeperColor];
        [self.contentView addSubview:_line];
        
        
        self.textLabel.font = [UIFont fontWithName:kChineseBoldFontName size:14];
        
        
        self.detailTextLabel.font = [UIFont fontWithName:kChineseFontNameXi size:15];
        self.detailTextLabel.numberOfLines = 0;
        self.detailTextLabel.textAlignment = NSTextAlignmentLeft;
        
        
        self.imageView.contentMode = UIViewContentModeScaleAspectFit;
        self.imageView.image = [UIImage imageNamed:@"default_user_icon"];

        
        _cornerCircle = [[YZImageView alloc] init];
        
        [_cornerCircle setThemeBackgroudImage:@"corner_circle@2x"];
        [_cornerCircle openThemeSkin];
        
        [self.imageView addSubview:_cornerCircle];
        
        [self.contentView addSubview:self.dataLabel];

        [self setThemeCellBackgroudColor:kTmDetailCellBackgroudColor];
        [self setThemeCellTextColor:kTmDetailCellTextColor];
        [self setThemeCellDetailTextColor:kTmDetailCellDetailTextColor];
        [self openThemeSkin];
        
    }
    return self;
}

- (void)setLineHidden:(BOOL)lineHidden{
    if (_lineHidden != lineHidden) {
        _lineHidden = lineHidden;
        
        _line.hidden = lineHidden;
    }
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
 
}


- (void)layoutSubviews{
    [self setContentSubviewFrames];
}

- (void)setViewModel:(YZDetailCommentViewModel *)viewModel{
    if (_viewModel != viewModel) {
        _viewModel = viewModel;
        self.textLabel.text = _viewModel.userNameText?_viewModel.userNameText:LocalizedString(@"NICK_NAME");
        self.detailTextLabel.text = _viewModel.detailText;
        self.dataLabel.text = _viewModel.dataText;
        
        _openMoreBtn.selected = _viewModel.isRealHeight;
        
    }
}

#pragma mark --
- (UIButton *)openMoreBtn{
    if (!_openMoreBtn) {
        _openMoreBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        
        [_openMoreBtn setTitle:@"展开" forState:UIControlStateNormal];
        [_openMoreBtn setTitle:@"收起" forState:UIControlStateSelected];
        [_openMoreBtn setTitleColor:  RGBCOLOR(153, 153, 153) forState:UIControlStateNormal];
        _openMoreBtn.titleLabel.font = [UIFont fontWithName:kChineseFontNameXi size:10];
        
    }
    return _openMoreBtn;
}

-(UILabel *)dataLabel{
    if (!_dataLabel) {
        
        _dataLabel = [[YZLabel alloc] init];
        _dataLabel.text = @"07-16";
        _dataLabel.font = [UIFont fontWithName:kChineseFontNameXi size:10];
      
        _dataLabel.textAlignment = NSTextAlignmentLeft;
        
        YZLabel *lab = (YZLabel *)_dataLabel;
        [lab setThemeLabBackgroundColor:kTmClearColor];
        [lab setThemeTextColor:kTmDetailCellTimeTextColor];
        [lab openThemeSkin];
        
          _dataLabel.textColor = RGBCOLOR(153, 153, 153);
        
    }
    return _dataLabel;
}

#pragma mark --

- (void)setContentSubviewFrames{
    
    self.imageView.frame = CGRectMake(25, kCommmetCellMarginTop, 40, 40);
    
    _cornerCircle.frame = self.imageView.bounds;
    
    self.textLabel.frame = _viewModel.userNameLabelFrame;

    self.dataLabel.frame  = _viewModel.dataTextLabelFrame;

    
    self.detailTextLabel.frame = _viewModel.detailLabelFrame;;
    
    
    self.openMoreBtn.frame = CGRectMake(self.width-15-28, self.height - 20, 28, 13);

    self.openMoreBtn.hidden = !_viewModel.isOpenShow;

}


- (NSInteger)textHeight:(NSString *)text width:(NSInteger)width{
    
    UIFont *font = [UIFont fontWithName:kChineseFontNameXi size:15];
    
    CGSize size = CGSizeMake(width, MAXFLOAT);
    
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    
    [paragraphStyle setLineSpacing:2];
    
    NSDictionary * dic = [NSDictionary dictionaryWithObjectsAndKeys:font, NSFontAttributeName,
                          paragraphStyle,NSParagraphStyleAttributeName,nil];
    
    size =[text boundingRectWithSize:size
                             options:NSStringDrawingUsesLineFragmentOrigin |NSStringDrawingUsesFontLeading attributes:dic
                             context:nil].size;
    return size.height;
}


@end
