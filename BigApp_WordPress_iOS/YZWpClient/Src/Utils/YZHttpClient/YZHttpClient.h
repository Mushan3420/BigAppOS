//
//  YZHttpClient.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.

#import <Foundation/Foundation.h>
#define AFRequestMethodPost       @"POST"
#define AFRequestMethodGet        @"GET"
#import "BuildConfig.h"
#import "HttpURLConstant.h"

#import "AFHTTPRequestOperation.h"
#import "AFHTTPSessionManager.h"




@interface YZHttpClient : NSObject

+ (NSOperation *)requestWithUrl:(NSString *)url
                  requestMethod:(NSString *)requestMethod
                     parameters:(NSDictionary *)parameters
                        success:(void (^)(id responseObject))success
                        failure:(void (^)(NSString *statusCode, NSString *error))failure;


+(NSString *)parserData:(NSString *)name;

+(void)uploadImage:(UIImage *)image returnInfo:(void (^)(NSInteger errorcode,NSString *avatarUrl))result;

@end
