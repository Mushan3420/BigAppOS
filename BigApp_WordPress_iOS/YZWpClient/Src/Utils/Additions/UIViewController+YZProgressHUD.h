//
//  UIViewController+YZProgressHUD.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/22.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIViewController (YZProgressHUD)

- (void)showLoadingAddedTo:(UIView *)view;

- (void)showLoadingWithTitle:(NSString *)title AddedTo:(UIView *)view;

- (void)dismissHuDForView:(UIView *)view;

- (void)presentSheet:(NSString *)indiTitle ForView:(UIView *)view;

@end
