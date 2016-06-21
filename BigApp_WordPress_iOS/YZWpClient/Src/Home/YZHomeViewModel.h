//
//  YZHomeViewModel.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/17.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "YZPostsModel.h"
typedef NS_ENUM(NSInteger, ImageCountsType) {
    NotImage = 0,
    OneImageType = 1,
    MoreThanOneImageType = 2
};



typedef NS_ENUM(NSInteger, CommentType) {
    NotAllowCommentType = 0, //不允许评论
    AllowCommentNotLoginType = 1,//允许匿名评论
    NotAllowAnonymousCommentType = 2,//不允许匿名评论
    NeedLoninCommentType = 3//必须是注册用户评论
};

@interface YZHomeViewModel : NSObject

@property (nonatomic, strong)YZPostsModel *poastModel;

@property (nonatomic, strong)NSMutableArray   *imageList;
@property (nonatomic, strong)NSString         *titleText;
@property (nonatomic, strong)NSString         *commentsText;
@property (nonatomic, strong)NSString         *articleDataText;


@property (nonatomic, assign)CGRect          imageListViewFrame;
@property (nonatomic, assign)CGRect          abstractLabelFrame;
@property (nonatomic, assign)CGRect          titleLabelFrame;
@property (nonatomic, assign)CGRect          articleDataLabelFrame;


@property (nonatomic, assign)NSInteger       cellHeight;

@property (nonatomic, assign)ImageCountsType imageCountsType;

@property (nonatomic, strong) NSMutableArray         *featuredImages;

@property (nonatomic, assign) CommentType      commentType;


+ (instancetype)viewModel;
@end
