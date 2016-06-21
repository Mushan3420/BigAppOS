//
//  YZSearchController.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/12.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZSearchController.h"
#import "BuildConfig.h"
#import "UIViewExt.h"
#import "UIImage+SNAdditions.h"
#import "SVProgressHUD.h"
#import "UIViewController+YZProgressHUD.h"
#import "YZSearchService.h"
#import "YZHomeCell.h"
#import "YZHomeViewModel.h"
#import "YZArticleDetailController.h"
#import "Config.h"
#import "SearchCell.h"
#import "YZHotTagsView.h"
#import "YZHomeController.h"
#import "YZNavListModel.h"
#import "YZQueryComponents.h"


#define kSearchResultCellIdentifier @"YZHomeCell"

#define kSearchBarColorName         @"searchBarColor"

@interface YZSearchController ()<UISearchBarDelegate,UITableViewDelegate,UITableViewDataSource,UIGestureRecognizerDelegate,YZHotTagsViewDelegate>
{
    UISearchBar *mySearchBar;
}
@property (nonatomic, strong)UITableView *tableView;
@property (nonatomic, strong)UIButton    *clearSearchHistoryBtn;
@property (nonatomic, assign)BOOL        showHistory;//是否显示搜索历史页面

@property (nonatomic, strong)NSArray     *historyDataSource;//搜索历史
@property (nonatomic, strong)NSMutableArray *dataSourceArr;//搜索结果
@property (nonatomic, strong)NSMutableArray *historySourceArr;//搜索历史

@property (nonatomic, strong) YZHotTagsView *hotTagView;

@end

@implementation YZSearchController

- (void)viewDidLoad {
    [super viewDidLoad];
    if (![self.tableView.backgroundView isKindOfClass:[YZView class]]) {
        YZView *vi = [[YZView alloc]init];
        self.tableView.backgroundView = vi;
        
    }
    YZView *vi = [[YZView alloc]init];
    self.view = vi;
    [(YZView *)self.view setThemeViewBackgroudColor:kSearchBarColorName];
    [(YZView *)self.view openThemeSkin];
    
    [(YZView *)self.tableView.backgroundView setThemeViewBackgroudColor:kTmViewBackgroudColor];
    [(YZView *)self.tableView.backgroundView openThemeSkin];
    
    [self loadDefaults];
    
    [self loadHotTags];
    

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [self showHistoryView];

}

- (void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
      self.navigationController.interactivePopGestureRecognizer.delegate = self;
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [self dismiss];
      self.navigationController.interactivePopGestureRecognizer.delegate = nil;
}

#pragma mark-- Actions
- (void)clearSearchHistoryAction:(UIButton *)btn{
    [Config deleteSearchHistory];
    [_historySourceArr removeAllObjects];
    
    [self showHistoryView];
    [self.tableView reloadData];
}//清除历史记录

#pragma mark--  Methods

- (void)loadHotTags{
    [YZSearchService doGetSearchPostTagsSuccess:^(NSArray *postsList) {
        self.hotTagView.hotTagModels = postsList;
        [UIView animateWithDuration:0.33 animations:^{
            self.tableView.tableHeaderView = self.hotTagView;
            
        }];
    } failure:^(NSString *statusCode, NSString *error) {
        
    }];
}
- (void)loadDefaults{
    _dataSourceArr = [@[] mutableCopy];
    _historySourceArr = [[Config getSearchHistoryList] mutableCopy];
 
    
    self.showHistory = YES;
    [self makeHeaderSearchView];
    
    
    
    [self.view addSubview:self.tableView];
}

-(void)doSearch:(BOOL)isRefresh andSearchText:(NSString *)text{
    
    [YZSearchService doSearchListPageCounts:@"15" currentPage:@"1" searchContent:text success:^(NSArray *postsList) {
        [self dismiss];
        if (isRefresh) {
            [_dataSourceArr removeAllObjects];
        }

        [postsList enumerateObjectsUsingBlock:^(YZPostsModel *obj, NSUInteger idx, BOOL *stop) {
            YZHomeViewModel *viewModel = [[YZHomeViewModel alloc] init];
            viewModel.poastModel = obj;
            [_dataSourceArr addObject:viewModel];
        }];
        if (_dataSourceArr.count>0) {
            self.showHistory = NO;
            [self showHistoryView];
            
            [self.tableView reloadData];
        }
        else{
            [self presentSheet:LocalizedString(@"NO_SEARCH_RESULT") ForView:self.view];
        }
        
    } failure:^(NSString *statusCode, NSString *error) {
        [self dismiss];
        [self presentSheet:error ForView:self.view];
    }];
}

