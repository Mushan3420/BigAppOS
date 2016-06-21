//
//  YZMainController.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/19.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ICSDrawerController.h"
#import "XLButtonBarPagerTabStripViewController.h"

@interface YZMainController : XLButtonBarPagerTabStripViewController<UIGestureRecognizerDelegate,ICSDrawerControllerChild, ICSDrawerControllerPresenting>

@property(nonatomic, weak) ICSDrawerController *drawer;
@end
