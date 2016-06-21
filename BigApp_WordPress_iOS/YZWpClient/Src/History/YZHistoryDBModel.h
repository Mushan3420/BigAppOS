//
//  YZHistoryDBModel.h
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/28.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "SDBManager.h"
#import "YZHistory.h"

@interface YZHistoryDBModel : NSObject
{
    FMDatabase *_db;
}


/**
 * @brief 创建数据库
 */
- (void) createDataBase;
/**
 * @brief 保存一条用户记录
 *
 * @param user 需要保存的用户数据
 */
- (void) saveAhistory:(YZHistory *) history;

/**
 * @brief 删除一条用户数据
 *
 * @param uid 需要删除的用户的id
 */
- (void) deleteAhistoryWithId:(NSString *) uid;
- (void) deleteAllHistory;
/**
 * @brief 修改用户的信息
 *
 * @param user 需要修改的用户信息
 */
- (void) mergeWithAhistory:(YZHistory *) history;

/**
 * @brief 模拟分页查找数据。取uid大于某个值以后的limit个数据
 *
 * @param uid
 * @param limit 每页取多少个
 */
- (NSArray *) findlimit:(NSString *) date;


@end
