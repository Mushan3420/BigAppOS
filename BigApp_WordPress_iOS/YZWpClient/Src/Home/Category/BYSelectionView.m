//
//  BYSelectionView.m
//  BYDailyNews
//
//  Created by bassamyan on 15/1/18.
//  Copyright (c) 2015年 apple. All rights reserved.
//

#import "BYSelectionView.h"
#import "BuildConfig.h"

@implementation BYSelectionView

-(void)makeSelectionViewWithCategory:(CatoryModel *)cateModel isFirst:(BOOL)first
{
    self.backgroundColor = [UIColor whiteColor];
    
    
    self.aCategroyModel = cateModel;
    self.gesture = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(panView:)];
    self.longGesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(longPress)];
    self.longGesture.minimumPressDuration = 1;
    self.longGesture.allowableMovement = 20;
//    [self addGestureRecognizer:self.longGesture];®
    self.isEqualFirst = first;
//    self.isEqualFirst = [cateModel.categoryName isEqualToString:@"首页"];
    
    [self setTitle:cateModel.categoryName forState:0];
    [self setTitleColor:Color_gray forState:0];
//    if (self.isEqualFirst) {
//        [self setTitleColor:[UIColor redColor] forState:0];
//    }
    self.titleLabel.font = [UIFont systemFontOfSize:13];
    
    

    
    
    //屏蔽按钮
    self.hid_btn = [[UIButton alloc] initWithFrame:self.bounds];
    self.hid_btn.tag = 0;
    self.hid_btn.hidden = NO;
    [self.hid_btn addTarget:self
                     action:@selector(operationWithHidBtn)
           forControlEvents:1 << 6];
    [self addSubview:self.hid_btn];
    self.hid_btn.backgroundColor = [UIColor clearColor];
    self.hid_btn.layer.cornerRadius = 1;
    self.hid_btn.layer.borderColor = [border_gray CGColor];
    self.hid_btn.layer.borderWidth = 0.5;
    
    //右上角的删除图标
    if (!self.isEqualFirst) {
        CGFloat delete_btn_width = 6;
        self.delete_btn = [[UIButton alloc] initWithFrame:CGRectMake(self.frame.size.width-delete_btn_width-2, -delete_btn_width+2, 2*delete_btn_width, 2*delete_btn_width)];
        self.delete_btn.userInteractionEnabled = NO;
        [self.delete_btn setImage:[UIImage imageNamed:@"buttonClose"] forState:UIControlStateNormal];
        self.delete_btn.layer.cornerRadius = self.delete_btn.frame.size.width/2;
        self.delete_btn.hidden = YES;
        self.delete_btn.backgroundColor = RGBCOLOR(203, 204, 205);
        self.delete_btn.backgroundColor = [UIColor clearColor];
        [self addSubview:self.delete_btn];
        
        [self addTarget:self
                 action:@selector(operationWithoutHidBtn)
       forControlEvents:1<<6];
    }
    
    
    __weak typeof(self) unself = self;
    self.operations = ^(NSString *notiName, NSString *notiTitle, int notiIndex){
        
        NSMutableDictionary *dic = [[NSMutableDictionary alloc] init];
        [dic setObject:notiName forKey:@"noti_name"];
        [dic setObject:notiTitle forKey:@"noti_title"];
        [dic setObject:[NSNumber numberWithInt:notiIndex] forKey:@"noti_index"];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"operations_from_selectionView"
                                                            object:unself
                                                          userInfo:dic];
        [[NSNotificationCenter defaultCenter] removeObserver:unself name:@"operations_from_selectionView" object:nil];
    };
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(conditionBarItemClick:)
                                                 name:@"click_conditionBarItem"
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(sortButtonClick)
                                                 name:@"srot_btn_click"
                                               object:nil];
}


/******************************
 
 longpress事件
 
 ******************************/
-(void)longPress
{
    if (self.hid_btn.hidden == NO) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"press_longPressGesture"
                                                            object:self
                                                          userInfo:nil];
        [[NSNotificationCenter defaultCenter] removeObserver:self name:@"press_longPressGesture" object:nil];
        if (self.tag == 1) {
            [self addGestureRecognizer:self.gesture];
        }
    }
}

/******************************
 
 通知:srot_btn_click
 
 ******************************/
