//
//  YZFavService.h
//  YZWpClient
//
//  Created by zhoutl on 15/8/17.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface YZFavService : NSObject

//*收藏列表的请求地址*
+ (void)requestFavoriteListSuccess:(void (^)(NSArray *favListsArray))success
                           failure:(void (^)(NSString *statusCode, NSString *error))failure;

//*添加收藏的请求地址*
+ (void)doAddFavoriteWithPostId:(NSString *)postId
                        success:(void (^)(NSDictionary *responeDic))success
                        failure:(void (^)(NSString *statusCode, NSString *error))failure;

//*删除某个收藏的请求*
+ (void)doRemoveFavoriteWithPostId:(NSString *)postId
                           success:(void (^)(NSDictionary *Dic))success
                           failure:(void (^)(NSString *statusCode, NSString *error))failure;

//*删除所有收藏的请求*
+ (void)doClearAllFavoriteSuccess:(void (^)(NSArray *navListsArray))success
                          failure:(void (^)(NSString *statusCode, NSString *error))failure;


@end
