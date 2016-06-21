//
//  YZUserService.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/16.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YZLoginModel.h"
@class YZLoginDto;
@class YZSysSaveArticle;

@interface YZUserService : NSObject

//登陆
+(void)doLoginWith:(YZLoginDto *)loginDto
       success:(void (^)(YZLoginModel *loginModel))success
       failure:(void (^)(NSString *statusCode, NSString *error))failure;

//注册
+(void)doRegisterWith:(NSDictionary *)params
          success:(void (^)(id responseObject))success
          failure:(void (^)(NSString *statusCode, NSString *error))failure;

//同步收藏按钮
+(void)doSyaArticle:(YZSysSaveArticle *)article success:(void (^)(BOOL sysSuccess))success
            failure:(void (^)(NSString *statusCode, NSString *error))failure;


@end

@interface YZLoginDto : NSObject

@property (nonatomic, strong)NSString *log;//用户名
@property (nonatomic, strong)NSString *pwd;//用户密码

//绑定信息
@property (nonatomic, strong)NSString *bind;//登陆绑定，需要绑定是传1，不需要绑定时传0

@property (nonatomic, strong)NSString *platform;//第三方登录平台，目前支持wechat、qq、sina，bind=1时必须传
@property (nonatomic, strong)NSString *openid;//第三方登陆平台返回的openid，目前支持wechat，bind=1时必须传
@property (nonatomic, strong)NSString *token;//第三方登陆平台返回的token信息，目前支持wechat，bind=1时必须传

@end

@interface YZSysSaveArticle : NSObject

@property (strong,nonatomic)NSString *wp_bigapp_favorite_posts;

@end