- (void)showHistoryView{
    if (self.showHistory) {
        [mySearchBar becomeFirstResponder];
        if (_historySourceArr.count > 0) {
            self.tableView.tableFooterView = self.clearSearchHistoryBtn;

        }
        else{
            self.tableView.tableFooterView = [UIView new];
        }
        self.tableView.height = kSCREEN_HEIGHT - 216- 64;
        
        if (_hotTagView.hotTagModels.count>0) {
            self.tableView.tableHeaderView = _hotTagView;
        }
        else{
            self.tableView.tableHeaderView = self.hotTagView.historyLable;
        }
    }else{
         self.tableView.tableFooterView = [UIView new];
        [mySearchBar endEditing:YES];
        UIButton *cancelButton = [mySearchBar valueForKey:@"_cancelButton"];
        cancelButton.enabled = YES;
        self.tableView.height = kSCREEN_HEIGHT- 64;
        
        self.tableView.tableHeaderView = nil;
    }
}

#pragma mark--
-(UITableView *)tableView{
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 64, kSCREEN_WIDTH, kSCREEN_HEIGHT- 64) style:UITableViewStylePlain];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        _tableView.backgroundColor = [UIColor clearColor];
        [self.tableView registerClass:[YZHomeCell class] forCellReuseIdentifier:kSearchResultCellIdentifier];

    }
    return _tableView;
}

- (void)makeHeaderSearchView{
    
    mySearchBar = [[UISearchBar alloc]initWithFrame:CGRectMake(0, 20, kSCREEN_WIDTH, 44)];
    mySearchBar.backgroundImage = [UIImage imageWithColor:[[ThemeManager sharedInstance] colorWithColorName:kSearchBarColorName] size:mySearchBar.size];
    [mySearchBar setPlaceholder:LocalizedString(@"SEARCH_CONTENT")];
    mySearchBar.delegate = self;
    mySearchBar.showsCancelButton = YES;
    mySearchBar.barTintColor =  RGBCOLOR(58, 211, 127);
    mySearchBar.tintColor =  RGBCOLOR(58, 211, 127);

    
    UIImageView *line = [[UIImageView alloc] initWithImage:[UIImage streImageNamed:@"separatory_line"]];
    line.top = mySearchBar.height-0.5;
    line.width =kSCREEN_WIDTH;
    [mySearchBar addSubview:line];
   
    UITextField *searchField = [mySearchBar valueForKey:@"_searchField"];
    searchField.font = [UIFont fontWithName:kChineseFontNameXi size:15];
    searchField.textColor = RGBCOLOR(104, 104, 104);
    searchField.backgroundColor = [[ThemeManager sharedInstance] colorWithColorName:kSearchBarColorName];
    
    UIButton *cancelButton = [mySearchBar valueForKey:@"_cancelButton"];
    [cancelButton setTitle:LocalizedString(@"CANCEL_BTN")  forState:UIControlStateNormal];
    cancelButton.titleLabel.font = [UIFont fontWithName:kChineseFontNameXi size:15];
    
    [self.view addSubview:mySearchBar];
}

- (UIView *)hotTagView{
    
    if (!_hotTagView) {
        _hotTagView = [[YZHotTagsView alloc] init];
        _hotTagView.delegate = self;

    }
    return _hotTagView;
}

#pragma mark - Search History

- (UIButton *)clearSearchHistoryBtn{
    if (!_clearSearchHistoryBtn) {
        _clearSearchHistoryBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_clearSearchHistoryBtn setTitle:LocalizedString(@"CLEAR_SEARCH_HISTORY") forState:UIControlStateNormal];
        [_clearSearchHistoryBtn setTitleColor:RGBCOLOR(140, 140, 140) forState:UIControlStateNormal];
        [_clearSearchHistoryBtn setTitleColor:RGBCOLOR(100, 100, 100) forState:UIControlStateHighlighted];
        _clearSearchHistoryBtn.backgroundColor = [UIColor clearColor];
        _clearSearchHistoryBtn.frame = CGRectMake(0, 0, kSCREEN_WIDTH, kSearchHistoryCellRow);
        _clearSearchHistoryBtn.titleLabel.font = [UIFont fontWithName:kChineseFontNameXi size:15];
        [_clearSearchHistoryBtn addTarget:self action:@selector(clearSearchHistoryAction:) forControlEvents:UIControlEventTouchUpInside];

        UIImageView *line = [[UIImageView alloc] initWithImage:[UIImage streImageNamed:@"separatory_line"]];
        line.top = kSearchHistoryCellRow - 0.5;
        line.width = _clearSearchHistoryBtn.width;
        [_clearSearchHistoryBtn addSubview:line];
    }
    return _clearSearchHistoryBtn;
}


