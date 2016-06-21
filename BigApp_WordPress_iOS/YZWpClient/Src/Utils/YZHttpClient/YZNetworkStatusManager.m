//
//  SNNetworkStatusManager.m
//  SNYifubao
//
//  Created by wangrui on 15/4/8.
//  Copyright (c) 2015年 Suning. All rights reserved.
//

#import "YZNetworkStatusManager.h"
#import "Reachability.h"
#import "AFNetworkActivityIndicatorManager.h"
#import "BuildConfig.h"
@interface YZNetworkStatusManager ()

@property (nonatomic, strong) Reachability *suningReach;
@property (nonatomic, strong) UIAlertView *networkAlert;

@end

@implementation YZNetworkStatusManager

+ (instancetype)sharedInstance
{
    static YZNetworkStatusManager *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
        
    });
    return sharedInstance;
}

- (void)startMonitoring
{
    
    [[AFNetworkActivityIndicatorManager sharedManager] setEnabled:YES];
    
    __weak __typeof(self)weakSelf = self;
    
    self.suningReach = [Reachability reachabilityForInternetConnection];
    
    self.suningReach.reachableBlock = ^(Reachability * reachability)
    {
        
        __strong __typeof(weakSelf)strongSelf = weakSelf;
        
        dispatch_async(dispatch_get_main_queue(), ^{

            [strongSelf networkBecomeReachable:reachability];
        });
    };
    
    self.suningReach.unreachableBlock = ^(Reachability * reachability)
    {
        
        __strong __typeof(weakSelf)strongSelf = weakSelf;
        
        dispatch_async(dispatch_get_main_queue(), ^{

            [strongSelf networkBecomeUneachable:reachability];
        });
    };
    
    [self.suningReach startNotifier];
    
}


- (void)networkBecomeReachable:(Reachability *)reachability
{
    
    DLog(@"Reachable(%@)", reachability.currentReachabilityString);
}

- (void)networkBecomeUneachable:(Reachability *)reachability
{
    DLog(@"Not Reachable(%@)", reachability.currentReachabilityString);
    
//    if ([UIApplication sharedApplication].applicationState != UIApplicationStateBackground)
//    {
//        
//        [self showNetworkAlertView];
//    }
    
    
}

- (void)showNetworkAlertView
{
    
    if (![self.networkAlert isVisible])
    {
        [self.networkAlert show];
    }
    
}

- (UIAlertView *)networkAlert
{
    if (!_networkAlert) {

    }
    return _networkAlert;
}


- (BOOL)isReachable
{
    
    return [self.suningReach isReachableViaWWAN] || [self.suningReach isReachableViaWiFi];

}

@end
