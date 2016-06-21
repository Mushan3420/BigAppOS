//
//  YZCacheHelper.m
//  YZWpClient
//
//  Created by zhoutl on 15/10/23.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZCacheHelper.h"
#import "NSString+Additions.h"
#import "AppConfigManager.h"
#import "HttpURLConstant.h"

@implementation YZCacheHelper

+ (NSString *)appHostServer{
    return [AppConfigManager sharedInstance].appHttpServer;
}

+ (NSString *)getNavListCacheKey{
    
    NSString *cacheKey = [[NSString stringWithFormat:@"%@%@",kGetNavListUrl,[self appHostServer]] md5Hash];
    
    return cacheKey;
}
@end
