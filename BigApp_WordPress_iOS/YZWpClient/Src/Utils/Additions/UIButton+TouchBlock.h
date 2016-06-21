//
//  UIButton+TouchBlock.h
//  SNYifubao
//
//  Created by zhoutl on 14-7-23.
//  Copyright (c) 2014年 Suning. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void (^TouchBlock)(UIButton *btn);

@interface UIButton (TouchBlock)

@property(nonatomic,readwrite)    BOOL disable;

- (void)addAction:(TouchBlock)block;
- (void)addAction:(TouchBlock)block forControlEvents:(UIControlEvents)controlEvents;

@end
