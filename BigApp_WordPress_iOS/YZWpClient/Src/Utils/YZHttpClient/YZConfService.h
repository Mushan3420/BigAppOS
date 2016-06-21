//
//  YZConfService.h
//  YZWpClient
//
//  Created by zhoutl on 15/9/22.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//  端上获取的基本配置

#import <Foundation/Foundation.h>
#import "YZServerConfig.h"

@class InVersionInfo;

@interface YZConfService : NSObject

//基本配置接口
+(void)getBaseServerConfigWith:(NSString *)withStr
           success:(void (^)(YZServerConfig *serverConfig))success
           failure:(void (^)(NSString *statusCode, NSString *error))failure;

@end


@interface InVersionInfo : NSObject

@property (strong,nonatomic)NSString *app_key;
@property (strong,nonatomic)NSString *os;
@property (strong,nonatomic)NSString *version;
@property (strong,nonatomic)NSString *channel_name;

@end
