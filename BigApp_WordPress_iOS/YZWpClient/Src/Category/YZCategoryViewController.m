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
#import "PlistFileOperation.h"

#import "YZHomeService.h"


@interface YZCategoryViewController ()
{
    BYSelectionDetails                  *selection_details;
    NSMutableArray                      *allOnlineCategoryArr;
    
    NSMutableArray                     *myCategoryArr;
    NSMutableArray                       *otherCategoryArr;
}

@end

@implementation YZCategoryViewController

@synthesize delegate;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self setupNaviBar];
    
//    [self loadNavList];
    
    myCategoryArr = [PlistFileOperation readcategoryWithType:MyCategory];
    otherCategoryArr = [PlistFileOperation readcategoryWithType:MoreCategor];
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
    
    UIButton *rightBtn = [[UIButton alloc]initWithFrame:CGRectMake(kSCREEN_WIDTH-45, 30, 40, 40)];
    [rightBtn setImage:[UIImage imageNamed:@"categoryClose"] forState:UIControlStateNormal];
    [rightBtn addTarget:self action:@selector(rigthClick:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:rightBtn];
}

#pragma mark exit button
//判断退出时是否与进来时一样，如果一样，则不刷新首页

- (BOOL)isSameToComeIn
{
    if (selection_details.views1.count != myCategoryArr.count) {
        return NO;
    }
    
    for (int i = 0; i< selection_details.views1.count; i++) {
        BYSelectionView *seleView = selection_details.views1[i];
        CatoryModel *cateM = seleView.aCategroyModel;
        CatoryModel *cateBefore = myCategoryArr[i];
        if (![cateM isEqual:cateBefore]) {
            return NO;
        }
    }
   
    return YES;
}

- (void)rigthClick:(id)sender
{
    if (selection_details!=nil) {
        
        if (![self isSameToComeIn]) {
            NSMutableArray *my_arr = [[NSMutableArray alloc]initWithCapacity:10];
            NSMutableArray *more_arr = [[NSMutableArray alloc]initWithCapacity:10];
            
            [selection_details.views1 enumerateObjectsUsingBlock:^(BYSelectionView *seleView,NSUInteger indx, BOOL *isStop){
                CatoryModel *cateM = seleView.aCategroyModel;
                [my_arr addObject:cateM];
            }];
            
            [selection_details.views2 enumerateObjectsUsingBlock:^(BYSelectionView *seleView,NSUInteger indx, BOOL *isStop){
                CatoryModel *cateM = seleView.aCategroyModel;
                [more_arr addObject:cateM];
            }];
            
            [PlistFileOperation writeCategory:my_arr categoryType:MyCategory];
            [PlistFileOperation writeCategory:more_arr categoryType:MoreCategor];
            
            [delegate categoryBackArray:my_arr];
        }
        
        
    }
    
    
    [self.navigationController popViewControllerAnimated:NO];
    
    
    
    
}
#pragma mark end

- (void)loadNavList{
    
    [YZHomeService doNavListSuccess:^(NSArray *navListsArray) {
        
        allOnlineCategoryArr = [[NSMutableArray alloc]initWithArray:navListsArray];
        
        [self writeToCategoryFile];
        
        [self makeContent];
        
    } failure:^(NSString *statusCode, NSString *error) {
        [self makeContent];
    }];
}



- (void)writeToCategoryFile
{
    [PlistFileOperation writeCategory:allOnlineCategoryArr categoryType:AllCategory];
    YZNavListModel *firstNavModel = [allOnlineCategoryArr objectAtIndex:0];
    [allOnlineCategoryArr removeObjectAtIndex:0];
    //首先判断是否已经保存我的栏目，没有保存选区前默认都少个栏目
    myCategoryArr = [[NSMutableArray alloc]initWithCapacity:10];
    otherCategoryArr = [[NSMutableArray alloc]initWithCapacity:10];
    if ([PlistFileOperation isExsitMyCategory]) {
        //
        NSMutableArray *my_temp_arr = [PlistFileOperation readcategoryWithType:MyCategory];
        
        //后台增加栏目／减少栏目，修改我的栏目和更多栏目
        [my_temp_arr enumerateObjectsUsingBlock:^(CatoryModel *model, NSUInteger indx,BOOL *isStop){
            BOOL isExitst = NO;
            YZNavListModel *navModel;
            for (int i = 0; i<allOnlineCategoryArr.count; i++) {
                navModel = allOnlineCategoryArr[i];
                if (navModel.ID == model.categoryId) {
                    isExitst = YES;
                    break;
                }
            }
            if (isExitst) {
                [myCategoryArr addObject:model];
                [allOnlineCategoryArr removeObject:navModel];
            }
            
            
        }];
        CatoryModel *model = [[CatoryModel alloc]init];
        model.categoryName = firstNavModel.name;
        model.categoryId = firstNavModel.ID;
        [myCategoryArr insertObject:model atIndex:0];
        
        
        [allOnlineCategoryArr enumerateObjectsUsingBlock:^(YZNavListModel *navModel, NSUInteger indx, BOOL *isStop){
            CatoryModel *model = [[CatoryModel alloc]init];
            model.categoryName = navModel.name;
            model.categoryId = navModel.ID;
            [otherCategoryArr addObject:model];
        }];
        
        
        [PlistFileOperation writeCategory:myCategoryArr categoryType:MyCategory];
        [PlistFileOperation writeCategory:otherCategoryArr categoryType:MoreCategor];
        
    }
    else
    {
        
        [allOnlineCategoryArr enumerateObjectsUsingBlock:^(YZNavListModel *navListModel, NSUInteger idx, BOOL *stop) {
            CatoryModel *model = [[CatoryModel alloc]init];
            model.categoryName = navListModel.name;
            model.categoryId = navListModel.ID;
            if (idx < DefaultCategoryNum) {
                
                [myCategoryArr addObject:model];
            }
            else
            {
                [otherCategoryArr addObject:model];
            }
            
        }];
        [PlistFileOperation writeCategory:myCategoryArr categoryType:MyCategory];
        [PlistFileOperation writeCategory:otherCategoryArr categoryType:MoreCategor];
    }
    
}

-(void)makeContent
{

    BYSelectNewBar *selection_newBar = [[BYSelectNewBar alloc] initWithFrame:CGRectMake(0, 74, BYScreenWidth, conditionScrollH)];
    selection_newBar.backgroundColor = RGBCOLOR(244, 245, 246);
    [self.view addSubview:selection_newBar];
    
    
    
    selection_details = [[BYSelectionDetails alloc] initWithFrame:CGRectMake(0, conditionScrollH+64, BYScreenWidth, BYScreenHeight-conditionScrollH-64)];
    selection_details.backgroundColor = RGBCOLOR(244, 245, 246);
    [selection_details makeMainContent:myCategoryArr otherList:otherCategoryArr];
    [self.view insertSubview:selection_details belowSubview:selection_newBar];
    
    
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
