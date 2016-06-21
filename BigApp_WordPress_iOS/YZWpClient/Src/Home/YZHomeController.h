//
//  YZHomeController.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/17.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//
//首页

#import <UIKit/UIKit.h>
#import "YZBaseViewController.h"


@class YZNavListModel;
@interface YZHomeController : YZBaseViewController

@property(nonatomic, assign)BOOL isBannerShow;

@property(nonatomic, weak)UIViewController *pagasContainer;

@property(nonatomic, strong)YZNavListModel *categoryModel;//当前分类

- (void)loadRereshing;

- (void)releseViewAndData;

@end
