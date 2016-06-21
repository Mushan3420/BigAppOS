//
//  YZUserService.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/16.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZUserService.h"
#import "YZHttpClient.h"

@implementation YZUserService

+(void)doLoginWith:(YZLoginDto *)loginDto
       success:(void (^)(YZLoginModel *loginModel))success
       failure:(void (^)(NSString *statusCode, NSString *error))failure{
    
    NSDictionary *params = [loginDto keyValues];
    
    NSString *urlSchame = @"/wp-login.php?yz_app=1&api_route=auth&action=login";
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodPost parameters:params success:^(id responseObject) {
        
        if ([responseObject isKindOfClass:[NSDictionary class]]) {
            YZLoginModel *loginModel = [YZLoginModel objectWithKeyValues:responseObject];
            loginModel.udescription = [(NSDictionary *)responseObject objectForKey:@"description"];
            if (success) {
                success(loginModel);
            }
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
    }];
    
}

+(void)doRegisterWith:(NSDictionary *)params
              success:(void (^)(id responseObject))success
              failure:(void (^)(NSString *statusCode, NSString *error))failure{
    
    NSString *urlSchame = @"/wp-login.php?yz_app=1&api_route=auth&action=register";
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodPost parameters:params success:^(id responseObject) {
        
        success(responseObject);
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
    }];
}

//同步收藏按钮
+(void)doSyaArticle:(YZSysSaveArticle *)article success:(void (^)(BOOL sysSuccess))success
            failure:(void (^)(NSString *statusCode, NSString *error))failure
{
    NSDictionary *params = [article keyValues];
    
    NSString *urlSchame = @"/?yz_app=1&api_route=favorite&action=syncfp";
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodPost parameters:params success:^(id responseObject) {
        
        if ([responseObject isKindOfClass:[NSNumber class]]) {
            NSNumber *numo = (NSNumber*)responseObject;
            success([numo boolValue]);
        }
        else
        {
            success(NO);
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
    }];
}

@end

@implementation YZLoginDto

@end

@implementation YZSysSaveArticle

@end
