//
//  SNNetworkStatusManager.h
//  SNYifubao
//
//  Created by wangrui on 15/4/8.
//  Copyright (c) 2015年 Suning. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface YZNetworkStatusManager : NSObject

+ (instancetype)sharedInstance;

- (void)startMonitoring;

/**
 Whether or not the network is currently reachable.
 */
@property (readonly, nonatomic, assign, getter = isReachable) BOOL reachable;

@end
