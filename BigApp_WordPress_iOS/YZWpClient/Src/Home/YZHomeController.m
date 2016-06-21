//
//  YZHomeController.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/17.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZHomeController.h"
#import "YZHomeCell.h"
#import "YZHomeViewModel.h"
#import "MJRefresh.h"
#import "HomeCycleScrollView.h"
#import "YZArticleDetailController.h"
#import "YZHomeService.h"
#import "UIViewController+YZProgressHUD.h"
#import "YZNavListModel.h"
#import "UIImageView+AFNetworking.h"
#import "YZLinkViewController.h"

#import "UIImageView+PINRemoteImage.h"
#import "PINCache.h"
#import "FLAnimatedImageView.h"
#import "NSString+Additions.h"
#import "Config.h"
#define kHomeIdentifier @"YZHomeCell"
#define kTableViewCellHeight  (218/2.0)

#define kTableTopViewHeight AutoDeviceHeight(342/2.0)

@interface YZHomeController ()

@property (nonatomic, strong)NSMutableArray *dataSourceArr;

@property (nonatomic, strong)HomeCycleScrollView         *tableTopView;

@property (nonatomic, assign)NSInteger    currentPage;

@end

@implementation YZHomeController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    
    self.tableTopView.hidden = YES;
    
    _dataSourceArr = [@[] mutableCopy];

    [self.tableView setSeparatorStyle:UITableViewCellSeparatorStyleNone];
    [self.tableView registerClass:[YZHomeCell class] forCellReuseIdentifier:kHomeIdentifier];
    self.tableView.rowHeight = kTableViewCellHeight;
    
    YZView *vi = [[YZView alloc]init];
    [vi setThemeViewBackgroudColor:kTmMainCellBackgroudColor];
    [vi openThemeSkin];
    self.tableView.backgroundView = vi;
    
  
    
    if (self.isBannerShow) {
        self.tableView.tableHeaderView = self.tableTopView;

    }
    self.tableView.tableFooterView = [UIView new];
    
    [self.tableView addHeaderWithTarget:self action:@selector(headerRereshing) dateKey:@"table"];
    [self.tableView addFooterWithTarget:self action:@selector(footerRereshing)];
    
     [self loadCache];
    
    [self loadRereshing];
    
   
    

}
- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    
//    if (self.isViewLoaded &&self.view.window == nil) {
//        self.view = nil;
//    }// 将视图控制器中的强引用释放

}
- (void)releseViewAndData{
//    self.view = nil;
    _dataSourceArr = nil;
    _dataSourceArr=[@[] mutableCopy];
    [self.tableView reloadData];

}

- (void)loadRereshing{
    [self.tableView headerBeginRefreshing];
}
#pragma mark 开始进入刷新状态
- (void)headerRereshing
{
    self.currentPage = 1;

    [self loadData:YES];
}

- (void)footerRereshing
{
    self.currentPage ++;
    
    [self loadData:NO];
}

#pragma mark -- propertys

- (UIView *)tableTopView{
    if (!_tableTopView) {
        _tableTopView.frame = CGRectMake(0, 0, kSCREEN_WIDTH, kTableTopViewHeight);
        
        NSMutableArray *viewsArray = [@[] mutableCopy];

        [self.categoryModel.banner_list enumerateObjectsUsingBlock:^(YZBannerList *obj, NSUInteger idx, BOOL *stop) {
            UIImageView *imageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, kSCREEN_WIDTH, kTableTopViewHeight)];
            imageView.contentMode = UIViewContentModeScaleAspectFill;
            imageView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
            imageView.clipsToBounds = YES;
            imageView.backgroundColor = RGBCOLOR(233, 243, 238);
//            [imageView setImageWithURL:[NSURL URLWithString:obj.img_url]];
            
            [imageView pin_setImageFromURL:[NSURL URLWithString:obj.img_url]
                                     completion:^(PINRemoteImageManagerResult *result) {
                                         if (result.requestDuration > 0.25) {
                                             [UIView animateWithDuration:0.3 animations:^{
                                                imageView.alpha = 1.0f;
                                             }];
                                         } else {
                                            imageView.alpha = 1.0f;
                                         }
                                     }];

            
            
            [viewsArray addObject:imageView];

        }];
        
        _tableTopView = [[HomeCycleScrollView alloc] initWithFrame:CGRectMake(0, 0, kSCREEN_WIDTH, kTableTopViewHeight) animationDuration:5];
        //    self.mainScorllView.backgroundColor = [[UIColor purpleColor] colorWithAlphaComponent:0.1];
        
        _tableTopView.totalPagesCount = ^NSInteger(void){
            return viewsArray.count;
        };
        _tableTopView.fetchContentViewAtIndex = ^UIView *(NSInteger pageIndex){
            return viewsArray[pageIndex];
        };
        __weak typeof(self) weakSelf = self;
       _tableTopView.TapActionBlock = ^(NSInteger pageIndex){
            NSLog(@"点击了第%ld个",(long)pageIndex);
           YZBannerList *obj = (YZBannerList *)(weakSelf.categoryModel.banner_list[pageIndex]);
           
           UIViewController *nextController = nil;
           if ([obj.type isEqualToString:@"2"]) {
               YZArticleDetailController *detailView = [[YZArticleDetailController alloc] initWithUrlString:obj.link];
//               detailView.urlString = obj.link;
               detailView.title = obj.name;
               nextController = detailView;
           }
           else{
               YZLinkViewController *linkView = [[YZLinkViewController alloc] init];
               linkView.urlString = obj.link;
               linkView.title = obj.name;
               nextController = linkView;
           }
           
           [weakSelf.pagasContainer.navigationController pushViewController:nextController animated:YES];

        };
        
        _tableTopView.backgroundColor = RGBCOLOR(236, 245, 240);

    }
    
    return _tableTopView;
}

