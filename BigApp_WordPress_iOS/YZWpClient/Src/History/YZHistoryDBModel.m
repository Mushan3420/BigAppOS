//
//  YZHistoryDBModel.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/9/28.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZHistoryDBModel.h"
#import "BuildConfig.h"
#define kUserTableName @"SHistory"

@implementation YZHistoryDBModel

- (id) init {
    self = [super init];
    if (self) {
        //========== 首先查看有没有建立message的数据库，如果未建立，则建立数据库=========
        _db = [SDBManager defaultDBManager].dataBase;
        [self createDataBase];
    }
    return self;
}

/**
 * @brief 创建数据库
 */
- (void) createDataBase
{
    FMResultSet * set = [_db executeQuery:[NSString stringWithFormat:@"select count(*) from sqlite_master where type ='table' and name = '%@'",kUserTableName]];
    
    [set next];
    
    NSInteger count = [set intForColumnIndex:0];
    
    BOOL existTable = !!count;
    
    if (existTable) {
        // TODO:是否更新数据库
//        [AppDelegate showStatusWithText:@"数据库已经存在" duration:2];
    } else {
        // TODO: 插入新的数据库
        NSString * sql = [NSString stringWithFormat:@"CREATE TABLE %@ (ID VARCHAR(20) , historyTitle VARCHAR(50), historyDate VARCHAR(20))",kUserTableName];
        BOOL res = [_db executeUpdate:sql];
        if (!res) {
//            [AppDelegate showStatusWithText:@"数据库创建失败" duration:2];
        } else {
//            [AppDelegate showStatusWithText:@"数据库创建成功" duration:2];
        }
    }
}
/**
 * @brief 保存一条用户记录
 *
 * @param user 需要保存的用户数据
 */
- (void) saveAhistory:(YZHistory *) history
{
    NSMutableString * query = [NSMutableString stringWithFormat:@"INSERT INTO %@",kUserTableName];
    NSMutableString * keys = [NSMutableString stringWithFormat:@" ("];
    NSMutableString * values = [NSMutableString stringWithFormat:@" ( "];
    NSMutableArray * arguments = [NSMutableArray arrayWithCapacity:5];
    if (history.ID) {
        [keys appendString:@"ID,"];
        [values appendString:@"?,"];
        [arguments addObject:history.ID];
    }
    if (history.historyTitle) {
        [keys appendString:@"historyTitle,"];
        [values appendString:@"?,"];
        [arguments addObject:history.historyTitle];
    }
    if (history.historyDate) {
        [keys appendString:@"historyDate,"];
        [values appendString:@"?,"];
        [arguments addObject:history.historyDate];
    }
    [keys appendString:@")"];
    [values appendString:@")"];
    [query appendFormat:@" %@ VALUES%@",
     [keys stringByReplacingOccurrencesOfString:@",)" withString:@")"],
     [values stringByReplacingOccurrencesOfString:@",)" withString:@")"]];
    [_db executeUpdate:query withArgumentsInArray:arguments];
    DLog(@"%@",query);
}

/**
 * @brief 删除一条用户数据
 *
 * @param uid 需要删除的用户的id
 */
- (void) deleteAhistoryWithId:(NSString *) uid
{
    NSString * query = [NSString stringWithFormat:@"DELETE FROM %@ WHERE ID = '%@'",kUserTableName,uid];
    [_db executeUpdate:query];
}

- (void) deleteAllHistory
{
    NSString * query = [NSString stringWithFormat:@"DELETE FROM %@ WHERE '1=1'",kUserTableName];
    [_db executeUpdate:query];
}

/**
 * @brief 修改用户的信息
 *
 * @param user 需要修改的用户信息
 */
- (void) mergeWithAhistory:(YZHistory *) history
{
    if (!history.ID) {
        return;
    }
    NSString * query = [NSString stringWithFormat:@"UPDATE %@ SET",kUserTableName];
    NSMutableString * temp = [NSMutableString stringWithCapacity:20];
    // xxx = xxx;
    if (history.ID) {
        [temp appendFormat:@" ID = '%@',",history.ID];
    }
    if (history.historyTitle) {
        [temp appendFormat:@" historyTitle = '%@',",history.historyTitle];
    }
    [temp appendString:@")"];
    query = [query stringByAppendingFormat:@"%@ WHERE ID = '%@'",[temp stringByReplacingOccurrencesOfString:@",)" withString:@""],history.ID];
    DLog(@"%@",query);
    
    [_db executeUpdate:query];
}

- (NSArray *) findlimit:(NSString *) date
{
    NSString * query = [NSString stringWithFormat:@"SELECT ID,historyTitle,historyDate FROM %@ where historyDate = '%@'",kUserTableName,date];
    
    FMResultSet * rs = [_db executeQuery:query];
    NSMutableArray * array = [NSMutableArray arrayWithCapacity:[rs columnCount]];
    while ([rs next]) {
        YZHistory * history = [YZHistory new];
        history.ID = [rs stringForColumn:@"ID"];
        history.historyTitle = [rs stringForColumn:@"historyTitle"];
        history.historyDate = [rs stringForColumn:@"historyDate"];
        [array addObject:history];
    }
    [rs close];
    return array;
}


@end
