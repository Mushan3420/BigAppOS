//
//  YZLoginView.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/16.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>
@class FlatPillButton;
@class YZShareLoginView;
@interface YZLoginView : UIView<UITextFieldDelegate>

@property (nonatomic, strong) UIImageView *logoIcon;

@property (nonatomic, strong) UITextField *acountNameField;
@property (nonatomic, strong) UITextField *passwordField;

@property (nonatomic, strong) UITextField *confirmPasswordField;

@property (nonatomic, strong) FlatPillButton    *loginButton;
@property (nonatomic, strong) UIButton    *registerButton;
@property (nonatomic, strong) UIButton    *forgetPwdButton;
@property (nonatomic, strong) UIButton    *closeButton;


@property (nonatomic, strong) YZShareLoginView *shareView;


@property (nonatomic, assign)  BOOL       isRegisterView;

@property (strong,nonatomic)void(^ThirdSdkLogin)(NSInteger logintType);

- (void)clearText;

@end
