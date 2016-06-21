//
//  BYSelectionView.h
//  BYDailyNews
//
//  Created by bassamyan on 15/1/18.
//  Copyright (c) 2015年 apple. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "CatoryModel.h"

typedef void(^operateBlock)(NSString * , NSString * , int);


@interface BYSelectionView : UIButton
{
    @public
    NSMutableArray *views_array;
    NSMutableArray *views1;
    NSMutableArray *views2;
}
@property(strong,nonatomic)CatoryModel *aCategroyModel;
@property (nonatomic,strong) UIView   *moreChanelslabel;
@property (nonatomic,strong) UIButton *delete_btn;
@property (nonatomic,strong) UIButton *hid_btn;
@property (nonatomic,copy)   operateBlock operations;


@property (nonatomic,assign) BOOL isEqualFirst;
@property (nonatomic,strong) UIPanGestureRecognizer *gesture;
@property (nonatomic,strong) UILongPressGestureRecognizer *longGesture;
-(void)makeSelectionViewWithCategory:(CatoryModel *)cateModel isFirst:(BOOL)first;
@end

// 版权属于原作者
// http://code4app.com (cn) http://code4app.net (en)
// 发布代码于最专业的源码分享网站: Code4App.com 
