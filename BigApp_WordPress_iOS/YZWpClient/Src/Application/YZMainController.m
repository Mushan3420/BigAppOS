	//
//  YZMainController.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/19.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZMainController.h"
#import "YZHomeController.h"
#import "YZLoginController.h"
#import "YZHomeService.h"
#import "UIViewExt.h"
#import "UIImage+SNAdditions.h"
#import "UIViewController+YZProgressHUD.h"
#import "YZNavListModel.h"
#import "YZLinkViewController.h"
#import "YZArticleDetailController.h"
#import "YZSearchController.h"
#import "AppConfigManager.h"
#import "YZCategoryViewController.h"
#import "YZSettingController.h"

#import "CatoryModel.h"


@interface YZMainController ()<CategoryBackDelegate>
{
    NSMutableArray                  *viewControllers;
}

@property (strong, nonatomic) UINavigationBar  *navBar;
@property (nonatomic, strong)  NSMutableArray         *navListArray;

@property (nonatomic, strong)UIView *sp_popupBackgroundView;

@property (nonatomic, strong)UIButton *searchBtn;

@property (nonatomic, strong)UIButton *cateBtn;

@property(nonatomic, strong) UIPanGestureRecognizer *panGestureRecognizer;

@end

@implementation YZMainController

- (void)viewDidLoad {
    [super viewDidLoad];
//       [self loadNavList:NO];
    [self.view addSubview:self.navBar];
    

    self.panGestureRecognizer = [[UIPanGestureRecognizer alloc] initWithTarget:self.drawer action:@selector(panGestureRecognized:)];
    self.panGestureRecognizer.maximumNumberOfTouches = 1;
    self.panGestureRecognizer.delegate = self.drawer;
    

    
//    [self.buttonBarView.selectedBar setBackgroundColor:[UIColor colorWithRed:106/255.0 green:177/255.0 blue:219/255.0 alpha:1]];
//    self.isProgressiveIndicator = YES;
    self.isElasticIndicatorLimit = YES;
    self.containerView.bounces = NO;
//    [self.containerView.panGestureRecognizer requireGestureRecognizerToFail:self.drawer.panGestureRecognizer ];

    [self loadNavList:NO];
    
    //显示声明信息
    NSString *bundleId = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleIdentifier"];
    
    if ([bundleId isEqualToString:@"com.youzu.bigwords.sit"]) {
        [self performSelector:@selector(showDeclareInfo) withObject:nil afterDelay:3.0];

    }
    

}
- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    self.searchBtn.enabled = YES;
    self.navigationController.interactivePopGestureRecognizer.delegate = self;
    
    self.view.backgroundColor = [[ThemeManager sharedInstance] colorWithColorName:kTmViewBackgroudColor];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}


- (BOOL)prefersStatusBarHidden
{
    return NO;
}