- (BOOL)isBannerShow{
    return self.categoryModel.banner_list.count > 0;
}

#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _dataSourceArr.count;
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    YZHomeViewModel *viewModel = _dataSourceArr[indexPath.row];
    
    return viewModel.cellHeight;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    YZHomeCell *cell = [tableView dequeueReusableCellWithIdentifier:kHomeIdentifier forIndexPath:indexPath];
    cell.viewModel = [_dataSourceArr objectAtIndex:indexPath.row];
    
    return cell;
}


#pragma mark--

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    YZHomeViewModel *viewModel = _dataSourceArr[indexPath.row];
    YZArticleDetailController *viewController = [[YZArticleDetailController alloc] initWithViewModel:viewModel];
    viewController.view.backgroundColor = [UIColor whiteColor];

    [self.navigationController pushViewController:viewController animated:YES];
    
}

#pragma mark -- Methods

- (void)loadCache{
    NSString *urlSchame = @"";
    
      NSString *imgMod = [Config getPictureQualityMod];
    //由于分类对应的列表url，是在获取分类导航时返回的完整url ，所以做特殊处理
    if (self.categoryModel.link.length > 0) {
        urlSchame = [NSString stringWithFormat:@"%@&filter[posts_per_page]=%@&page=%@&img_mod=%@",self.categoryModel.link,kPagesCount,@1,imgMod];
    }
    else{
        //无分类导航时，首页的分类列表
        NSString *urlstr = @"/?yz_app=1&api_route=posts&action=get_posts&filter[posts_per_page]=%@&page=%@&img_mod=%@";
        urlSchame = [NSString stringWithFormat:urlstr,kPagesCount,@1,imgMod];
    }
    
    NSString *cacheKey = [urlSchame md5Hash];
    NSArray *object = [[PINCache sharedCache] objectForKey:cacheKey];
    if (object) {
        NSMutableArray *postsArray =[YZPostsModel objectArrayWithKeyValuesArray:object];
        [self refreshUIWith:postsArray];
    }
}


-(void)loadData:(BOOL)isRefresh{
    
        NSString *currentPage = [NSString stringWithFormat:@"%@",@(_currentPage)];
        
        [YZHomeService doPostsListPageCounts:kPagesCount
                                 currentPage:currentPage
                                 categoryUrl:self.categoryModel.link
                                     success:^(NSArray *postsList)
         {
             if (isRefresh) {
                 [self.tableView headerEndRefreshing];
                 [_dataSourceArr removeAllObjects];
                 
             }
             else{
                 [self.tableView footerEndRefreshing];
             }
             
             if (postsList.count < [kPagesCount integerValue]) {
                 self.tableView.footerHidden = YES;
             }
             
             [self refreshUIWith:postsList];
             
         } failure:^(NSString *statusCode, NSString *error) {
             
             [self.tableView headerEndRefreshing];
             
             [self presentSheet:error ForView:self.view];
             
         }];
        
}


- (void)refreshUIWith:(NSArray*)postsList{

    [postsList enumerateObjectsUsingBlock:^(YZPostsModel *obj, NSUInteger idx, BOOL *stop) {
        YZHomeViewModel *viewModel = [[YZHomeViewModel alloc] init];
        viewModel.poastModel = obj;
        
        [_dataSourceArr addObject:viewModel];
    }];
    if (_dataSourceArr.count>0) {
        self.tableTopView.hidden = NO;
        [self.tableView reloadData];
    }

}



@end
