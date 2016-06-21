//
//  YZSavePostDBModel.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/10/20.
//  Copyright © 2015年 com.youzu. All rights reserved.
//

#import "YZSavePostDBModel.h"

#define kUserTableName @"SPostModelTable"

#define PostDBId              @"PostDBId"
#define PostJson           @"postJson"

#define CreatTableSql  @"CREATE TABLE SPostModelTable (PostDBId integer, postJson text)"

@implementation YZSavePostDBModel

- (id) init {
    self = [super init];
    if (self) {
        //========== 首先查看有没有建立message的数据库，如果未建立，则建立数据库=========
        _db = [SDBManager defaultDBManager].dataBase;
        [self createDataBase];
    }
    return self;
}

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
        NSString * sql = CreatTableSql;
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
- (void) saveApostModel:(YZPostsModel *) postModel
{
    NSDictionary *dic = [postModel keyValues];
    NSData * jsonData = [NSJSONSerialization dataWithJSONObject:dic options:0 error:nil];
    NSString * myString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    
    NSMutableString * query = [NSMutableString stringWithFormat:@"INSERT INTO %@",kUserTableName];
    NSMutableString * keys = [NSMutableString stringWithFormat:@" ("];
    NSMutableString * values = [NSMutableString stringWithFormat:@" ( "];
    NSMutableArray * arguments = [NSMutableArray arrayWithCapacity:5];
    if (postModel.postID) {
        [keys appendString:@"PostDBId,"];
        [values appendString:@"?,"];
        [arguments addObject:@(postModel.postID)];
    }
    if (myString) {
        [keys appendString:@"postJson,"];
        [values appendString:@"?,"];
        [arguments addObject:myString];
    }
    
    [keys appendString:@")"];
    [values appendString:@")"];
    [query appendFormat:@" %@ VALUES%@",
     [keys stringByReplacingOccurrencesOfString:@",)" withString:@")"],
     [values stringByReplacingOccurrencesOfString:@",)" withString:@")"]];
    [_db executeUpdate:query withArgumentsInArray:arguments];
}
- (BOOL)isExistApostModel:(NSInteger )postId
{
    NSString * query = [NSString stringWithFormat:@"SELECT PostDBId,postJson FROM %@ where PostDBId = '%@'",kUserTableName,@(postId)];
    FMResultSet * rs = [_db executeQuery:query];
    return [rs next];
}
/**
 * @brief 删除一条用户数据
 *
 * @param uid 需要删除的用户的id
 */
- (void) deleteApostWithId:(NSInteger )postId
{
    NSString * query = [NSString stringWithFormat:@"DELETE FROM %@ WHERE PostDBId = '%@'",kUserTableName,@(postId)];
    [_db executeUpdate:query];
}

- (void) deleteAllPostModel
{
    NSString * query = [NSString stringWithFormat:@"DELETE FROM %@ WHERE '1=1'",kUserTableName];
    [_db executeUpdate:query];
}
/**
 * @brief 修改用户的信息
 *
 * @param user 需要修改的用户信息
 */

/**
 * @brief 模拟分页查找数据。取uid大于某个值以后的limit个数据
 *
 * @param uid
 * @param limit 每页取多少个
 */
- (NSArray *) findAllSavePost
{
    NSString * query = [NSString stringWithFormat:@"SELECT PostDBId,postJson FROM %@ where '1=1'",kUserTableName];
    
    FMResultSet * rs = [_db executeQuery:query];
    NSMutableArray * array = [NSMutableArray arrayWithCapacity:[rs columnCount]];
    while ([rs next]) {
        NSString *jsonStr = [rs stringForColumn:PostJson];
        NSData *resData = [[NSData alloc] initWithData:[jsonStr dataUsingEncoding:NSUTF8StringEncoding]];
        //系统自带JSON解析
        NSDictionary *resultDic = [NSJSONSerialization JSONObjectWithData:resData options:NSJSONReadingMutableLeaves error:nil];
        YZPostsModel *model = [YZPostsModel objectWithKeyValues:resultDic];
        NSString *postIdStr = [resultDic objectForKey:@"postID"];
        model.postID = postIdStr.integerValue;
        
        [array addObject:model];
    }
    [rs close];
    return array;
}

@end