- (void)writeToCategoryFile
{
    //如果总分类数量小于默认数量6，则不显示分类排序按钮，没有分类排序功能
    if (self.navListArray.count > DefaultCategoryNum) {
        
        [self.view addSubview:self.cateBtn];
        self.cateBtn.hidden = NO;
    }
    else
    {
        self.cateBtn.hidden = YES;
    }
    //首先判断是否已经保存我的栏目，没有保存选区前默认都少个栏目
    NSMutableArray *my_arr;
    NSMutableArray *nav_temp_arr = [[NSMutableArray alloc]initWithCapacity:10];
    NSMutableArray *my_temp_arr = [PlistFileOperation readcategoryWithType:MyCategory];

    NSMutableArray *otherNavArray = [[NSMutableArray alloc]initWithArray:self.navListArray];
    if ([PlistFileOperation isExsitMyCategory] && my_temp_arr.count > 0) {
        //
        NSMutableArray *my_write_arr = [[NSMutableArray alloc]initWithCapacity:10];
        NSMutableArray *other_write_arr = [[NSMutableArray alloc]initWithCapacity:10];
        ///后台增加栏目／减少栏目，修改我的栏目和更多栏目
        [my_temp_arr enumerateObjectsUsingBlock:^(CatoryModel *model, NSUInteger indx,BOOL *isStop){
            BOOL isExitst = NO;
            YZNavListModel *navModel;
            for (int i = 1; i<self.navListArray.count; i++) {
                navModel = self.navListArray[i];
                if (navModel.ID == model.categoryId) {
                    isExitst = YES;
                    model.categoryName = navModel.name;
                    break;
                }
            }
            if (isExitst) {
                [nav_temp_arr addObject:navModel];
                [otherNavArray removeObject:navModel];
            }
            
        }];
        [nav_temp_arr insertObject:self.navListArray[0] atIndex:0];
        [otherNavArray removeObjectAtIndex:0];
        //重新梳理我的栏目和推荐栏目
        [nav_temp_arr enumerateObjectsUsingBlock:^(YZNavListModel *navListModel, NSUInteger idx, BOOL *stop) {
            CatoryModel *model = [[CatoryModel alloc]init];
            model.categoryName = navListModel.name;
            model.categoryId = navListModel.ID;
            [my_write_arr addObject:model];
        }];
        
        [otherNavArray enumerateObjectsUsingBlock:^(YZNavListModel *navListModel, NSUInteger idx, BOOL *stop) {
            CatoryModel *model = [[CatoryModel alloc]init];
            model.categoryName = navListModel.name;
            model.categoryId = navListModel.ID;
            [other_write_arr addObject:model];
        }];
        [PlistFileOperation writeCategory:my_write_arr categoryType:MyCategory];
        [PlistFileOperation writeCategory:other_write_arr categoryType:MoreCategor];
    }
    else
    {
        my_arr = [[NSMutableArray alloc]initWithCapacity:10];
        NSMutableArray *more_arr = [[NSMutableArray alloc]initWithCapacity:10];
        [self.navListArray enumerateObjectsUsingBlock:^(YZNavListModel *navListModel, NSUInteger idx, BOOL *stop) {
            CatoryModel *model = [[CatoryModel alloc]init];
            model.categoryName = navListModel.name;
            model.categoryId = navListModel.ID;
            if (idx < DefaultCategoryNum) {
                
                [my_arr addObject:model];
                [nav_temp_arr addObject:navListModel];
            }
            else
            {
                [more_arr addObject:model];
            }
            
        }];
        [PlistFileOperation writeCategory:my_arr categoryType:MyCategory];
        [PlistFileOperation writeCategory:more_arr categoryType:MoreCategor];
        
        
    }
    
    _navListArray = nav_temp_arr;
    
}

- (void)showNavListView{

    viewControllers = [[NSMutableArray alloc]initWithCapacity:10];

    
    if (self.navListArray.count > 0) {
        [self.navListArray enumerateObjectsUsingBlock:^(YZNavListModel *navListModel, NSUInteger idx, BOOL *stop) {
            
            //站长自定义外部链接  //站内链接
            if ([navListModel.type isEqualToString:@"custom"]) {
                YZLinkViewController *linkView = [[YZLinkViewController alloc] init];
                linkView.urlString = navListModel.link;
                linkView.title = navListModel.name;
                [viewControllers addObject:linkView];
                
            }
            else if([navListModel.type isEqualToString:@"post_type"]){
                YZArticleDetailController *detailView = [[YZArticleDetailController alloc] initWithUrlString:navListModel.link];
                detailView.title = navListModel.name;
                [viewControllers addObject:detailView];
                
            }
            else{
                //分类列表 navListModel.type=taxonomy
                YZHomeController *viewController = [[YZHomeController alloc] init];
                viewController.categoryModel = navListModel;
                viewController.title = navListModel.name;
                viewController.pagasContainer = self;
                [viewControllers addObject:viewController];
            }

        }];
        
        self.buttonBarView.hidden = NO;
        self.containerView.top = 64+39;
        self.containerView.height = CGRectGetHeight(self.view.bounds)-64-39;
        
    }
    else{
        
        YZHomeController *viewController = [[YZHomeController alloc] init];
        viewController.pagasContainer = self;
        viewController.title = @"首页";
        self.buttonBarView.hidden = YES;
        self.containerView.top = 64;
        self.containerView.height = CGRectGetHeight(self.view.bounds)-64;
        
        [viewControllers addObject:viewController];
        
    }

   
    
    
    [self reloadPagerTabStripView];
    
    self.bottomLine.hidden = self.buttonBarView.hidden;
    
    
    [self.view addSubview:self.sp_popupBackgroundView];
}

- (UIView *)sp_popupBackgroundView
{
    if (_sp_popupBackgroundView == nil) {
        _sp_popupBackgroundView = [[UIView alloc] initWithFrame:self.view.bounds];
        _sp_popupBackgroundView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
        _sp_popupBackgroundView.alpha = 0.4f;
        _sp_popupBackgroundView.backgroundColor = [UIColor blackColor];
        _sp_popupBackgroundView.hidden =YES;
    }
    return _sp_popupBackgroundView;
}

