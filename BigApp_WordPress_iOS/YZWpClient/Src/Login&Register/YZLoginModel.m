//
//	YZLoginModel.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/2015
//	Copyright © 2015. All rights reserved.




#import "YZLoginModel.h"

@interface YZLoginModel ()
@end
@implementation YZLoginModel

+ (NSString *)replacedKeyFromPropertyName121:(NSString *)propertyName
{
    // nickName -> nick_name
    return [propertyName underlineFromCamel];
}
@end

@implementation YZMobLoginModel

@end