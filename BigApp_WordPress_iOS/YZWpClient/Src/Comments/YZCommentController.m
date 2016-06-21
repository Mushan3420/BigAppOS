//
//  YZCommentController.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/12.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZCommentController.h"
#import "MozTopAlertView.h"
#import "YZHomeService.h"
#import "UIViewController+YZProgressHUD.h"
#import "YZLoginController.h"
#import "Config.h"
@interface YZCommentController ()<UITextViewDelegate, UITextFieldDelegate>

@property (nonatomic, strong)UITextField *emailTextField;//邮箱
@property (nonatomic, strong)UITextField *nickNameTextField;//昵称
@property (nonatomic, strong)UITextView  *contentTextView;//评论内容

@property (nonatomic, assign)CGRect      keyboardRect;
@property double doubleValue;

@property (nonatomic, strong)UIButton *sendCommnetBtn;

@end

@implementation YZCommentController
- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.isBackBarButtonItemShow = YES;
    [self setUpTableView];

    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyBoardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyBoardWillHide:) name:UIKeyboardWillHideNotification object:nil];
    
    
}
- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    if ([Config isLogin]) {
        [self.navigationController popViewControllerAnimated:YES];
        
        double delayInSeconds = 1.0;
        dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayInSeconds * NSEC_PER_SEC));
        dispatch_after(popTime, dispatch_get_main_queue(), ^(void){
            [self.delegate showWriteCommentAction:nil];
        });
    }
    else{
        [self setUpLoginButton];
        
        self.title = LocalizedString(@"WRITE_COMMENT");
        [self.navigationController.view addSubview:self.sendCommnetBtn];
    }
}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    
    [self.sendCommnetBtn removeFromSuperview];
}

- (void)setUpTableView{
    self.tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStyleGrouped];
    self.tableView.delegate = self;
    self.tableView.backgroundColor = RGBCOLOR(249, 249, 249);
    self.tableView.showsVerticalScrollIndicator = NO;
    self.tableView.tableFooterView = [UIView new];
}

- (void)setUpLoginButton{
    UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
    button.frame = CGRectMake(0, 0, 44, 44);
    [button setTitle:LocalizedString(@"LOGIN_BTN") forState:UIControlStateNormal];
    button.titleLabel.font = [UIFont fontWithName:kChineseFontNameXi size:14];
    [button setTitleColor:RGBCOLOR(60, 60, 60) forState:UIControlStateNormal];
    [button addTarget:self action:@selector(loginAction) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backItem = [[UIBarButtonItem alloc] initWithCustomView:button];
    self.navigationItem.rightBarButtonItem = backItem;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}
#pragma mark --

- (void)loginAction{
    
    YZLoginController *login= [YZLoginController new];
    [[UIApplication sharedApplication].keyWindow.rootViewController presentViewController:login
                                                                                 animated:YES completion:^{
                                                                                 }];
}

- (void)sendCommnetAction:(UIButton *)btn{
    
    //必须是注册用户，所以必须登录
    if ((self.delegate.viewModel.commentType == NotAllowAnonymousCommentType)) {
        
        if (!(_emailTextField.text.length>0 && _nickNameTextField.text.length>0)) {
            [MozTopAlertView hideViewWithParentView:self.navigationController.view];
            [MozTopAlertView showWithType:MozAlertTypeInfo text:@"请输入用户名和邮箱" parentView:self.navigationController.view];
            return;

        }
    }
    
    NSString *comment = _contentTextView.text;
    NSString *articleId = [NSString stringWithFormat:@"%@",@(self.delegate.viewModel.poastModel.postID)];
    [self showLoadingWithTitle:@"正在发送评论" AddedTo:self.navigationController.view];
    
    [YZHomeService doSendCommentsWithComment:comment
                                   articleId:articleId
                                      author:_nickNameTextField.text
                                       email:_emailTextField.text
                                     success:^(NSDictionary *dic){
                                         [self dismissHuDForView:self.navigationController.view];
                                         [self presentSheet:LocalizedString(@"PUBLISH_COMMENT_SUCCUSS") ForView:self.navigationController.view];
                                         
                                         [self.navigationController popViewControllerAnimated:YES];
                                     } failure:^(NSString *statusCode, NSString *error) {
                                         [self dismissHuDForView:self.navigationController.view];
                                        [MozTopAlertView showWithType:MozAlertTypeInfo
                                                                 text:error
                                                           parentView:self.navigationController.view];
                                     }];

}

#pragma mark-- property

-(UITextField *)emailTextField{
    if (!_emailTextField) {
        
        UITextField *theTextField = [[UITextField alloc] initWithFrame:CGRectMake(20, 0, kSCREEN_WIDTH-20, 44)];
        theTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        theTextField.returnKeyType = UIReturnKeyNext;
        theTextField.clearButtonMode = YES;
        theTextField.placeholder = @"邮箱";
        theTextField.font = [UIFont fontWithName:kChineseFontNameXi size:16];
        theTextField.textColor = RGBCOLOR(88, 88, 88);
        theTextField.delegate = self;
        theTextField.enablesReturnKeyAutomatically = YES;

        _emailTextField = theTextField;
        
    }
    return _emailTextField;
}

- (UITextField *)nickNameTextField{
    if (!_nickNameTextField) {
        UITextField *theTextField = [[UITextField alloc] initWithFrame:CGRectMake(20, 0, kSCREEN_WIDTH-20, 44)];
        theTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        theTextField.returnKeyType = UIReturnKeyNext;
        theTextField.clearButtonMode = YES;
        theTextField.placeholder = @"昵称";
        theTextField.font = [UIFont fontWithName:kChineseFontNameXi size:16];
        theTextField.textColor = RGBCOLOR(88, 88, 88);
        theTextField.delegate = self;
        theTextField.enablesReturnKeyAutomatically = YES;

        _nickNameTextField = theTextField;
    }
    return _nickNameTextField;
}

- (UITextView *)contentTextView{
    if (!_contentTextView) {
        _contentTextView = [[UITextView alloc] initWithFrame:CGRectZero];
        _contentTextView.backgroundColor=[UIColor whiteColor];
        _contentTextView.editable = YES;
        _contentTextView.returnKeyType = UIReturnKeySend;
        _contentTextView.keyboardType = UIKeyboardTypeDefault;
        _contentTextView.textAlignment = NSTextAlignmentLeft;
        _contentTextView.dataDetectorTypes = UIDataDetectorTypeAll;
        _contentTextView.enablesReturnKeyAutomatically = YES;

        _contentTextView.frame = CGRectMake(20, 5, kSCREEN_WIDTH-20, 130);
        _contentTextView.delegate = self;
        _contentTextView.font = [UIFont fontWithName:kChineseFontNameXi size:16];
        _contentTextView.textColor = RGBCOLOR(88, 88, 88);


    }
    return _contentTextView;
}

- (UIButton *)sendCommnetBtn{
    if (!_sendCommnetBtn) {
        _sendCommnetBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        _sendCommnetBtn.frame = CGRectMake(0, kSCREEN_HEIGHT-44, kSCREEN_WIDTH, 44);
        [_sendCommnetBtn addTarget:self action:@selector(sendCommnetAction:) forControlEvents:UIControlEventTouchUpInside];

        [_sendCommnetBtn setImage:[UIImage imageNamed:@"comment_confirm_button"] forState:UIControlStateNormal];
        [_sendCommnetBtn setImage:[UIImage imageNamed:@"comment_confirm_button_down"] forState:UIControlStateDisabled];
        
        [self sendCommentBtnenabled:NO];
        
    }
    return _sendCommnetBtn;
}

- (void)sendCommentBtnenabled:(BOOL)bol{
    if (!bol) {
        self.sendCommnetBtn.enabled = NO;
        self.sendCommnetBtn.backgroundColor = RGBCOLOR(197, 197, 197);
    }
    else{
        self.sendCommnetBtn.enabled = YES;
        self.sendCommnetBtn.backgroundColor = RGBCOLOR(145, 227, 182);
    }
}

#pragma mark-- UITableViewDelegate,UITableViewDataSource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    switch (section) {
        case 0:
            return 2;
            break;
        case 1:
            return 1;
            break;
        default:
            break;
    }
    
    return 1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 20.f;
}
- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return 0.0001f;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    switch (indexPath.section) {
        case 0:
            return 44;
            break;
        case 1:
            return 140;
            break;
            
        default:
            break;
    }
    
    return 80;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *kCellIdentifier = @"cell_id";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kCellIdentifier];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kCellIdentifier];
        cell.backgroundColor = [UIColor whiteColor];
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    
    [cell.contentView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    switch (indexPath.section) {
        case 0:
        {
            if (indexPath.row == 0) {
                [cell.contentView addSubview:self.emailTextField];
            }
            else{
                [cell.contentView addSubview:self.nickNameTextField];
            }
        }
            
            break;
        case 1:
        {
            [cell.contentView addSubview:self.contentTextView];
        }
            break;
            
        default:
            break;
    }
    
    return  cell;
}

