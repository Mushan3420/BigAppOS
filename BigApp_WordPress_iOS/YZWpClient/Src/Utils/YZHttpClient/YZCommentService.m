//
//  YZCommentService.m
//  YZWpClient
//
//  Created by zhoutl on 15/8/18.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZCommentService.h"
#import "YZHttpClient.h"
#import "YZADetailCommentModel.h"

@implementation YZCommentService
+(void)requestCommentRecordsWithPageCounts:(NSString *)pageCounts
                               currentPage:(NSString *)currentPage
                                   success:(void (^)(NSArray *))success
                                   failure:(void (^)(NSString *statusCode, NSString *error))failure{
    
    NSString *urlSchame = [NSString stringWithFormat:@"/?yz_app=1&api_route=comments&action=my_comments&filter[pre_page]=%@&filter[page]=%@",pageCounts,currentPage];
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodGet parameters:nil success:^(NSArray *responseObject) {
        
        if ([responseObject isKindOfClass:[NSArray class]]){
            
            NSMutableArray *commentsList =[YZADetailCommentModel objectArrayWithKeyValuesArray:responseObject];
            if (success) {
                success(commentsList);
            }
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
    }];
}

+ (void)doRemoveCommentWithPostId:(NSString *)postId
                          success:(void (^)(NSDictionary *Dic))success
                          failure:(void (^)(NSString *statusCode, NSString *error))failure{
    
    NSString *urlSchame = [NSString stringWithFormat:@"/?yz_app=1&api_route=comments&action=delete_comment&comment=%@",postId];
    
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
@end
