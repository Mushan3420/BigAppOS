//
//	YZADetailData.h
//
//	Create by 桃林 周 on 29/7/2015
//	Copyright © 2015. All rights reserved.
//

//	Model file Generated using JSONExport: https://github.com/Ahmed-Ali/JSONExport

#import <UIKit/UIKit.h>
#import "YZBaseModel.h"
#import "YZADetailAuthor.h"
#import "YZADetailMeta.h"
#import "YZPostInfoModel.h"

@interface YZADetailCommentModel : NSObject

@property (nonatomic, assign) NSInteger ID;
@property (nonatomic, strong) YZADetailAuthor * author;
@property (nonatomic, strong) NSString * content;
@property (nonatomic, strong) NSString * date;
@property (nonatomic, strong) NSString * date_gmt;
@property (nonatomic, strong) NSString * dateTz;
@property (nonatomic, strong) YZADetailMeta * meta;
@property (nonatomic, assign) NSInteger parent;
@property (nonatomic, assign) NSInteger post;
@property (nonatomic, strong) NSString * status;
@property (nonatomic, strong) NSString * type;
@property (nonatomic, strong) YZPostInfoModel * post_info;


@end