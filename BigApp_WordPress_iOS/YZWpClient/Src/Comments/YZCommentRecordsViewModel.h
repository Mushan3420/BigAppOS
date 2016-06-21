//
//  YZDetailCommentViewModel.h
//  YZWpClient
//
//  Created by zhoutl on 15/7/29.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "YZADetailCommentModel.h"


#define kCommmetCellMarginTop 18
#define kContentMaxHeight  55

@interface YZCommentRecordsViewModel : NSObject

@property (nonatomic, strong)YZADetailCommentModel *model;

@property (nonatomic, strong)NSString  *userNameText;
@property (nonatomic, strong)NSString  *detailText;
@property (nonatomic, strong)NSString  *dataText;

@property (nonatomic, assign)BOOL      isOpenShow;

@property (nonatomic, assign)NSInteger  cellHeight;
@property (nonatomic, assign)BOOL       isRealHeight;
@property (nonatomic, assign)NSInteger  detailTextLabelHeight;
@property (nonatomic, assign)NSInteger  textRealHeight;


@property (nonatomic, assign)CGRect          detailLabelFrame;
@property (nonatomic, assign)CGRect          dataTextLabelFrame;
@property (nonatomic, assign)CGRect          postViewFrame;






@end
