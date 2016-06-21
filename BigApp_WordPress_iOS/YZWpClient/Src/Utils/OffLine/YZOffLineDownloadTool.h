//
//  YZOffLineDownload.h
//  YZWpClient
//
//  Created by zhoutl on 15/10/22.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface YZOffLineDownloadTool : NSObject

@property (nonatomic, copy) void (^callback)(NSString *offlineStatus);

+ (void)startOffLine;

+ (void)startOffLine:(void (^)(NSString *))callback;

@end
