//
//  DAO.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.

#import "DAO.h"
#import "DatabaseManager.h"

@implementation DAO

@synthesize databaseQueue = _databaseQueue;

- (id)init
{
    self = [super init];

    if (self)
    {
        self.databaseQueue = [DatabaseManager currentManager].databaseQueue;
    }

    return self;
}



+ (void)createTablesNeeded
{
    FMDatabaseQueue *databaseQueue = [DatabaseManager currentManager].databaseQueue;

    // 信息收集表
    NSArray *ssaList = [DAO getSSAInfoStatements];
    
    [databaseQueue inDatabase:^(FMDatabase *database) {

        for (NSString * sql in ssaList)
        {
            [database executeUpdate:sql];
        }
    }];

}

// 信息收集的建表语句
+ (NSArray *)getSSAInfoStatements
{
    // 1.页面的pv和uv
    /*info_system*/
    NSString *sql1 = [NSString stringWithFormat:@"CREATE TABLE IF NOT EXISTS info_system (system_id INTEGER PRIMARY KEY NOT NULL,app_version TEXT NOT NULL,terminal_type TEXT NOT NULL,os_version TEXT NOT NULL,device_type TEXT NOT NULL,unique_id TEXT NOT NULL,app_down_way TEXT NOT NULL,user_ip TEXT NOT NULL,session_id TEXT NOT NULL,client_location TEXT)"];


    NSArray *sqlList = [NSArray arrayWithObjects:sql1, nil];

    return sqlList;
}



@end
