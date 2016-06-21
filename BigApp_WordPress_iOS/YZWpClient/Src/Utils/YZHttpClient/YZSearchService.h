//
//  YZSearchService.h
//  YZWpClient
//
//  Created by zhoutl on 15/8/13.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface YZSearchService : NSObject


//搜索文章列表
+ (void)doSearchListPageCounts:(NSString *)pageCounts//每页条数
                  currentPage:(NSString *)currentPage//当前页码
                  searchContent:(NSString *)searchContent
                      success:(void (^)(NSArray *postsList))success
                      failure:(void (^)(NSString *statusCode, NSString *error))failure;

+ (void)doGetSearchPostTagsSuccess:(void (^)(NSArray *postsList))success
                           failure:(void (^)(NSString *statusCode, NSString *error))failure;

@end
