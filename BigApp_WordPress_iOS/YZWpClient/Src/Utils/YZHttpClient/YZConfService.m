//
//  YZConfService.m
//  YZWpClient
//
//  Created by zhoutl on 15/9/22.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZConfService.h"
#import "YZHttpClient.h"

@implementation YZConfService
+(void)getBaseServerConfigWith:(NSString *)withStr
                       success:(void (^)(YZServerConfig *serverConfig))success
                       failure:(void (^)(NSString *statusCode, NSString *error))failure
{
    
    NSString *urlSchame = @"/?yz_app=1&client_type=1&api_route=bigapp_api&action=get_conf";
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodPost parameters:nil success:^(id responseObject) {
        
        if ([responseObject isKindOfClass:[NSDictionary class]]) {
            YZServerConfig *serverModel = [YZServerConfig objectWithKeyValues:responseObject];
            if (success) {
                success(serverModel);
            }
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
    }];
}

@end

@implementation InVersionInfo


@end
