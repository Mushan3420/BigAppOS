//
//  DatabaseManager.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/9.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//
#define kDatabaseFileName          @"youzu_wordpress.sqlite"

#import <Foundation/Foundation.h>


static NSString *_DatabaseDirectory;

static inline NSString *DatabaseDirectory()
{
    if (!_DatabaseDirectory)
    {
        NSString *cachesDirectory = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) objectAtIndex:0];
        
        _DatabaseDirectory = [[[cachesDirectory stringByAppendingPathComponent:[[NSProcessInfo processInfo] processName]] stringByAppendingPathComponent:@"Database"] copy];
        
        NSFileManager *fileManager = [NSFileManager defaultManager];
        BOOL           isDir = YES;
        BOOL           isExist = [fileManager fileExistsAtPath:_DatabaseDirectory isDirectory:&isDir];
        
        if (!isExist)
        {
            [fileManager createDirectoryAtPath:_DatabaseDirectory withIntermediateDirectories:YES attributes:nil error:NULL];
        }
    }
    
    return _DatabaseDirectory;
}


@class FMDatabaseQueue;
@interface DatabaseManager : NSObject
{
    BOOL _isInitializeSuccess;

    BOOL _isDataBaseOpened;

}

@property (nonatomic, copy) NSString *writablePath;

@property (nonatomic, retain) FMDatabaseQueue *databaseQueue;

+ (DatabaseManager *)currentManager;

- (void)openDataBase;

- (void)closeDataBase;

+ (void)releaseManager;

@end
