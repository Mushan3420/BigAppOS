//
//  YZModifyInfoController.h
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/15.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZBaseViewController.h"

@interface YZModifyInfoController : YZBaseViewController<UITextViewDelegate>

@property (strong,nonatomic)NSString *infoStr;

@property (strong,nonatomic)void(^drawBackPersonInfo)(NSString *personInfo);

@end
