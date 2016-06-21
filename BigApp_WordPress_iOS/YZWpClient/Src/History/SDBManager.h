//
//  SDBManager.h
//  SDatabase
//
//  Created by SunJiangting on 12-10-20.
//  Copyright (c) 2012年 sun. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "FMDatabaseAdditions.h"

@class FMDatabase;

/**
 * @brief 对数据链接进行管理，包括链接，关闭连接
 * 可以建立长连接 长连接
 */
@interface SDBManager : NSObject {
    NSString * _name;
}
/// 数据库操作对象，当数据库被建立时，会存在次至
@property (nonatomic, readonly) FMDatabase * dataBase;  // 数据库操作对象
/// 单例模式
+(SDBManager *) defaultDBManager;

// 关闭数据库
- (void) close;

@end
