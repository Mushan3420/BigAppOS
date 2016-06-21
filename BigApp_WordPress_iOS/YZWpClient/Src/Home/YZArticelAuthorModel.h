//
//	YZArticelAuthor.h
//
//	Create by 桃林 周 on 20/7/2015
//	Copyright © 2015. All rights reserved.
//


#import <UIKit/UIKit.h>
#import "YZArticelMeta.h"
#import "MJExtension.h"
@interface YZArticelAuthorModel : NSObject

@property (nonatomic, assign) NSInteger iD;
@property (nonatomic, strong) NSString * uRL;
@property (nonatomic, strong) NSString * avatar;
//@property (nonatomic, strong) NSString * description;
@property (nonatomic, strong) NSString * firstName;
@property (nonatomic, strong) NSString * lastName;
@property (nonatomic, strong) YZArticelMeta * meta;
@property (nonatomic, strong) NSString * name;
@property (nonatomic, strong) NSString * nickname;
@property (nonatomic, strong) NSString * registered;
@property (nonatomic, strong) NSString * slug;
@property (nonatomic, strong) NSString * username;


@end