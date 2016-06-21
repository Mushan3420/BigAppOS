//
//  YZDetailCommentCell.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/28.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>

@class YZCommentRecordsViewModel;

@interface YZCommentRecordsCell : UITableViewCell

@property (nonatomic, strong)YZCommentRecordsViewModel *viewModel;

@property (nonatomic, assign)BOOL lineHidden;

@property (nonatomic, strong)UILabel  *dataLabel;

@property (nonatomic, strong)UIButton *openMoreBtn;

@property (nonatomic, strong)UIButton  *postTitleView;

@end
