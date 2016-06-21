//
//  YZSearchService.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/13.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZSearchService.h"
#import "YZHttpClient.h"
#import "YZPostsModel.h"



@implementation YZSearchService


+ (void)doSearchListPageCounts:(NSString *)pageCounts//每页条数
                  currentPage:(NSString *)currentPage
                  searchContent:(NSString *)searchContent
                      success:(void (^)(NSArray *))success
                      failure:(void (^)(NSString *, NSString *))failure{
    
    NSString *urlSchame = @"";
    
    NSString *urlstr = @"/?yz_app=1&api_route=posts&action=get_posts&filter[s]=%@&filter[posts_per_page]=%@&page=%@";
    urlSchame = [NSString stringWithFormat:urlstr,searchContent,pageCounts,currentPage];
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodGet parameters:nil success:^(NSArray *responseObject) {
        
        if ([responseObject isKindOfClass:[NSArray class]]){
            
            NSMutableArray *postsArray =[YZPostsModel objectArrayWithKeyValuesArray:responseObject];
            if (success) {
                success(postsArray);
            }
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
    }];
}

+ (void)doGetSearchPostTagsSuccess:(void (^)(NSArray *postsList))success
                           failure:(void (^)(NSString *statusCode, NSString *error))failure;{
    NSString *urlstr = @"/?yz_app=1&api_route=taxonomies&action=get_post_tags";
    
    [YZHttpClient requestWithUrl:urlstr requestMethod:AFRequestMethodGet parameters:nil success:^(NSArray *responseObject) {
        
        if ([responseObject isKindOfClass:[NSArray class]]){
            NSMutableArray *postsArray =[YZPostTag objectArrayWithKeyValuesArray:responseObject];
            if (success) {
                success(postsArray);
            }
        }
        else{
            DLog(@"热门标签---数据返回异常");
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
    }];
}

@end
