//
//  YZMobLoginService.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/10.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZMobLoginService.h"
#import "YZHttpClient.h"

@implementation MobLoginModel

@end

@implementation YZMobLoginService

+(void)checkMobLoginBinding:(MobLoginModel *)mobLogin
                    success:(void (^)(YZMobLoginModel *loginModel))success
                    failure:(void (^)(NSString *statusCode, NSString *error))failure
{
    NSDictionary *params = [mobLogin keyValues];
     NSString *urlSchame = @"/wp-login.php?yz_app=1&api_route=sociallogin&action=check";
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodPost parameters:params success:^(id responseObject) {
        
        if ([responseObject isKindOfClass:[NSDictionary class]]) {
            YZMobLoginModel *sversionModel = [YZMobLoginModel objectWithKeyValues:responseObject];
            sversionModel.udescription = [(NSDictionary *)responseObject objectForKey:@"description"];
            if (success) {
                success(sversionModel);
            }
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
    }];
}


@end

