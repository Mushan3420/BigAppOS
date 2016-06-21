//
//  DAO.m
//  YZWpClient
//
//  Created by zhoutl on 15/7/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.

#import <Foundation/Foundation.h>
#import "FMDatabase.h"
#import "FMDatabaseAdditions.h"
#import "FMDatabaseQueue.h"
#define CurrentDataBaseVersionCode 1000
@interface DAO : NSObject {
    @protected
    FMDatabaseQueue *_databaseQueue;
}

@end

@interface DAO ()

@property (nonatomic, retain) FMDatabaseQueue *databaseQueue;

+ (void)createTablesNeeded;

@end
