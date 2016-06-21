//
//  YZSubSpecialController.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/22.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZSubSpecialController.h"
#import "UIViewController+YZProgressHUD.h"
#import "MJRefresh.h"
#import "YZSpecialService.h"

#define kSubSpeCellIdentifier @"YZSubSpecialCell"


@interface YZSubSpecialController ()

@property (nonatomic, strong)NSMutableArray *dataSourceArr;
@property (nonatomic, assign)NSInteger    currentPage;


@end

@implementation YZSubSpecialController

@synthesize urlStr;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"列表详情";
    self.isBackBarButtonItemShow = YES;
    
    _dataSourceArr = [@[] mutableCopy];
    [self.tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:kSubSpeCellIdentifier];
    [self.tableView setSeparatorStyle:UITableViewCellSeparatorStyleSingleLine];
    
    //    [self loadFavoriteData:YES];
    [self.tableView addHeaderWithTarget:self action:@selector(headerRereshing) dateKey:@"table"];
    
}

#pragma mark-- Methods
#pragma mark 开始进入刷新状态
- (void)headerRereshing
{
    self.currentPage = 1;
    
    [self loadFavoriteData:YES];
}

- (void)loadFavoriteData:(BOOL)isRefresh{
    [YZSpecialService getSubSpecialList:self.urlStr success:^(NSMutableArray *listArray){
        
        [self.tableView headerEndRefreshing];
        
        self.dataSourceArr = listArray;
        [self.tableView reloadData];
        
        if ([_dataSourceArr count]==0) {
            [self presentSheet:@"暂无专题" ForView:self.view];
        }
        
    }failure:^(NSString *eorror_code,NSString *error){
        [self.tableView headerEndRefreshing];
        [self presentSheet:error ForView:self.view];
    }];
}



#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _dataSourceArr.count;
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    return 100;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kSubSpeCellIdentifier forIndexPath:indexPath];
    YZPostsModel *postM = [self.dataSourceArr objectAtIndex:indexPath.row];
    cell.textLabel.text = postM.title;
    return cell;
}


#pragma mark--

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{

    
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
