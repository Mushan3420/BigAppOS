//
//  YZLauchViewController.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/3.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZLauchViewController.h"
#import "YZMainController.h"
#import "YZLeftViewController.h"
#import "YZBaseNavgationController.h"
#import "BuildConfig.h"
#import "UIViewExt.h"
@interface YZLauchViewController ()
{
    UINavigationBar             *navBar;
}

@property (nonatomic, strong)UIImageView *remoteImage;

@end

@implementation YZLauchViewController

#pragma mark - ViewController Life


- (void)loadView
{
    UIImageView *view = [[UIImageView alloc] initWithFrame:[UIScreen mainScreen].applicationFrame];

    NSDictionary* launchNames = @{@(480):@"LaunchImage-700@2x.png",
                                  @(568):@"LaunchImage-700-568h@2x.png",
                                  @(667):@"LaunchImage-800-667h@2x.png",
                                  @(736):@"LaunchImage-800-Portrait-736h@3x.png"
                                  
                                  };

    NSString *imageName = launchNames[@(kSCREEN_HEIGHT)];
    
    view.image = [UIImage imageNamed:imageName];
    self.view = view;

}
- (BOOL)prefersStatusBarHidden
{
    return YES;
}
- (UIImageView *)remoteImage{
    if (!_remoteImage) {
        
        _remoteImage = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"lauch_defult_image.jpg"]];
        _remoteImage.contentMode = UIViewContentModeScaleAspectFill;
        _remoteImage.size = CGSizeMake(kSCREEN_WIDTH, kSCREEN_HEIGHT);
        
    }
    return _remoteImage;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor purpleColor];
     [self performSelector:@selector(loadMainView) withObject:nil afterDelay:2.0];
    
    [self.view addSubview:self.remoteImage];
    
}


- (void)loadMainView{
    
    YZMainController  *mainView = [[YZMainController alloc] init];
    
    
    
    YZBaseNavgationController *navHome = [[YZBaseNavgationController alloc] initWithRootViewController:mainView];
    navBar = navHome.navigationBar;
    YZLeftViewController *colorsVC = [[YZLeftViewController alloc] init];

    
    ICSDrawerController *drawer = [[ICSDrawerController alloc] initWithLeftViewController:colorsVC
                                                                     centerViewController:navHome];
    

    
    self.view.window.rootViewController = drawer;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - Private Method


@end
