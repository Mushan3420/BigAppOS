//
//  YZModifyPersonInfoService.h
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/15.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>

@class YZModifyPersonModel;

@interface YZModifyPersonInfoService : NSObject

//修改个人信息接口
+ (void)doModifyPersonNickNameWith:(NSString *)nickname success:(void (^)(NSString *success_msg)) msg failure:(void (^)(NSString *statusCode, NSString *erro))failure;
+ (void)doModifyPersonPasswordWith:(NSString *)password success:(void (^)(NSString *success_msg)) msg failure:(void (^)(NSString *statusCode, NSString *erro))failure;
+ (void)doModifyPersondescriptionWith:(NSString *)description success:(void (^)(NSString *success_msg)) msg failure:(void (^)(NSString *statusCode, NSString *erro))failure;

@end

@interface YZModifyPersonModel : NSObject

@property (nonatomic,strong)NSString *nickname;
@property (nonatomic,strong)NSString *password;
@property (nonatomic,strong)NSString *description;

@end