#pragma mark - UISearchBarDelegate

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar{
//    [self.navigationController popViewControllerAnimated:YES];
    self.showHistory = YES;
    [self showHistoryView];
    [self.tableView reloadData];
    
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar{
    [SVProgressHUD setBackgroundColor:[UIColor blackColor]];
    [SVProgressHUD setForegroundColor:[UIColor whiteColor]];
    [SVProgressHUD showWithStatus:LocalizedString(@"SEARCHING")];
    
    [Config saveSearchHistoryWithString:searchBar.text];
    
    [self doSearch:YES andSearchText:searchBar.text];
}


- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (!_showHistory) {
        return _dataSourceArr.count;
    }
    return _historySourceArr.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {

    if (!_showHistory) {
        YZHomeViewModel *viewModel = _dataSourceArr[indexPath.row];
        
        return viewModel.cellHeight;
    }
    return 44;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    YZTableViewCell *cell = nil;
    if (self.showHistory) {
        static NSString *kCellIdentifier = @"cell_id";
        cell = [tableView dequeueReusableCellWithIdentifier:kCellIdentifier];
        
        if (!cell) {
            cell = [[SearchCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kCellIdentifier];
            
        }
        
        cell.imageView.image = [UIImage imageNamed:@"search_history"];
        cell.textLabel.text = _historySourceArr[indexPath.row];
        cell.textLabel.font = [UIFont fontWithName:kChineseFontNameXi size:16];
        cell.textLabel.backgroundColor = [UIColor clearColor];
        cell.textLabel.textColor = RGBCOLOR(88, 88, 88);
        
        [(SearchCell *)cell setSearchTitleBlock:^void(NSString *title){
            mySearchBar.text = title;
        }];
        
    }
    else{
        YZHomeCell *homecell = [tableView dequeueReusableCellWithIdentifier:kSearchResultCellIdentifier forIndexPath:indexPath];
        homecell.viewModel = [_dataSourceArr objectAtIndex:indexPath.row];
        cell = homecell;
    }
    
    return cell;
}

#pragma mark--

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    if (self.showHistory) {
        [SVProgressHUD setBackgroundColor:[UIColor blackColor]];
        [SVProgressHUD setForegroundColor:[UIColor whiteColor]];
        [SVProgressHUD showWithStatus:LocalizedString(@"SEARCHING")];
        mySearchBar.text = _historySourceArr[indexPath.row];
        [self doSearch:YES andSearchText:_historySourceArr[indexPath.row]];
    }else{
        YZHomeViewModel *viewModel = _dataSourceArr[indexPath.row];
        //        viewController.urlString = viewModel.poastModel.link;
        YZArticleDetailController *viewController = [[YZArticleDetailController alloc] initWithViewModel:viewModel];
        viewController.view.backgroundColor = [UIColor whiteColor];

        [self.navigationController pushViewController:viewController animated:YES];
    }

    
}

- (void)dismiss {
    [SVProgressHUD dismiss];
    
}

#pragma mark -- YZHotTagsViewDelegate
-(void)doSearchTagWithUrl:(NSString *)urlString{
    YZHomeController *viewController = [[YZHomeController alloc] init];
    viewController.categoryModel = [[YZNavListModel alloc] init];
    viewController.categoryModel.link = urlString;
    
    NSURL *url = [NSURL URLWithString:urlString];
    NSMutableDictionary *params = [url queryComponents];
    
    NSArray *tagName = params[@"filter[tag]"];
    viewController.title = tagName.firstObject;
    
    viewController.isBackBarButtonItemShow = YES;
    
    [self.navigationController pushViewController:viewController animated:YES];
}


@end
