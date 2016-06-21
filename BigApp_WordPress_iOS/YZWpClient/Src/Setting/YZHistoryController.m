//
//  YZHistoryController.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/25.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZHistoryController.h"

@interface YZHistoryController ()

@end

@implementation YZHistoryController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.isBackBarButtonItemShow = YES;
    self.title = NSLocalizedString(@"about_us", nil);
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
