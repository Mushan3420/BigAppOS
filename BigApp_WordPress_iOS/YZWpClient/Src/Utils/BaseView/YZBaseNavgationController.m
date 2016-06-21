//
//  YZBaseNavgationController.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/19.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZBaseNavgationController.h"
#import "UIImage+SNAdditions.h"
@interface YZBaseNavgationController ()<UINavigationControllerDelegate,UIGestureRecognizerDelegate>

@end

//需要隐藏navigationBar的controller
static NSArray *hiddenNavBarClassArray = nil;
@implementation YZBaseNavgationController

+ (void)initialize
{
    hiddenNavBarClassArray = @[NSClassFromString(@"YZMainController"),NSClassFromString(@"YZSearchController")];
//    hiddenNavBarClassArray = @[NSClassFromString(@"YZSearchController")];

}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
//    if ([self.navigationBar respondsToSelector:@selector(setBackgroundImage:forBarMetrics:)]) {
//        [self.navigationBar setBackgroundImage:[UIImage streImageNamed:@"white_image"] forBarMetrics:UIBarMetricsDefault];
//    }
    
//    [[UINavigationBar appearance] setShadowImage:[UIImage streImageNamed:@"separatory_line"]];
    self.navigationBar.translucent = NO;
    
    self.delegate = self;
    
//    [self fullScreenPanBack];//全屏滑动返回
}

- (void)fullScreenPanBack{
//    id target = self.interactivePopGestureRecognizer.delegate;
//    // 创建全屏滑动手势，调用系统自带滑动手势的target的action方法
//    UIPanGestureRecognizer *pan = [[UIPanGestureRecognizer alloc] initWithTarget:target action:@selector(handleNavigationTransition:)];
//    // 设置手势代理，拦截手势触发
//    pan.delegate = self;
//    // 给导航控制器的view添加全屏滑动手势
//    [self.view addGestureRecognizer:pan];
//    // 禁止使用系统自带的滑动手势
//    self.interactivePopGestureRecognizer.enabled = NO;

}

- (void)navigationController:(UINavigationController *)navigationController
      willShowViewController:(UIViewController *)viewController
                    animated:(BOOL)animated
{
    Class vcClass = [viewController class];
    
    if ([hiddenNavBarClassArray containsObject:vcClass]){
        [navigationController setNavigationBarHidden:YES animated:YES];
    }
    else{
        [navigationController setNavigationBarHidden:NO animated:YES];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}


@end
