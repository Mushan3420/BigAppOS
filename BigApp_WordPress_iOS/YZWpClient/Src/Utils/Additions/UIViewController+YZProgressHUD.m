//
//  UIViewController+YZProgressHUD.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/22.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "UIViewController+YZProgressHUD.h"
#import "MBProgressHUD.h"
@implementation UIViewController (YZProgressHUD)


- (void)showLoadingAddedTo:(UIView *)view
{
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.margin = 10.0f;
    hud.yOffset = 20.0f;
    hud.removeFromSuperViewOnHide = YES;
}

- (void)showLoadingWithTitle:(NSString *)title AddedTo:(UIView *)view
{
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:view animated:YES];
    hud.labelText = title;
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.margin = 10.0f;
    hud.yOffset = 20.0f;
    hud.removeFromSuperViewOnHide = YES;
}


- (void)dismissHuDForView:(UIView *)view
{
    [MBProgressHUD hideAllHUDsForView:view animated:YES];
}

- (void)presentSheet:(NSString *)indiTitle ForView:(UIView *)view
{
    [self dismissHuDForView:view];

    if (![indiTitle isKindOfClass:[NSString class] ] || !indiTitle || indiTitle.length <1)
    {
        return;
    }
    
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:view animated:YES];
    hud.mode = MBProgressHUDModeText;
    hud.detailsLabelText = indiTitle;
    hud.margin = 15;
    hud.yOffset = 20.0f;
    hud.removeFromSuperViewOnHide = YES;
    
    NSTimeInterval delayInterval = 2;
    if ([indiTitle length] > 20)
    {
        delayInterval = 2;
    }
    
    [hud hide:YES afterDelay:delayInterval];
}
@end
