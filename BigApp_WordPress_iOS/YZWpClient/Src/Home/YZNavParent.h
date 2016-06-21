//
//	YZNavParent.h
//
//	Create by 桃林 周 on 22/7/2015
//	Copyright © 2015. All rights reserved.
//


#import <UIKit/UIKit.h>
#import "YZNavMeta.h"

@interface YZNavParent : NSObject

@property (nonatomic, assign) NSInteger iD;
@property (nonatomic, assign) NSInteger count;
@property (nonatomic, strong) NSString * descriptions;
@property (nonatomic, strong) NSString * link;
@property (nonatomic, strong) YZNavMeta * meta;
@property (nonatomic, strong) NSString * name;
@property (nonatomic, assign) NSInteger parent;
@property (nonatomic, strong) NSString * slug;
@property (nonatomic, strong) NSString * taxonomy;

@end