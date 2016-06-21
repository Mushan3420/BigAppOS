//
//  YZCategoryViewController.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/8/26.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZCategoryViewController.h"
#import "BuildConfig.h"
#import "BYConditionBar.h"
#import "SelectionButton.h"
#import "BYSelectionDetails.h"
#import "BYSelectNewBar.h"

@interface YZCategoryViewController ()

@end

@implementation YZCategoryViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self setupNaviBar];
    
    [self makeContent];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    self.navigationController.navigationBarHidden = NO;
}

-(void)setupNaviBar
{
    self.view.backgroundColor = RGBCOLOR(244, 245, 246);
    
//    [self.navigationController.navigationBar setTranslucent:NO];
//    [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent];
//    [self.navigationController.navigationBar setBackgroundImage:[UIImage imageNamed:@"nav_bg"] forBarMetrics:UIBarMetricsDefault];
//    
//    self.navigationItem.hidesBackButton = YES;
    
    UIButton *rightBtn = [[UIButton alloc]initWithFrame:CGRectMake(kSCREEN_WIDTH-50, 20, 40, 40)];
    rightBtn.backgroundColor = [UIColor blueColor];
    [rightBtn addTarget:self action:@selector(rigthClick:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:rightBtn];
}

- (void)rigthClick:(id)sender
{
    [self.navigationController popViewControllerAnimated:NO];
}


-(void)makeContent
{
    //    BYConditionBar *conditionBar = [[BYConditionBar alloc] initWithFrame:CGRectMake(0, 0, BYScreenWidth, conditionScrollH)];
    //    conditionBar.backgroundColor = [UIColor greenColor];
    //    [self.view addSubview:conditionBar];
    
    
    BYSelectNewBar *selection_newBar = [[BYSelectNewBar alloc] initWithFrame:CGRectMake(0, 74, BYScreenWidth, conditionScrollH)];
    selection_newBar.backgroundColor = RGBCOLOR(244, 245, 246);
    [self.view addSubview:selection_newBar];
    
    
    NSString *plistPath1 = [[NSBundle mainBundle] pathForResource:@"properties" ofType:@"plist"];
    NSMutableArray *listArray = (NSMutableArray *)@[@"推荐",@"热点",@"财经",@"热点",@"财经",@"热点",@"财经",@"热点",@"财经",@"热点",@"财经",@"热点",@"财经",@"热点",@"财经",@"热点",@"财经"];
    
    NSString *plistPath2 = [[NSBundle mainBundle] pathForResource:@"otherProperties" ofType:@"plist"];
    NSArray *otherValues = @[@"推荐",@"热点",@"财经",@"热点",@"财经",@"热点",@"财经",@"热点",@"财经",@"热点",@"财经",@"热点",@"财经",@"热点",@"财经",@"热点",@"财经"];
    
    BYSelectionDetails *selection_details = [[BYSelectionDetails alloc] initWithFrame:CGRectMake(0, conditionScrollH+64, BYScreenWidth, BYScreenHeight-conditionScrollH-64)];
    selection_details.backgroundColor = RGBCOLOR(244, 245, 246);
    [selection_details makeMainContent:listArray otherList:otherValues];
    [self.view insertSubview:selection_details belowSubview:selection_newBar];
    
    
    
    
    //    SelectionButton *arrow = [[SelectionButton alloc] initWithFrame:CGRectMake(BYScreenWidth-arrow_width, 0, arrow_width, conditionScrollH)];
    //    arrow.Detail = selection_details;
    //    arrow.Newbar = selection_newBar;
    //    [self.view addSubview:arrow];
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
