//
//  YZHomeCell.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/17.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZHomeCell.h"
#import "BuildConfig.h"
#import "UIImage+SNAdditions.h"
#import "UIViewExt.h"
#import "UIImageView+AFNetworking.h"

@implementation YZHomeCell
{
    UIImageView *_line;
}
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        [self setThemeCellBackgroudColor:kTmMainCellBackgroudColor];
        [self openThemeSkin];

        
        _line = [[YZImageView alloc] initWithImage:[UIImage streImageNamed:@"separatory_line"]];
        [self.contentView addSubview:_line];


        [self.contentView addSubview:self.imageListView];
        
        [self.contentView addSubview:self.titleTextLabel];
        [self.contentView addSubview:self.commentsTextLabel];
        [self.contentView addSubview:self.articleDataLabel];
        
    }
    return self;
}

- (void)setHighlighted:(BOOL)highlighted animated:(BOOL)animated
{
    [super setHighlighted:highlighted animated:animated];
    [self setBgStatus:highlighted];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];
    [self setBgStatus:selected];
}


#pragma mark-- Propertys

- (UIView *)imageListView
{
    if (!_imageListView) {
        _imageListView = [[UIView alloc] initWithFrame:CGRectZero];
        _imageListView.backgroundColor = [UIColor clearColor];
    }
    return _imageListView;
}

- (UILabel *)titleTextLabel
{
    if (!_titleTextLabel) {
        _titleTextLabel = [[YZLabel alloc] initWithFrame:CGRectZero];
        [_titleTextLabel setFont:[UIFont systemFontOfSize:16]];
        [_titleTextLabel setTextColor:RGBCOLOR(48, 48, 48)];
        [_titleTextLabel setNumberOfLines:0];
        [_titleTextLabel setTextAlignment:NSTextAlignmentLeft];
        _titleTextLabel.text = @"[v]移动医疗在硅谷】RockHealth、CellScop";
        _titleTextLabel.backgroundColor = [UIColor clearColor];
        _titleTextLabel.lineBreakMode = NSLineBreakByCharWrapping;
        
        YZLabel *lab = (YZLabel *)_titleTextLabel;
        [lab setThemeLabBackgroundColor:kTmClearColor];
        [lab setThemeTextColor:kTmMainCellTitleColor];
        [lab openThemeSkin];

    }
    return _titleTextLabel;
}


- (UILabel *)commentsTextLabel
{
    if (!_commentsTextLabel) {
        _commentsTextLabel = [[YZLabel alloc] initWithFrame:CGRectZero];
//        [_abstractTextLabel setFont:[UIFont systemFontOfSize:14]];
        [_commentsTextLabel setFont:[UIFont fontWithName:kChineseFontNameXi size:11]];
        [_commentsTextLabel setTextColor:[UIColor grayColor]];
        [_commentsTextLabel setNumberOfLines:0];
        [_commentsTextLabel setTextAlignment:NSTextAlignmentLeft];
        _commentsTextLabel.text = @"评论数";
        
        YZLabel *lab = (YZLabel *)_commentsTextLabel;
        [lab setThemeLabBackgroundColor:kTmClearColor];
        [lab setThemeTextColor:kTmMainCellDetailTitleColor];
        [lab openThemeSkin];
        
    }
    return _commentsTextLabel;
}

- (UILabel *)articleDataLabel
{
    if (!_articleDataLabel) {
        _articleDataLabel = [[YZLabel alloc] initWithFrame:CGRectZero];
        [_articleDataLabel setFont:[UIFont systemFontOfSize:11]];
        [_articleDataLabel setTextColor:RGBCOLOR(213, 213, 213)];
        _articleDataLabel.text = @"07-15";
        _articleDataLabel.backgroundColor = [UIColor clearColor];
        
        YZLabel *lab = (YZLabel *)_articleDataLabel;
        [lab setThemeLabBackgroundColor:kTmClearColor];
        [lab setThemeTextColor:kTmMainCellTimeColor];
        [lab openThemeSkin];
    }
    return _articleDataLabel;
}



#pragma mark-- Methods

- (void)layoutSubviews{
    _line.frame = CGRectMake(10, _viewModel.cellHeight-1, kSCREEN_WIDTH-20, _line.height);
    
    _imageListView.frame       = _viewModel.imageListViewFrame;
    _titleTextLabel.frame      = _viewModel.titleLabelFrame;
    _commentsTextLabel.frame = _viewModel.abstractLabelFrame;
    _articleDataLabel.frame    = _viewModel.articleDataLabelFrame;
    
    
    [self addImageList];
}


- (void)setViewModel:(YZHomeViewModel *)viewModel{
    if (_viewModel != viewModel) {
        _viewModel = viewModel;
        
        
        //页面赋值
        _titleTextLabel.text = _viewModel.titleText;
        _commentsTextLabel.text = _viewModel.commentsText;
        _articleDataLabel.text = _viewModel.articleDataText;
        
        [self handelAttributedString:_commentsTextLabel];

    }
}

- (void)setBgStatus:(BOOL)bol
{
    
    UIColor *color = nil;
    
    if (bol) {
        color = [[ThemeManager sharedInstance] colorWithColorName:kTmMainCellSelectBackgroudColor];
    }
    else {
        color = [[ThemeManager sharedInstance] colorWithColorName:kTmMainCellBackgroudColor];
    }
    
    self.contentView.backgroundColor = color;
    
    
}

- (void)handelAttributedString:(UILabel*)label{
    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithString:label.text];
    [attributedString addAttribute:NSForegroundColorAttributeName
                             value:(id)self.commentsTextLabel.textColor
                             range:NSMakeRange(label.text.length - 1, 1)];
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc]init];
    paragraphStyle.lineSpacing = 6;
    //        paragraphStyle.alignment = NSTextAlignmentCenter;
    
    [attributedString addAttribute:NSParagraphStyleAttributeName
                             value:paragraphStyle
                             range:NSMakeRange(0, label.text.length)];
    
    label.attributedText = attributedString;

}



- (void)addImageList{
    [_imageListView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    _imageListView.backgroundColor = [UIColor clearColor];
    
    if (_viewModel.imageCountsType == OneImageType) {
        
        _imageListView.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"defaultImage"]];
        
        if (_viewModel.featuredImages.count > 0) {
            
            UIImageView *imageview = _viewModel.featuredImages.firstObject;
            imageview.frame = _imageListView.bounds;
            [_imageListView addSubview:imageview];
            
        }
        
    }
    else if (_viewModel.imageCountsType == MoreThanOneImageType) {
        
        CGFloat imageSpace = 8;
        CGFloat imageWidth =  (_viewModel.imageListViewFrame.size.width - imageSpace*2)/3.0;
        
        for (int i = 0; i<_viewModel.featuredImages.count; i++) {
            UIImageView *imageView = _viewModel.featuredImages[i];
            imageView.frame = CGRectMake(i * (imageWidth+imageSpace), 0, imageWidth, (111));
            [_imageListView addSubview:imageView];
        }
        
    }

}



@end