-(void)sortButtonClick
{
    if (self.tag == 1){self.delete_btn.hidden = !self.delete_btn.hidden;}
    self.hid_btn.hidden = !self.hid_btn.hidden;
    self.hid_btn.tag = !self.hid_btn.tag;
    
    if (self.gestureRecognizers != nil) {
        [self removeGestureRecognizer:self.gesture];
    }
    if (self.tag == 1 && !self.isEqualFirst && self.hid_btn.hidden == YES) {
        [self addGestureRecognizer:self.gesture];
    }
}

/******************************
 
 通知:selfBarItemClick
 selectionView中的按钮被点击后
 修改conditionBar中的点击的item
 
 ******************************/
-(void)conditionBarItemClick:(NSNotification *)noti
{
    BOOL isEqualToTitle = [self.titleLabel.text isEqualToString:[noti.userInfo objectForKey:@"title"]];
    
    if (self.isEqualFirst && !isEqualToTitle){
        [self setTitleColor:border_gray forState:0];
    }
    else if (isEqualToTitle) {
        [self setTitleColor:[UIColor redColor] forState:0];
    }
    else{
        [self setTitleColor:Color_gray forState:0];
    }
}

/******************************
 
 未点击“排序”前 hid_btn显示并覆盖
 self的target点击事件 只激活hid_btn
 的点击事件
 
 self.tag  1代表位于上方
           0代表位于下方
 
 1区域点击: 通知conditionBar选择
 这个item的title的区域
 并设置这个item的字体颜色为红色
 其他items的字体颜色为普通灰 第一个为淡灰
 
 
 0区域点击: 将点击的item移到第一区域
 并设置tag为1 并且通知conditionbar
 在最后面添加这个item
 ******************************/

-(void)operationWithHidBtn
{
    if (self.hid_btn.hidden == NO) {
        if (self.tag == 1) {
//            [self setTitleColor:[UIColor redColor] forState:0];
            self.operations(@"select_itemOfView",self.titleLabel.text,0);
            [self animationActionFinal];
        }
        else if (self.tag == 0)
        {
            [self changeView2toView1];
        }
    }
}

/******************************
 
 点击“排序”后 hid_btn被隐藏 
 self的 target的点击事件被激活
 
 self.tag  1代表位于上方
 0代表位于下方
 
 1区域点击: 删除在1区域的这个item
 并转移到第二区域 设置tag为0 并通知
 conditionBar移除相应位置的这个item
 
 0区域点击: 将点击的item移到第一区域
 并设置tag为1 并且通知conditionbar
 在最后面添加这个item 并添加可移动手势
 
 ******************************/
-(void)operationWithoutHidBtn
{

    if (self.tag == 1)
    {
        [self changeView1toView2];
    }
    else if (self.tag == 0) {
        self.delete_btn.hidden = NO;
        [self addGestureRecognizer:self.gesture];
        [self changeView2toView1];
    }
}

/******************************
 
 手势内容
 self.tag 1代表在上方 0代表在下方
 
 1. 1区域
 
 ******************************/
- (void)panView:(UIPanGestureRecognizer *)pan
{

    [self.superview exchangeSubviewAtIndex:[self.superview.subviews indexOfObject:self] withSubviewAtIndex:[[self.superview subviews] count] - 1];
    CGPoint translation = [pan translationInView:pan.view];
    CGPoint center = pan.view.center;
    center.x += translation.x;
    center.y += translation.y;
    pan.view.center = center;
    [pan setTranslation:CGPointZero inView:pan.view];
    
    switch (pan.state) {
        case UIGestureRecognizerStateBegan:
        {
            CGRect self_frame = self.frame;
            [self setFrame:CGRectMake(self_frame.origin.x-view_addWith, self_frame.origin.y-view_addWith, self_frame.size.width+view_addWith*2, self_frame.size.height+view_addWith*2)];
        }
            break;
        case UIGestureRecognizerStateChanged:
        {
            BOOL isInArea1 = [self whetherInAreaWithArray:views1 Point:center];
            if (isInArea1) {
                NSInteger indexX = (center.x <= view_width+20)? 0 : (center.x - view_width-20)/(5+view_width) + 1;
                NSInteger indexY = (center.y <= 55)? 0 : (center.y - 55)/35 + 1;
                NSInteger index = indexX + indexY*4;
                index = (index == 0)? 1:index;
                [views_array removeObject:self];
                [views1 insertObject:self atIndex:index];
                views_array = views1;
                [self animationForView1];
                self.operations(@"switch_position",self.titleLabel.text,(int)index);
                
            }
            else if (!isInArea1 && center.y < [self array1MaxY]+55) {
                [views_array removeObject:self];
                [views1 insertObject:self atIndex:views1.count];
                views_array = views1;
                [self animationForView1];
                self.operations(@"move_itemToLast",self.titleLabel.text,0);
                
            }
            else if (center.y > [self array1MaxY]+55){
                [self changeView1toView2];
            }
        }
            break;
        case UIGestureRecognizerStateEnded:
            [self animationActionFinal];
            break;
        default:
            break;
    }
}

