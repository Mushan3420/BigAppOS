//
//	YZPostsModel.h
//
//	Create by 桃林 周 on 17/7/2015
//	Copyright © 2015. All rights reserved.
//


#import <UIKit/UIKit.h>
#import "YZBaseModel.h"

@class YZAttachmentMeta;
@class YZAuthor;
@class YZFeaturedImage;
@class YZMeta;
@class YZTerm;
@class YZImageMeta;
@class YZSize;

@interface YZPostsModel : YZBaseModel
@property (nonatomic, assign) NSInteger postID;
@property (nonatomic, strong) YZAuthor * author;
@property (nonatomic, assign) NSInteger comment_num;
@property (nonatomic, assign) NSInteger views;
@property (nonatomic, strong) NSString * comment_status;
@property (nonatomic, strong) NSString * content;
@property (nonatomic, strong) NSString * date;
@property (nonatomic, strong) NSString * date_gmt;
@property (nonatomic, strong) NSString * date_tz;
@property (nonatomic, strong) NSString * excerpt;
@property (nonatomic, strong) NSArray  * featured_image;
@property (nonatomic, strong) NSString * format;
@property (nonatomic, strong) NSString * guid;
@property (nonatomic, strong) NSString * link;
@property (nonatomic, assign) NSInteger menu_order;
@property (nonatomic, strong) YZMeta   * meta;
@property (nonatomic, strong) NSString * modified;
@property (nonatomic, strong) NSString * modified_gmt;
@property (nonatomic, strong) NSString * modified_tz;
@property (nonatomic, strong) NSString * parent;
@property (nonatomic, strong) NSString * ping_status;
@property (nonatomic, strong) NSString * slug;
@property (nonatomic, strong) NSString * status;
@property (nonatomic, assign) BOOL sticky;
@property (nonatomic, strong) YZTerm * terms;
@property (nonatomic, strong) NSString * title;
@property (nonatomic, strong) NSString * type;
@property (nonatomic, strong) NSString *comment_type;
@property (nonatomic, strong) NSArray  *all_article_images;

@end


@interface YZAttachmentMeta : YZBaseModel
@property (nonatomic, strong) NSString * file;
@property (nonatomic, assign) NSInteger height;
@property (nonatomic, strong) YZImageMeta * imageMeta;
@property (nonatomic, strong) YZSize * sizes;
@property (nonatomic, assign) NSInteger width;
@end


@interface YZAuthor : YZBaseModel
@property (nonatomic, assign) NSInteger authorID;
@property (nonatomic, strong) NSString * URL;
@property (nonatomic, strong) NSString * avatar;
@property (nonatomic, strong) NSString * descriptions;
@property (nonatomic, strong) NSString * firstName;
@property (nonatomic, strong) NSString * lastName;
@property (nonatomic, strong)   YZMeta * meta;
@property (nonatomic, strong) NSString * name;
@property (nonatomic, strong) NSString * nickname;
@property (nonatomic, strong) NSString * registered;
@property (nonatomic, strong) NSString * slug;
@property (nonatomic, strong) NSString * username;
@end



@interface YZCategory : YZBaseModel
@property (nonatomic, assign) NSInteger iD;
@property (nonatomic, assign) NSInteger count;
@property (nonatomic, strong) NSString * descriptions;
@property (nonatomic, strong) NSString * link;
@property (nonatomic, strong) YZMeta * meta;
@property (nonatomic, strong) NSString * name;
@property (nonatomic, strong) NSObject * parent;
@property (nonatomic, strong) NSString * slug;
@property (nonatomic, strong) NSString * taxonomy;
@end


@interface YZFeaturedImage : YZBaseModel
@property (nonatomic, assign) NSInteger iD;
@property (nonatomic, strong) YZAttachmentMeta * attachmentMeta;
@property (nonatomic, strong) YZAuthor * author;
@property (nonatomic, assign) NSInteger comment_num;
@property (nonatomic, strong) NSString * comment_status;
@property (nonatomic, strong) NSString * content;
@property (nonatomic, strong) NSString * date;
@property (nonatomic, strong) NSString * date_gmt;
@property (nonatomic, strong) NSString * date_tz;
@property (nonatomic, strong) NSObject * excerpt;
@property (nonatomic, strong) NSString * format;
@property (nonatomic, strong) NSString * guid;
@property (nonatomic, assign) BOOL isImage;
@property (nonatomic, strong) NSString * link;
@property (nonatomic, assign) NSInteger menu_order;
@property (nonatomic, strong) YZMeta * meta;
@property (nonatomic, strong) NSString * modified;
@property (nonatomic, strong) NSString * modified_gmt;
@property (nonatomic, strong) NSString * modified_tz;
@property (nonatomic, assign) NSInteger parent;
@property (nonatomic, strong) NSString * ping_status;
@property (nonatomic, strong) NSString * slug;
@property (nonatomic, strong) NSString * source;
@property (nonatomic, strong) NSString * status;
@property (nonatomic, assign) BOOL sticky;
@property (nonatomic, strong) NSArray * terms;
@property (nonatomic, strong) NSString * title;
@property (nonatomic, strong) NSString * type;
@end


@interface YZImageMeta : YZBaseModel
@property (nonatomic, assign) NSInteger aperture;
@property (nonatomic, strong) NSString * camera;
@property (nonatomic, strong) NSString * caption;
@property (nonatomic, strong) NSString * copyright;
@property (nonatomic, assign) NSInteger createdTimestamp;
@property (nonatomic, strong) NSString * credit;
@property (nonatomic, assign) NSInteger focalLength;
@property (nonatomic, assign) NSInteger iso;
@property (nonatomic, assign) NSInteger orientation;
@property (nonatomic, assign) NSInteger shutterSpeed;
@property (nonatomic, strong) NSString * title;
@end


@interface YZLink : YZBaseModel
@property (nonatomic, strong) NSString * archives;
@property (nonatomic, strong) NSString * self;
@end


@interface YZMeta : YZBaseModel
@property (nonatomic, strong) YZLink * links;
@end

@interface YZSize : YZBaseModel
@end



@interface YZTerm : YZBaseModel
@property (nonatomic, strong) NSArray * category;
@property (nonatomic, strong) NSArray * post_tag;
@end

@interface YZPostTag : YZBaseModel
@property (nonatomic, assign) NSInteger iD;
@property (nonatomic, assign) NSInteger count;
@property (nonatomic, strong) NSString * descriptions;
@property (nonatomic, strong) NSString * link;
@property (nonatomic, strong) NSString * name;
@property (nonatomic, strong) NSObject * parent;
@property (nonatomic, strong) NSString * slug;
@property (nonatomic, strong) NSString * taxonomy;
@end


