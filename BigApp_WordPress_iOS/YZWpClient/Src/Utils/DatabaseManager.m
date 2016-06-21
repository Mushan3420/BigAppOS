//
//  DatabaseManager.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "DatabaseManager.h"
#include <sqlite3.h>
#import "FMDatabaseQueue.h"
#import "FMDatabase.h"

@interface DatabaseManager ()

@end

@implementation DatabaseManager

@synthesize writablePath = _writablePath;

@synthesize  databaseQueue = _databaseQueue;

static DatabaseManager *manager = nil;

- (id)init
{
    if (self = [super init])
    {
        _isDataBaseOpened = NO;

        [self setWritablePath:[DatabaseDirectory() stringByAppendingPathComponent:kDatabaseFileName]];

        [self openDataBase];
    }

    return self;
}

- (void)openDataBase
{
    _databaseQueue = [FMDatabaseQueue databaseQueueWithPath:self.writablePath];

    if (_databaseQueue == 0x00)
    {
        _isDataBaseOpened = NO;
        return;
    }

    _isDataBaseOpened = YES;
    NSLog(@"Open Database OK!");
    [_databaseQueue inDatabase:^(FMDatabase *db) {
        [db setShouldCacheStatements:YES];
    }];
}

- (void)closeDataBase
{
    if (!_isDataBaseOpened)
    {
        NSLog(@"数据库已打开，或打开失败。请求关闭数据库失败。");
        return;
    }

    [_databaseQueue close];
    _isDataBaseOpened = NO;
    NSLog(@"关闭数据库成功。");
}

+ (DatabaseManager *)currentManager
{
    @synchronized(self)
    {
        if (!manager)
        {
            manager = [[DatabaseManager alloc] init];
        }
    }

    return manager;
}

+ (void)releaseManager
{
    if (manager)
    {
        manager = nil;
    }
}

- (void)dealloc
{
    _writablePath = nil;
    [self closeDataBase];
    _databaseQueue = nil;
}

@end
