//
//  PlistFileOperation.m
//  YZWpClient
//
//  Created by chaoliangmei on 15/8/27.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import "PlistFileOperation.h"
#import "AppConfigManager.h"
#import "NSString+SNAdditions.h"

@implementation PlistFileOperation

+ (NSString *)filePathName
{
    // 判断存放音频、视频的文件夹是否存在，不存在则创建对应文件夹
    
    NSString *fileName = [AppConfigManager sharedInstance].appHttpServer;
    fileName = [fileName MD5];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    
    NSArray *path = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *filePath = [path objectAtIndex:0];
    filePath = [filePath stringByAppendingPathComponent:fileName];

    BOOL isDir = [fileManager fileExistsAtPath:filePath];
    if (isDir == NO) {
        NSError *erro ;
        [fileManager createDirectoryAtPath:filePath withIntermediateDirectories:YES attributes:nil error:&erro];
    }
    return filePath;

}

+(NSDictionary *)cagegoryFileName
{
    NSDictionary *textSizeAdjust = @{@"0":ALL_CATEGORY_NAMES,
                                     @"1":MY_CATEGORY_NAMES,
                                     @"2":MORE_CATEGORY_NAMES,
                                     };
    return textSizeAdjust;
}

+ (void)writeCategory:(NSMutableArray *)cateArray categoryType:(NSInteger )cateType
{
    NSString *filePath = [self filePathName];
  
    NSString *filename = [[self cagegoryFileName] objectForKey:[NSString stringWithFormat:@"%@",@(cateType)]];
    NSString *plistPath = [filePath stringByAppendingPathComponent:filename];
    
    
    NSMutableArray *arr = [[NSMutableArray alloc]initWithCapacity:3];
    
    if (cateType == 0) {
        [cateArray enumerateObjectsUsingBlock:^(YZNavListModel *catemodel, NSUInteger idx,BOOL *stop){
            NSDictionary *cateDic = catemodel.keyValues;
            [arr addObject:cateDic];
        }];
        
        
        [arr writeToFile:plistPath atomically:YES];
    }
    else
    {
        [cateArray enumerateObjectsUsingBlock:^(CatoryModel *catemodel, NSUInteger idx,BOOL *stop){
            NSDictionary *cateDic = catemodel.keyValues;
            [arr addObject:cateDic];
        }];
        
        
        [arr writeToFile:plistPath atomically:YES];
    }
    
    

}
+(BOOL)isExsitMyCategory
{
  
    
    NSString *filePath = [self filePathName];
    
    NSString *plistPath = [filePath stringByAppendingPathComponent:MY_CATEGORY_NAMES];
    
    return [[NSFileManager defaultManager] fileExistsAtPath:plistPath];
    
}

+ (NSMutableArray *)readcategoryWithType:(NSInteger )cateType
{
    NSString *filePath = [self filePathName];
    
    
    NSString *plistPath = [filePath stringByAppendingPathComponent:[[self cagegoryFileName] objectForKey:[NSString stringWithFormat:@"%@",@(cateType)]]];

    
    if (![[NSFileManager defaultManager] fileExistsAtPath:plistPath]) {
        return nil;
    }
    if (cateType == 0) {
        return [YZNavListModel objectArrayWithFile:plistPath];
    }
    else
    {
        return [CatoryModel objectArrayWithFile:plistPath];
    }
    
}


@end
