//
//  YZSpecialService.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/22.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZSpecialService.h"
#import "YZHttpClient.h"

@implementation YZSpecialService

+ (void)getSpecialList:(NSString *)specialUrl success:(void(^)(NSMutableArray *specialList))success failure:(void(^)(NSString *statusCode, NSString *error))failue
{
    NSString *urlSchame = @"/?yz_app=1&api_route=taxonomies&action=get_post_bigapp_special";
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodGet parameters:nil success:^(id responseObject) {
        
        if ([responseObject isKindOfClass:[NSMutableArray class]]) {
            NSMutableArray *array = [YZSpecialModel objectArrayWithKeyValuesArray:(NSArray *)responseObject];
            
            if (success) {
                success(array);
            }
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failue) {
            failue(statusCode ,error);
        }
    }];
}

+ (void)getSubSpecialList:(NSString *)specialUrl success:(void(^)(NSMutableArray *specialList))success failure:(void(^)(NSString *statusCode, NSString *error))failue
{
    NSString *urlSchame = specialUrl;
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodGet parameters:nil success:^(id responseObject) {
        
        if ([responseObject isKindOfClass:[NSMutableArray class]]) {
            NSMutableArray *array = [YZPostsModel objectArrayWithKeyValuesArray:(NSArray *)responseObject];
            
            if (success) {
                success(array);
            }
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failue) {
            failue(statusCode ,error);
        }
    }];
}


@end
