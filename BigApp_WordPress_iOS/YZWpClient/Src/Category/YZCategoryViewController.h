//
//  YZCategoryViewController.h
//  YZWpClient
//
//  Created by chaoliangmei on 15/8/26.
//  Copyright (c) 2015年 com.youzu. All rights reserved.
//

#import <UIKit/UIKit.h>

#define DefaultCategoryNum          6


@protocol CategoryBackDelegate <NSObject>

- (void)categoryBackArray:(NSMutableArray *)myCategoryArray;

@end


@interface YZCategoryViewController : UIViewController

@property(strong,nonatomic)id<CategoryBackDelegate>delegate;

@end
