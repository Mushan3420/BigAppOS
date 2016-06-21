//
//  YZCommentService.h
//  YZWpClient
//
//  Created by zhoutl on 15/8/18.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface YZCommentService : NSObject

//我的评论列表
+(void)requestCommentRecordsWithPageCounts:(NSString *)pageCounts
             currentPage:(NSString *)currentPage
                 success:(void (^)(NSArray *))success
                 failure:(void (^)(NSString *statusCode, NSString *error))failure;

//*删除某条评论的请求*
+ (void)doRemoveCommentWithPostId:(NSString *)postId
                           success:(void (^)(NSDictionary *Dic))success
                           failure:(void (^)(NSString *statusCode, NSString *error))failure;


@end
