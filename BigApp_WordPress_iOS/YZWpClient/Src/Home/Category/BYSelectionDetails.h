//
//  BYSelectionDetails.h
//  BYDailyNews
//
//  Created by bassamyan on 15/1/18.
//  Copyright (c) 2015年 apple. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BYSelectionView.h"

@interface BYSelectionDetails : UIScrollView
-(void)makeMainContent;
@property (nonatomic,strong) NSMutableArray *views1;
@property (nonatomic,strong) NSMutableArray *views2;

-(void)makeMainContent:(NSMutableArray *)listArray otherList:(NSArray *)otherValues;

@end

// 版权属于原作者
// http://code4app.com (cn) http://code4app.net (en)
// 发布代码于最专业的源码分享网站: Code4App.com 
