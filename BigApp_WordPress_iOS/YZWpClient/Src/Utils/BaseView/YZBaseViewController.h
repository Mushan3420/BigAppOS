//
//  YZBaseViewController.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/18.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BuildConfig.h"
#import "XLPagerTabStripViewController.h"
@interface YZBaseViewController : UITableViewController<UIGestureRecognizerDelegate,XLPagerTabStripChildItem>

@property(nonatomic, assign)BOOL isBackBarButtonItemShow;


@property (nonatomic,strong)void(^RightBarButtonItem)(NSInteger type);

- (void)setRightItemTitle:(NSString *)itemName;

@end
