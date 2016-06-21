//
//  YZBaseModel.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/17.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZBaseModel.h"

@implementation YZBaseModel

+(NSDictionary *)replacedKeyFromPropertyName{
    return @{
             @"postiD" : @"ID",
             @"contents" : @"content",
             };
}


@end
