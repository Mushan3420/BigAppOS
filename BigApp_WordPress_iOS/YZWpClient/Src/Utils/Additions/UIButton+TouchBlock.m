//
//  UIButton+TouchBlock.m
//  SNYifubao
//
//  Created by zhoutl on 14-7-23.
//  Copyright (c) 2014年 Suning. All rights reserved.
//

#import "UIButton+TouchBlock.h"
#import <objc/runtime.h>
#import "UIColor+SNAdditions.h"
static char     SnActionTag;
static char     SnButtonBackGroundColorKey ;//= @"kSNButtonBackGroundColorKey";
static char     SnButtonDisableKey ;//= @"kSnButtonDisableKey";

@implementation UIButton (TouchBlock)

- (void)addAction:(TouchBlock)block
{
    objc_setAssociatedObject(self, &SnActionTag, block, OBJC_ASSOCIATION_COPY_NONATOMIC);
    [self addTarget:self action:@selector(snBtnAction:) forControlEvents:UIControlEventTouchUpInside];
}

- (void)addAction:(TouchBlock)block forControlEvents:(UIControlEvents)controlEvents
{
    objc_setAssociatedObject(self, &SnActionTag, block, OBJC_ASSOCIATION_COPY_NONATOMIC);
    [self addTarget:self action:@selector(snBtnAction:) forControlEvents:controlEvents];
}

- (void)snBtnAction:(id)sender
{
    TouchBlock blockAction = (TouchBlock)objc_getAssociatedObject(self, &SnActionTag);
    if (blockAction)
    {
        blockAction(self);
    }
}

- (BOOL)disable
{
    return [(NSNumber*)objc_getAssociatedObject(self, &SnButtonDisableKey) boolValue];
}

- (void)setDisable:(BOOL)newdisable
{//只针对（ 无背景图片的 + backgroundcolor为纯色 ）按钮，disable后，设置backgroundcolor为特定的0xdddddd值
    
    if (self.disable == newdisable) {
        return;
    }
    
    objc_setAssociatedObject(self, &SnButtonDisableKey, [NSNumber numberWithBool:newdisable]  , OBJC_ASSOCIATION_ASSIGN);
    
    if (newdisable) {
        objc_setAssociatedObject(self, &SnButtonBackGroundColorKey, self.backgroundColor, OBJC_ASSOCIATION_COPY_NONATOMIC);
        self.enabled = NO;
        self.backgroundColor = [UIColor colorWithHexString:@"e8e8e8"];
        self.layer.borderWidth = 1.;
        self.layer.borderColor = [UIColor colorWithHexString:@"dddddd"].CGColor;
    }
    else
    {
        self.enabled = YES;
        self.backgroundColor = (UIColor*)objc_getAssociatedObject(self, &SnButtonBackGroundColorKey) ;
        self.layer.borderWidth = 0.;
    }
}

@end
