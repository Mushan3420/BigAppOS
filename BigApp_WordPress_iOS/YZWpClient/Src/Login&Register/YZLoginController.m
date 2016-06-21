//
//  YZLoginController.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/16.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZLoginController.h"
#import "YZLoginView.h"
#import "FlatPillButton.h"
#import "YZUserService.h"
#import "UIViewController+YZProgressHUD.h"
#import "Config.h"
#import <ShareSDK/ShareSDK.h>
#import <ShareSDKExtension/ShareSDK+Extension.h>
#import "YZMobLoginService.h"
#import "YZSavePostDBModel.h"

@interface YZLoginController ()<UITextFieldDelegate>
{
    MobLoginModel                   *mobLoginModel;  //绑定的帐号,默认nil
    NSInteger                       MobLoginType;
}

@property (nonatomic, strong)YZSavePostDBModel *postModel;

@property(nonatomic, strong)YZLoginView *loginView;

@end

@implementation YZLoginController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.tableView.tableHeaderView = self.loginView;
    self.tableView.tableFooterView = [UIView new];
    
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

- (YZLoginView *)loginView{
    
    if (!_loginView) {
        _loginView = [[YZLoginView alloc] initWithFrame:[UIScreen mainScreen].bounds];
        
        [_loginView.closeButton addTarget:self action:@selector(closeAction) forControlEvents:UIControlEventTouchUpInside];
        
        [_loginView.registerButton addTarget:self action:@selector(goRegisterAction) forControlEvents:UIControlEventTouchUpInside];
        
        [_loginView.loginButton addTarget:self action:@selector(loginAction) forControlEvents:UIControlEventTouchUpInside];
        
        typeof(self) __weak weakSelf = self;
        [_loginView setThirdSdkLogin:^(NSInteger type){
            [weakSelf MobLogin:type];
        }];
        
    }
    return _loginView;
}

-(NSString *)platForm:(NSInteger)type
{
    NSArray *dicArr = @[@"wechat",@"qq",@"sina"];
    return [dicArr objectAtIndex:type];
}

-(SSDKPlatformType)fromType:(NSInteger)type
{
    SSDKPlatformType formtype;
    switch (type) {
        case 0:
            formtype = SSDKPlatformSubTypeWechatSession;
            break;
        case 1:
            formtype = SSDKPlatformSubTypeQZone;
            break;
        case 2:
            formtype = SSDKPlatformTypeSinaWeibo;
            break;
        default:
            break;
    }
    return formtype;
}


- (void)MobLogin:(NSInteger)type
{
    MobLoginType = type;
    [ShareSDK authorize:[self fromType:type] settings:nil onStateChanged:^(SSDKResponseState state, SSDKUser *user, NSError *error){
        SSDKCredential *credential = user.credential;
        NSLog(@"%@",credential.token);
        
        MobLoginModel *mobLogin = [[MobLoginModel alloc]init];
        mobLogin.platform = [self platForm:type];
        mobLogin.openid = credential.uid;
        mobLogin.token = credential.token;
        mobLoginModel = mobLogin;
        
        [self showLoadingAddedTo:self.view];
        [YZMobLoginService checkMobLoginBinding:mobLogin success:^(YZMobLoginModel *sversionModel){
            
            [self dismissHuDForView:self.view];
            
            if (sversionModel.hasbind == 1) {
                YZLoginModel *loginModel = [[YZLoginModel alloc]init];
                loginModel.display_name = sversionModel.username;
                loginModel.niceName = sversionModel.nice_name;
                loginModel.id = sversionModel.uid.integerValue;
                loginModel.email = sversionModel.email;
                loginModel.avatar = sversionModel.avatar;
                loginModel.udescription = sversionModel.udescription;
                
                [Config saveUserAccount:loginModel.niceName password:@""];
                
                [Config saveLoginUser:loginModel];
                
                //同步文章
                [self sysArticle];
                
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self closeAction];
                });
            }
            else
            {
                UIAlertView *alertView = [[UIAlertView alloc]initWithTitle:@"提示" message:@"您尚未绑定账号，请使用现有账号登录绑定，或者注册新账号绑定" delegate:self cancelButtonTitle:@"去登录" otherButtonTitles:@"去注册", nil];
                [alertView show];
 
            }

        }failure:^(NSString *statusCode, NSString *error){
            if ([error isKindOfClass:[NSArray class]]) {
                error = ((NSArray *)error).firstObject;
            }
            [self presentSheet:error ForView:self.view];
        }];
        
    }];

    
    

}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 0) {
        //
        [self.loginView.acountNameField becomeFirstResponder];
        
    }
    else if(buttonIndex == 1)
    {
        [self goRegisterAction];

    }
}

- (void)gotoBindAccout
{
    
}

#pragma mark - Actions

