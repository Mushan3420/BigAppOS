//
//  YZArticleDetailBottomBar.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/28.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface YZArticleDetailBottomBar : UIToolbar


@property (nonatomic, strong)UIImageView *iconView;

@property (nonatomic, strong)UIButton *sendCommentBtn;

@property (nonatomic, strong)UIButton *commentLocateBtn;//定位到评论列表位置

@property (nonatomic, strong)UIButton *favBtn;//收藏

@property (nonatomic, strong)UIButton *shareBtn;//分享



@end
