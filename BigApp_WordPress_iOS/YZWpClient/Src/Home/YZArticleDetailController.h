//
//  YZArticleDetailController.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/20.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//
//文章详情

#import "YZBaseViewController.h"

#import "YZHomeViewModel.h"

@interface YZArticleDetailController : YZBaseViewController

@property(nonatomic,strong)NSString *urlString;

@property(nonatomic, strong)YZHomeViewModel *viewModel;

- (instancetype)initWithViewModel:(YZHomeViewModel *)viewModel;
- (instancetype)initWithUrlString:(NSString *)urlString;
- (void)showWriteCommentAction:(UIButton *)button;

@end
