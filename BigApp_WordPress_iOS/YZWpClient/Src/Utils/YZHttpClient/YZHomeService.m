//
//  YZHomeService.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/17.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "YZHomeService.h"
#import "YZHttpClient.h"
#import "YZPostsModel.h"
#import "YZArticelDetailModel.h"
#import "YZNavListModel.h"
#import "YZADetailCommentModel.h"
#import "NSString+Additions.h"
#import "PINCache.h"
#import "Config.h"
#import "YZCacheHelper.h"
@implementation YZHomeService

+(void)doNavListSuccess:(void (^)(NSArray *navListsArray))success
                failure:(void (^)(NSString *statusCode, NSString *error))failure{
    
    NSString *urlSchame = kGetNavListUrl;
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodGet parameters:nil success:^(NSArray *responseObject) {
        
        if ([responseObject isKindOfClass:[NSArray class]]){
            
            [[PINCache sharedCache] setObject:responseObject forKey:[YZCacheHelper getNavListCacheKey] block:nil];

            NSMutableArray *navListsArray =[YZNavListModel objectArrayWithKeyValuesArray:responseObject];
            if (success) {
                success(navListsArray);
            }
        }
        else{

            DLog(@"返回数据为空");
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }

    }];

    
}

+ (void)doPostsListPageCounts:(NSString *)pageCounts
                  currentPage:(NSString *)currentPage
                  categoryUrl:(NSString *)urlString
                      success:(void (^)(NSArray *))success
                      failure:(void (^)(NSString *, NSString *))failure{
    
    NSString *urlSchame = @"";
    
    NSString *imgMod = [Config getPictureQualityMod];
    //由于分类对应的列表url，是在获取分类导航时返回的完整url ，所以做特殊处理
    if (urlString.length > 0) {
        urlSchame = [NSString stringWithFormat:@"%@&filter[posts_per_page]=%@&page=%@&img_mod=%@",urlString,pageCounts,currentPage,imgMod];
    }
    else{
        //无分类导航时，首页的分类列表
        NSString *urlstr = @"/?yz_app=1&api_route=posts&action=get_posts&filter[posts_per_page]=%@&page=%@&img_mod=%@";
        urlSchame = [NSString stringWithFormat:urlstr,pageCounts,currentPage,imgMod];
    }
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodGet parameters:nil success:^(NSArray *responseObject) {
        
        if ([responseObject isKindOfClass:[NSArray class]]){
            if ([currentPage isEqualToString:@"1"]) {
                //只缓存第一页
                [[PINCache sharedCache] setObject:responseObject forKey:[urlSchame md5Hash] block:nil];
            }
            
            NSMutableArray *postsArray =[YZPostsModel objectArrayWithKeyValuesArray:responseObject];
            if (success) {
                success(postsArray);
            }
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
//#warning 测试
//        NSString *jsonString = [YZHttpClient parserData:@"list.json"];
//        
//        NSDictionary *weatherDic = [NSJSONSerialization JSONObjectWithData:[jsonString dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableLeaves error:nil];
//        
//        NSMutableArray *postsArray =[YZPostsModel objectArrayWithKeyValuesArray:weatherDic[@"data"]];
//        if (success) {
//            success(postsArray);
//        }

    }];
}

+(void)doArticleDetailWith:(NSInteger)postId
                  cacheKey:(NSString *)cacheKey
                articleUrl:(NSString *)urlString
                   success:(void (^)(YZArticelDetailModel *detailModel))success
                   failure:(void (^)(NSString *statusCode, NSString *error))failure{
    
    NSString *urlSchame = @"";
    
    NSString *imgMod = [Config getPictureQualityMod];
    //由于分类对应的列表url，是在获取分类导航时返回的完整url ，所以做特殊处理
    if (urlString.length > 0) {
        urlSchame = [NSString stringWithFormat:@"%@&img_mod=%@",urlString,imgMod];
    }
    else{
        //无分类导航时，首页的分类列表
        urlSchame = [NSString stringWithFormat:@"/?yz_app=1&api_route=posts&action=get_post&id=%@&img_mod=%@",@(postId),imgMod];
    }

    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodGet parameters:nil success:^(NSArray *responseObject) {
        
        if ([responseObject isKindOfClass:[NSDictionary class]]){
            
            
//            NSDictionary *dic = [responseObject firstObject];
            
            [[PINCache sharedCache] setObject:responseObject forKey:cacheKey block:nil];

            YZArticelDetailModel *detailModel = [YZArticelDetailModel objectWithKeyValues:responseObject];
            
            if (success) {
                success(detailModel);
            }
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
    }];
}


+(void)doGetCommentsWith:(NSInteger)articleId
              pageCounts:(NSString *)pageCounts
             currentPage:(NSString *)currentPage
                 success:(void (^)(NSArray *))success
                 failure:(void (^)(NSString *statusCode, NSString *error))failure{
    NSString *urlSchame = [NSString stringWithFormat:@"/?yz_app=1&api_route=comments&action=get_comments&id=%@&filter[pre_page]=%@&page=%@",@(articleId),pageCounts,currentPage];
    
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


+(void)doSendCommentsWithComment:(NSString *)comment
                       articleId:(NSString *)articleId
                          author:(NSString *)author
                           email:(NSString *)email
                  success:(void (^)(id))success
                  failure:(void (^)(NSString *statusCode, NSString *error))failure{
    
    NSString *urlSchame = [NSString stringWithFormat:@"/?yz_app=1&api_route=comments&action=add_comment&comment=%@&id=%@&author=%@&email=%@",comment,articleId,author,email];
    
    [YZHttpClient requestWithUrl:urlSchame requestMethod:AFRequestMethodGet parameters:nil success:^(NSDictionary *responseObject) {
        
        if ([responseObject isKindOfClass:[NSDictionary class]]){
            
            if (success) {
                success(responseObject);
            }
        }
    } failure:^(NSString *statusCode, NSString *error) {
        if (failure) {
            failure(statusCode ,error);
        }
    }];
    
}//发送评论






@end
