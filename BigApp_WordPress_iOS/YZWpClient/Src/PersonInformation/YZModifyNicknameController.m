//
//  YZModifyNicknameController.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/15.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZModifyNicknameController.h"
#import "YZModifyPersonInfoService.h"
#import "UIViewController+YZProgressHUD.h"

@interface YZModifyNicknameController ()

@property (strong,nonatomic)UITextField *nickNameField;

@end

@implementation YZModifyNicknameController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    
    self.isBackBarButtonItemShow = YES;
    self.title = @"修改昵称";
    
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStyleGrouped];
    self.tableView.backgroundColor = [UIColor colorWithRed:242.0/255 green:242.0/255 blue:242.0/255 alpha:1];
    self.tableView.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    self.tableView.showsVerticalScrollIndicator = NO;
    
    [self setRightItemTitle:@"提交"];
    __weak __typeof(self)selfweak = self;
    [self setRightBarButtonItem:^(NSInteger type){
        [selfweak commitInfo];
    }];
}

- (void)commitInfo
{
    if (self.nickNameField.text != 0) {
        [self.nickNameField resignFirstResponder];
        [self showLoadingAddedTo:self.view];
        [YZModifyPersonInfoService doModifyPersonNickNameWith:self.nickNameField.text success:^(NSString *successCode){
            _drawBackNickName(self.nickNameField.text);
            [self.navigationController popViewControllerAnimated:YES];
        }failure:^(NSString *statusCode, NSString *error){
            [self presentSheet:error ForView:self.view];
        }];
    }
    else
    {
        [self presentSheet:@"昵称不可为空" ForView:self.view];
    }
    
    
}

- (UITextField *)nickNameField
{
    if (!_nickNameField) {
        _nickNameField = [[UITextField alloc]initWithFrame:CGRectMake(10, 0, kSCREEN_WIDTH-20, 50)];
        _nickNameField.delegate = self;
        
    }
    return _nickNameField;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if (textField.text.length == 0) {
        [textField becomeFirstResponder];
        return YES;
    }
    
    
    [self commitInfo];
    
    return YES;
}

#pragma mark - Table view data source

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 20;
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return 1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 50;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *kCellIdentifier = @"cell_id";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kCellIdentifier];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:kCellIdentifier];
    }
    
    
    cell.backgroundColor = [UIColor whiteColor];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    [cell.contentView addSubview:self.nickNameField];
    [self.nickNameField becomeFirstResponder];
    self.nickNameField.text = self.nickNameStr;
    
    return cell;
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
