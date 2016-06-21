//
//  YZModifyPwdViewController.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/15.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZModifyPwdViewController.h"
#import "YZModifyPersonInfoService.h"
#import "UIViewController+YZProgressHUD.h"

@interface YZModifyPwdViewController ()

@property (strong,nonatomic)UITextField *oldTextField;
@property (strong,nonatomic)UITextField *anewTextField;
@property (strong,nonatomic)UITextField *secTextField;


@end

@implementation YZModifyPwdViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.isBackBarButtonItemShow = YES;
    self.title = @"修改密码";
    
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
    if ([self checkPassword]) {
        [self.oldTextField resignFirstResponder];
        [self.anewTextField resignFirstResponder];
        [self.secTextField resignFirstResponder];
        [self showLoadingAddedTo:self.view];
        [YZModifyPersonInfoService doModifyPersonPasswordWith:self.secTextField.text success:^(NSString *successCode){
            _drawBackPersonPwd(self.secTextField.text);
            [self presentSheet:@"修改密码成功!" ForView:self.view];
            [self.navigationController popViewControllerAnimated:YES];
        }failure:^(NSString *statusCode, NSString *error){
            [self presentSheet:error ForView:self.view];
        }];
    }
}

- (UITextField *)oldTextField
{
    if (!_oldTextField) {
        _oldTextField = [[UITextField alloc]initWithFrame:CGRectMake(15, 0, kSCREEN_WIDTH-20, 50)];
        _oldTextField.delegate = self;
        _oldTextField.placeholder = @"旧密码";
        _oldTextField.keyboardType = UIKeyboardTypeNamePhonePad;
        _oldTextField.secureTextEntry = YES;
        _oldTextField.font = [UIFont systemFontOfSize:14];
        _oldTextField.returnKeyType =UIReturnKeyContinue;
        [_oldTextField becomeFirstResponder];
    }
    return _oldTextField;
}

- (UITextField *)anewTextField
{
    if (!_anewTextField) {
        _anewTextField = [[UITextField alloc]initWithFrame:CGRectMake(15, 0, kSCREEN_WIDTH-20, 50)];
        _anewTextField.delegate = self;
        _anewTextField.font = [UIFont systemFontOfSize:14];
        _anewTextField.placeholder = @"新密码";
        _anewTextField.keyboardType = UIKeyboardTypeNamePhonePad;
        _anewTextField.secureTextEntry = YES;
        _anewTextField.returnKeyType =UIReturnKeyContinue;
    }
    return _anewTextField;
}

- (UITextField *)secTextField
{
    if (!_secTextField) {
        _secTextField = [[UITextField alloc]initWithFrame:CGRectMake(15, 0, kSCREEN_WIDTH-20, 50)];
        _secTextField.delegate = self;
        _secTextField.font = [UIFont systemFontOfSize:14];
        _secTextField.placeholder = @"确认密码";
        _secTextField.keyboardType = UIKeyboardTypeNamePhonePad;
        _secTextField.secureTextEntry = YES;
        _secTextField.returnKeyType =UIReturnKeyDone;
    }
    return _secTextField;
}

- (BOOL)checkPassword
{
    if (self.oldTextField.text.length == 0) {
        [self.oldTextField becomeFirstResponder];
        return NO;
    }
    else if (self.anewTextField.text.length == 0)
    {
        [self.anewTextField becomeFirstResponder];
        return NO;
    }
    else if (![self.secTextField.text isEqualToString:self.anewTextField.text])
    {
        UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"提示" message:@"两次输入密码不一致" delegate:nil cancelButtonTitle:@"确定" otherButtonTitles:nil, nil];
        [alert show];
        [self.secTextField becomeFirstResponder];
        return NO;
    }
    return YES;
    
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    
    if ([textField isEqual:self.oldTextField]) {
        [self.anewTextField becomeFirstResponder];
        return YES;
    } 
    else if ([textField isEqual:self.anewTextField])
    {
        [self.secTextField becomeFirstResponder];
        return YES;
    }
    
    [self commitInfo];
    
    
    return NO;
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

    return 3;
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

    if (indexPath.row == 0) {
        [cell.contentView addSubview:self.oldTextField];
    }
    else if (indexPath.row == 1)
    {
        [cell.contentView addSubview:self.anewTextField];
    }
    else if (indexPath.row == 2)
    {
        [cell.contentView addSubview:self.secTextField];
    }
    
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
