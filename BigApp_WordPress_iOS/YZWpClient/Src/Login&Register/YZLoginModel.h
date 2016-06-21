//
//	YZLoginModel.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/2015
//	Copyright © 2015. All rights reserved.
//


#import <UIKit/UIKit.h>
#import "MJExtension.h"
@interface YZLoginModel : NSObject

@property (nonatomic, strong) NSString * display_name;
@property (nonatomic, strong) NSString * email;
@property (nonatomic, assign) NSInteger  id;
@property (nonatomic, assign) NSInteger  name;
@property (nonatomic, strong) NSString * niceName;
@property (nonatomic, assign) NSInteger  regTime;
@property (nonatomic, strong) NSArray  * roles;
@property (nonatomic, strong) NSString * status;
@property (nonatomic, strong) NSString * udescription;//与系统的description冲突
@property (nonatomic, strong) NSString * avatar;//头像
@property (nonatomic, assign) NSInteger  hasbind;

@end

@interface YZMobLoginModel : NSObject

@property (nonatomic, strong) NSString * username;
@property (nonatomic, strong) NSString * uid;
@property (nonatomic, assign) NSInteger  hasbind;
@property (nonatomic, strong) NSString * avatar;//头像
@property (nonatomic, strong) NSString * udescription;//与系统的description冲突
@property (nonatomic, strong) NSString * display_name;
@property (nonatomic, strong) NSString * email;
@property (nonatomic, strong) NSString * nice_name;
@property (nonatomic, strong) NSString * status;


@end