//
//  ViewController.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "ViewController.h"
#import "YZHttpClient.h"
#import "YZLoginModel.h"
#import "YZLoginController.h"
#import "YZHomeService.h"
#import "YZHomeController.h"
#import "YZMainController.h"
#import "YZBaseNavgationController.h"
@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
//    [self loginTest];
//    [self registerTest];
    [self getPostsListTest];
}

- (void)loginTest{
    NSString *url = @"/wp-login.php?yz_app=1&api_route=auth&action=login";
    
    NSDictionary *parameters = @{
                                 @"log" : @"admin",
                                 @"pwd" : @"admin",
                                 @"client_type" : @"2",
                                 @"version" : @"1.0.0"
                                 };
    
    [YZHttpClient requestWithUrl:url
                   requestMethod:AFRequestMethodPost
                      parameters:parameters
                         success:^(id responseObject) {
        
        if ([responseObject isKindOfClass:[NSDictionary class]]) {
            YZLoginModel *loginModel = [YZLoginModel objectWithKeyValues:responseObject];
            NSLog(@"%@",loginModel);
        }
                             
    } failure:^(NSString *statusCode, NSString *error) {
        
    }];
}

- (void)registerTest{
    NSString *url = @"/wp-login.php?yz_app=1&api_route=auth&action=register";
    
    NSDictionary *parameters = @{
                                 @"user_login" : @"zhoutl2121111",
                                 @"password" : @"123123123",
                                 @"user_email" : @"taolin21@163.com",
                                 @"client_type" : @"2",
                                 @"version" : @"1.0.0"
                                 };
    
    [YZHttpClient requestWithUrl:url
                   requestMethod:AFRequestMethodPost
                      parameters:parameters
                         success:^(id responseObject) {
        
    } failure:^(NSString *statusCode, NSString *error) {
        
    }];
}

- (void)getPostsListTest{
  
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (IBAction)login:(id)sender {
    YZLoginController  *loginView = [[YZLoginController alloc] init];
    
    [self presentViewController:loginView animated:YES completion:^{
        
    }];
    
}
- (IBAction)homeView:(id)sender {
    
    YZMainController  *homeView = [[YZMainController alloc] init];
    
    YZBaseNavgationController *navHome = [[YZBaseNavgationController alloc] initWithRootViewController:homeView];
//    homeView.navigationController.navigationBar.translucent = NO;
    
    [self presentViewController:navHome animated:YES completion:^{
        
    }];
}

@end
