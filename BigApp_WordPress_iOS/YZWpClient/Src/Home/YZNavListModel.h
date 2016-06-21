//
//	YZNavListModel.h
//
//	Create by 桃林 周 on 22/7/2015
//	Copyright © 2015. All rights reserved.
//


#import <UIKit/UIKit.h>
#import "YZNavMeta.h"
#import "YZNavParent.h"
@class YZBannerList;
@interface YZNavListModel : NSObject

@property (nonatomic, assign) NSInteger ID;
@property (nonatomic, assign) NSInteger count;
@property (nonatomic, strong) NSString * descriptions;
@property (nonatomic, strong) NSString * link;
@property (nonatomic, strong) YZNavMeta * meta;
@property (nonatomic, strong) NSString * name;
@property (nonatomic, strong) YZNavParent * parent;
@property (nonatomic, strong) NSString * slug;
@property (nonatomic, strong) NSString * type;//   1.站长自定义外部链接 custom 2.站内链接 post_type 3.分类列表 navListModel.type=taxonomy

@property (nonatomic, strong) NSArray * banner_list;



@end

@interface YZBannerList : NSObject

@property (nonatomic, strong) NSString * iD;
@property (nonatomic, strong) NSString * img_url;
@property (nonatomic, strong) NSString * link;
@property (nonatomic, strong) NSString * name;
@property (nonatomic, strong) NSString * rank;
@property (nonatomic, assign) BOOL show;
@property (nonatomic, strong) NSString * type;//1 站外 2站内文章

@end