/******************************
 
 从上方 移到 下方
 
 ******************************/
-(void)changeView1toView2
{
    [views_array removeObject:self];
    [views2 insertObject:self atIndex:0];
    views_array = views2;
    self.tag = 0;
    self.delete_btn.hidden = YES;
    [self removeGestureRecognizer:self.gesture];
    self.operations(@"remove_item",self.titleLabel.text,0);
    [self animationActionFinal];
}

/******************************
 
 从下方 移到 上方
 
 ******************************/
-(void)changeView2toView1
{
    [views_array removeObject:self];
    [views1 insertObject:self atIndex:views1.count];
    views_array = views1;
    self.tag = 1;
    self.operations(@"add_newItem",self.titleLabel.text,0);
    [self animationActionFinal];
}

/******************************
 
 计算移动物体的中点是否在上方的1区域
 
 ******************************/
- (BOOL)whetherInAreaWithArray:(NSMutableArray *)array Point:(CGPoint)point{
    
    int row = (array.count%4 == 0)? 4 : array.count%4;
    int column =  (int)(array.count-1)/4+1;
    if ((point.x > 0 && point.x <=BYScreenWidth &&point.y > 0 && point.y <= 35*(column-1)+10 )||
        (point.x > 0 && point.x <= (row*(5+view_width)+10 )&& point.y > 35*(column -1)+10 && point.y <= 35 * column+10))
    {
        return YES;
    }
    return NO;
}

/******************************
 
 计算上方区域的最大值
 
 ******************************/
- (unsigned long)array1MaxY{
    unsigned long y = 0;
    y = ((views1.count-1)/4+1)*35 +30;
    return y;
}

/******************************
 
 重新排列整个 上方区域
 
 ******************************/
- (void)animationForView1
{
    for (int i = 0; i < views1.count; i++){
        if ([views1 objectAtIndex:i] != self){
            [self animationWithView:[views1 objectAtIndex:i] frame:CGRectMake(20+(5+view_width)*(i%4), 20+35*(i/4), view_width, 30)];
        }
    }
}

/******************************
 
 重新排列整个 下方区域和“更多”label
 
 ******************************/
-(void)animationForView2{
    for (int i = 0; i < views2.count; i++) {
        if ([views2 objectAtIndex:i] != self) {
            [self animationWithView:[views2 objectAtIndex:i] frame:CGRectMake(20+(5+view_width)*(i%4), [self array1MaxY]+38+35*(i/4), view_width, 25)];
        }
    }
    [self animationWithView:self.moreChanelslabel frame:CGRectMake(0,[self array1MaxY],BYScreenWidth,30)];
}


/******************************
 
 重新排列整个 上方区域、下方区域和“更多label”
 
 ******************************/
- (void)animationActionFinal
{
    for (int i = 0; i <views1.count; i++) {
        [self animationWithView:[views1 objectAtIndex:i] frame:CGRectMake(20+(5+view_width)*(i%4), 20+35*(i/4), view_width, 30)];
    }
    for (int i = 0; i < views2.count; i++) {
        [self animationWithView:[views2 objectAtIndex:i] frame:CGRectMake(20+(5+view_width)*(i%4), [self array1MaxY]+38+35*(i/4), view_width, 30)];
    }
    [self animationWithView:self.moreChanelslabel frame:CGRectMake(0,[self array1MaxY],BYScreenWidth,30)];
}

-(void)animationWithView:(UIView *)view frame:(CGRect)frame
{
    [UIView animateWithDuration:resetView_time delay:0 options:UIViewAnimationOptionLayoutSubviews animations:^{
        [view setFrame:frame];
    } completion:^(BOOL finished){}];
}

@end

// 版权属于原作者
// http://code4app.com (cn) http://code4app.net (en)
// 发布代码于最专业的源码分享网站: Code4App.com 
