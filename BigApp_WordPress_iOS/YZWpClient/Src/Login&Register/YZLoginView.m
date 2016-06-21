//
//  YZLoginView.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/16.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZLoginView.h"
#import "BuildConfig.h"
#import "UIViewExt.h"
#import "FlatPillButton.h"
#import "UIImage+SNAdditions.h"
#import "YZShareLoginView.h"

@implementation YZLoginView 

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        
        self.backgroundColor = RGBCOLOR(250, 250, 250);
        
        [self addSubview:self.logoIcon];
        [self addSubview:self.closeButton];
        
        [self addSubview:self.acountNameField];
        [self addSubview:self.passwordField];
        
        [self addSubview:self.loginButton];
        
        [self addSubview:self.registerButton];
//        [self addSubview:self.forgetPwdButton];//1.0隐藏
        
        [self addSubview:self.confirmPasswordField];
        
        [self addSubview:self.shareView];//1.0隐藏
        
        [self setSeparatoryLine];

    }
    return self;
}

//输入框描边
- (void)setSeparatoryLine{
    
    UIView *lineTop = [[UIView alloc] init];
    lineTop.backgroundColor = RGBCOLOR(210, 210, 210);
    lineTop.width = self.width;
    lineTop.height=0.5;
    [_acountNameField addSubview:lineTop];
    
    UIView *lineCenter = [[UIView alloc] init];
    lineCenter.backgroundColor = RGBACOLOR(210, 210, 210,0.7);
    lineCenter.width = self.width - 28;
    lineCenter.height=0.5;
    lineCenter.left = 28;
    [_passwordField addSubview:lineCenter];
    
    UIView *lineBottom = [[UIView alloc] init];
    lineBottom.backgroundColor = RGBCOLOR(210, 210, 210);
    lineBottom.width = self.width;
    lineBottom.height=0.5;
    lineBottom.top = 43.5;
    [_passwordField addSubview:lineBottom];
}

#pragma mark - Propertys

- (UIImageView *)logoIcon{
    if (!_logoIcon) {
        _logoIcon = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"login_logo"]];
    }
    return _logoIcon;
}

- (UITextField *)acountNameField{
    if (!_acountNameField) {
        _acountNameField = [UITextField new];
        _acountNameField.placeholder = LocalizedString(@"USER_ACCOUNT");
        _acountNameField.autocapitalizationType = UITextAutocapitalizationTypeNone;
        _acountNameField.keyboardType = UIKeyboardTypeEmailAddress;
//        _acountNameField.delegate = self;
        _acountNameField.returnKeyType = UIReturnKeyNext;
        _acountNameField.clearButtonMode = UITextFieldViewModeWhileEditing;
        _acountNameField.enablesReturnKeyAutomatically = YES;
        _acountNameField.size = CGSizeMake(self.width, 44);
        [_acountNameField setValue:RGBCOLOR(214, 214, 214) forKeyPath:@"_placeholderLabel.textColor"];
        [_acountNameField setValue:[UIFont fontWithName:kChineseFontNameXi size:14.0] forKeyPath:@"_placeholderLabel.font"];
        _acountNameField.backgroundColor = [UIColor whiteColor];
        _acountNameField.delegate =self;
        
        UIView *spaceView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 28, 28)];
        spaceView.backgroundColor = [UIColor clearColor];
        _acountNameField.leftView = spaceView;
        _acountNameField.leftViewMode =  UITextFieldViewModeAlways;
        _acountNameField.tintColor = RGBCOLOR(58, 211, 127);
        _acountNameField.font = [UIFont fontWithName:kChineseFontNameXi size:14];

    }
    
    return _acountNameField;
}

- (UITextField *)passwordField{
    if (!_passwordField) {
        _passwordField = [UITextField new];
        _passwordField.placeholder = LocalizedString(@"PASSWORD");
        _passwordField.secureTextEntry = YES;
//        _passwordField.delegate = self;
        _passwordField.returnKeyType = UIReturnKeyDone;
        _passwordField.clearButtonMode = UITextFieldViewModeWhileEditing;
        _passwordField.enablesReturnKeyAutomatically = YES;
        _passwordField.size = self.acountNameField.size;
        [_passwordField setValue:RGBCOLOR(214, 214, 214) forKeyPath:@"_placeholderLabel.textColor"];
        [_passwordField setValue:[UIFont fontWithName:kChineseFontNameXi size:14.0] forKeyPath:@"_placeholderLabel.font"];
        _passwordField.backgroundColor = [UIColor whiteColor];
        _passwordField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        UIView *spaceView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 28, 28)];
        spaceView.backgroundColor = [UIColor clearColor];
        _passwordField.leftView = spaceView;
        _passwordField.leftViewMode =  UITextFieldViewModeAlways;
        _passwordField.tintColor = RGBCOLOR(58, 211, 127);
        _passwordField.delegate =self;
        _passwordField.font = [UIFont fontWithName:kChineseFontNameXi size:14];
    }
    
    return _passwordField;
}

