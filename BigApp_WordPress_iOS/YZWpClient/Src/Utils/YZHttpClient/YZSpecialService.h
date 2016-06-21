//
//  YZSpecialService.h
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/22.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YZSpecialModel.h"
#import "YZPostsModel.h"

@interface YZSpecialService : NSObject

+ (void)getSpecialList:(NSString *)specialUrl success:(void(^)(NSMutableArray *specialList))success failure:(void(^)(NSString *statusCode, NSString *error))failue;

+ (void)getSubSpecialList:(NSString *)specialUrl success:(void(^)(NSMutableArray *specialList))success failure:(void(^)(NSString *statusCode, NSString *error))failue;


@end
