//
//  YZModifyNicknameController.h
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/15.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZBaseViewController.h"

@interface YZModifyNicknameController : YZBaseViewController<UITextFieldDelegate>

@property (strong,nonatomic)NSString *nickNameStr;

@property (strong,nonatomic)void(^drawBackNickName)(NSString *nickName);

@end