- (UITextField *)confirmPasswordField{
    if (!_confirmPasswordField) {
        _confirmPasswordField = [UITextField new];
        _confirmPasswordField.placeholder = LocalizedString(@"PASSWORD");
        _confirmPasswordField.secureTextEntry = YES;
        //        _passwordField.delegate = self;
        _confirmPasswordField.returnKeyType = UIReturnKeyNext;
        _confirmPasswordField.clearButtonMode = UITextFieldViewModeWhileEditing;
        _confirmPasswordField.enablesReturnKeyAutomatically = YES;
        _confirmPasswordField.size = self.acountNameField.size;
        [_confirmPasswordField setValue:RGBCOLOR(214, 214, 214) forKeyPath:@"_placeholderLabel.textColor"];
        [_confirmPasswordField setValue:[UIFont fontWithName:kChineseFontNameXi size:14.0] forKeyPath:@"_placeholderLabel.font"];
        _confirmPasswordField.backgroundColor = [UIColor whiteColor];
        _confirmPasswordField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        
        UIView *spaceView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 28, 28)];
        spaceView.backgroundColor = [UIColor clearColor];
        _confirmPasswordField.leftView = spaceView;
        _confirmPasswordField.leftViewMode =  UITextFieldViewModeAlways;
        _confirmPasswordField.tintColor = RGBCOLOR(58, 211, 127);
        
        _confirmPasswordField.font = [UIFont fontWithName:kChineseFontNameXi size:14];
        _confirmPasswordField.hidden = YES;
        _confirmPasswordField.delegate = self;
        
        UIView *lineCenter = [[UIView alloc] init];
        lineCenter.backgroundColor = RGBACOLOR(210, 210, 210,0.7);
        lineCenter.width = self.width - 28;
        lineCenter.height=0.5;
        lineCenter.left = 28;
        [_confirmPasswordField addSubview:lineCenter];
    }
    
    return _confirmPasswordField;
}

- (FlatPillButton *)loginButton{
    if (!_loginButton) {
        _loginButton = [[FlatPillButton alloc] init];
         UIFont *font = [UIFont fontWithName:kChineseFontNameXi size:13.0];
        _loginButton.titleLabel.font = font;
        [_loginButton setTitle:LocalizedString(@"LOGIN_BTN") forState:UIControlStateNormal];
//        [_loginButton addTarget:self action:@selector(login) forControlEvents:UIControlEventTouchUpInside];
        _loginButton.size = CGSizeMake(self.frame.size.width-60, 44);
        
        [_loginButton setTitleColor:RGBCOLOR(48, 48, 48) forState:UIControlStateNormal];
        _loginButton.fillColor = RGBCOLOR(58, 211, 127);
    }
    
    return _loginButton;
}

- (UIButton *)registerButton{
    if (!_registerButton) {
        _registerButton = [UIButton buttonWithType:UIButtonTypeCustom];
        UIFont *font = [UIFont fontWithName:kChineseFontNameXi size:14.0];
        _registerButton.titleLabel.font = font;
        [_registerButton setTitle:LocalizedString(@"CREAT_ACCOUNT") forState:UIControlStateNormal];
        
        [_registerButton setTitleColor:RGBCOLOR(153, 153, 153) forState:UIControlStateNormal];
        [_registerButton setTitleColor:RGBCOLOR(48, 48, 48) forState:UIControlStateHighlighted];
        
        _registerButton.size =CGSizeMake(100, 20);
    }
    
    return _registerButton;
}

- (UIButton *)forgetPwdButton{
    if (!_forgetPwdButton) {
        _forgetPwdButton = [UIButton buttonWithType:UIButtonTypeCustom];
        UIFont *font = [UIFont fontWithName:kChineseFontNameXi size:13.0];
        _forgetPwdButton.titleLabel.font = font;
        [_forgetPwdButton setTitle:LocalizedString(@"FORGET_PASSWORD") forState:UIControlStateNormal];
        [_forgetPwdButton setTitleColor:RGBCOLOR(153, 153, 153) forState:UIControlStateNormal];
        [_forgetPwdButton setTitleColor:RGBCOLOR(48, 48, 48) forState:UIControlStateHighlighted];
        _forgetPwdButton.size = CGSizeMake(71, 26);
        
    }
    return _forgetPwdButton;
}

