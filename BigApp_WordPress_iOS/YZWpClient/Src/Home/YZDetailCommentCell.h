//
//  YZDetailCommentCell.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/28.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>

@class YZDetailCommentViewModel;

@interface YZDetailCommentCell : YZTableViewCell

@property (nonatomic, strong)YZDetailCommentViewModel *viewModel;

@property (nonatomic, assign)BOOL lineHidden;

@property (nonatomic, strong)UILabel  *dataLabel;

@property (nonatomic, strong)UIButton *openMoreBtn;

@end
