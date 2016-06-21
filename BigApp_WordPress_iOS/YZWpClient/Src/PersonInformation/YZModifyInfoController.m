//
//  YZModifyInfoController.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/15.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZModifyInfoController.h"
#import "YZModifyPersonInfoService.h"
#import "UIViewController+YZProgressHUD.h"


#define kCellHeight   120

@interface YZModifyInfoController ()

@property (strong,nonatomic)UITextView *infoTextView;

@end

@implementation YZModifyInfoController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.isBackBarButtonItemShow = YES;
    self.title = @"修改个人说明";
    
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
    [self.infoTextView resignFirstResponder];
    [self showLoadingAddedTo:self.view];
    [YZModifyPersonInfoService doModifyPersondescriptionWith:self.infoTextView.text success:^(NSString *successCode){
        _drawBackPersonInfo(self.infoTextView.text);
        [self.navigationController popViewControllerAnimated:YES];
    }failure:^(NSString *statusCode, NSString *error){
        [self presentSheet:error ForView:self.view];
    }];
}

- (UITextView *)infoTextView
{
    if (!_infoTextView) {
        _infoTextView = [[UITextView alloc]initWithFrame:CGRectMake(0, 0, kSCREEN_WIDTH, kCellHeight)];
        _infoTextView.delegate = self;
    }
    return _infoTextView;
}

-(BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    if ([text isEqualToString:@"\n"])
    {
        [textView resignFirstResponder];
        
        [self commitInfo];
        
        return NO;
    }
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
    return kCellHeight;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *kCellIdentifier = @"cell_id";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kCellIdentifier];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:kCellIdentifier];
    }
    
    [cell.contentView addSubview:self.infoTextView];
    [self.infoTextView becomeFirstResponder];
    self.infoTextView.text = self.infoStr;
    
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
