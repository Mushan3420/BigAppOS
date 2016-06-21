//
//	YZArticelCategory.h
//
//	Create by 桃林 周 on 20/7/2015
//	Copyright © 2015. All rights reserved.
//


#import <UIKit/UIKit.h>
#import "YZArticelMeta.h"

@interface YZArticelCategory : NSObject

@property (nonatomic, assign) NSInteger iD;
@property (nonatomic, assign) NSInteger count;
@property (nonatomic, strong) NSString * descriptions;
@property (nonatomic, strong) NSString * link;
@property (nonatomic, strong) YZArticelMeta * meta;
@property (nonatomic, strong) NSString * name;
@property (nonatomic, strong) NSObject * parent;
@property (nonatomic, strong) NSString * slug;
@property (nonatomic, strong) NSString * taxonomy;


@end