- (void)loginAction{
    
    if (!_loginView.isRegisterView) {
        
        if ([self loginInfoVerify]) {
            YZLoginDto *loginDto = [[YZLoginDto alloc] init];
            loginDto.log = _loginView.acountNameField.text;
            loginDto.pwd = _loginView.passwordField.text;
            if (mobLoginModel != nil) {
                loginDto.bind = @"1";
                loginDto.platform = mobLoginModel.platform;
                loginDto.openid = mobLoginModel.openid;
                loginDto.token = mobLoginModel.token;

            }
            
            [self showLoadingAddedTo:self.view];
            [YZUserService doLoginWith:loginDto success:^(YZLoginModel *loginModel) {
                [self presentSheet:@"登陆成功" ForView:self.view];
                
                [Config saveUserAccount:loginModel.display_name password:@""];
                [Config saveLoginUser:loginModel];
                
                //同步文章
                [self sysArticle];
                
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self closeAction];
                });
                
            } failure:^(NSString *statusCode, NSString *error) {
                if ([error isKindOfClass:[NSArray class]]) {
                    error = ((NSArray *)error).firstObject;
                }
                [self presentSheet:error ForView:self.view];
            }];
        }
        
    }
    else{
        if ([self registerInfoVerify]) {
            NSDictionary *params = @{
                                     @"user_login":_loginView.acountNameField.text,
                                     @"password":_loginView.passwordField.text
                                     };
            if (mobLoginModel != nil) {
                params = @{
                                         @"user_login":_loginView.acountNameField.text,
                                         @"password":_loginView.passwordField.text,
                                         @"bind":@"1",
                                         @"platform":mobLoginModel.platform,
                                         @"openid":mobLoginModel.openid,
                                         @"token":mobLoginModel.token,
                                         
                                         };
                
            }
            [self showLoadingAddedTo:self.view];
            [YZUserService doRegisterWith:params success:^(id responseObject) {
                [self dismissHuDForView:self.view];
                [self presentSheet:@"注册成功" ForView:self.view];
                
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self closeAction];
                });
                
                //第三方登录，注册成功后再次登陆
                if (mobLoginModel != nil) {
                    [self MobLogin:MobLoginType];
                }
                
            } failure:^(NSString *statusCode, NSString *error) {
                [self dismissHuDForView:self.view];
                [self presentSheet:error ForView:self.view];
                
            }];
        }
        
    }
    
    
}//登陆


- (void)goRegisterAction{
    [_loginView clearText];
    _loginView.isRegisterView = YES;
    [self animationBaseView:_loginView flag:YES];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self.loginView.acountNameField becomeFirstResponder];
        
    });
}

- (void)closeAction{
    
    [self.view endEditing:YES];
    [_loginView clearText];
    if (_loginView.isRegisterView) {
        _loginView.isRegisterView = NO;
        [self animationBaseView:_loginView flag:NO];
    }else{
        [self dismissViewControllerAnimated:YES completion:nil];
    }
}//退出登陆页面



- (void)animationBaseView:(UIView *)baseView flag:(BOOL)flag
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.5];
    [baseView exchangeSubviewAtIndex:0 withSubviewAtIndex:1];
    [UIView setAnimationTransition:flag ? UIViewAnimationTransitionFlipFromLeft : UIViewAnimationTransitionFlipFromRight forView:baseView cache:YES];
    [UIView commitAnimations];
    

    
}// 翻转过渡动画效果


- (BOOL)loginInfoVerify{
    
    if ([_loginView.acountNameField.text isEqualToString:@""]) {
        
        [self presentSheet:@"请输入用户名" ForView:self.view];
        return NO;
    }
    
    if ([_loginView.passwordField.text isEqualToString:@""]) {
        
        [self presentSheet:@"请输入密码" ForView:self.view];
        return NO;
    }
    
    return YES;
    
}

- (BOOL)registerInfoVerify{
    
    if ([self loginInfoVerify]) {
        if ([_loginView.confirmPasswordField.text isEqualToString:@""]) {
            
            [self presentSheet:@"请输入密码" ForView:self.view];
            return NO;
        }
        
        if (![_loginView.passwordField.text isEqualToString:_loginView.confirmPasswordField.text]) {
            
            [self presentSheet:@"两次输入密码不一致，请确认" ForView:self.view];
            return NO;
            
        }
        return YES;
    }
    else{
        return NO;
    }
    
}

#pragma mark 同步收藏文章

- (void)sysArticle
{
    YZSysSaveArticle *sysArt = [[YZSysSaveArticle alloc]init];
    NSArray *arrar = [self.postModel findAllSavePost];
    NSMutableString *arrStr = [[NSMutableString alloc]init];
    [arrar enumerateObjectsUsingBlock:^(YZPostsModel *obj, NSUInteger idx, BOOL *stop) {
        [arrStr appendString:[NSString stringWithFormat:@"%@",@(obj.postID)]];
        if (idx < arrar.count - 1) {
            [arrStr appendString:@","];
        }
    }];
    sysArt.wp_bigapp_favorite_posts = arrStr;
    [YZUserService doSyaArticle:sysArt success:^(BOOL respon){
        if (respon) {
            [self.postModel deleteAllPostModel];
        }
    }failure:^(NSString *error_code, NSString *error){
        
    }];
}

@end
