//
//  YZFavoriteController.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/17.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//
//  我的收藏

#import "YZFavoriteController.h"
#import "YZFavService.h"
#import "YZHomeCell.h"
#import "YZHomeViewModel.h"
#import "YZArticleDetailController.h"
#import "UIViewController+YZProgressHUD.h"
#import "MJRefresh.h"
#import "Config.h"
#import "YZSavePostDBModel.h"
#define kFavCellIdentifier @"YZHomeCell"

@interface YZFavoriteController ()
{
    
}
@property (nonatomic, strong)NSMutableArray *dataSourceArr;
@property (nonatomic, assign)NSInteger    currentPage;
@property (nonatomic, strong)YZSavePostDBModel *postModel;
@end

@implementation YZFavoriteController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _dataSourceArr = [@[] mutableCopy];
    [self.tableView registerClass:[YZHomeCell class] forCellReuseIdentifier:kFavCellIdentifier];
    [self.tableView setSeparatorStyle:UITableViewCellSeparatorStyleNone];

//    [self loadFavoriteData:YES];
    [self.tableView addHeaderWithTarget:self action:@selector(headerRereshing) dateKey:@"table"];
}


- (void)setThemeModel
{
    
    if (![self.tableView.backgroundView isKindOfClass:[YZView class]]) {
        YZView *vi = [[YZView alloc]init];
        self.tableView.backgroundView = vi;
    }
    
    
    [(YZView *)self.tableView.backgroundView setThemeViewBackgroudColor:kTmViewBackgroudColor];
    [(YZView *)self.tableView.backgroundView openThemeSkin];
    
}

- (void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    
    [self setThemeModel];
    
    if ([Config isLogin]) {
        [self.tableView headerBeginRefreshing];
    }
    else
    {
        NSArray *postModel = [self.postModel findAllSavePost];
        [_dataSourceArr removeAllObjects];
        //        [self dismissHuDForView:self.view];
        [postModel enumerateObjectsUsingBlock:^(YZPostsModel *obj, NSUInteger idx, BOOL *stop) {
            YZHomeViewModel *viewModel = [[YZHomeViewModel alloc] init];
            viewModel.poastModel = obj;
            
            [_dataSourceArr addObject:viewModel];
        }];
        if (_dataSourceArr.count == 0) {
            [self presentSheet:@"暂无收藏文章" ForView:self.view];
        }
        [self.tableView reloadData];
    }
}

- (YZSavePostDBModel *)postModel
{
    if (!_postModel) {
        _postModel = [[YZSavePostDBModel alloc]init];
    }
    return _postModel;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark-- Methods
#pragma mark 开始进入刷新状态
- (void)headerRereshing
{
    if ([Config isLogin]) {
        self.currentPage = 1;
        
        [self loadFavoriteData:YES];
    }
    else
    {
        [self.tableView headerEndRefreshing];
    }
}

- (void)loadFavoriteData:(BOOL)isRefresh{
//    NSString *currentPage = [NSString stringWithFormat:@"%@",@(_currentPage)];

//    [self showLoadingAddedTo:self.view];
    [YZFavService requestFavoriteListSuccess:^(NSArray *favListsArray) {
        [self.tableView headerEndRefreshing];
        [_dataSourceArr removeAllObjects];
//        [self dismissHuDForView:self.view];
        [favListsArray enumerateObjectsUsingBlock:^(YZPostsModel *obj, NSUInteger idx, BOOL *stop) {
            YZHomeViewModel *viewModel = [[YZHomeViewModel alloc] init];
            viewModel.poastModel = obj;
            
            [_dataSourceArr addObject:viewModel];
        }];
        if (_dataSourceArr.count>0) {
            [self.tableView reloadData];
        }
        if ([_dataSourceArr count]==0) {
            [self presentSheet:@"暂无收藏文章" ForView:self.view];
        }
        
    } failure:^(NSString *statusCode, NSString *error) {
//        [self dismissHuDForView:self.view];
                [self.tableView headerEndRefreshing];
        [self presentSheet:error ForView:self.view];
    }];
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
    YZHomeCell *cell = [tableView dequeueReusableCellWithIdentifier:kFavCellIdentifier forIndexPath:indexPath];
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
- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return  UITableViewCellEditingStyleDelete;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath{
    if (editingStyle == UITableViewCellEditingStyleDelete){
        if ([Config isLogin]) {
            YZHomeViewModel *viewModel = _dataSourceArr[indexPath.row];
            
            NSString *postId = [NSString stringWithFormat:@"%@",@(viewModel.poastModel.postID)];
            [self showLoadingAddedTo:self.navigationController.view];
            [YZFavService doRemoveFavoriteWithPostId:postId success:^(NSDictionary *dic) {
                [self dismissHuDForView:self.navigationController.view];
                [_dataSourceArr removeObjectAtIndex:indexPath.row];
                [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
            } failure:^(NSString *statusCode, NSString *error) {
                [self dismissHuDForView:self.navigationController.view];
                [self presentSheet:error ForView:self.navigationController.view];
                
                
            }];
        }
        else
        {
            YZHomeViewModel *viewModel = _dataSourceArr[indexPath.row];
            [self.postModel deleteApostWithId:viewModel.poastModel.postID];
            [_dataSourceArr removeObjectAtIndex:indexPath.row];
            [tableView reloadData];
        }
        



    }
    
}


#pragma mark -- Methods



@end