#pragma mark - 键盘处理
#pragma mark 键盘即将显示
- (void)keyBoardWillShow:(NSNotification *)note{
    
    _keyboardRect = [note.userInfo[UIKeyboardFrameEndUserInfoKey] CGRectValue];
    _doubleValue  = [note.userInfo[UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    
    CGFloat ty = - _keyboardRect.size.height;
    
    [UIView animateWithDuration:_doubleValue animations:^{
        self.sendCommnetBtn.transform = CGAffineTransformMakeTranslation(0, ty);
        
    }];
}
#pragma mark 键盘即将退出
- (void)keyBoardWillHide:(NSNotification *)note{
    
    [UIView animateWithDuration:[note.userInfo[UIKeyboardAnimationDurationUserInfoKey] doubleValue] animations:^{
        self.view.transform = CGAffineTransformIdentity;
        self.sendCommnetBtn.transform = CGAffineTransformIdentity;
    }];
}

- (void)textViewDidBeginEditing:(UITextView *)textView{
    
    CGRect rectInTableView = self.contentTextView.frame;
    
    CGRect rect = [self.view convertRect:rectInTableView toView:[self.tableView superview]];
    
    CGFloat h = CGRectGetMaxY(rect);
    CGFloat ty = (self.tableView.frame.size.height-h) - _keyboardRect.size.height;
    
    if (ty < 0) {
        [UIView animateWithDuration:_doubleValue animations:^{
            self.view.transform = CGAffineTransformMakeTranslation(0, ty);
            
        }];
    }
}
#pragma mark-- UITextViewDelegate
- (void)textViewDidChange:(UITextView *)textView{

    [self sendCommentBtnenabled:(textView.text.length > 0)];
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text
{
    
    if ([text isEqualToString:@"\n"]) {
        
        if (textView.text.length > 0) {
            [self sendCommnetAction:nil];
        }

        return NO;
    }
    
    return YES;
}
#pragma mark-- UITextField Delegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if(textField.returnKeyType==UIReturnKeyNext){       //显示下一个
        if (textField == _emailTextField) {
            [_nickNameTextField becomeFirstResponder];
        }
        
        if (textField == _nickNameTextField) {
            [_contentTextView becomeFirstResponder];
        }
    }
    return YES;
}

@end
