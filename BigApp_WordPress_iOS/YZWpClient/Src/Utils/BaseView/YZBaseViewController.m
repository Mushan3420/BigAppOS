//
//  YZBaseViewController.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/18.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZBaseViewController.h"
@interface YZBaseViewController ()

@end

@implementation YZBaseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
 
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    if (self.isBackBarButtonItemShow ){

        [[UINavigationBar appearance] setBarTintColor:[[ThemeManager sharedInstance] colorWithColorName:kTmNavBarColor]];
        
        UIButton *button = [YZButton buttonWithType:UIButtonTypeCustom];
        [button setImage:[UIImage imageNamed:@"nav_back"] forState:UIControlStateNormal];
        [button setImage:[UIImage imageNamed:@"nav_back"] forState:UIControlStateHighlighted];
        button.frame = CGRectMake(0, 0, AutoDeviceWidth(57.0/2),AutoDeviceWidth(57/2.0));
        button.contentEdgeInsets = UIEdgeInsetsMake(0, -15, 0, 0);
        [button addTarget:self action:@selector(backAction) forControlEvents:UIControlEventTouchUpInside];
        UIBarButtonItem *backItem = [[UIBarButtonItem alloc] initWithCustomView:button];
        
        YZButton *but = (YZButton *)button;
        [but setThemeButtonBackgroudColor:kTmClearColor];
        [but setThemeButtonImage:@"nav_back@2x"];
        [but openThemeSkin];
        
        self.navigationItem.leftBarButtonItem = backItem;
    }
    
    self.navigationController.interactivePopGestureRecognizer.delegate = self;
    
//    [Analysis beginLogPageView:NSStringFromClass([self class])];
}

- (void)setRightItemTitle:(NSString *)itemName
{
    YZButton *_clearAllBtn = [YZButton buttonWithType:UIButtonTypeCustom];
    
    [_clearAllBtn setTitle:itemName forState:UIControlStateNormal];
    _clearAllBtn.titleLabel.font = [UIFont systemFontOfSize:14];
    _clearAllBtn.frame = CGRectMake(0, 0, 40,40);
    [_clearAllBtn addTarget:self action:@selector(rightClick:) forControlEvents:UIControlEventTouchUpInside];
    
    [_clearAllBtn setThemeButtonBackgroudColor:kTmClearColor];
    [_clearAllBtn setThemeButtonImage:nil];
    [_clearAllBtn setthemeTextColor:kTmViewBackgroudColor];
    [_clearAllBtn openThemeSkin];
    
    UIBarButtonItem *fontSetItem = [[UIBarButtonItem alloc] initWithCustomView:_clearAllBtn];
    self.navigationItem.rightBarButtonItem = fontSetItem;

}

- (void)rightClick:(id)sender
{
    _RightBarButtonItem(1);
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    //页面结束
//    [Analysis endLogPageView:NSStringFromClass([self class])];
      self.navigationController.interactivePopGestureRecognizer.delegate = nil;
}

- (void)backAction{
    if (self.navigationController.viewControllers.count > 1) {
        [self.navigationController popViewControllerAnimated:YES];
    }else{
        [self dismissViewControllerAnimated:YES completion:nil];
    }
}


- (void)setTitle:(NSString *)title {
    [super setTitle:title];

    YZLabel *titleLabel = [[YZLabel alloc]init];
    titleLabel.font = [UIFont fontWithName:kChineseFontNameXi size:18];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.textColor = RGBCOLOR(60, 60, 60);
    titleLabel.text = title;
    [titleLabel sizeToFit];
    
    [titleLabel setThemeLabBackgroundColor:kTmClearColor];
    [titleLabel setThemeTextColor:kTmNavTitleColor];
    [titleLabel openThemeSkin];
    
    self.navigationItem.titleView = titleLabel;
}

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
{
    if (self.navigationController.viewControllers.count == 1){
        return NO;
    }
    else{
        return YES;
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}



#pragma mark - XLPagerTabStripViewControllerDelegate

-(NSString *)titleForPagerTabStripViewController:(XLPagerTabStripViewController *)pagerTabStripViewController
{

    return self.title;
}

-(UIColor *)colorForPagerTabStripViewController:(XLPagerTabStripViewController *)pagerTabStripViewController
{
    return [UIColor whiteColor];
}
@end