- (UIButton *)closeButton{
    if (!_closeButton) {
        UIImage *image = [UIImage imageNamed:@"login_close"];
        _closeButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_closeButton setImage:image forState:UIControlStateNormal];
        _closeButton.size = CGSizeMake(image.size.width+10, image.size.height+10) ;
        
    }
    return _closeButton;
}


-(YZShareLoginView *)shareView{
    if (!_shareView) {
        _shareView = [YZShareLoginView new];
        
        
        [_shareView.qqBtn addTarget:self action:@selector(QQLogin:) forControlEvents:UIControlEventTouchUpInside];
        [_shareView.weichatBtn addTarget:self action:@selector(WeichatLogin:) forControlEvents:UIControlEventTouchUpInside];
        [_shareView.weiboBtn addTarget:self action:@selector(WeiboLogin:) forControlEvents:UIControlEventTouchUpInside];
        

        
    }
    return _shareView;
}

#pragma mark -- ThirdSdkLogin

- (void)WeichatLogin:(id)sender
{
    _ThirdSdkLogin(0);
}

- (void)QQLogin:(id)sender
{
    _ThirdSdkLogin(1);
}

- (void)WeiboLogin:(id)sender
{
    _ThirdSdkLogin(2);
}

#pragma mark -- Methods

- (void)setUpContentFrames{

    
    if (!_isRegisterView) {
        _logoIcon.center = CGPointMake(self.center.x, AutoDeviceHeight(190)-15-_logoIcon.height/2);
        _closeButton.center = CGPointMake(self.width - 25 - _closeButton.width/2.0, 35+_closeButton.width/2.0);
        
        _acountNameField.center = CGPointMake(self.center.x , _logoIcon.bottom + 22 + 15);
        _passwordField.center = CGPointMake(self.center.x , _acountNameField.bottom + 22);
        _loginButton.center = CGPointMake(self.center.x, _passwordField.bottom + 32 + 22);
        
        _registerButton.center = CGPointMake(self.center.x, self.height-17-7);
        _forgetPwdButton.center = CGPointMake(self.width-21-30,  self.height-17-7);
        
        _shareView.center =  CGPointMake(self.center.x, _loginButton.bottom + 21 + 12);
    }
    else{
        _closeButton.center = CGPointMake(self.width - 25 - _closeButton.width/2.0, 35+_closeButton.width/2.0);
        
        _acountNameField.center = CGPointMake(self.center.x , AutoDeviceHeight(190) -15 - 25);
        _confirmPasswordField.center = CGPointMake(self.center.x , _acountNameField.bottom + 22);
        _passwordField.center = CGPointMake(self.center.x , _confirmPasswordField.bottom + 22);
        _loginButton.center = CGPointMake(self.center.x, _passwordField.bottom + 32 + 22);
        
    }
}



- (void)layoutSubviews{
    [self setUpContentFrames];
    
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event{
    [self endEditing:YES];
}



-(void)setIsRegisterView:(BOOL)isRegisterView{
    
    _isRegisterView = isRegisterView;
    
    if (_isRegisterView) {
        _passwordField.placeholder = LocalizedString(@"CONFER_PASSWORD");
        [_loginButton setTitle:LocalizedString(@"REGIST_BTN") forState:UIControlStateNormal];

    }
    else{
        _passwordField.placeholder = LocalizedString(@"PASSWORD");
        [_loginButton setTitle:LocalizedString(@"LOGIN_BTN") forState:UIControlStateNormal];

    }
    
    _registerButton.hidden = _isRegisterView;
    _forgetPwdButton.hidden = _isRegisterView;
    _confirmPasswordField.hidden = !_isRegisterView;
    _logoIcon.hidden = _isRegisterView;
    _shareView.hidden = _isRegisterView;

}

- (void)clearText{
    _acountNameField.text = @"";
    _passwordField.text = @"";
    _confirmPasswordField.text = @"";
}

//要实现的Delegate方法,键盘next下跳
- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    if(textField.returnKeyType==UIReturnKeyNext){       //显示下一个
        if (textField == _acountNameField) {
            if (!_confirmPasswordField.hidden) {
                [_confirmPasswordField becomeFirstResponder];
            }
            else{
                [_passwordField becomeFirstResponder];
            }
        }
        
        if (textField == _confirmPasswordField) {
            [_passwordField becomeFirstResponder];
        }
        
        
    }
    
    if (textField.returnKeyType ==  UIReturnKeyDone) {
        [self endEditing:YES];
    }
    return YES;
}

@end