- (UIButton *)cateBtn
{
    if (_cateBtn == nil) {
        _cateBtn = [[YZButton alloc]initWithFrame:CGRectMake(kSCREEN_WIDTH-40, 65, 40, 36)];
        _cateBtn.backgroundColor = RGBACOLOR(252, 250, 251, 1);
        [_cateBtn setImage:[UIImage imageNamed:@"addCategory"] forState:UIControlStateNormal];
        [_cateBtn addTarget:self action:@selector(gotoCategory:) forControlEvents:UIControlEventTouchUpInside];
        
        YZButton *but = (YZButton *)_cateBtn;
        [but setThemeButtonBackgroudColor:kTmMainTopBackgroudColor];
        [but setThemeButtonImage:@"addCategory@2x"];
        [but openThemeSkin];
        
    }
    return _cateBtn;
}

- (void)gotoCategory:(id)sender
{
    
    YZCategoryViewController *category = [[YZCategoryViewController alloc]init];
    category.delegate = self;
    [self.navigationController pushViewController:category animated:NO];
    
}

#pragma mark 重新排序—分类

- (void)categoryBackArray:(NSMutableArray *)myCategoryArray
{

    NSMutableArray *allCategory = [PlistFileOperation readcategoryWithType:AllCategory];
    [_navListArray removeAllObjects];
    
    [myCategoryArray enumerateObjectsUsingBlock:^(CatoryModel *cateModel, NSUInteger indx, BOOL *isStop){
        [allCategory enumerateObjectsUsingBlock:^(YZNavListModel *navModel, NSUInteger indxy,BOOL *isStop){
            if (cateModel.categoryId == navModel.ID) {
                [_navListArray addObject:navModel];
                *isStop = YES;
            }
        }];
    }];
    
    [self showNavListView];
}


#pragma mark---

- (void)makeNavItems{
    UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
    [button setImage:[UIImage imageNamed:@"nav_tal"] forState:UIControlStateNormal];
    [button setImage:[UIImage imageNamed:@"nav_tal"] forState:UIControlStateHighlighted];
    [button addTarget:self action:@selector(openDrawer:) forControlEvents:UIControlEventTouchUpInside];
    button.frame = CGRectMake(0, 0, 44,44);
    UIBarButtonItem *backItem = [[UIBarButtonItem alloc] initWithCustomView:button];
    self.navigationItem.leftBarButtonItem = backItem;
}
#pragma mark end

- (UINavigationBar *)navBar{
    if (!_navBar) {
        _navBar = [[YZNavigationBar alloc] initWithFrame:CGRectMake(0, 0, kSCREEN_WIDTH, 64)];
        _navBar.translucent = NO;
        YZNavigationBar *bar = (YZNavigationBar *)_navBar;
        [bar setThemeBarTintColor:kTmNavBarColor];
        [bar setThemeTintColor:kTmNavTintColor];
        [bar setThemeTitleColor:kTmNavTitleColor];
        [bar openThemeSkin];
        
//        [_navBar setShadowImage:[UIImage streImageNamed:@"separatory_line"]];
        
        YZButton *button = [YZButton buttonWithType:UIButtonTypeCustom];
        [button setImage:[UIImage imageNamed:@"nav_tal_new"] forState:UIControlStateNormal];
        [button setImage:[UIImage imageNamed:@"nav_tal_new"] forState:UIControlStateHighlighted];
        [button addTarget:self action:@selector(openDrawer:) forControlEvents:UIControlEventTouchUpInside];
        button.frame = CGRectMake(2, 20, 44,44);
        [_navBar addSubview:button];

        
        [button setThemeButtonBackgroudColor:kTmClearColor];
        [button setThemeButtonImage:@"nav_tal_new@2x"];
        [button openThemeSkin];
        
        
        YZLabel *titleLabel = [[YZLabel alloc] initWithFrame:CGRectMake(44, 20, _navBar.width-44*2, 44)];
        titleLabel.font = [UIFont fontWithName:kChineseFontNameXi size:18];
        titleLabel.text = [AppConfigManager sharedInstance].appDispalyName;
        titleLabel.textColor = RGBCOLOR(104, 104, 104);
        titleLabel.textAlignment = NSTextAlignmentCenter;
        [titleLabel setThemeLabBackgroundColor:kTmClearColor];
        [titleLabel setThemeTextColor:kTmNavTitleColor];
        [titleLabel openThemeSkin];
        [_navBar addSubview:titleLabel];
        
        YZButton *search = [YZButton buttonWithType:UIButtonTypeCustom];
        [search setImage:[UIImage imageNamed:@"home_search_icon"] forState:UIControlStateNormal];
        [search setImage:[UIImage imageNamed:@"home_search_icon"] forState:UIControlStateHighlighted];
        [search addTarget:self action:@selector(search:) forControlEvents:UIControlEventTouchDown];
        
        search.frame = CGRectMake(kSCREEN_WIDTH-44, 20, 44,44);
        _searchBtn = search;
        [_navBar addSubview:_searchBtn];
        [search setThemeButtonBackgroudColor:kTmClearColor];
        [search setThemeButtonImage:@"home_search_icon@2x"];
        [search openThemeSkin];
        
    }
    
    return _navBar;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];

}

