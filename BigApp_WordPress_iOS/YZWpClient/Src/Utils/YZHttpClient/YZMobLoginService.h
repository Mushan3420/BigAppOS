//
//  YZMobLoginService.h
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/10.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "YZLoginModel.h"

@interface MobLoginModel : NSObject

@property (strong,nonatomic)NSString *platform;
@property (strong,nonatomic)NSString *openid;
@property (strong,nonatomic)NSString *token;

@end

@interface YZMobLoginService : NSObject


//第三方登录检查绑定接口
+(void)checkMobLoginBinding:(MobLoginModel *)mobLogin
                       success:(void (^)(YZMobLoginModel *loginModel))success
                       failure:(void (^)(NSString *statusCode, NSString *error))failure;


@end

