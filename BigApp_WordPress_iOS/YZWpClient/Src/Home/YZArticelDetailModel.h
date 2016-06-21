//
//	YZArticelDetail.h
//
//	Create by 桃林 周 on 20/7/2015
//	Copyright © 2015. All rights reserved.
//


#import <UIKit/UIKit.h>
#import "YZArticelAuthorModel.h"
#import "YZArticelMeta.h"
#import "YZArticelTerm.h"

@interface YZArticelDetailModel : NSObject

@property (nonatomic, assign) NSInteger iD;
@property (nonatomic, strong) YZArticelAuthorModel * author;
@property (nonatomic, assign) NSInteger commentNum;
@property (nonatomic, strong) NSString * commentStatus;
@property (nonatomic, strong) NSString * content;
@property (nonatomic, strong) NSString * date;
@property (nonatomic, strong) NSString * dateGmt;
@property (nonatomic, strong) NSString * dateTz;
@property (nonatomic, strong) NSString * excerpt;
@property (nonatomic, strong) NSObject * featuredImage;
@property (nonatomic, strong) NSString * format;
@property (nonatomic, strong) NSString * guid;
@property (nonatomic, strong) NSString * link;
@property (nonatomic, assign) NSInteger menuOrder;
@property (nonatomic, strong) YZArticelMeta * meta;
@property (nonatomic, strong) NSString * modified;
@property (nonatomic, strong) NSString * modifiedGmt;
@property (nonatomic, strong) NSString * modifiedTz;
@property (nonatomic, strong) NSObject * parent;
@property (nonatomic, strong) NSString * pingStatus;
@property (nonatomic, strong) NSString * slug;
@property (nonatomic, strong) NSString * status;
@property (nonatomic, assign) BOOL sticky;
@property (nonatomic, strong) YZArticelTerm * terms;
@property (nonatomic, strong) NSString * title;
@property (nonatomic, strong) NSString * type;
@property (nonatomic, assign) BOOL     is_favorited;

@end