- (void)loginAction{
    YZLoginController  *loginView = [[YZLoginController alloc] init];
    
    [self presentViewController:loginView animated:YES completion:^{
        
    }];
}

- (void)loadNavList:(BOOL)isUpdate{
    
    [YZHomeService doNavListSuccess:^(NSArray *navListsArray) {
        
        _navListArray = [navListsArray mutableCopy];
        if (_navListArray.count > 0) {
            [PlistFileOperation writeCategory:_navListArray categoryType:AllCategory];
            [self writeToCategoryFile];
        }
        
        if (!isUpdate) {
            [self showNavListView];

        }
        
    } failure:^(NSString *statusCode, NSString *error) {
        //首先读取缓存
        _navListArray = [PlistFileOperation readcategoryWithType:AllCategory];
        if (_navListArray.count > 0) {
            //
            [self writeToCategoryFile];
            [self showNavListView];
        }
        else{
            [self presentSheet:error ForView:self.view];
            
        }

//        [self presentSheet:error ForView:self.view];

    }];
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

- (void)openDrawer:(id)sender
{
    [self.drawer open];
}

- (void)search:(UIButton *)sender{
    sender.enabled = NO;
    YZSearchController *search = [[YZSearchController alloc] init];
    [self.navigationController pushViewController:search animated:YES];
    
    
}

#pragma mark - ICSDrawerControllerPresenting


- (void)drawerControllerWillOpen:(ICSDrawerController *)drawerController
{
    self.sp_popupBackgroundView.hidden =NO;
    self.sp_popupBackgroundView.alpha = 0.0f;
    [UIView animateWithDuration: 0.33
                     animations:^{
                         
                         self.sp_popupBackgroundView.alpha = 0.2f;
                     }
                     completion:^(BOOL finished) {
                     }];
    
    
    self.view.userInteractionEnabled = NO;
}

- (void)drawerControllerDidClose:(ICSDrawerController *)drawerController
{
    self.view.userInteractionEnabled = YES;
}
-(void)drawerControllerWillClose:(ICSDrawerController *)drawerController
{
    self.sp_popupBackgroundView.hidden = YES;
}


#pragma mark - XLPagerTabStripViewControllerDataSource

-(NSArray *)childViewControllersForPagerTabStripViewController:(XLPagerTabStripViewController *)pagerTabStripViewController
{
    if (viewControllers.count <= 0) {
        self.buttonBarView.hidden = YES;
        return @[[[UIViewController alloc]init]];
    }
   
    UITableViewController *firstController= [viewControllers firstObject];
    
    [firstController.view addGestureRecognizer:self.panGestureRecognizer];
//    [firstController.tableView.panGestureRecognizer requireGestureRecognizerToFail:self.panGestureRecognizer ];

    
    
    return viewControllers;
    
}


#pragma mark--- 测试版本提示

- (void)showDeclareInfo{
    UIAlertView*alert = [[UIAlertView alloc]initWithTitle:@"您现在使用的应用是由BigApp企业证书打包生成的版本，该应用仅限于BigApp内部测试使用，严禁提供给用户下载安装，如违反规定，造成一切后果由您本人负责。如想普通用户下载使用，请尽快联系我们，我们将会协助您上传app到AppStore。特此通告！"
                         
                                                  message:@""
                         
                                                 delegate:self
                         
                                        cancelButtonTitle:@"确定"
                         
                                        otherButtonTitles:nil];
    
    [alert show];  
    

}

#pragma marks -- UIAlertViewDelegate --
//根据被点击按钮的索引处理点击事件
-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    [self.drawer resetDrawState];
}




@end
