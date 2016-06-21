//
//  YZArticleDetailBottomBar.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/28.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZArticleDetailBottomBar.h"
#import "BuildConfig.h"

#define kBarButtonWidth (kSCREEN_WIDTH/8.0)-5
#define kBarButtonHeight 50

@implementation YZArticleDetailBottomBar
- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    
    
    if (self) {

         UIBarButtonItem *sendCommentBtnItem = [[UIBarButtonItem alloc] initWithCustomView:self.sendCommentBtn];
         UIBarButtonItem *commentLocateBtnBtnItem = [[UIBarButtonItem alloc] initWithCustomView:self.commentLocateBtn];
         UIBarButtonItem *favBtnBtnItem = [[UIBarButtonItem alloc] initWithCustomView:self.favBtn];
         UIBarButtonItem *shareBtnBtnItem = [[UIBarButtonItem alloc] initWithCustomView:self.shareBtn];
        
         self.items = @[sendCommentBtnItem,
                        commentLocateBtnBtnItem,
                        favBtnBtnItem,
                        shareBtnBtnItem];
    }
    
    return self;
}

- (UIImageView *)iconView{
    if (!_iconView) {
        _iconView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"home_edit_commetn_button"]];
        _iconView.contentMode = UIViewContentModeScaleAspectFit;
        _iconView.frame = CGRectMake(0, 0, _iconView.frame.size.width, kBarButtonHeight);
    }
    return _iconView;
}

-(UIButton *)sendCommentBtn
{
    if (!_sendCommentBtn) {
        _sendCommentBtn = [UIButton buttonWithType:UIButtonTypeCustom];
//        [_sendCommentBtn setImage:[UIImage imageNamed:@""] forState:UIControlStateNormal];
//        [_sendCommentBtn setImage:[UIImage imageNamed:@""] forState:UIControlStateHighlighted];
        _sendCommentBtn.frame = CGRectMake(0, 0, kSCREEN_WIDTH/2.0, kBarButtonHeight);
        [_sendCommentBtn setTitle:LocalizedString(@"PUBLISH_COMMENT") forState:UIControlStateNormal];
        [_sendCommentBtn setTitleColor:RGBCOLOR(153, 153, 153) forState:UIControlStateNormal];
        [_sendCommentBtn setTitleColor:RGBCOLOR(48, 48, 48) forState:UIControlStateHighlighted];
        _sendCommentBtn.titleLabel.font = [UIFont fontWithName:kChineseFontNameXi size:14];
        _sendCommentBtn.contentEdgeInsets =  UIEdgeInsetsMake(0, AutoDeviceWidth(-60), 0, 0);
        
        [_sendCommentBtn addSubview:self.iconView];
    }
    
    return _sendCommentBtn;
}


-(UIButton *)commentLocateBtn
{
    if (!_commentLocateBtn) {
        _commentLocateBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_commentLocateBtn setImage:[UIImage imageNamed:@"home_detail_comment_button"] forState:UIControlStateNormal];
        _commentLocateBtn.frame = CGRectMake(0, 0, kBarButtonWidth, kBarButtonHeight);
    }
    
    return _commentLocateBtn;
}


-(UIButton *)favBtn
{
    if (!_favBtn) {
        _favBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_favBtn setImage:[UIImage imageNamed:@"home_detail_collection"] forState:UIControlStateNormal];
        [_favBtn setImage:[UIImage imageNamed:@"home_detail_collection_down"] forState:UIControlStateHighlighted];
        [_favBtn setImage:[UIImage imageNamed:@"home_detail_collection_down"] forState:UIControlStateSelected];
        _favBtn.frame = CGRectMake(0, 0, kBarButtonWidth, kBarButtonHeight);
    }
    
    return _favBtn;
}

-(UIButton *)shareBtn
{
    if (!_shareBtn) {
        _shareBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_shareBtn setImage:[UIImage imageNamed:@"home_detail_share"] forState:UIControlStateNormal];
//        [_shareBtn setImage:[UIImage imageNamed:@"home_detail_share"] forState:UIControlStateHighlighted];
        _shareBtn.frame = CGRectMake(0, 0, kBarButtonWidth, kBarButtonHeight);
    }
    
    return _shareBtn;
}

@end
