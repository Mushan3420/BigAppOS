//
//  YZLeftViewController.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/27.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//


#import "ICSDrawerController.h"

@interface YZLeftViewController : UIViewController<ICSDrawerControllerChild, ICSDrawerControllerPresenting>

@property(nonatomic, weak) ICSDrawerController *drawer;


@end
