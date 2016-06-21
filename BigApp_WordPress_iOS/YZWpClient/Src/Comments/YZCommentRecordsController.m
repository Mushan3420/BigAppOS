//
//  YZCommentRecordsController.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/17.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//
//  我的评论

#import "YZCommentRecordsController.h"
#import "YZCommentService.h"
#import "YZCommentRecordsCell.h"
#import "YZCommentRecordsViewModel.h"
#import "YZArticleDetailController.h"
#import "MJRefresh.h"
#import "UIViewController+YZProgressHUD.h"
#define kYZCommentRecordsCell   @"YZCommentRecordsCell"

@interface YZCommentRecordsController ()

@property (nonatomic, strong) NSMutableArray    *commentsDataArray;

@property (nonatomic, assign) NSInteger         currentPage;
@property (nonatomic, strong) YZCommentRecordsViewModel *lastOpenCommentModel;

@end

@implementation YZCommentRecordsController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _currentPage = 1;
    
    _commentsDataArray = [@[] mutableCopy];
    
    [self.tableView registerClass:[YZCommentRecordsCell class] forCellReuseIdentifier:kYZCommentRecordsCell];
    
    [self.tableView setSeparatorStyle:UITableViewCellSeparatorStyleNone];
        
     [self.tableView addHeaderWithTarget:self action:@selector(headerRereshing) dateKey:@"table"];
}

- (void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [self.tableView headerBeginRefreshing];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark--
#pragma mark 开始进入刷新状态
- (void)headerRereshing
{
    self.currentPage = 1;
    
    [self loadCommentRecordsData:YES];
}


- (void)loadCommentRecordsData:(BOOL)isRefresh{
     __weak typeof(self) weakSelf = self;
    [YZCommentService requestCommentRecordsWithPageCounts:@"1000" currentPage:@"1" success:^(NSArray *records) {
        if (weakSelf) {
            [weakSelf.tableView headerEndRefreshing];
            [weakSelf.commentsDataArray removeAllObjects];
            [records enumerateObjectsUsingBlock:^(YZADetailCommentModel *model, NSUInteger idx, BOOL *stop) {
                
                YZCommentRecordsViewModel *viewModel = [[YZCommentRecordsViewModel alloc] init];
                viewModel.model = model;
                [_commentsDataArray addObject:viewModel];
                
            }];
            
            [weakSelf.tableView reloadData];
             weakSelf.currentPage ++;
        }
        
    } failure:^(NSString *statusCode, NSString *error) {
        
    }];
}

#pragma mark -- UITableViewDelegate UITableViewDataSource


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.commentsDataArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    YZCommentRecordsViewModel *commentViewModel = self.commentsDataArray[indexPath.row];
    
    return commentViewModel.cellHeight + 59;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    YZCommentRecordsCell *cell = [tableView dequeueReusableCellWithIdentifier:kYZCommentRecordsCell forIndexPath:indexPath];
    
    cell.viewModel = self.commentsDataArray[indexPath.row];
    [cell.postTitleView addTarget:self action:@selector(articleDetailAction:) forControlEvents:UIControlEventTouchUpInside];
    cell.postTitleView.tag = indexPath.row;
    
    
    return cell;
}


-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    [self reloadRowsAtIndexPath:indexPath];
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return  UITableViewCellEditingStyleDelete;
}

- (void) tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath{
    if (editingStyle == UITableViewCellEditingStyleDelete){
        
        YZCommentRecordsViewModel *viewModel = _commentsDataArray[indexPath.row];
        
        NSString *postId = [NSString stringWithFormat:@"%@",@(viewModel.model.ID)];
        [self showLoadingAddedTo:self.navigationController.view];
        [YZCommentService doRemoveCommentWithPostId:postId success:^(NSDictionary *dic) {
            [self dismissHuDForView:self.navigationController.view];
            [_commentsDataArray removeObjectAtIndex:indexPath.row];
            [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
        } failure:^(NSString *statusCode, NSString *error) {
            [self dismissHuDForView:self.navigationController.view];
            [self presentSheet:error ForView:self.navigationController.view];
            
            
        }];

        
    }
    
}

- (void)reloadRowsAtIndexPath:(NSIndexPath *)indexPath{
    
    YZCommentRecordsViewModel *viewModel = self.commentsDataArray[indexPath.row];
    
    if (viewModel.isOpenShow) {
        
        if (_lastOpenCommentModel !=  viewModel || !_lastOpenCommentModel.isRealHeight) {
            
            _lastOpenCommentModel.isRealHeight = NO;
            _lastOpenCommentModel = viewModel;
            
            _lastOpenCommentModel.isRealHeight = YES;
            
        }
        else{
            viewModel.isRealHeight = NO;
        }
        
        NSArray *indexArray=[NSArray arrayWithObject:indexPath];
        [self.tableView reloadRowsAtIndexPaths:indexArray withRowAnimation:UITableViewRowAnimationAutomatic];
        
    }
}

- (void)articleDetailAction:(UIButton *)btn{
    YZCommentRecordsViewModel *viewModel = self.commentsDataArray[btn.tag];
    
    YZHomeViewModel *vModel = [YZHomeViewModel viewModel];
    vModel.titleText = viewModel.model.post_info.title;
    vModel.articleDataText = viewModel.model.date;
    vModel.poastModel.postID = viewModel.model.post;
    vModel.poastModel.title = vModel.titleText;
    
    YZArticleDetailController *viewController = [[YZArticleDetailController alloc] initWithViewModel:vModel];
    viewController.view.backgroundColor = [UIColor whiteColor];
    
    [self.navigationController pushViewController:viewController animated:YES];
}


@end
