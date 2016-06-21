//
//  PlistFileOperation.h
//  YZWpClient
//
//  Created by chaoliangmei on 15/8/27.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "CatoryModel.h"
#import "YZNavListModel.h"

typedef enum {
    AllCategory,
    MyCategory,
    MoreCategor
}CategoryType;

#define ALL_CATEGORY_NAMES   @"all_category.plist"

#define MY_CATEGORY_NAMES    @"my_category.plist"

#define MORE_CATEGORY_NAMES    @"more_category.plist"

@interface PlistFileOperation : NSObject

+ (void)writeCategory:(NSMutableArray *)cateArray categoryType:(NSInteger )cateType;
+ (NSMutableArray *)readcategoryWithType:(NSInteger )cateType;
+(BOOL)isExsitMyCategory;

@end
