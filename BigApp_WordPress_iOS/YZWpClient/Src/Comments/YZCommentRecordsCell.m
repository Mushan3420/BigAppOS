//
//  YZDetailCommentCell.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/28.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//
//  文章详情评论

#import "YZCommentRecordsCell.h"
#import "UIImage+SNAdditions.h"
#import "UIViewExt.h"
#import "BuildConfig.h"

#import "YZCommentRecordsViewModel.h"

@implementation YZCommentRecordsCell
{
    UIImageView *_line;
}
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        _line = [[UIImageView alloc] initWithImage:[UIImage streImageNamed:@"separatory_line"]];
        _line.left =20;
        _line.width = kSCREEN_WIDTH;
        [self.contentView addSubview:_line];
        
        
        self.textLabel.font = [UIFont fontWithName:kChineseBoldFontName size:13];
        self.textLabel.textColor =RGBCOLOR(40, 40, 40);
        
        self.detailTextLabel.font = [UIFont fontWithName:kChineseFontNameXi size:15];
        self.detailTextLabel.textColor = RGBCOLOR(153, 153, 153);
        self.detailTextLabel.numberOfLines = 0;
        self.detailTextLabel.textAlignment = NSTextAlignmentLeft;
        
        self.imageView.contentMode = UIViewContentModeScaleAspectFit;
        
        [self.contentView addSubview:self.dataLabel];
        [self.contentView addSubview:self.openMoreBtn];
        [self.contentView addSubview:self.postTitleView];

        
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

- (void)setViewModel:(YZCommentRecordsViewModel *)viewModel{
    if (_viewModel != viewModel) {
        _viewModel = viewModel;
        self.textLabel.text = _viewModel.userNameText?_viewModel.userNameText:LocalizedString(@"NICK_NAME");
        self.detailTextLabel.text = _viewModel.detailText;
        
        _openMoreBtn.selected = _viewModel.isRealHeight;
        [_postTitleView setTitle:_viewModel.model.post_info.title forState:UIControlStateNormal];
        
    }
}

#pragma mark --

-(UIButton *)postTitleView{
    if (!_postTitleView) {
        
        _postTitleView = [UIButton buttonWithType:UIButtonTypeCustom];
        _postTitleView.titleLabel.font = [UIFont fontWithName:kChineseFontNameXi size:14];
        [_postTitleView setTitleColor:RGBCOLOR(44, 44, 44) forState:UIControlStateNormal];
        _postTitleView.backgroundColor = RGBCOLOR(249, 248, 248);
        [_postTitleView setTitle:@"这个骠骑是标题" forState:UIControlStateNormal];
        _postTitleView.contentHorizontalAlignment=UIControlContentHorizontalAlignmentLeft ;//设置文字位置，现设为居左，默认的是居中
        _postTitleView.contentEdgeInsets = UIEdgeInsetsMake(0, 10, 0, 0);
        _postTitleView.layer.borderWidth = 0.5;
        _postTitleView.layer.borderColor = [RGBCOLOR(218, 218, 218) CGColor];

    }
    return _postTitleView;
}
- (UIButton *)openMoreBtn{
    if (!_openMoreBtn) {
        _openMoreBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        
        [_openMoreBtn setTitle:@"展开" forState:UIControlStateNormal];
        [_openMoreBtn setTitle:@"收起" forState:UIControlStateSelected];
        [_openMoreBtn setTitleColor:  RGBCOLOR(153, 153, 153) forState:UIControlStateNormal];
        _openMoreBtn.titleLabel.font = [UIFont fontWithName:kChineseFontNameXi size:10];
         _openMoreBtn.contentHorizontalAlignment=UIControlContentHorizontalAlignmentRight ;//设置文字位置，现设为居左，
        _openMoreBtn.enabled = YES;
        
    }
    return _openMoreBtn;
}

-(UILabel *)dataLabel{
    if (!_dataLabel) {
        
        _dataLabel = [[UILabel alloc] init];
        _dataLabel.text = @"07-16";
        _dataLabel.font = [UIFont fontWithName:kChineseFontNameXi size:10];
        _dataLabel.textColor = RGBCOLOR(153, 153, 153);
        _dataLabel.textAlignment = NSTextAlignmentRight;
        
    }
    return _dataLabel;
}

#pragma mark --

- (void)setContentSubviewFrames{
    
    self.imageView.frame = CGRectMake(25, kCommmetCellMarginTop, self.imageView.image.size.width, 15);
    
    self.detailTextLabel.frame = _viewModel.detailLabelFrame;
    self.postTitleView.frame = _viewModel.postViewFrame;
    
    self.dataLabel.frame  = CGRectMake(self.width-15-60, kCommmetCellMarginTop, 60, 11);
    
    self.openMoreBtn.frame = CGRectMake(self.width-20-28, self.detailTextLabel.bottom - 20, 28, 13);

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
