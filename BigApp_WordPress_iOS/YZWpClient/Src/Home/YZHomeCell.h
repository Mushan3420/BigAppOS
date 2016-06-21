//
//  YZHomeCell.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/17.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "YZHomeViewModel.h"

@interface YZHomeCell : YZTableViewCell

@property (nonatomic, strong) YZHomeViewModel *viewModel;

@property (nonatomic, strong) UIView          *imageListView;
@property (nonatomic, strong) UILabel         *articleDataLabel;
@property (nonatomic, strong) UILabel         *titleTextLabel;
@property (nonatomic, strong) UILabel         *commentsTextLabel;


@end
