//
//  YZSpecialController.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/22.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZSpecialController.h"
#import "UIViewController+YZProgressHUD.h"
#import "MJRefresh.h"
#import "YZSubSpecialController.h"
#import "YZSpecialService.h"

#define kSpecialCellIdentifier @"YZSpecialCell"


@interface YZSpecialController ()

@property (nonatomic, strong)NSMutableArray *dataSourceArr;
@property (nonatomic, assign)NSInteger    currentPage;

@end

@implementation YZSpecialController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    _dataSourceArr = [@[] mutableCopy];
    [self.tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:kSpecialCellIdentifier];
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
    [YZSpecialService getSpecialList:nil success:^(NSMutableArray *listArray){
        
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
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kSpecialCellIdentifier forIndexPath:indexPath];
    YZSpecialModel *special = [self.dataSourceArr objectAtIndex:indexPath.row];
    cell.textLabel.text = special.name;
    return cell;
}


#pragma mark--

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    YZSpecialModel *special = [self.dataSourceArr objectAtIndex:indexPath.row];
    YZSubSpecialController *subspe = [[YZSubSpecialController alloc]init];
    subspe.urlStr = special.link;
    [self.navigationController pushViewController:subspe animated:YES];
    
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
