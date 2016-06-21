//
//  YZOffLineDownloadController.m
//  YZWpClient
//
//  Created by zhoutl on 15/10/20.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZOffLineDownloadController.h"

@interface YZOffLineDownloadController ()

@end

@implementation YZOffLineDownloadController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.isBackBarButtonItemShow = YES;
    self.title = LocalizedString(@"OFFLINE_CACHE");

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}



@end
