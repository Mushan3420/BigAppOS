//
//  YZModifyPersonInfoService.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/15.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZModifyPersonInfoService.h"
#import "YZHttpClient.h"


@implementation YZModifyPersonInfoService

+ (void)doModifyPersonNickNameWith:(NSString *)nickname success:(void (^)(NSString *success_msg)) msg failure:(void (^)(NSString *statusCode, NSString *erro))failure
{
    NSString *urlSchame = @"/?yz_app=1&api_route=users&action=edit_user";
    NSDictionary *params = [[NSDictionary alloc]initWithObjects:@[nickname] forKeys:@[@"nickname"]];
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodPost parameters:params success:^(id responseObject) {
        
        msg([responseObject objectForKey:@"error_code"]);
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
    }];
}

+ (void)doModifyPersonPasswordWith:(NSString *)password success:(void (^)(NSString *success_msg)) msg failure:(void (^)(NSString *statusCode, NSString *erro))failure
{
    NSString *urlSchame = @"/?yz_app=1&api_route=users&action=edit_user";
    NSDictionary *params = [[NSDictionary alloc]initWithObjects:@[password] forKeys:@[@"password"]];
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodPost parameters:params success:^(id responseObject) {
        
        msg([responseObject objectForKey:@"error_code"]);
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
    }];
}
+ (void)doModifyPersondescriptionWith:(NSString *)description success:(void (^)(NSString *success_msg)) msg failure:(void (^)(NSString *statusCode, NSString *erro))failure
{
    NSString *urlSchame = @"/?yz_app=1&api_route=users&action=edit_user";
    NSDictionary *params = [[NSDictionary alloc]initWithObjects:@[description] forKeys:@[@"description"]];
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodPost parameters:params success:^(id responseObject) {
        
        msg([responseObject objectForKey:@"error_code"]);
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
    }];
}

@end
