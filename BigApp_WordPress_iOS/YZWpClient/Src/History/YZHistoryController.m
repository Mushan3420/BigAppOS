//
//  YZHistoryController.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/25.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZHistoryController.h"
#import "YZHistoryDBModel.h"
#import "YZArticleDetailController.h"
#import "YZHistoryCell.h"

#define kHistoryCellIdentifier  @"YZHistoryCell"
#define heightHeadSection       30.0

#define kSearchBarColorName         @"searchBarColor"


@interface YZHistoryController ()
{
    YZHistoryDBModel    *db;
}

@property (nonatomic, strong)NSMutableArray *dataSourceArr;
@property (nonatomic, strong)YZButton *clearAllBtn;

@end

@implementation YZHistoryController

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
    [self loadHistoryData];
    
    [self.tableView reloadData];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.isBackBarButtonItemShow = YES;
    
    _dataSourceArr = [[NSMutableArray alloc]initWithCapacity:10];
    
    db = [[YZHistoryDBModel alloc]init];
    
    
    

    
    [self.tableView registerClass:[YZHistoryCell class] forCellReuseIdentifier:kHistoryCellIdentifier];
    [self.tableView setSeparatorStyle:UITableViewCellSeparatorStyleSingleLine];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    
    if (![self.tableView.backgroundView isKindOfClass:[YZView class]]) {
        YZView *vi = [[YZView alloc]init];
        self.tableView.backgroundView = vi;
        
    }
//    YZView *vi = [[YZView alloc]init];
//    self.view = vi;
//    [(YZView *)self.view setThemeViewBackgroudColor:kSearchBarColorName];
//    [(YZView *)self.view openThemeSkin];
//    
    [(YZView *)self.tableView.backgroundView setThemeViewBackgroudColor:kTmViewBackgroudColor];
    [(YZView *)self.tableView.backgroundView openThemeSkin];
    
    [self setRightItemTitle:@"清空"];
    __weak __typeof(self)selfweak = self;
    [self setRightBarButtonItem:^(NSInteger type){
        [selfweak clearAllClick];
    }];
}

- (void)clearAllClick
{
    [db deleteAllHistory];
    [_dataSourceArr removeAllObjects];
    [self.tableView reloadData];
}

- (void)loadHistoryData
{
    [_dataSourceArr removeAllObjects];
    
    NSDateFormatter *sdf = [[NSDateFormatter alloc]init];
    [sdf setDateFormat:@"yyyy年MM月dd日"];
    
    for (int i = 0; i<7; i++) {
        NSString *datestr = [sdf stringFromDate:[NSDate dateWithTimeIntervalSinceNow:-i*24*3600]];
        NSArray *dateArray = [db findlimit:datestr];
        if (dateArray.count > 0) {
            [_dataSourceArr addObject:dateArray];
        }
        
    }
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return _dataSourceArr.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return heightHeadSection;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    UIView *headView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, kSCREEN_WIDTH, heightHeadSection)];
    headView.backgroundColor = RGBACOLOR(239, 240, 241, 1);
    
    YZLabel *titleLab = [[YZLabel alloc]initWithFrame:CGRectMake(0, 0, kSCREEN_WIDTH, heightHeadSection)];
    titleLab.textColor = RGBACOLOR(82, 83, 84, 1);
    titleLab.textAlignment = NSTextAlignmentCenter;
    titleLab.font = [UIFont systemFontOfSize:14];
    
    [titleLab setThemeLabBackgroundColor:kTmDetailSectionBackgroudColor];
    [titleLab setThemeTextColor:kTmDetailSectionTextColor];
    [titleLab openThemeSkin];
    
    NSArray *arr = _dataSourceArr[section];
    YZHistory *hist = [arr objectAtIndex:0];
    titleLab.text = [NSString stringWithFormat:@"%@您阅读了%@条",hist.historyDate,@(arr.count)];
    [headView addSubview:titleLab];
    return headView;
}


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    NSArray *arr = _dataSourceArr[section];
    return arr.count;
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    return kSearchHistoryCellRow;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    YZHistoryCell *cell = [tableView dequeueReusableCellWithIdentifier:kHistoryCellIdentifier forIndexPath:indexPath];
    
    NSArray *arr = _dataSourceArr[indexPath.section];
    if (arr.count > indexPath.row)
    {
        YZHistory *hist = [arr objectAtIndex:indexPath.row];
        cell.textLabel.text = hist.historyTitle;
    }
    
    return cell;
}


#pragma mark--

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSArray *arr = _dataSourceArr[indexPath.section];
    
    if (arr.count > indexPath.row)
    {
        YZHistory *hist = arr[indexPath.row];
        YZHomeViewModel *viewModel = [YZHomeViewModel viewModel];
        viewModel.poastModel.postID = hist.ID.integerValue;
        viewModel.poastModel.title = hist.historyTitle;
        
        YZArticleDetailController *viewController = [[YZArticleDetailController alloc] initWithViewModel:viewModel];
        viewController.view.backgroundColor = [UIColor whiteColor];
        
        [self.navigationController pushViewController:viewController animated:YES];
    }
    
}
- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return  UITableViewCellEditingStyleDelete;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath{
    if (editingStyle == UITableViewCellEditingStyleDelete){
        
        NSArray *arr = _dataSourceArr[indexPath.section];
        YZHistory *hist = [arr objectAtIndex:indexPath.row];
        [db deleteAhistoryWithId:hist.ID];
        [self loadHistoryData];
        [tableView reloadData];
        
        
    }
    
}


#pragma mark -- Methods


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
