//
//  YZServerConfig.h
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/22.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MJExtension.h"
@interface YZServerConfig : NSObject

@property (strong,nonatomic)NSString *users_can_register;
@property (nonatomic)NSInteger thread_comments;
@property (strong,nonatomic)NSString *version_api_url;

@end
