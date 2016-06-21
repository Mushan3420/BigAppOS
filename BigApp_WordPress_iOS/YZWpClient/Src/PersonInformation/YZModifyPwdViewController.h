//
//  YZModifyPwdViewController.h
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/15.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZBaseViewController.h"

@interface YZModifyPwdViewController : YZBaseViewController<UITextFieldDelegate>

@property (strong,nonatomic)void(^drawBackPersonPwd)(NSString *personNeWpaw);


@end
