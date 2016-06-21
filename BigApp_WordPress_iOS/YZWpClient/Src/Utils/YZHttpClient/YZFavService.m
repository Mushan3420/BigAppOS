//
//  YZFavService.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/17.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZFavService.h"
#import "YZHttpClient.h"
#import "YZPostsModel.h"

@implementation YZFavService

//*收藏列表的请求地址*
+ (void)requestFavoriteListSuccess:(void (^)(NSArray *favListsArray))success
                           failure:(void (^)(NSString *statusCode, NSString *error))failure{
    NSString *urlSchame = [NSString stringWithFormat:@"/?yz_app=1&api_route=favorite&action=list"];
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodGet parameters:nil success:^(NSArray *responseObject) {
        
        if ([responseObject isKindOfClass:[NSArray class]]){
            
            NSMutableArray *postsArray =[YZPostsModel objectArrayWithKeyValuesArray:responseObject];

            if (success) {
                success(postsArray);
            }
        }else{
            success(nil);
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }

    }];

}

//*添加收藏的请求地址*
+ (void)doAddFavoriteWithPostId:(NSString *)postId
                        success:(void (^)(NSDictionary *))success
                        failure:(void (^)(NSString *, NSString *))failure{
    
    NSString *urlSchame = [NSString stringWithFormat:@"/?yz_app=1&api_route=favorite&action=add&post_id=%@",postId];
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodGet parameters:nil success:^(id responseObject) {
        
            if (success) {
                success(responseObject);
            }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
        
        
        
    }];
    
}

//*删除某个收藏的请求*
+ (void)doRemoveFavoriteWithPostId:(NSString *)postId success:(void (^)(NSDictionary *))success failure:(void (^)(NSString *, NSString *))failure{
    
    NSString *urlSchame = [NSString stringWithFormat:@"/?yz_app=1&api_route=favorite&action=remove&post_id=%@",postId];
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodGet parameters:nil success:^(id responseObject) {        
        if (success) {
            success(responseObject);
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
        
        
        
    }];
}

//*删除所有收藏的请求*
+ (void)doClearAllFavoriteSuccess:(void (^)(NSArray *))success failure:(void (^)(NSString *, NSString *))failure{
    NSString *urlSchame = [NSString stringWithFormat:@"/?yz_app=1&api_route=favorite&action=clear"];
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodGet parameters:nil success:^(id responseObject) {
        
        if ([responseObject isKindOfClass:[NSArray class]]){
            
            if (success) {
                success(responseObject);
            }
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
        
        
        
    }];}

@end
