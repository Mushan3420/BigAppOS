//
//  YZCommentController.h
//  YZWpClient
//
//  Created by zhoutl on 15/8/12.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//  匿名用户评论

#import "YZBaseViewController.h"
#import "YZArticleDetailController.h"
@interface YZCommentController : YZBaseViewController
@property (nonatomic, weak)YZArticleDetailController *delegate;

@end
