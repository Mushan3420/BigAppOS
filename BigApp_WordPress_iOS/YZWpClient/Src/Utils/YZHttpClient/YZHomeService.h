//
//  YZHomeService.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/17.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>
@class YZArticelDetailModel;

@interface YZHomeService : NSObject

//分类导航
+(void)doNavListSuccess:(void (^)(NSArray *navListsArray))success
                failure:(void (^)(NSString *statusCode, NSString *error))failure;

//文章列表
+ (void)doPostsListPageCounts:(NSString *)pageCounts//每页条数
                  currentPage:(NSString *)currentPage//当前页码
                  categoryUrl:(NSString *)urlString
                      success:(void (^)(NSArray *postsList))success
                      failure:(void (^)(NSString *statusCode, NSString *error))failure;


//文章详情
+(void)doArticleDetailWith:(NSInteger)postId
                  cacheKey:(NSString *)cacheKey
                articleUrl:(NSString *)urlString
                   success:(void (^)(YZArticelDetailModel *detailModel))success
                   failure:(void (^)(NSString *statusCode, NSString *error))failure;




//文章详情评论列表
+(void)doGetCommentsWith:(NSInteger)articleId
              pageCounts:(NSString *)pageCounts
             currentPage:(NSString *)currentPage
                 success:(void (^)(NSArray *))success
                 failure:(void (^)(NSString *statusCode, NSString *error))failure;


//发送评论
+(void)doSendCommentsWithComment:(NSString *)comment
                       articleId:(NSString *)articleId
                          author:(NSString *)author
                           email:(NSString *)email
                 success:(void (^)(id))success
                 failure:(void (^)(NSString *statusCode, NSString *error))failure;


@end
